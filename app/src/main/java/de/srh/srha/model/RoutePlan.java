package de.srh.srha.model;
import java.util.*;


public class RoutePlan {
// Vars

	protected ArrayList<RouteStation> Route;
// Constructor
	RoutePlan(ArrayList<RouteStation> Route){
		if(Route == null)
			this.Route = new ArrayList<RouteStation>();
		else
			this.Route = Route;
	}

// Add Station
	public void addStation(RouteStation station){
		this.Route.add(station);
	}
// Getter
	public String getStartStation(){
		return this.Route.get(0).getNameStation();
	}
	
	public String getDestinationStation(){
		return this.Route.get(this.Route.size()-1).getNameStation();
	}

	public String getStartTime(){
		return this.Route.get(0).getGoOnTime();
	}
	
	public String getDestinationTime(){
		return this.Route.get(this.Route.size()-1).getArrivalTime();
	}
	
	public RouteStation getStation(int index){
		return this.Route.get(index);
	}
	
	public String toString(){
		String result = new String();
		for(int i=0; i < Route.size(); ++i){
			result+= Route.get(i) + "\n";
		}
		return result;
	}
	
}
