package mapapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.arina.mapapp.R;

/**
 * Created by Arina on 01.02.2017.
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
                .setPositiveButton(getActivity().getString(R.string.fragment_button_title),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
