package com.test;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class DaoBases {


	protected SessionFactory factory;
	protected Configuration configuration;
	protected ServiceRegistry serviceRegistry;


	protected void init(){
		configuration = new Configuration().configure();
		serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		factory = configuration.buildSessionFactory(serviceRegistry);
		
	}

	protected void destory(){
		factory.close();
	}
}
