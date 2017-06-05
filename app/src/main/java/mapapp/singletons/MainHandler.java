package mapapp.singletons;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import static mapapp.BuildConfig.DEBUG;

/**
 * Created by Arina on 05.06.2017
 */

public class MainHandler {

    private static Handler handler;

    public static synchronized Handler getInstance(){
        if (handler == null){
            handler = new Handler(Looper.getMainLooper());
        }

        return handler;
    }
}
