package com.drsn.flights.dao;

import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.drsn.flights.bean.PrefixBean;


/**
 * （prefix）航空公司3位ICAO码存储DAO类
 * @author drsnow
 */
@Repository
public class PrefixDao extends Daobasics{
	
	/**
	 * 向数据库存入prefix
	 * @param prefix 传入航空公司3位ICAO码
	 */
	@Transactional
	public void savePrefix(String prefix) {
			getSession().save(new PrefixBean(prefix));
			System.out.println("新获取prefix："+prefix);
	}

	/**
	 * 向数据库存入prefix批量
	 * @param prefixSet 传入航空公司3位ICAO码集合
	 */
	@Transactional
	public void savePrefix(Set<String> prefixSet) {
		Session session = getSession();
		int i = 0;
		for (String prefix : prefixSet) {
			i++;
			session.save(new PrefixBean(prefix));
			if (i % 50 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
		System.out.println("新获取prefix："+prefixSet);
	}

	/**
	 * 获取从数据库中id等于传入id值开始的number个prefix数据，并返回
	 * @param id 传入起始id
	 * @param number 传入获取个数
	 * @return 返回获取结果list
	 */
	@Transactional
	public List<String> getPrefixList(Integer id, Integer number) {
		return (List<String>)getSession().createQuery("SELECT p.prefix FROM PrefixBean p order by p.id").setFirstResult(id-1).setMaxResults(number).list();
	}
	/**
	 * 获取数据库中全部航空公司icao码
	 * @return 返回 航空公司3位ICAO码集合
	 */
	@Transactional
	public List<String> getPrefixAll() {
		return (List<String>)getSession().createQuery("SELECT p.prefix FROM PrefixBean p").list();
	}
}
