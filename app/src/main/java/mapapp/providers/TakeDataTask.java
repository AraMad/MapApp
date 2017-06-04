package mapapp.providers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

import mapapp.interfaces.PrivatAPIInterface;
import mapapp.managers.DataBaseManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static mapapp.settings.Constants.BASE_URL;

/**
 * Created by Arina on 28.12.2016
 */

public class TakeDataTask extends AsyncTask<String, Void, String> {

    private final String TAG = getClass().getSimpleName();

    private WeakReference weakrefOnActivity;
    private DataBaseManager dataBaseManager;
    private Retrofit client;

    public TakeDataTask(Activity activity){
        weakrefOnActivity = new WeakReference<>(activity);

        client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public interface TaskCompleteListener {
        void onTaskCompleteCallBack(String result);
    }

    @Override
    protected String doInBackground(String... parameters) {

        dataBaseManager = new DataBaseManager((Activity) weakrefOnActivity.get());
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

        String data = null;

        PrivatAPIInterface service = client.create(PrivatAPIInterface.class);
        Call<String> result = service.getATMInfo(city);
        try {
            data = result.execute().body();
        } catch (IOException e){
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null && weakrefOnActivity != null) {
            TaskCompleteListener callback = (TaskCompleteListener) weakrefOnActivity.get();
            callback.onTaskCompleteCallBack(result);
        } else {
            Log.i(TAG, "onPostExecute: null");
        }
    }
}
