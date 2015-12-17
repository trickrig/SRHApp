package de.srh.srha.model;

/**
 * Created by fettpet on 17.12.15.
 */
public class RouteConnection {
    private String StartStation, ZielStation, Zeit, Linie;

    public String getZeit() {
        return Zeit;
    }

    public String getStartStation() {
        return StartStation;
    }

    public String getZielStation() {
        return ZielStation;
    }

    public String getLinie() {
        return Linie;
    }

    public RouteConnection(String StartStation, String ZielStation, String Zeit, String Linie){
        this.StartStation = StartStation;
        this.ZielStation = ZielStation;
        this.Zeit = Zeit;
        this.Linie = Linie;
    }

}
