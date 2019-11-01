package com.drsn.flights.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.drsn.flights.bean.ProxyBean;
import com.drsn.flights.dao.ProxyDao;

/**
 * 代理ip获取线程，数据来源于数据库
 * @author drsnow
 *
 */
@Service
public class ProxyFromSqlTask extends TaskBasics {
	@Autowired
	private ProxyDao proxyDao;
	
	private int index = 0;
	private List<ProxyBean> proxyList = Collections.synchronizedList(new ArrayList<ProxyBean>());
	
	/**
	 * 周期性循环从数据库获取代理IP
	 */
	@Override
	public void doWork() {
		System.out.println("代理ip个数："+proxyList.size());
		proxyDao.getAll();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}

	/**
	 * 从代理ip缓存中获取一个代理IP
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
	
	public ProxyDao getProxyDao() {
		return proxyDao;
	}

	public void setProxyDao(ProxyDao proxyDao) {
		this.proxyDao = proxyDao;
	}
	
	public List<ProxyBean> getProxyList() {
		return proxyList;
	}
	public void addProxy(ProxyBean bean) {
		this.proxyList.add(bean);
	}
	
}
