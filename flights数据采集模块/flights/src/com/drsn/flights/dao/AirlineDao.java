package com.drsn.flights.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.drsn.flights.bean.AirlineBean;

/**
 * 航空公司信息储存DAO类
 * @author drsnow
 *
 */
@Repository
public class AirlineDao extends Daobasics {
	/**
	 * 向数据库存入airline
	 * @param bean 传入航空公司信息实体类对象
	 */
	@Transactional
	public void saveAirline(AirlineBean bean) {
		getSession().save(bean);
		System.out.println("新获取airline："+bean.getIcao());
	}

	/**
	 * 获取数据库中全部航空公司信息
	 * @return 返回航空公司信息集合
	 */
	@Transactional
	public List<String> getFleetListAll() {
		return (List<String>)getSession().createQuery("SELECT a.icao FROM AirlineBean a").list();
	}
}
