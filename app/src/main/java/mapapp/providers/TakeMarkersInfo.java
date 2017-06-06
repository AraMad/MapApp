package mapapp.providers;

import java.io.IOException;
import java.lang.ref.WeakReference;

import mapapp.MapApplication;
import mapapp.interfaces.PrivatAPIInterface;
import mapapp.interfaces.TaskCompleteListener;
import mapapp.managers.DataBaseManager;
import mapapp.singletons.MainHandler;
import mapapp.singletons.PrivatAPIClient;
import retrofit2.Call;

/**
 * Created by Arina on 05.06.2017
 */

public class TakeMarkersInfo extends Thread {

    private final String TAG = getClass().getSimpleName();

    private WeakReference weakReferenceOnListener;
    private String city;

    public TakeMarkersInfo(TaskCompleteListener listener, String city) {
        weakReferenceOnListener = new WeakReference<>(listener);
        this.city = city;
    }

    @Override
    public void run() {

        DataBaseManager dataBaseManager = new DataBaseManager(MapApplication.getMainContext());
        String data;

        if (dataBaseManager.openDataBase()){
            data = dataBaseManager.getJSONStringByCityName(city);
            if (data == null) {
                data = takeDataFromInternet(city);
                dataBaseManager.addNewRowToBase(city, data);
            }
        } else {
            data = takeDataFromInternet(city);
        }
        dataBaseManager.closeDataBase();

        if (data != null && weakReferenceOnListener != null) {

            final String result = data;
            MainHandler.getInstance().post(() ->
                    ((TaskCompleteListener)
                            weakReferenceOnListener.get()).onTaskCompleteCallBack(result));
        }
    }

    private String takeDataFromInternet(String city){

        String data = null;

        PrivatAPIInterface service = PrivatAPIClient.getInstance()
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
