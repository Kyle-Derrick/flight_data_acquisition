package com.drsn.flights.bean;

/**
 * 数据库中prefix表对应的实体类
 * @author drsnow
 *
 */
public class PrefixBean {
	private Integer id;
	private String prefix;
	
	public PrefixBean() {}
	public PrefixBean(String prefix) {
		super();
		this.prefix = prefix;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
}
