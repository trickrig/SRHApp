package de.srh.srha.model;


import android.app.NotificationManager;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.HashSet;
import java.util.LinkedList;

import de.srh.srha.R;
import de.srh.srha.database.ProfileDbHelper;

public class ProfileManager {


    private LinkedList<Profile> profiles;

    private Context activityContext;

    private String prefsFile = "SRHA-prefs";
    private String currProfilePrefsKey = "currentProfile";

    private Settings settings;

    public ProfileManager(Context context) {
        this.activityContext = context;

        this.profiles = loadProfilesFromPersistance();
        // for every profile there MUST be a setting
        for (Profile p: this.profiles) {
            p.settingsManager = new SettingsManager(p, this.activityContext); // loads settings
        }
    }

    public LinkedList<Profile> getProfiles() {
        if (profiles.size() == 0) {
            LinkedList<Profile> profiles = new LinkedList<Profile>();
            profiles.add(getDefaultProfile());
            return profiles;
        }
        return this.profiles;
    }

    public void updateProfile(Profile profile, Settings settings) {
        if (profile.getProfileName().equals("<default>")) {
            return;
        }
        Profile oldProfile = getProfileByName(profile.getProfileName());
        if (oldProfile != null) {
            // we want to replace a profile with a certain ssid
            // but we cannot do remove(profile) because the new one has another reference
            this.profiles.remove(oldProfile);

            long updated = updateProfileInPersistance(profile);
            profile.settingsManager = new SettingsManager(profile, this.activityContext, settings); // stores settings
            this.profiles.add(profile);
            Toast.makeText(this.activityContext, updated + " profiles updated", Toast.LENGTH_SHORT).show();
            // TODO set profile if we are currently connected to the corresponding wifi
        }
        else {
            storeProfileInPersistance(profile);
            profile.settingsManager = new SettingsManager(profile, this.activityContext, settings); // stores settings
            this.profiles.add(profile);
        }
    }

    public void deleteProfile(Profile profile) {
        if (this.profiles.contains(profile)) {
            profile.settingsManager.deleteSettings();
            profile.settingsManager = null;
            deleteProfileInPersistance(profile);
            this.profiles.remove(profile);
        }
    }

    public Profile getCurrentProfile() {
        Profile profile = readCurrentProfile();
        if (profile == null) {
            profile = getDefaultProfile();
        }
        return profile;
    }

    public void onConnectivityChange(String ssid) {
        // TODO handle case that connectivity has been lost -> what is the default?
        Profile profile = getProfileBySsid(ssid);
        if (profile != null) {
            // there is a profile that corresponds to the newly connected wifi
            profile.setProfile();
            showProfileSetNotification(profile);
        }
    }

    // ********** PRIVATE *************

    public Profile getProfileBySsid(String ssid) {
        for (Profile p: profiles) {
            if (p.isWifiInProfile(ssid)) {
                return p;
            }
        }
        return null;
    }

    private Profile getProfileByName(String name) {
        for (Profile p: this.profiles) {
            if (name.equals(p.getProfileName())) {
                return p;
            }
        }
        return null;
    }

    private Profile readCurrentProfile() {
        SharedPreferences settings = this.activityContext.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        String currentProfile = settings.getString(this.currProfilePrefsKey, null);

        if (currentProfile != null) {
            return getProfileByName(currentProfile);
        }
        return null;
    }

    private void writeCurrentProfile(Profile profile) {
        SharedPreferences settings = this.activityContext.getSharedPreferences(this.prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(this.currProfilePrefsKey, profile.getProfileName());
        editor.commit();
    }

    private Profile getDefaultProfile() {
        WifiManager wifiManager = (WifiManager) this.activityContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Profile p = new Profile("<default>", wifiInfo.getSSID(), "", "", "", "");
        p.settingsManager = new SettingsManager(p, this.activityContext);
        p.settingsManager.setSettings(p.settingsManager.getDefaultSettings(), false);
        return p;
    }

    private void showProfileSetNotification(Profile profile) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence title = "Profile " + profile.getProfileName() + " set";
        CharSequence text = "Tap to see profile settings (unimplemented)";

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(activityContext)
                // icon is important: http://stackoverflow.com/questions/16045722
                //.setSmallIcon(R.drawable.icon)  // the status icon
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setTicker(title)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(activityContext.getText(R.string.app_name))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                        // set this to the activity with these profile settings
                        //.setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        NotificationManager myNM = (NotificationManager)
                activityContext.getSystemService(activityContext.NOTIFICATION_SERVICE);

        myNM.notify(R.string.app_name, notification);
    }

    private LinkedList<Profile> loadProfilesFromPersistance() {
        LinkedList<Profile> profiles = new LinkedList<Profile>();

        ProfileDbHelper dbHelper = new ProfileDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // get all profiles => get all entries from the profiles table, therefore everything is null
        Cursor cursor = db.query(ProfileDbHelper.TABLE_NAME, null, null, null, null, null, null);

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); ++i) {
            profiles.add(new Profile(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5)));
            cursor.moveToNext();
        }
        db.close();

        return profiles;
    }

    // assumes that profile is going to be updated in the class, does persistance only!
    private long updateProfileInPersistance(Profile profile) {
        ProfileDbHelper dbHelper = new ProfileDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = ProfileDbHelper.KEY_NAME + " = ?";
        String[] args = { profile.getProfileName() };

        ContentValues values = new ContentValues();
        values.put(ProfileDbHelper.KEY_NAME, profile.getProfileName());
        values.put(ProfileDbHelper.KEY_ASSOC_WIFI, profile.getAssociatedWifi());
        values.put(ProfileDbHelper.KEY_DEPART_ID, profile.getPreferredDepartureId());
        values.put(ProfileDbHelper.KEY_DEPART_NAME, profile.getPreferredDepartureName());
        values.put(ProfileDbHelper.KEY_ARRIV_ID, profile.getPreferredArrivalId());
        values.put(ProfileDbHelper.KEY_ARRIV_NAME, profile.getPreferredArrivalName());

        int updatedRecords = db.update(ProfileDbHelper.TABLE_NAME, values, where, args);
        db.close();

        return updatedRecords;
    }

    private void storeProfileInPersistance(Profile profile) {
        ProfileDbHelper dbHelper = new ProfileDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProfileDbHelper.KEY_NAME, profile.getProfileName());
        values.put(ProfileDbHelper.KEY_ASSOC_WIFI, profile.getAssociatedWifi());
        values.put(ProfileDbHelper.KEY_DEPART_ID, profile.getPreferredDepartureId());
        values.put(ProfileDbHelper.KEY_DEPART_NAME, profile.getPreferredDepartureName());
        values.put(ProfileDbHelper.KEY_ARRIV_ID, profile.getPreferredArrivalId());
        values.put(ProfileDbHelper.KEY_ARRIV_NAME, profile.getPreferredArrivalName());

        db.insert(ProfileDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    private long deleteProfileInPersistance(Profile profile) {
        ProfileDbHelper dbHelper = new ProfileDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = ProfileDbHelper.KEY_NAME + " = ?";
        String[] args = { profile.getProfileName() };

        int deletedRecords = db.delete(ProfileDbHelper.TABLE_NAME, where, args);
        db.close();

        return deletedRecords;
    }
}