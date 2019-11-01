package com.drsn.flights.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.drsn.flights.bean.ProxyBean;
import com.drsn.flights.dao.ProxyDao;

/**
 * 代理ip爬取线程，数据来源于西刺代理
 * @author drsnow
 *
 */
@Service
public class ProxyTask extends TaskBasics {
	private List<ProxyBean> proxyListAll = Collections.synchronizedList(new ArrayList<ProxyBean>());
	private List<ProxyBean> proxyList = Collections.synchronizedList(new ArrayList<ProxyBean>());
	private int index = 0;
	private boolean ifContinue = false;
	private ValidateProxy validateProxy = new ValidateProxy();
	private ProxyDao proxyDao;
	
	/**
	 * 线程周期性循环爬取代理IP
	 */
	@Override
	public void doWork() {
		int page = 1;
		long startTime = new Date().getTime();
		long oTime = startTime;
		while (getIs()) {
			System.out.println("当前代理ip拥有个数：" + proxyList.size());
			long time = new Date().getTime();
			//每10分钟检测一遍所有代理ip，无法使用的就删除
			if ((time - oTime) >= 600000) {
				oTime = time;

				for (int i = 0; i < 50; i++) {
					new Thread(new ValidateProxyEx()).start();
				}
			}
			if ((time - startTime) >= 60000) {
				startTime = time;
				page = 1;
			}
			if (proxyList.size() <= 2000) {
				collectProxy(page);
				for (int i = 0; i < 50; i++) {
					new Thread(validateProxy).start();
				}
				while (ifContinue) {}
				if (proxyList.size() > 0) {
					proxyDao.saveAll(new ArrayList<ProxyBean>(proxyList));
				}
				
				int size = proxyList.size();
				if (size < 10) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {}
					
				}else if (size <= 30){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
					
				}else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
				}
					page++;
			}
		}
	}
	
	/**
	 * 从网页上获取ip的主要方法
	 * @param page 传入爬取的页码
	 * @return 。
	 */
	private boolean collectProxy(int page) {
		try {
			Connection connection = Jsoup.connect("http://www.xicidaili.com/wn/" + page)
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
					.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
					.timeout(30000);
			if (proxyList.size() > 0) {
				ProxyBean proxyEntry = getProxy();
				if (proxyEntry != null) {
					connection.proxy(proxyEntry.getIp(), proxyEntry.getPort());
				}
			}else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {}
			}
			Document document = connection.get();
			Elements elements = document.selectFirst("table").select("tr");
			elements.remove(0);
			for (Element element : elements) {
				final String ip = element.selectFirst("td:matchesOwn(\\d+.\\d+.\\d+.\\d)").text();
				final int port = Integer.valueOf(element.select("td").get(2).text());
				ProxyBean simpleEntry = new ProxyBean(ip, port);
				if (!proxyListAll.contains(simpleEntry)) {
					proxyListAll.add(simpleEntry);
				}
				
			}
		} catch (IOException e) {
			return false;
		}catch (Exception e) {}
		return true;
	}
	
	/**
	 * 从代理IP缓存中获取一个代理ip
	 * @return 返回一个代理IP实体类对象
	 */
	public synchronized ProxyBean getProxy() {
		if (proxyList.size() == 0) {
			return null;
		}
		if (index >= proxyList.size()) {
			index = 0;
		}
		return proxyList.get(index++);
	}
	
	/**
	 * 用于清洗爬到的初始代理ip数据
	 */
	private class ValidateProxy implements Runnable{
		volatile int validateIndex = 0;
		@Override
		public void run() {
			while(true){
				ProxyBean entry = get();
				if (entry == null) {
					return;
				}
				
				try {
					Document document = Jsoup.connect("https://zh.flightaware.com/account/login.rvt")
							.proxy(entry.getIp(), entry.getPort())
							.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
							.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
							.timeout(15000)
							.get();
					if (document != null && (!proxyList.contains(entry))) {
						proxyList.add(entry);
					}
				} catch (IOException e) {}
			}
		}
		
		private synchronized ProxyBean get() {
			if (proxyListAll.size() == 0) {
				return null;
			}
			if (validateIndex >= proxyListAll.size()) {
				proxyListAll.clear();
				validateIndex = 0;
				ifContinue = false;
				return null;
			}
			return proxyListAll.get(validateIndex++);
		}
	}
	

	/**
	 * 用于清洗已储存的代理ip数据
	 */
	private class ValidateProxyEx implements Runnable{
		volatile int validateIndex = 0;
		@Override
		public void run() {
			while(true){
				ProxyBean entry = get();
				if (entry == null) {
					return;
				}
				for (int i = 0; i < 10; i++) {
					try {
						Jsoup.connect("https://zh.flightaware.com/account/login.rvt")
								.proxy(entry.getIp(), entry.getPort())
								.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
								.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
								.timeout(15000)
								.get();
					} catch (IOException e) {
						if (i == 9) {
							deleteProxy(entry);
						}
					}
				}
			}
		}

		
		private synchronized ProxyBean get() {
			if (proxyList.size() == 0) {
				return null;
			}
			if (validateIndex >= proxyList.size()) {
				return null;
			}
			return proxyList.get(validateIndex++);
		}
	}
	/**
	 * 删除代理IP（缓存和数据库）
	 * @param bean 传入要删除的代理IP实体类对象
	 */
	public void deleteProxy(ProxyBean bean){
		proxyList.remove(bean);
		proxyDao.delete(bean);
	}

	/**
	 * 添加一个代理ip
	 * @param ip 代理ip的ip号
	 * @param port 代理ip的端口
	 */
	public void addProxy(String ip, int port){
		proxyList.add(new ProxyBean(ip, port));
	}

	public ProxyDao getProxyDao() {
		return proxyDao;
	}

	@Autowired
	public void setProxyDao(ProxyDao proxyDao) {
		this.proxyDao = proxyDao;
	}
	
	
}
