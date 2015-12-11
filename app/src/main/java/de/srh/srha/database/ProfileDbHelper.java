package de.srh.srha.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class ProfileDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "test";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ASSOC_WIFI = "assoc_wifi";
    public static final String KEY_DEPART_ID = "departure_id";
    public static final String KEY_DEPART_NAME = "departure_name";
    public static final String KEY_ARRIV_ID = "arrival_id";
    public static final String KEY_ARRIV_NAME = "arrival_name";

    public static final String TABLE_NAME = "profiles";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" + KEY_ID
            + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, " + KEY_ASSOC_WIFI + " TEXT, "
            + KEY_DEPART_ID + " TEXT, " + KEY_DEPART_NAME + " TEXT, "
            + KEY_ARRIV_ID + " TEXT, " + KEY_ARRIV_NAME + " TEXT);";

    public ProfileDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // would upgrade table schema (drop, create ...)
    }

}
