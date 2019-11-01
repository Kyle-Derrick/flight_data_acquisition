package com.drsn.flights.bean;

/**
 * 机场信息实体类
 * @author drsnow
 *
 */
public class AirportBean {
	//数据表主键id
	private Integer id;
	//时区
	private String tz;
	//3位iata码
	private String iata;
	//4位icao码
	private String icao;
	//机场名，后期会删除，用name字段
	private String friendlyName;
	//所在地，后期会删除，用country，province，city字段
	private String friendlyLocation;
	//所在地经度
	private Double longitude;
	//所在地纬度
	private Double latitude;
	//机场所在国家，数据来源于百度经纬度查询api
	private String country;
	//机场所在省份，数据来源于百度经纬度查询api
	private String province;
	//机场所在城市，数据来源于百度经纬度查询api
	private String city;
	//机场名，数据来源于安能全球机场代码，http://airport.anseo.cn/，若安能上没有数据则利用flightaware上的数据
	private String name;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTz() {
		return tz;
	}
	public void setTz(String tz) {
		this.tz = tz;
	}
	public String getIata() {
		return iata;
	}
	public void setIata(String iata) {
		this.iata = iata;
	}
	public String getIcao() {
		return icao;
	}
	public void setIcao(String icao) {
		this.icao = icao;
	}
	public String getFriendlyName() {
		return friendlyName;
	}
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	public String getFriendlyLocation() {
		return friendlyLocation;
	}
	public void setFriendlyLocation(String friendlyLocation) {
		this.friendlyLocation = friendlyLocation;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
