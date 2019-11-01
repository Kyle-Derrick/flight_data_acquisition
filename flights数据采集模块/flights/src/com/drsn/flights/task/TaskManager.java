package com.drsn.flights.task;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.drsn.flights.bean.CfgBean;
import com.drsn.flights.bean.SpringConfig;
import com.drsn.flights.dao.AirlineDao;
import com.drsn.flights.dao.AirportDao;
import com.drsn.flights.dao.FleetDao;
import com.drsn.flights.dao.PrefixDao;
import com.drsn.flights.dao.ProxyDao;

/**
 * 该程序的线程管理器
 * @author drsnow
 *
 */
@Service
public class TaskManager {
	//PrefixCollectTask线程类，由spring自动装配
	private PrefixCollectTask prefixCollectTask;
	//fleetCollectTask线程类，由spring自动装配
	private FleetCollectTask fleetCollectTask;
	//flightCollectTask线程类，由spring自动装配
	private FlightsCollectTask flightsCollectTask;
	//ProxyTask线程类，由spring自动装配
	private ProxyTask proxyTask;
	//ProxyFromSqlTask线程类，由spring自动装配
	private ProxyFromSqlTask proxyFromSqlTask;
	//prefixCollectTask线程开启条数，默认为0
	private int prefixTaskNum = 0;
	//fleetCollectTask线程开启条数，默认为0
	private int fleetTaskNum = 0;
	//flightCollectTask线程开启条数，默认为0
	private int flightTaskNum = 0;
	//开启的线程对象集合
	private Thread proxyTaskThread = null;
	private Thread proxyFromSqlTaskThread = null;
	private Thread prefixTask = null;
	private List<Thread> fleetTaskList = new ArrayList<Thread>();
	private List<Thread> flightTaskList = new ArrayList<Thread>();
	//爬虫线程维护类的对象及其线程
	private MaintainTask maintain;
	private Thread maintainTask;
	//Dao类，由spring自动装配
	private PrefixDao prefixDao;
	private FleetDao fleetDao;
	private ProxyDao proxyDao;
	private AirlineDao airlineDao;
	private AirportDao airportDao;
	
	
	/**
	 * 开启爬虫及维护线程
	 */
	public void startTask(){
		//获取并配置开启线程条数，prefix，fleet缓存条数
		CfgBean cfg = SpringConfig.getCfg();
		prefixTaskNum = cfg.getPrefixTasknum();
		fleetCollectTask.setNumber(cfg.getPrefixListNum());
		fleetTaskNum = cfg.getFleetCollectTaskNum();
		flightsCollectTask.setNumber(cfg.getFleetListNum());
		flightTaskNum = cfg.getFlightsCollectTaskNum();
		
		init();

		System.out.println("开启线程中。。。");
		//开启各线程
		if(prefixTaskNum > 0){
			proxyTaskThread = new Thread(proxyTask);
			proxyTaskThread.start();
			prefixTask = new Thread(prefixCollectTask);
			prefixTask.start();
		}
		for (int i = 0; i < fleetTaskNum; i++) {
			Thread fleetThread = new Thread(fleetCollectTask);
			fleetTaskList.add(fleetThread);
			fleetThread.start();
		}
		if (flightTaskNum > 0) {
			proxyFromSqlTaskThread = new Thread(proxyFromSqlTask);
			proxyFromSqlTaskThread.start();
		}
		for (int i = 0; i < flightTaskNum; i++) {
			Thread flightThread = new Thread(flightsCollectTask);
			flightTaskList.add(flightThread);
			flightThread.start();
		}
		maintain = new MaintainTask();
		maintainTask = new Thread(maintain);
		maintainTask.start();
		System.out.println("线程已开启！");
	}
	
	/**
	 * 初始化相关
	 */
	public void init(){
		if (prefixTaskNum > 0) {
			proxyDao.clear();
			prefixCollectTask.addExistedPrefix(prefixDao.getPrefixAll());
//			proxyTask.addProxy("81.89.71.166", 51890);
		}
		if (fleetTaskNum > 0) {
			fleetCollectTask.addExistedFleet(fleetDao.getFleetListAll());
		}
		if (flightTaskNum > 0) {
			flightsCollectTask.addExistedAirlineAll(airlineDao.getFleetListAll());
			flightsCollectTask.addExistedAirportAll(airportDao.getFleetListAll());
		}
		
	}
	
	/**
	 * 关闭爬虫及维护线程
	 */
	public void stopTask(){
		maintain.setIs(false);
		while(maintainTask.isAlive()){}
		proxyTask.setIs(false);
		proxyFromSqlTask.setIs(false);
		prefixCollectTask.setIs(false);
		fleetCollectTask.setIs(false);
		flightsCollectTask.setIs(false);
		boolean is = true;
		//等待线程全部关闭
		while(is){
			is = false;
			if (prefixTask != null) {
				if (prefixTask.isAlive()) {
					is = true;
				}
			}
			if (proxyTaskThread != null) {
				if (proxyTaskThread.isAlive()) {
					is = true;
				}
			}
			if (proxyFromSqlTaskThread != null) {
				if (proxyFromSqlTaskThread.isAlive()) {
					is = true;
				}
			}
			for (Thread thread : fleetTaskList) {
				if (thread.isAlive()) {
					is = true;
				}
			}
			for (Thread thread : flightTaskList) {
				if (thread.isAlive()) {
					is = true;
				}
			}
		}
		
	}
	
