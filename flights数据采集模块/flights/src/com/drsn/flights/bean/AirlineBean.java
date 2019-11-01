package com.drsn.flights.bean;

/**
 * 航空公司信息实体类
 * @author drsnow
 *
 */
public class AirlineBean {
	//数据库主键id
	private Integer id;
	//航空公司全称
	private String fullName;
	//简称
	private String shortName;
	//3位icao码
	private String icao;
	//2位iata码
	private String iata;
	//称呼
	private String callsign;
	//航空公司网站url
	private String url;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getIcao() {
		return icao;
	}
	public void setIcao(String icao) {
		this.icao = icao;
	}
	public String getIata() {
		return iata;
	}
	public void setIata(String iata) {
		this.iata = iata;
	}
	public String getCallsign() {
		return callsign;
	}
	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
