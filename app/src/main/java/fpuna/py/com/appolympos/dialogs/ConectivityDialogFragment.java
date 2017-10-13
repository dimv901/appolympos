package fpuna.py.com.appolympos.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import fpuna.py.com.appolympos.R;

/**
 * Created by Diego on 5/30/2017.
 */

public class ConectivityDialogFragment extends DialogFragment {


    public static ConectivityDialogFragment newInstance() {
        ConectivityDialogFragment frag = new ConectivityDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_connectivity_title);
        builder.setMessage(R.string.dialog_connectivity_message);
        builder.setIcon(R.mipmap.ic_wifi_black_24dp);
        builder.setPositiveButton(R.string.label_activate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().getApplicationContext().startActivity(intent);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
