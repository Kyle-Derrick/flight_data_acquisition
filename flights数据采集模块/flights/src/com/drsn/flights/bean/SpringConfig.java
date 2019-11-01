package com.drsn.flights.bean;

import org.springframework.context.ApplicationContext;

import com.drsn.flights.task.TaskManager;

/**
 * 用来存放spring的ApplicationContext的类
 * @author drsnow
 *
 */
public class SpringConfig {
	private static ApplicationContext context;

	/**
	 * 获取spring的ApplicationContext对象
	 * @return 返回spring的ApplicationContext对象
	 */
	public static ApplicationContext getContext() {
		return context;
	}
	/**
	 * 设置spring的ApplicationContext对象
	 * @param context 传入spring的ApplicationContext对象
	 */
	public static void setContext(ApplicationContext context) {
		SpringConfig.context = context;
	}

	/**
	 * 获取本程序初始化设置信息对象
	 * @return 返回初始化设置信息对象
	 */
	public static CfgBean getCfg() {
		return context.getBean(CfgBean.class);
	}

	/**
	 * 获取线程管理器对象
	 * @return 返回线程管理对象
	 */
	public static TaskManager getTaskManager() {
		return context.getBean(TaskManager.class);
	}
}
