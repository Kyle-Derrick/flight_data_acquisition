package com.drsn.flights.task;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.drsn.flights.bean.ProxyBean;
import com.drsn.flights.dao.PrefixDao;

/**
 * prefix（航空公司3位ICAO码）爬取线程类
 * @author drsnow
 *
 */
@Service
public class PrefixCollectTask extends TaskBasics {
	//已存在的prefix
	private Set<String> existedPrefix = new HashSet<>();
	//新的prefix
	private Set<String> newPrefix = new HashSet<>();

	//Dao类，由spring自动装配
	private PrefixDao prefixDao;
	private ProxyTask proxyTask;
	
	/**
	 * 线程执行爬取航空公司ICAO码任务，周期为10秒
	 */
	@Override
	public void doWork() {
		ProxyBean proxy = proxyTask.getProxy();
		if (proxy == null) {
			return;
		}
		try {
			//解析指定网页，
			Document document = Jsoup.connect("https://zh.flightaware.com/live/fleet/")
					.proxy(proxy.getIp(), proxy.getPort())
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
					.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
					.timeout(15000)
					.get();
			//选择符合条件的dom
			Elements prefixs = document.select("tr[class^=smallrow2] td:matchesOwn([A-Z]{3})");
			for (Element prefix : prefixs) {
				//遍历结果并存入
				if (!contains(prefix.text())) {
					addNewPrefix(prefix.text());
				}
			}
			if (newPrefix.size() > 0) {
				try {
					prefixDao.savePrefix(newPrefix);
					existedPrefix.addAll(newPrefix);
				} catch (Exception e) {}
				newPrefix.clear();
			}
			//休眠5秒
			Thread.sleep(1000);
		} catch (IOException e) {} catch (InterruptedException e) {}
	}

	public Set<String> getExistedPrefix() {
		return existedPrefix;
	}

	public void addExistedPrefix(Collection<? extends String> existedPrefix) {
		this.existedPrefix.addAll(existedPrefix);
	}

	public boolean contains(String info) {
		return this.existedPrefix.contains(info);
	}

	public Set<String> getNewPrefix() {
		return newPrefix;
	}

	public void addNewPrefix(String prefix) {
		this.newPrefix.add(prefix);
	}

	public PrefixDao getPrefixDao() {
		return prefixDao;
	}

	@Autowired
	public void setPrefixDao(PrefixDao prefixDao) {
		this.prefixDao = prefixDao;
	}

	public ProxyTask getProxyTask() {
		return proxyTask;
	}

	@Autowired
	public void setProxyTask(ProxyTask proxyTask) {
		this.proxyTask = proxyTask;
	}
	

}
