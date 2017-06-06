package mapapp.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import mapapp.MapApplication;

/**
 * Created by Arina on 24.04.2017
 */

public class MessageDisplayer {

    public void showToastMessage(String message){
        Toast.makeText(MapApplication.getMainContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    public void showSnackbarMessage(View view, String message, int duration){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setDuration(duration)
                .show();
    }

    public void showSnackbarMessageWithAction(View view, String message, int duration,
                                              String buttonTitle,
                                              View.OnClickListener listener){

        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(buttonTitle, listener)
                .setDuration(duration)
                .show();
    }
}
