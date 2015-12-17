package de.srh.srha.model;
import android.util.Log;

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

	public int getStationCount(){
		return Route.size();
	}

	public RouteConnection[] getConnections(){
		Log.i("Route", this.toString());
		RouteConnection[] res = new RouteConnection[Route.size()-1];
		for(int i=1; i<Route.size(); ++i){
			String StartHalteStelle = Route.get(i-1).getNameStation().replace("Dresden", "");
			String ZielHalteStelle = Route.get(i).getNameStation().replace("Dresden", "");
			String StartZeit = Route.get(i-1).getGoOnTime();
			String getLinie = Route.get(i-1).getGoOnTram();
			RouteConnection buffer = new RouteConnection(StartHalteStelle, ZielHalteStelle, StartZeit, getLinie);
			res[i-1] = buffer;
		}
		return res;
	}

	public String toString(){
		String result = new String();
		for(int i=0; i < Route.size(); ++i){
			result+= Route.get(i) + "\n";
		}
		return result;
	}
	
}
