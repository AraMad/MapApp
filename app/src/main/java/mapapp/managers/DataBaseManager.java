package mapapp.managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import mapapp.R;

import static mapapp.settings.Constants.DATABASE_CREATE_COMMAND;
import static mapapp.settings.Constants.DATABASE_NAME;
import static mapapp.settings.Constants.DATABASE_VERSION;
import static mapapp.settings.Constants.DATA_STRING_COLUMN;
import static mapapp.settings.Constants.KEY_CITY_COLUMN;
import static mapapp.settings.Constants.TABLE_NAME;
import static mapapp.settings.Constants.TIME_UPDATE_COLUMN;

/**
 * Created by Arina on 06.01.2017
 */

public class DataBaseManager {

    private final String TAG = getClass().getSimpleName();

    private final Context applicationContext;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase dataBase;

    public DataBaseManager(Context context){
        applicationContext = context;
        dataBaseHelper = new DataBaseHelper(applicationContext);
    }

    public boolean openDataBase() {
        try {
            dataBase = dataBaseHelper.getWritableDatabase();
            return true;
        } catch (SQLiteException ex) {
            try {
                dataBase = dataBaseHelper.getReadableDatabase();
                Log.i(TAG, "openDataBase: " + ex.toString());
                return true;
            } catch (SQLiteException e) {
                Log.i(TAG, "openDataBase: " + e.toString());
                return false;
            }
        }
    }

    public void closeDataBase() {
        if (dataBaseHelper != null) {
            dataBaseHelper.close();
        }
    }

    public String getJSONStringByCityName(String city) {
        Cursor cursor;

        try {
            cursor = dataBase.query(TABLE_NAME,
                    new String[] {DATA_STRING_COLUMN},
                    KEY_CITY_COLUMN + " = '" + city + "'",
                    null, null, null, null);
            cursor.moveToFirst();
            String JSON_string = cursor.getString(cursor.getColumnIndexOrThrow(
                    DATA_STRING_COLUMN));
            cursor.close();
            return JSON_string;
        } catch (Exception e){
            Log.i(TAG, e.toString());
        }

        return null;
    }

    public void addNewRowToBase(String city, String data_string) {

        try {
            dataBase.execSQL("INSERT INTO " + TABLE_NAME
                    + " (" + KEY_CITY_COLUMN +", "
                    + DATA_STRING_COLUMN + ", "
                    + TIME_UPDATE_COLUMN + ") VALUES ("
                    + "'" + city + "'" + ", '" + data_string + "', " + System.currentTimeMillis() + ");");
        } catch (Exception e){
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }

    }

    private class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_COMMAND);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
