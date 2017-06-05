package mapapp.providers;

import android.app.Activity;
import android.os.Message;

import java.io.IOException;
import java.lang.ref.WeakReference;

import mapapp.interfaces.PrivatAPIInterface;
import mapapp.interfaces.TaskCompleteListener;
import mapapp.managers.DataBaseManager;
import mapapp.singletons.MainHandler;
import mapapp.singletons.RetrofitClient;
import retrofit2.Call;

/**
 * Created by Arina on 05.06.2017
 */

public class TakeMarkersInfo extends Thread {

    private WeakReference weakrefOnActivity;
    private DataBaseManager dataBaseManager;

    private String city;

    public TakeMarkersInfo(Activity activity, String city) {
        weakrefOnActivity = new WeakReference<>(activity);
        dataBaseManager = new DataBaseManager((Activity) weakrefOnActivity.get());
        this.city = city;
    }

    @Override
    public void run() {
        String data;

        if (dataBaseManager.openDataBase()){
            data = takeDataFromBase(city);
            if (data == null) {
                data = takeDataFromInternet(city);
                dataBaseManager.addNewRowToBase(city, data);
            }
        } else {
            data = takeDataFromInternet(city);
        }
        dataBaseManager.closeDataBase();

        if (data != null && weakrefOnActivity != null) {
            final Message message = new Message();
            message.obj = data;
            //MainHandler.getInstance().sendMessage(message);

            TaskCompleteListener callback = (TaskCompleteListener)
                    weakrefOnActivity.get();

            MainHandler.getInstance().post(() -> {
                callback.onTaskCompleteCallBack(message);
                });
        }
        //TODO: add message to main Handler
    }

    private String takeDataFromBase(String city){
        return  dataBaseManager.getJSONStringByCityName(city);
    }

    private String takeDataFromInternet(String city){

        String data = null;

        PrivatAPIInterface service = RetrofitClient.getInstance()
                .create(PrivatAPIInterface.class);
        Call<String> result = service.getATMInfo(city);
        try {
            data = result.execute().body();
        } catch (IOException e){
            e.printStackTrace();
        }

        return data;
    }
}
