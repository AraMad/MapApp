package mapapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arina on 28.12.2016.
 */

public class TakeDataTask extends AsyncTask<String, Void, String> {

    public static final String TAG = "MapApp_log";

    private final String API_URL = "https://api.privatbank.ua/p24api/infrastructure?json&atm&address=&city=";

    private  Activity current_activity;
    private Context current_context;
    private DataBaseManager dataBaseManager;

    TakeDataTask(Context context, Activity activity){
        current_activity = activity;
        current_context = context;
    }

    public interface TaskCompleteListener {
        void onTaskCompleteCallBack(String result);
    }

    @Override
    protected String doInBackground(String... parameters) {

        dataBaseManager = new DataBaseManager(current_context);
        String data;

        if (dataBaseManager.openDataBase()){
                data = takeDataFromBase(parameters[0]);
                if (data == null) {
                    data = takeDataFromInternet(parameters[0]);
                    dataBaseManager.addNewRowToBase(parameters[0], data);
                }
        } else {
                data = takeDataFromInternet(parameters[0]);
        }
        dataBaseManager.closeDataBase();

        return data;
    }

    private String takeDataFromBase(String city){
        return  dataBaseManager.getJSONStringByCityName(city);
    }

    private String takeDataFromInternet(String city){

        StringBuffer buffer = new StringBuffer();
        HttpURLConnection urlConnection;
        BufferedReader reader;

        try {
            URL url = new URL(API_URL + city);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }

        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            TaskCompleteListener callback = (TaskCompleteListener) current_activity;
            callback.onTaskCompleteCallBack(result);
        } else {
            Log.i(TAG, "onPostExecute: null");
        }
    }
}
