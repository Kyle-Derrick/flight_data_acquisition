package com.drsn.flights.bean;

/**
 * 数据库中fleet表对应的实体类
 * @author drsnow
 *
 */
public class FleetBean {
	private Long id;
	private String fleet;
	private Boolean isAcquired = false;
	
	public FleetBean() {}
	public FleetBean(String fleet) {
		super();
		this.fleet = fleet;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFleet() {
		return fleet;
	}
	public void setFleet(String fleet) {
		this.fleet = fleet;
	}
	public Boolean getIsAcquired() {
		return isAcquired;
	}
	public void setIsAcquired(Boolean isAcquired) {
		this.isAcquired = isAcquired;
	}
	
}
