package de.srh.srha.model;

public class RouteStation {
// Vars
	protected String nameStation;
	protected String arrivalTime, goOnTime;
	protected String arrivalTram, goOnTram;
//constructor
	RouteStation(String nameStation, String arrivalTime, String goOnTime,
				 String arrivalTram, String goOnTram){
		this.arrivalTime = arrivalTime.trim();
		this.nameStation = nameStation.trim();
		this.goOnTime = goOnTime.trim();
		this.arrivalTram = arrivalTram.trim();
		this.goOnTram = goOnTram.trim();
	}
	/**
	 * @return the nameStation
	 */
	public String getNameStation() {
		return nameStation;
	}
	/**
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}
	/**
	 * @return the goOnTime
	 */
	public String getGoOnTime() {
		return goOnTime;
	}
	/**
	 * @return the arrivalTram
	 */
	public String getArrivalTram() {
		return arrivalTram;
	}
	/**
	 * @return the goOnTram
	 */
	public String getGoOnTram() {
		return goOnTram;
	}
	
	public String toString(){
		String result;
		result = arrivalTime + ", Tram " + arrivalTram + " -> " + nameStation + " -> Tram " + goOnTram + ", " + goOnTime;
		return result;
	}
	
}
