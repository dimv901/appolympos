package fpuna.py.com.appolympos.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by diego on 13/10/16.
 */

public class CancelableAlertDialogFragment extends DialogFragment {

    public static final String TAG = CancelableAlertDialogFragment.class.getName();

    private CancelableAlertDialogFragment.CancelableAlertDialogFragmentListener mListener;

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE_BUTTON = "positiveButton";
    private static final String ARG_NEGATIVE_BUTTON = "negativeButton";
    private static final String ARG_ICON_RESOURCES = "iconResource";


    public interface CancelableAlertDialogFragmentListener {
        void onCancelableAlertDialogPositiveClick(DialogFragment dialog);

        void onCancelableAlertDialogNegativeClick(DialogFragment dialog);
    }

    public static CancelableAlertDialogFragment newInstance(String title, String message, String positiveButton, String negativeButton, int iconResource) {
        CancelableAlertDialogFragment frag = new CancelableAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON, positiveButton);
        args.putString(ARG_NEGATIVE_BUTTON, negativeButton);
        args.putInt(ARG_ICON_RESOURCES, iconResource);
        frag.setArguments(args);
        return frag;
    }


    public static CancelableAlertDialogFragment newInstance(String title, String message, String positiveButton, String negativeButton) {
        CancelableAlertDialogFragment frag = new CancelableAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON, positiveButton);
        args.putString(ARG_NEGATIVE_BUTTON, negativeButton);
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
            mListener = (CancelableAlertDialogFragment.CancelableAlertDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement CancelableAlertDialogFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title;
        String message;
        String positiveButton;
        String negativeButton;
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
                    mListener.onCancelableAlertDialogPositiveClick(CancelableAlertDialogFragment.this);
                }
            });
        }

        if (getArguments().containsKey(ARG_NEGATIVE_BUTTON)) {
            negativeButton = getArguments().getString(ARG_NEGATIVE_BUTTON);
            builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onCancelableAlertDialogNegativeClick(CancelableAlertDialogFragment.this);
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
