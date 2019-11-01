package com.drsn.flights.bean;

/**
 * 存放该程序相关配置数据的Bean
 * @author drsnow
 *
 */
public class CfgBean {
	//关闭命令监听的端口
	private Integer acceptPort = 9833;
	//爬取prefix数据的线程数,默认0条
	private Integer prefixTasknum = 0;
	//爬取fleet的线程中用到的prefix缓存大小默认200条
	private Integer prefixListNum = 200;
	//爬取fleet的线程数量，默认0条
	private Integer fleetCollectTaskNum = 0;
	//爬取flight的线程中用到的fleet缓存大小默认200条
	private Integer fleetListNum = 200;
	//爬取flight的线程数量，默认0条
	private Integer flightsCollectTaskNum = 0;
	//爬取从何时开始的数据，格式如：20180123
	private Integer startTime = 20180201;
	//爬取从何时结束的数据，格式如：20180423
	private Integer stopTime = 20180430;
	
	
	public Integer getAcceptPort() {
		return acceptPort;
	}
	public void setAcceptPort(Integer acceptPort) {
		this.acceptPort = acceptPort;
	}
	public Integer getPrefixListNum() {
		return prefixListNum;
	}
	public void setPrefixListNum(Integer prefixListNum) {
		this.prefixListNum = prefixListNum;
	}
	public Integer getFleetCollectTaskNum() {
		return fleetCollectTaskNum;
	}
	public void setFleetCollectTaskNum(Integer fleetCollectTaskNum) {
		this.fleetCollectTaskNum = fleetCollectTaskNum;
	}
	public Integer getPrefixTasknum() {
		return prefixTasknum;
	}
	public void setPrefixTasknum(Integer prefixTasknum) {
		this.prefixTasknum = prefixTasknum;
	}
	public Integer getFleetListNum() {
		return fleetListNum;
	}
	public void setFleetListNum(Integer fleetListNum) {
		this.fleetListNum = fleetListNum;
	}
	public Integer getFlightsCollectTaskNum() {
		return flightsCollectTaskNum;
	}
	public void setFlightsCollectTaskNum(Integer flightsCollectTaskNum) {
		this.flightsCollectTaskNum = flightsCollectTaskNum;
	}
	public Integer getStartTime() {
		return startTime;
	}
	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}
	public Integer getStopTime() {
		return stopTime;
	}
	public void setStopTime(Integer stopTime) {
		this.stopTime = stopTime;
	}
	
}
