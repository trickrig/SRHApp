package de.srh.srha.model;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.widget.Toast;

import java.util.LinkedList;

import de.srh.srha.MainActivity;
import de.srh.srha.R;
import de.srh.srha.database.ProfileDbHelper;
import de.srh.srha.receivers.WifiExpirator;

public class ProfileManager {

    private LinkedList<Profile> profiles;

    private Context activityContext;

    public static final String prefsFile = "SRHA-prefs";
    public static final String currProfilePrefsKey = "currentProfile";
    public static final String defaultProfileName = "<default>";
    public static int wifiExpirationTimeSeconds = 30 * 60;

    public ProfileManager(Context context) {
        this.activityContext = context;

        this.profiles = loadProfilesFromPersistance();
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
        if (profile.getProfileName().equals(ProfileManager.defaultProfileName)) {
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

            // if we are in the wifi for which we have changed the settings, directly apply i
            WifiManager wifiManager = (WifiManager) this.activityContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSSID().equals(profile.getAssociatedWifi())) {
                onConnectivityChange(profile.getAssociatedWifi());
            }
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
        Profile profile = getProfileBySsid(ssid);
        if (profile != null) {
            // there is a profile that corresponds to the newly connected wifi
            if (readCurrentProfile() == null) {
                // no profile set -> store current value so that we can reset later in case
                Profile resetProfile = getDefaultProfile();
                storeProfileInPersistance(resetProfile);
                resetProfile.settingsManager = new SettingsManager(resetProfile, this.activityContext,
                        resetProfile.settingsManager.getSettings()); // stores settings
            }
            profile.setProfile();
            writeCurrentProfile(profile);
            showProfileSetNotification(profile);
            killWifiExpirator();
        }
    }

    public void onDisconnectivityChange() {
        // if there is a profile set
        if (readCurrentProfile() != null) {
            // start async task to reset it in 30 min
            startWifiExpirator();
        }
    }

    public void wifiExpires() {
        Profile resetProfile = loadDefaultProfileFromPersistance();
        if (resetProfile != null) {
            deleteCurrentProfile();
            deleteProfileInPersistance(resetProfile);
            resetProfile.setProfile();
            // Cancel all notifications, there is no active profile
            NotificationManager myNM = (NotificationManager)
                    activityContext.getSystemService(activityContext.NOTIFICATION_SERVICE);
            myNM.cancelAll();
            Toast.makeText(ProfileManager.this.activityContext,
                    "SRHA: profile expired (no wi-fi connectivity)", Toast.LENGTH_SHORT).show();
        }
    }

    public Profile getProfileBySsid(String ssid) {
        for (Profile p: profiles) {
            if (p.isWifiInProfile(ssid)) {
                return p;
            }
        }
        return null;
    }

    // ********** PRIVATE *************

    public void startWifiExpirator() {
        AlarmManager alarmMgr = (AlarmManager) this.activityContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.activityContext, WifiExpirator.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this.activityContext, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + ProfileManager.wifiExpirationTimeSeconds * 1000, alarmIntent);
    }

    private void killWifiExpirator() {
        AlarmManager alarmMgr = (AlarmManager) this.activityContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.activityContext, WifiExpirator.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this.activityContext, 0, intent, 0);

        alarmMgr.cancel(alarmIntent);
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

    private void deleteCurrentProfile() {
        SharedPreferences settings = this.activityContext.getSharedPreferences(this.prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.remove(ProfileManager.currProfilePrefsKey);
        editor.commit();
    }

    private Profile getDefaultProfile() {
        WifiManager wifiManager = (WifiManager) this.activityContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Profile p = new Profile(ProfileManager.defaultProfileName, wifiInfo.getSSID(), "", "", "", "");
        p.settingsManager = new SettingsManager(p, this.activityContext);
        p.settingsManager.setSettings(p.settingsManager.getDefaultSettings(), false);
        return p;
    }

    private void showProfileSetNotification(Profile profile) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence title = "SRHA: Profile \"" + profile.getProfileName() + "\" set";
        CharSequence text = "Tap to see profile settings";

        Intent reviewIntent = new Intent(this.activityContext, MainActivity.class);
        reviewIntent.putExtra("de.srh.srha.showSettings", true);
        PendingIntent piReview = PendingIntent.getActivity(this.activityContext, 0, reviewIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the info for the views that show in the notification panel.
        Notification.Builder notificationBuilder = new Notification.Builder(activityContext)
                // icon is important: http://stackoverflow.com/questions/16045722
                //.setSmallIcon(R.drawable.icon)  // the status icon
                .setSmallIcon(android.R.drawable.stat_notify_more)
                //.setTicker(title)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(title)  // the label of the entry
                .setContentText(text)  // the contents of the entry
                        // set this to the activity with these profile settings
                .setContentIntent(piReview)  // The intent to send when the entry is clicked
                .setAutoCancel(true)
                ;

        // Send the notification.
        NotificationManager myNM = (NotificationManager)
                activityContext.getSystemService(activityContext.NOTIFICATION_SERVICE);

        myNM.notify(R.string.app_name, notificationBuilder.build());
    }

    private LinkedList<Profile> loadProfilesFromPersistance() {
        LinkedList<Profile> profiles = new LinkedList<Profile>();

        ProfileDbHelper dbHelper = new ProfileDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where = ProfileDbHelper.KEY_NAME + " != ?";
        String[] args = { ProfileManager.defaultProfileName };

        // get all profiles => get all entries from the profiles table, therefore everything is null
        Cursor cursor = db.query(ProfileDbHelper.TABLE_NAME, null, where, args, null, null, null);

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); ++i) {
            profiles.add(new Profile(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5)));
            cursor.moveToNext();
        }
        db.close();

        // for every profile there MUST be a setting
        for (Profile p: profiles) {
            p.settingsManager = new SettingsManager(p, this.activityContext); // loads settings
        }

        return profiles;
    }

    private Profile loadDefaultProfileFromPersistance() {
        Profile profile = null;

        ProfileDbHelper dbHelper = new ProfileDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where = ProfileDbHelper.KEY_NAME + " = ?";
        String[] args = { ProfileManager.defaultProfileName };

        // get all profiles => get all entries from the profiles table, therefore everything is null
        Cursor cursor = db.query(ProfileDbHelper.TABLE_NAME, null, where, args, null, null, null);

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            profile = new Profile(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5));
            profile.settingsManager = new SettingsManager(profile, this.activityContext);
        }
        db.close();

        return profile;
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