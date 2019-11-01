package com.test;

public class TianqiBean {
	private Integer id;
	private String city;
	private Integer date;
	private String tianqiDay;
	private String tianqiNight;
	private String wenduDay;
	private String wenduNight;
	private String fengxiangDay;
	private String fengxiangNight;
	
	public TianqiBean() {}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getDate() {
		return date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	public String getTianqiDay() {
		return tianqiDay;
	}
	public void setTianqiDay(String tianqiDay) {
		this.tianqiDay = tianqiDay;
	}
	public String getTianqiNight() {
		return tianqiNight;
	}
	public void setTianqiNight(String tianqiNight) {
		this.tianqiNight = tianqiNight;
	}
	public String getWenduDay() {
		return wenduDay;
	}

	public void setWenduDay(String wenduDay) {
		this.wenduDay = wenduDay;
	}

	public String getWenduNight() {
		return wenduNight;
	}

	public void setWenduNight(String wenduNight) {
		this.wenduNight = wenduNight;
	}

	public String getFengxiangDay() {
		return fengxiangDay;
	}
	public void setFengxiangDay(String fengxiangDay) {
		this.fengxiangDay = fengxiangDay;
	}
	public String getFengxiangNight() {
		return fengxiangNight;
	}
	public void setFengxiangNight(String fengxiangNight) {
		this.fengxiangNight = fengxiangNight;
	}

	@Override
	public String toString() {
		return "TianqiBean [id=" + id + ", city=" + city + ", date=" + date
				+ ", tianqiDay=" + tianqiDay + ", tianqiNight=" + tianqiNight
				+ ", wenduDay=" + wenduDay + ", wenduNight=" + wenduNight
				+ ", fengxiangDay=" + fengxiangDay + ", fengxiangNight="
				+ fengxiangNight + "]";
	}
	
	
}
