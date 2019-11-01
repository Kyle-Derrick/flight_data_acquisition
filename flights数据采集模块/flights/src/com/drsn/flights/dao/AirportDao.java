package com.drsn.flights.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.drsn.flights.bean.AirportBean;

/**
 * 机场信息储存DAO类
 * @author drsnow
 *
 */
@Repository
public class AirportDao extends Daobasics {
	
	/**
	 * 向数据库存入airport
	 * @param bean 传入机场信息实体类对象
	 */
	@Transactional
	public void saveAirport(AirportBean bean) {
		getSession().save(bean);
		System.out.println("新获取airport："+bean.getIata());
	}
	/**
	 * 获取数据库中全部机场信息
	 * @return 返回机场信息集合
	 */
	@Transactional
	public List<String> getFleetListAll() {
		return (List<String>)getSession().createQuery("SELECT a.iata FROM AirportBean a").list();
	}
}
