package com.drsn.flights.bean;

/**
 * 航班数据实体类
 * @author drsnow
 *
 */
public class FlightBean {
	//数据库中的主键id
	private Long id;
	//此次航班id
	private String flightId;
	//iata航班号
	private String iataIdent;
	//航班号
	private String ident;
	//航班号全称
	private String friendlyIdent;
	//航空公司icao码
	private String airlineIcao;
	//飞机型号
	private String flighType;
	//飞机型号全名
	private String flighTypeFriendlyType;
	//起飞地点3位IATA码
	private String origin;
	//降落地点3位IATA码
	private String destination;
	//实际起飞时间
	private Integer takeoffTimesActual;
	//实际降落时间
	private Integer landingTimesActual;
	//计划起飞时间
	private Integer takeoffTimesScheduled;
	//计划降落时间
	private Integer landingTimesScheduled;
	//实际离开停机位时间
	private Integer gateDepartureTimesActual;
	//实际到达停机位时间
	private Integer gateArrivalTimesActual;
	//计划离开停机位时间
	private Integer gateDepartureTimesScheduled;
	//计划到达停机位时间
	private Integer gateArrivalTimesScheduled;
	//起飞平均延误时间
	private Integer averageDelaysDeparture;
	//到达平均延误时间
	private Integer averageDelaysArrival;
	//距离（实际）（单位：海里）
	private Integer distanceActual;
	//距离（直线）（单位：海里）
	private Integer directDistance;
	//速度（单位：节）
	private Integer speed;
	//海拔（单位：英尺）注：末尾自补一个0
	private Integer altitude;
	//耗油量（单位：加仑）
	private Integer fuelBurnGallons;
	//耗油金额（单位：英镑）
	private Integer fuelBurnPounds;
	//航班是否取消
	private Boolean cancelled = false;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFlightId() {
		return flightId;
	}
	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}
	public String getIataIdent() {
		return iataIdent;
	}
	public void setIataIdent(String iataIdent) {
		this.iataIdent = iataIdent;
	}
	public String getIdent() {
		return ident;
	}
	public void setIdent(String ident) {
		this.ident = ident;
	}
	public String getFriendlyIdent() {
		return friendlyIdent;
	}
	public void setFriendlyIdent(String friendlyIdent) {
		this.friendlyIdent = friendlyIdent;
	}
	public String getAirlineIcao() {
		return airlineIcao;
	}
	public void setAirlineIcao(String airlineIcao) {
		this.airlineIcao = airlineIcao;
	}
	public String getFlighType() {
		return flighType;
	}
	public void setFlighType(String flighType) {
		this.flighType = flighType;
	}
	public String getFlighTypeFriendlyType() {
		return flighTypeFriendlyType;
	}
	public void setFlighTypeFriendlyType(String flighTypeFriendlyType) {
		this.flighTypeFriendlyType = flighTypeFriendlyType;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Integer getTakeoffTimesActual() {
		return takeoffTimesActual;
	}
	public void setTakeoffTimesActual(Integer takeoffTimesActual) {
		this.takeoffTimesActual = takeoffTimesActual;
	}
	public Integer getLandingTimesActual() {
		return landingTimesActual;
	}
	public void setLandingTimesActual(Integer landingTimesActual) {
		this.landingTimesActual = landingTimesActual;
	}
	public Integer getTakeoffTimesScheduled() {
		return takeoffTimesScheduled;
	}
	public void setTakeoffTimesScheduled(Integer takeoffTimesScheduled) {
		this.takeoffTimesScheduled = takeoffTimesScheduled;
	}
	public Integer getLandingTimesScheduled() {
		return landingTimesScheduled;
	}
	public void setLandingTimesScheduled(Integer landingTimesScheduled) {
		this.landingTimesScheduled = landingTimesScheduled;
	}
	public Integer getGateDepartureTimesActual() {
		return gateDepartureTimesActual;
	}
	public void setGateDepartureTimesActual(Integer gateDepartureTimesActual) {
		this.gateDepartureTimesActual = gateDepartureTimesActual;
	}
	public Integer getGateArrivalTimesActual() {
		return gateArrivalTimesActual;
	}
	public void setGateArrivalTimesActual(Integer gateArrivalTimesActual) {
		this.gateArrivalTimesActual = gateArrivalTimesActual;
	}
	public Integer getGateDepartureTimesScheduled() {
		return gateDepartureTimesScheduled;
	}
	public void setGateDepartureTimesScheduled(Integer gateDepartureTimesScheduled) {
		this.gateDepartureTimesScheduled = gateDepartureTimesScheduled;
	}
	public Integer getGateArrivalTimesScheduled() {
		return gateArrivalTimesScheduled;
	}
	public void setGateArrivalTimesScheduled(Integer gateArrivalTimesScheduled) {
		this.gateArrivalTimesScheduled = gateArrivalTimesScheduled;
	}
	public Integer getAverageDelaysDeparture() {
		return averageDelaysDeparture;
	}
	public void setAverageDelaysDeparture(Integer averageDelaysDeparture) {
		this.averageDelaysDeparture = averageDelaysDeparture;
	}
	public Integer getAverageDelaysArrival() {
		return averageDelaysArrival;
	}
	public void setAverageDelaysArrival(Integer averageDelaysArrival) {
		this.averageDelaysArrival = averageDelaysArrival;
	}
	public Integer getDistanceActual() {
		return distanceActual;
	}
	public void setDistanceActual(Integer distanceActual) {
		this.distanceActual = distanceActual;
	}
	public Integer getDirectDistance() {
		return directDistance;
	}
	public void setDirectDistance(Integer directDistance) {
		this.directDistance = directDistance;
	}
	public Integer getSpeed() {
		return speed;
	}
	public void setSpeed(Integer speed) {
		this.speed = speed;
	}
	public Integer getAltitude() {
		return altitude;
	}
	public void setAltitude(Integer altitude) {
		this.altitude = altitude;
	}
	public Integer getFuelBurnGallons() {
		return fuelBurnGallons;
	}
	public void setFuelBurnGallons(Integer fuelBurnGallons) {
		this.fuelBurnGallons = fuelBurnGallons;
	}
	public Integer getFuelBurnPounds() {
		return fuelBurnPounds;
	}
	public void setFuelBurnPounds(Integer fuelBurnPounds) {
		this.fuelBurnPounds = fuelBurnPounds;
	}
	public Boolean getCancelled() {
		return cancelled;
	}
	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
