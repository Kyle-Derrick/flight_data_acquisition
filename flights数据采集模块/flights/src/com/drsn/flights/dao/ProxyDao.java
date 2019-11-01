package com.drsn.flights.dao;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.drsn.flights.bean.ProxyBean;
import com.drsn.flights.task.ProxyFromSqlTask;

/**
 * 代理ip实体类
 * @author drsnow
 *
 */
@Repository
public class ProxyDao extends Daobasics {
	private List<ProxyBean> oldList = new ArrayList<ProxyBean>();
	@Autowired
	private ProxyFromSqlTask proxyFromSqlTask;
	
	/**
	 * 存入代理IP
	 * @param list 传入代理ip集合
	 */
	@Transactional
	public void saveAll(List<ProxyBean> list) {
		Session session = getSession();
		int i = 0;
		for (ProxyBean proxyBean : list) {
			if (!oldList.contains(proxyBean)) {
				session.save(proxyBean);
				oldList.add(proxyBean);
				i++;
				if (i%50 == 0) {
					session.flush();
					session.clear();
				}
			}
		}
		session.flush();
		session.clear();
	}
	
	/**
	 * 获取数据库中全部的代理IP
	 */
	@Transactional
	public void getAll() {
		List<ProxyBean> list = (List<ProxyBean>)getSession().createQuery("FROM ProxyBean").list();
		for (ProxyBean proxyBean : list) {
			if (!proxyFromSqlTask.getProxyList().contains(proxyBean)) {
				proxyFromSqlTask.addProxy(proxyBean);
			}
		}
		for (int i = 0; i < proxyFromSqlTask.getProxyList().size(); i++) {
			if (!list.contains(proxyFromSqlTask.getProxyList().get(i))) {
				proxyFromSqlTask.getProxyList().remove(i);
			}
		}
	}

	/**
	 * 删除代理ip
	 * @param bean 传入代理ip实体类
	 */
	@Transactional
	public void delete(ProxyBean bean) {
		Session session = getSession();
		session.createQuery("DELETE ProxyBean p where p.ip=:ip and p.port=:port").setString("ip", bean.getIp()).setInteger("port", bean.getPort()).executeUpdate();
		oldList.remove(bean);
	}

	/**
	 * 清空代理ip数据库
	 */
	@Transactional
	public void clear() {
		Session session = getSession();
		session.createSQLQuery("truncate table proxy").executeUpdate();
	}


	public ProxyFromSqlTask getProxyFromSqlTask() {
		return proxyFromSqlTask;
	}


	public void setProxyFromSqlTask(ProxyFromSqlTask proxyFromSqlTask) {
		this.proxyFromSqlTask = proxyFromSqlTask;
	}
	
	
}
