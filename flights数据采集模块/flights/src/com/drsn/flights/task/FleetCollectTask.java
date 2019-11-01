package com.drsn.flights.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.drsn.flights.bean.ProxyBean;
import com.drsn.flights.dao.FleetDao;
import com.drsn.flights.dao.PrefixDao;

/**
 * 爬取fleet（航班号）的线程类
 * @author drsnow
 *
 */
@Service
public class FleetCollectTask extends TaskBasics {
	//缓存的prefix的集合
	private volatile List<String> prefixlist = new ArrayList<String>();
	//当前缓存集合的起始id
	private volatile int id = 1;
	//缓存条数，由用户配置
	private int number;

	//已存在的Fleet
	private Set<String> existedFleet = Collections.synchronizedSet(new HashSet<String>());
	//新的Fleet
	private Set<String> newFleet = Collections.synchronizedSet(new HashSet<String>());
	//当前Prefix
	private Map<Thread,String> nowPrefix = Collections.synchronizedMap(new HashMap<Thread,String>());
	private PrefixDao prefixDao;
	private FleetDao fleetDao;
	private ProxyTask proxyTask;
	
	/**
	 * 获取新的prefix集合
	 */
	private void addPrefixList() {
		prefixlist.addAll(prefixDao.getPrefixList(id, number));
	}
	/**
	 * 从缓存集合获取prefix，如果没有了，就调用addPrefixList获取新的，如果数据库里没有，则返回null
	 * @return 返回获取的prefix，若没有则返回null
	 */
	private synchronized String getPrefix(){
		String result;
		if (prefixlist.size()<=0) {
			addPrefixList();
			if (prefixlist.size()<number) {
				if (id == 1&&prefixlist.size() == 0) {
					return null;
				}else if (id == 1&&prefixlist.size() > 0) {
				}else if (id >= 1&&prefixlist.size()== 0) {
					id = 1;
					addPrefixList();
					id+=number;
				}else {
					id = 1;
				}
			}else {
				id+=number;				
			}
		}
		result = prefixlist.get(0);
		prefixlist.remove(0);
		return result;
	}

	/**
	 * 线程执行任务
	 */
	@Override
	public void doWork() {
		String prefix;
		Thread thisThread = Thread.currentThread();
		if (nowPrefix.containsKey(thisThread)) {
			prefix = nowPrefix.get(thisThread);
			System.out.println("断线重启后继续fleetCollect： " + prefix);
		}else {
			prefix = getPrefix();
			if (prefix == null) {
				return;
			}			
		}
		collectNext("https://zh.flightaware.com/live/fleet/"+prefix, prefix);
		if (!getIs() && newFleet.size() > 0) {
			synchronized (newFleet) {
				if (newFleet.size() <= 0) {
					 return;
				}
				try {
					fleetDao.saveFleet(newFleet, existedFleet);
					newFleet.clear();
				} catch (Exception e) {}
			}
		}
	}
	/**
	 * 递归爬取每一页
	 * @param url 传入爬取的网页url
	 * @param prefix 传入正在爬的航空公司3位ICAO码
	 * @return 。
	 */
	private Void collectNext(String url,String prefix) {
		ProxyBean proxy = null;
		while (getIs()){
			try {
				proxy = proxyTask.getProxy();
				if (proxy == null) {
					continue;
				}
				Thread thisThread = Thread.currentThread();
				nowPrefix.put(thisThread,prefix);
				//解析指定网页，
				Document document = Jsoup.connect(url)
						.proxy(proxy.getIp(), proxy.getPort())
						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
						.timeout(15000)
						.get();
				nowPrefix.remove(thisThread);
				//选择符合条件的dom
				Elements fleets = document.select("a:matchesOwn("+prefix+"[A-Z0-9]+)");
				for (Element fleet : fleets) {
					//遍历结果并存入

					if (!contains(fleet.text())) {
						save(fleet.text());
					}
				}
				//查找下一页入口，如果有就递归，
				Element next = document.selectFirst("a:containsOwn(后40条)");
				if (next != null) {
					return collectNext(next.attr("href"),prefix);
				}
				return null;
			} catch (IOException e) {}
			catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 调用dao保存到数据库
	 * @param fleet 传入航班号
	 */
	private synchronized void save(String fleet) {
		if (contains(fleet)) {
			return;
		}
		addNewFleet(fleet);
		if (newFleet.size() >= 100) {
			final Set<String> set = new HashSet<>(newFleet);
			newFleet.clear();
			existedFleet.addAll(newFleet);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						fleetDao.saveFleet(set, existedFleet);
					} catch (Exception e) {
						for (String prefixStr: set) {
							try {
								fleetDao.saveFleet(prefixStr);
							} catch (Exception e2) {
								existedFleet.remove(prefixStr);								
							}
						}
					}
				}
			}).start();
		}
	}

	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


	public Set<String> getExistedFleet() {
		return existedFleet;
	}

	public void addExistedFleet(Collection<? extends String> existedFleet) {
		this.existedFleet.addAll(existedFleet);
	}

	public boolean contains(String info) {
		return this.existedFleet.contains(info);
	}

	public void addNewFleet(String fleet) {
		this.newFleet.add(fleet);
	}
	public PrefixDao getPrefixDao() {
		return prefixDao;
	}
	@Autowired
	public void setPrefixDao(PrefixDao prefixDao) {
		this.prefixDao = prefixDao;
	}
	public FleetDao getFleetDao() {
		return fleetDao;
	}
	@Autowired
	public void setFleetDao(FleetDao fleetDao) {
		this.fleetDao = fleetDao;
	}
	public ProxyTask getProxyTask() {
		return proxyTask;
	}
	@Autowired
	public void setProxyTask(ProxyTask proxyTask) {
		this.proxyTask = proxyTask;
	}
	public String getNowPrefix(Thread thread) {
		return nowPrefix.get(thread);
	}
	public void setNowPrefix(Thread thread, String prefix) {
		this.nowPrefix.put(thread, prefix);
	}
	public void delNowPrefix(Thread thread) {
		this.nowPrefix.remove(thread);
	}
	
	
}
