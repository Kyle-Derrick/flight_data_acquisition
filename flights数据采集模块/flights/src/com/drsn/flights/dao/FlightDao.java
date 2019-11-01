package com.drsn.flights.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.drsn.flights.bean.FlightBean;

/**
 * 航班具体数据存储DAO类
 * @author drsnow
 *
 */
@Repository
public class FlightDao extends Daobasics {

	/**
	 * 向数据库存入航班具体数据
	 * @param bean 传入航班具体数据实体类
	 */
	@Transactional
	public void saveFlight(FlightBean bean) {
		getSession().save(bean);
		System.out.println("新获取flight："+bean.getFlightId());
	}
}