	/**
	 * 维护爬虫线程的类
	 * 当线程意外终止时重启线程
	 */
	private class MaintainTask extends TaskBasics{
		@Override
		public void doWork() {
			try {
				int i = 0;
				int l = 0;
				if (prefixTask != null) {
					l += 2;
					if (!prefixTask.isAlive()) {
						System.out.println(prefixTask.getName()+"线程已停止活动");
						prefixTask = new Thread(prefixCollectTask);
						prefixTask.start();
						System.out.println(prefixTask.getName()+"线程重启动");
						i++;
					}

					if (!proxyTaskThread.isAlive()) {
						System.out.println(proxyTaskThread.getName()+"线程已停止活动");
						proxyTaskThread = new Thread(proxyTask);
						proxyTaskThread.start();
						System.out.println(proxyTaskThread.getName()+"线程重启动");
						i++;
					}
				}
				for (int j = 0; (j < fleetTaskList.size()) && getIs(); j++) {
					l++;
					Thread thread = fleetTaskList.get(j);
					if (!thread.isAlive()) {
						System.out.println(thread.getName()+"线程已停止活动");
						Thread newThread = new Thread(fleetCollectTask);
						newThread.start();
						fleetCollectTask.setNowPrefix(newThread, fleetCollectTask.getNowPrefix(thread));
						fleetCollectTask.delNowPrefix(thread);
						fleetTaskList.remove(j);
						fleetTaskList.add(newThread);
						System.out.print(thread.getName()+"线程重启动"+"\t" + newThread.getName());
						i++;
					}
				}
				for (int j = 0; (j < flightTaskList.size()) && getIs(); j++) {
					l++;
					Thread thread = flightTaskList.get(j);
					if (!thread.isAlive()) {
						System.out.println(thread.getName()+"线程已停止活动");

						Thread newThread = new Thread(flightsCollectTask);
						newThread.start();
						flightsCollectTask.setNowFleet(newThread, flightsCollectTask.getNowFleet(thread));
						flightsCollectTask.setNowtime(newThread, flightsCollectTask.getNowtime(thread));
						flightsCollectTask.delNowFleet(thread);
						flightsCollectTask.delNowtime(thread);
							flightTaskList.remove(j);
							flightTaskList.add(newThread);

						System.out.println(thread.getName()+"线程重启动" + newThread.getName());
						i++;
					}
				}
				if (proxyFromSqlTaskThread != null) {
					if (!proxyFromSqlTaskThread.isAlive()) {
						System.out.println(proxyFromSqlTaskThread.getName()+"线程已停止活动");
						proxyFromSqlTaskThread = new Thread(proxyFromSqlTask);
						proxyFromSqlTaskThread.start();
						System.out.println(proxyFromSqlTaskThread.getName()+"线程重启动");
						i++;
					}					
				}
				if (i == 0) {
					System.out.println("线程正常！"+l);
				}else {
					System.out.println("线程出错！"+i);
				}
			}catch (Exception e) {}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}
	}
	
	public PrefixCollectTask getPrefixCollectTask() {
		return prefixCollectTask;
	}
	
	@Autowired
	public void setPrefixCollectTask(PrefixCollectTask prefixCollectTask) {
		this.prefixCollectTask = prefixCollectTask;
	}
	public FleetCollectTask getFleetCollectTask() {
		return fleetCollectTask;
	}
	
	@Autowired
	public void setFleetCollectTask(FleetCollectTask fleetCollectTask) {
		this.fleetCollectTask = fleetCollectTask;
	}
	public FlightsCollectTask getFlightsCollectTask() {
		return flightsCollectTask;
	}
	@Autowired
	public void setFlightsCollectTask(FlightsCollectTask flightsCollectTask) {
		this.flightsCollectTask = flightsCollectTask;
	}

	public ProxyTask getProxyTask() {
		return proxyTask;
	}

	@Autowired
	public void setProxyTask(ProxyTask proxyTask) {
		this.proxyTask = proxyTask;
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

	public ProxyDao getProxyDao() {
		return proxyDao;
	}

	@Autowired
	public void setProxyDao(ProxyDao proxyDao) {
		this.proxyDao = proxyDao;
	}


	public AirlineDao getAirlineDao() {
		return airlineDao;
	}

	@Autowired
	public void setAirlineDao(AirlineDao airlineDao) {
		this.airlineDao = airlineDao;
	}

	public AirportDao getAirportDao() {
		return airportDao;
	}

	@Autowired
	public void setAirportDao(AirportDao airportDao) {
		this.airportDao = airportDao;
	}

	public ProxyFromSqlTask getProxyFromSqlTask() {
		return proxyFromSqlTask;
	}

	@Autowired
	public void setProxyFromSqlTask(ProxyFromSqlTask proxyFromSqlTask) {
		this.proxyFromSqlTask = proxyFromSqlTask;
	}
	
}
