package mapapp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import mapapp.R;

/**
 * Created by Arina on 01.02.2017
 */

public class OwnDialogFragment extends DialogFragment {

    private String title;
    private String message;

    public void setTitleAndMessage(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(getActivity().getString(R.string.fragment_gps_negative_button_title),
                        (DialogInterface dialog, int id) ->
                                dialog.dismiss())
                .setPositiveButton(getActivity().getString(R.string.fragment_gps_positive_button_title),
                       (DialogInterface dialog, int id) ->
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        return builder.create();
    }
}
