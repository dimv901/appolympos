package fpuna.py.com.appolympos.utiles;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.IntentCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.activities.LoginActivity;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.entities.Vendedores;
import fpuna.py.com.appolympos.receivers.ClientObserver;
import fpuna.py.com.appolympos.receivers.MarkersObserver;
import fpuna.py.com.appolympos.receivers.NoSaleObserver;
import fpuna.py.com.appolympos.receivers.OrderObserver;
import fpuna.py.com.appolympos.repository.TransactionRepository;
import fpuna.py.com.appolympos.repository.VendedorRepository;

/**
 * Created by Diego on 5/31/2017.
 */

public class Utils {

    public static void getSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);
        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                snackbar.dismiss();
            }
        };
        snackbar.setAction(R.string.label_close, clickListener);
        snackbar.show();
    }

    public static String formatDate(Date date, String format) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        result = sdf.format(date);
        return result;
    }

    public static String formatNumber(String number) {
        int data = 0;
        try {
            data = Integer.parseInt(number);
        } catch (Exception e) {
            //Fail in silence
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("###,###.###");

        String output = df.format(data);
        return output;
    }

    public static void getToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static void callClienteLoader(Context context) {
        context.sendBroadcast(new Intent(ClientObserver.ACTION_LOAD_CLIENTS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void callNoSaleLoader(Context context) {
        context.sendBroadcast(new Intent(NoSaleObserver.ACTION_LOAD_NO_SALE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void callOrderLoader(Context context) {
        context.sendBroadcast(new Intent(OrderObserver.ACTION_LOAD_ORDERS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void callMarkersOptionLoader(Context context){
        context.sendBroadcast(new Intent(MarkersObserver.ACTION_LOAD_MARKERS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    public static void updateTransaction(Transactions mTransaction, int status, String message) {
        if (mTransaction != null) {
            mTransaction.setUpdatedAt(new Date());
            mTransaction.setStatus(status);
            mTransaction.setObservation(message);
            TransactionRepository.store(mTransaction);
        }
    }

    public static Date trimDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public static void addTextChangeListener(TextInputEditText txtMonto) {
        txtMonto.addTextChangedListener(new TextWatcher() {
            boolean isEditing;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                if (s != null && !TextUtils.isEmpty(s.toString())) {
                    String str = s.toString().replaceAll("[^\\d]", "");
                    double s1 = Double.parseDouble(str);
                    DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("es"));
                    dfs.setDecimalSeparator('.');
                    DecimalFormat df = new DecimalFormat("###,###.###", dfs);
                    s.replace(0, s.length(), df.format(s1));
                }
                isEditing = false;
            }
        });
    }

    public static void endSession(Context context, String message) {
        AppPreferences.getAppPreferences(context).edit().putBoolean(AppPreferences.KEY_PREFERENCE_LOGGED_IN, false).apply();
        AppPreferences.getAppPreferences(context).edit().putString(AppPreferences.KEY_PREFERENCE_USUARIO, null).apply();
        Utils.getToast(context, (message == null) ? context.getString(R.string.error_session_end) : message);
        context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public static void setSalesmanInfo(View headerView, Context context) {
        TextView mTextSalesmanName = (TextView) headerView.findViewById(R.id.textView_salesman_name);
        TextView mTextSalesmanDocument = (TextView) headerView.findViewById(R.id.textView_salesman_document);
        Vendedores mSalesman = VendedorRepository.getById(1);
        if (mSalesman != null) {
            mTextSalesmanName.setText(mSalesman.getNombre() + " " + mSalesman.getApellido());
            mTextSalesmanDocument.setText(mSalesman.getCedula());
        } else {
            mTextSalesmanName.setText(context.getString(R.string.error_no_info_salesmen));
            mTextSalesmanDocument.setText(context.getString(R.string.error_no_info_salesmen));
        }
    }
}
