package de.srh.srha.model;


import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import de.srh.srha.database.SettingsDbHelper;

public class SettingsManager {

    private Settings settings;
    private Profile profile;
    private Context activityContext;

    public SettingsManager(Profile profile, Context context) {
        // TODO load settings from Persistance
        this.profile = profile;
        this.activityContext = context;
        this.settings = null;
        if (!this.profile.getProfileName().equals("<default>")) {
            this.settings = loadSettingsFromPersistance();
        }
    }

    public SettingsManager(Profile profile, Context context, Settings settings) {
        this.profile = profile;
        this.activityContext = context;
        Settings oldSettings = loadSettingsFromPersistance();
        if (oldSettings == null) {
            this.settings = settings;
            storeSettingsInPersistance();
        } else {
            if (!oldSettings.equals(settings)) {
                this.settings = settings;
                updateSettingsInPersistance();
            }
        }
    }

    public Settings getSettings() {
        return settings;
    }

    // TODO method is called only once -> omit store/updateSettingsInPersistance?
    public void setSettings(Settings settings, boolean storeSettings) {
        if (this.settings == null) {
            this.settings = settings;
            if (storeSettings) {
                storeSettingsInPersistance();
            }
        }
        else {
            this.settings = settings;
            if (storeSettings) {
                updateSettingsInPersistance();
            }
        }
    }

    public void deleteSettings() {
        if (this.settings != null) {
            deleteSettingsInPersistance();
            this.settings = null;
        }
    }

    // returns current system settings
    public Settings getDefaultSettings() {
        return new Settings(
                getWifiState(),
                getBluetoothState(),
                getGpsState(),
                getMobileDataState(),
                getVibrationState(),
                getVolume()
        );
    }

    public void applySettings() {
        // TODO apply settings
    }

    private Settings loadSettingsFromPersistance() {
        SettingsDbHelper dbHelper = new SettingsDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where = SettingsDbHelper.KEY_NAME + " = ?";
        String[] args = { this.profile.getProfileName() };

        // get all settings => get all entries from the profiles table, therefore everything is null
        Cursor cursor = db.query(SettingsDbHelper.TABLE_NAME, null, where, args, null, null, null);

        if (cursor.getCount() > 1) {
            throw new IllegalStateException("there is more than one setting for the given profile " + this.profile.getProfileName());
        }

        cursor.moveToFirst();
        Settings s = null;
        if (cursor.getCount() == 1) {
            s =  new Settings(cursor.getInt(1) != 0, cursor.getInt(2) != 0,
                    cursor.getInt(3) != 0, cursor.getInt(4) != 0, cursor.getInt(5) != 0,
                    cursor.getInt(6));
        }
        // else: nothing found, s is null
        db.close();

        return s;
    }

    private long updateSettingsInPersistance() {
        SettingsDbHelper dbHelper = new SettingsDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = SettingsDbHelper.KEY_NAME + " = ?";
        String[] args = { this.profile.getProfileName() };

        ContentValues values = new ContentValues();
        values.put(SettingsDbHelper.KEY_NAME, this.profile.getProfileName());
        values.put(SettingsDbHelper.KEY_WIFI, boolToInt(this.settings.isWifiOn()));
        values.put(SettingsDbHelper.KEY_BLUETOOTH, boolToInt(this.settings.isBluetoothOn()));
        values.put(SettingsDbHelper.KEY_GPS, boolToInt(this.settings.isGpsOn()));
        values.put(SettingsDbHelper.KEY_MOBILE_DATA, boolToInt(this.settings.isMobileDataOn()));
        values.put(SettingsDbHelper.KEY_VIBRATION, boolToInt(this.settings.isVibrateOn()));
        values.put(SettingsDbHelper.KEY_VOLUME, this.settings.getRingVolume());

        int updatedRecords = db.update(SettingsDbHelper.TABLE_NAME, values, where, args);
        if (updatedRecords != 1) {
            throw new IllegalStateException("there is more than one setting for the given profile " + this.profile.getProfileName());
        }
        db.close();

        return updatedRecords;
    }

    private void storeSettingsInPersistance() {
        SettingsDbHelper dbHelper = new SettingsDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SettingsDbHelper.KEY_NAME, this.profile.getProfileName());
        values.put(SettingsDbHelper.KEY_WIFI, boolToInt(this.settings.isWifiOn()));
        values.put(SettingsDbHelper.KEY_BLUETOOTH, boolToInt(this.settings.isBluetoothOn()));
        values.put(SettingsDbHelper.KEY_GPS, boolToInt(this.settings.isGpsOn()));
        values.put(SettingsDbHelper.KEY_MOBILE_DATA, boolToInt(this.settings.isMobileDataOn()));
        values.put(SettingsDbHelper.KEY_VIBRATION, boolToInt(this.settings.isVibrateOn()));
        values.put(SettingsDbHelper.KEY_VOLUME, this.settings.getRingVolume());

        db.insert(SettingsDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    private long deleteSettingsInPersistance() {
        SettingsDbHelper dbHelper = new SettingsDbHelper(this.activityContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = SettingsDbHelper.KEY_NAME + " = ?";
        String[] args = { this.profile.getProfileName() };

        int deletedRecords = db.delete(SettingsDbHelper.TABLE_NAME, where, args);
        if (deletedRecords != 1) {
            throw new IllegalStateException("there is more than one setting for the given profile " + this.profile.getProfileName());
        }
        db.close();

        return deletedRecords;
    }

    private int boolToInt(boolean val) { return ((val) ?  1 :  0); }

    private boolean getWifiState() {
        WifiManager wifiManager = (WifiManager) this.activityContext.getSystemService(Context.WIFI_SERVICE);
        switch (wifiManager.getWifiState()) {
            case WifiManager.WIFI_STATE_ENABLED:
            case WifiManager.WIFI_STATE_ENABLING:
                return true;
            default: // should only be WIFI_STATE_DISABLED or DISABLING or UNKNOWN
                return false;
        }
    }

    // http://developer.android.com/guide/topics/connectivity/bluetooth.html
    private boolean getBluetoothState() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        switch (bluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_ON:
            case BluetoothAdapter.STATE_TURNING_ON:
                return true;
            default: // should only be STATE_OFF pr STATE_TURNING_OFF acc. to doc
                return false;
        }
    }

    // not that easy to implement, see
    // http://stackoverflow.com/questions/2021176
    private boolean getGpsState() {
        // TODO to implement
        return false;
    }

    // http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
    private boolean getMobileDataState() {
        ConnectivityManager cm = (ConnectivityManager) this.activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE
                && activeNetwork.isConnectedOrConnecting();
    }

    // http://developer.android.com/reference/android/media/AudioManager.html
    private boolean getVibrationState() {
        AudioManager audioManager = (AudioManager) this.activityContext.getSystemService(Context.AUDIO_SERVICE);
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
            case AudioManager.RINGER_MODE_VIBRATE:
                return true;
            default: // should only be RINGER_MODE_SILENT
                return false;
        }
    }

    private int getVolume() {
        AudioManager audioManager = (AudioManager) this.activityContext.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_RING);
    }

}
