package com.drsn.flights.task;

/**
 * 该程序线程基础类
 * @author drsnow
 *
 */
public class TaskBasics implements Runnable {
	//线程结束标识，要结束线程就设为false
	private volatile boolean is = true;
	
	public boolean getIs() {
		return is;
	}

	public void setIs(boolean is) {
		this.is = is;
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+"线程启动");
		while (getIs()){
			doWork();
		}
		System.out.println(Thread.currentThread().getName()+"线程关闭");
	}
	
	/**
	 * 线程执行任务，由子类重写
	 */
	public void doWork() {
		
	}

}
