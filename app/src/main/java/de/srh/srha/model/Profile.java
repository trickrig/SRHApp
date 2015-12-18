package de.srh.srha.model;

public class Profile {

    private String name;
    private String associatedWifi;
    private String preferredDepartureId;
    private String preferredDepartureName;
    private String preferredArrivalId;
    private String preferredArrivalName;

    public SettingsManager settingsManager;

    public String toString(){
        String result = "Name: " + name + " AssociatedWifi " + associatedWifi + " Arravial "
                + preferredArrivalName + " Departure " + preferredDepartureName;
        return result;
    }
    public Profile(String name, String associatedWifi, String preferredDepartureId,
                   String preferredDepartureName, String preferredArrivalId,
                   String preferredArrivalName) {
        this.name = name;
        this.associatedWifi = associatedWifi;
        this.preferredDepartureId = preferredDepartureId;
        this.preferredDepartureName = preferredDepartureName;
        this.preferredArrivalId = preferredArrivalId;
        this.preferredArrivalName = preferredArrivalName;
    }

    public void setProfileName(String name) {
        if (name != null && !name.isEmpty())
            this.name = name;
    }

    public String getProfileName() {
        return this.name;
    }

    public void addWifi(String ssid) {
        if (ssid != null && !ssid.isEmpty()) {
            //this.associatedWifis.add(ssid);
            this.associatedWifi = ssid;
        }
    }

    public Boolean isWifiInProfile(String ssid) {
        //return this.associatedWifis.contains(ssid);
        return this.associatedWifi.equals(ssid);
    }

    public String getAssociatedWifi() {
        return this.associatedWifi;
    }

    public void setPreferredDeparture(String preferredDepartureId, String preferredDepartureName) {
        this.preferredDepartureId = preferredDepartureId;
        this.preferredDepartureName = preferredDepartureName;
    }

    public String getPreferredDepartureName() { return this.preferredDepartureName; }

    public String getPreferredDepartureId() { return this.preferredDepartureId; }

    public void setPreferredArrival(String preferredArrivalId, String preferredArrivalName ) {
        this.preferredArrivalId = preferredArrivalId;
        this.preferredArrivalName = preferredArrivalName;
    }

    public String getPreferredArrivalName() { return this.preferredArrivalName; }

    public String getPreferredArrivalId() { return this.preferredArrivalId; }

    public void setProfile() {
        this.settingsManager.applySettings();
    }

    public boolean equals(Profile other) {
        return this.name.equals(other.getProfileName()) || this.associatedWifi.equals(other.getAssociatedWifi());
    }

}

