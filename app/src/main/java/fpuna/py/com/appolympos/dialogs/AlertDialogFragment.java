package fpuna.py.com.appolympos.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by diego on 12/10/16.
 */

public class AlertDialogFragment extends DialogFragment {

    public static final String TAG_CLASS = AlertDialogFragment.class.getName();
    private AlertDialogFragmentListener mListener;

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE_BUTTON = "positiveButton";
    private static final String ARG_ICON_RESOURCES = "iconResource";


    public interface AlertDialogFragmentListener {
        void onAlertDialogPositiveClick(DialogFragment dialog);
    }

    public static AlertDialogFragment newInstance(String title, String message, String positiveButton, int iconResource) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON, positiveButton);
        args.putInt(ARG_ICON_RESOURCES, iconResource);
        frag.setArguments(args);
        return frag;
    }


    public static AlertDialogFragment newInstance(String title, String message, String positiveButton) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON, positiveButton);
        frag.setArguments(args);
        return frag;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        //noinspection deprecation
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AlertDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement AlertDialogFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title;
        String message;
        String positiveButton;
        int iconResource;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        if (getArguments().containsKey(ARG_TITLE)) {
            title = getArguments().getString(ARG_TITLE);
            builder.setTitle(title);
        }

        if (getArguments().containsKey(ARG_MESSAGE)) {
            message = getArguments().getString(ARG_MESSAGE);
            builder.setMessage(message);
        }

        if (getArguments().containsKey(ARG_POSITIVE_BUTTON)) {
            positiveButton = getArguments().getString(ARG_POSITIVE_BUTTON);
            builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onAlertDialogPositiveClick(AlertDialogFragment.this);
                }
            });
        }

        if (getArguments().containsKey(ARG_ICON_RESOURCES)) {
            iconResource = getArguments().getInt(ARG_ICON_RESOURCES);
            builder.setIcon(iconResource);
        }

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
