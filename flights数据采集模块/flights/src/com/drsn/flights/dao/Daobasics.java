package com.drsn.flights.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 该程序的所有DAO的基础类，该程序DAO类都继承这个类
 * @author drsnow
 *
 */
public class Daobasics {
	//hibernate的sessionFactory，由spring自动装配
	private SessionFactory sessionFactory = null;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	/**
	 * 获取当前线程的Session
	 * @return 返回session
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
}
