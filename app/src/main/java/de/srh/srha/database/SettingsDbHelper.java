package de.srh.srha.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.srh.srha.database.ProfileDbHelper;

public class SettingsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "srha";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";

    public static final String KEY_NAME = "profile_name";
    public static final String KEY_WIFI = "wifi";
    public static final String KEY_BLUETOOTH = "bluetooth";
    public static final String KEY_GPS = "gps";
    public static final String KEY_MOBILE_DATA = "mobile_data";
    public static final String KEY_VIBRATION = "vibration";
    public static final String KEY_VOLUME = "volume";

    public static final String TABLE_NAME = "settings";

    // booleans are stored as integers
    // source: https://www.sqlite.org/datatype3.html

    // combined primary key foreign key
    // source: http://stackoverflow.com/questions/5371371
    // source: http://alvinalexander.com/android/sqlite-foreign-keys-example
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
            + KEY_NAME + " TEXT PRIMARY KEY, " + KEY_WIFI + " INTEGER, "
            + KEY_BLUETOOTH + " INTEGER, " + KEY_GPS + " INTEGER, "
            + KEY_MOBILE_DATA + " INTEGER, " + KEY_VIBRATION + " INTEGER, "
            + KEY_VOLUME + " INTEGER, FOREIGN KEY (" + KEY_NAME + ")"
            + " REFERENCES " + ProfileDbHelper.TABLE_NAME + "("
            + ProfileDbHelper.KEY_NAME+ "));";

    public SettingsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // this should actually never be called, as the onCreate events is only called
        // once (when the db does not exist), and this is (usually?) the case in the
        // ProfileDbHelper class as we use this before -> it creates the tables for
        // profiles and settings
        db.execSQL(ProfileDbHelper.CREATE_TABLE_SQL);
        db.execSQL(SettingsDbHelper.CREATE_TABLE_SQL);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // would upgrade table schema (drop, create ...)
    }
}
