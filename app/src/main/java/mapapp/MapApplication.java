package mapapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Arina on 06.06.2017
 */

public class MapApplication extends Application {

    private static MapApplication mapApplication;

    public static synchronized MapApplication getInstance() {
        return mapApplication;
    }

    public static synchronized Context getMainContext() {
        return mapApplication.getApplicationContext();
    }

    @Override
    public void onCreate() {
        mapApplication = this;
        super.onCreate();
    }
}
