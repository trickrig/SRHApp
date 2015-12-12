package de.srh.srha.model;


import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public void setSettings(Settings settings) {
        if (this.settings == null) {
            this.settings = settings;
            storeSettingsInPersistance();
        }
        else {
            this.settings = settings;
            updateSettingsInPersistance();
        }
    }

    public void deleteSettings() {
        if (this.settings != null) {
            deleteSettingsInPersistance();
            this.settings = null;
        }
    }

    // sources:
    // http://developer.android.com/reference/android/media/AudioManager.html
    // http://developer.android.com/guide/topics/connectivity/bluetooth.html
    // http://developer.android.com/guide/topics/location/strategies.html
    // http://stackoverflow.com/questions/23100298/android-turn-on-off-mobile-data-using-code
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
        // TODO to implement
        return false;
    }

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

    private boolean getGpsState() {
        // TODO to implement
        return false;
    }
    private boolean getMobileDataState() {
        // TODO to implement
        return false;
    }

    private boolean getVibrationState() {
        // TODO to implement
        return false;
    }

    private int getVolume() {
        // TODO to implement
        return 0;
    }

}
