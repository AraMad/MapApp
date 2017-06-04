package mapapp.settings;

/**
 * Created by Arina on 03.06.2017
 */

public class Constants {
    public static final String BASE_URL = "https://api.privatbank.ua/p24api/";

    public static final int SNACKBAR_DURATION = 5000;
    public static final int LATLNG_ZOOM = 15;

    public static final String DATABASE_NAME = "devices.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "ATM_Privat_Ukraine";
    public static final String KEY_CITY_COLUMN = "CITY";
    public static final String DATA_STRING_COLUMN = "JSONString";
    public static final String TIME_UPDATE_COLUMN = "TIME_OF_UPDATE";

    public static final String DATABASE_CREATE_COMMAND = "CREATE TABLE " + TABLE_NAME
            + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CITY_COLUMN + " TEXT NOT NULL, "
            + DATA_STRING_COLUMN + " TEXT, "
            + TIME_UPDATE_COLUMN + " NUMERIC);";

    public static final String LOCALE = "in";

    public static final String FRAGMENT_GPS_TAG = "gps_dialog";
}
