package com.drsn.flights.dao;

import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.drsn.flights.bean.FleetBean;

/**
 * （fleet）航班号DAO类
 * @author drsnow
 *
 */
@Repository
public class FleetDao extends Daobasics {
	
	/**
	 * 向数据库存入fleet，如果已存在则忽略
	 * @param fleet 传入fleet
	 */
	@Transactional
	public void saveFleet(String fleet) {
			getSession().save(new FleetBean(fleet));
			System.out.println("新获取fleet："+fleet);
	}

	/**
	 * 向数据库存入fleet批量
	 * @param fleetSet 。
	 * @param existedFleet 。
	 */
	@Transactional
	public void saveFleet(Set<String> fleetSet,Set<String> existedFleet) {
		Session session = getSession();
		int i = 0;
		for (String fleet : fleetSet) {
			i++;
			session.save(new FleetBean(fleet));
			if (i % 50 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
		System.out.println("新获取fleetList："+fleetSet);
	}

	/**
	 * 获取从数据库中id等于传入id值开始的number个fleet数据，并返回
	 * @param id 传入起始id
	 * @param number 传入获取个数
	 * @return 返回获取结果list
	 */
	@Transactional
	public List<String> getFleetList(Integer id, Integer number) {
		return (List<String>)getSession().createQuery("SELECT f.fleet FROM FleetBean f where f.isAcquired=false order by f.id").setFirstResult(id-1).setMaxResults(number).list();
	}
	/**
	 * 标记航班号为已爬取
	 * @param fleet 传入航班号
	 */
	@Transactional
	public void setExisted(String fleet) {
//		Session session = getSession();
//		FleetBean bean = (FleetBean) session.createQuery("FROM FleetBean f where f.fleet=:fleet").setString("fleet", fleet).uniqueResult();
//		bean.setIs(true);
//		session.update(bean);
		getSession().createQuery("update FleetBean f set f.isAcquired=:isAcquired where f.fleet=:fleet").setBoolean("isAcquired", true).setString("fleet", fleet).executeUpdate();
	}
	/**
	 * 获取数据库中全部航班号
	 * @return 返回航班号集合
	 */
	@Transactional
	public List<String> getFleetListAll() {
		return (List<String>)getSession().createQuery("SELECT f.fleet FROM FleetBean f").list();
	}
}
