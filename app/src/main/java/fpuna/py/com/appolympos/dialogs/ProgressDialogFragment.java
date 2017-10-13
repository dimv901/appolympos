package fpuna.py.com.appolympos.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import dmax.dialog.SpotsDialog;
import fpuna.py.com.appolympos.R;

/**
 * Created by Diego on 5/30/2017.
 */

public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG_CLASS = ProgressDialogFragment.class.getName();
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ICON_RESOURCES = "iconResource";

    public static ProgressDialogFragment newInstance(String title, String message, int iconResource) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putInt(ARG_ICON_RESOURCES, iconResource);
        frag.setArguments(args);
        return frag;
    }

    public static ProgressDialogFragment newInstance(Context context) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, context.getString(R.string.dialog_progress_title));
        args.putString(ARG_MESSAGE, context.getString(R.string.dialog_progess_message));
        args.putInt(ARG_ICON_RESOURCES, R.mipmap.ic_hourglass_empty_black_24dp);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title;
        String message;
        int iconResource;
        SpotsDialog dialog = new SpotsDialog(getActivity(), R.style.MyCutomDialog);
        if (getArguments().containsKey(ARG_TITLE)) {
            title = getArguments().getString(ARG_TITLE);
            dialog.setTitle(title);
        }

        if (getArguments().containsKey(ARG_MESSAGE)) {
            message = getArguments().getString(ARG_MESSAGE);
            dialog.setMessage(message);
        }

        if (getArguments().containsKey(ARG_ICON_RESOURCES)) {
            iconResource = getArguments().getInt(ARG_ICON_RESOURCES);
            dialog.setIcon(iconResource);
        }
        // dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

}
