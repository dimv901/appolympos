package fpuna.py.com.appolympos.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.ClientsAdapter;
import fpuna.py.com.appolympos.adapters.NoSaleSpinnerAdapter;
import fpuna.py.com.appolympos.dialogs.ProgressDialogFragment;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.entities.NoCompra;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.entities.Vendedores;
import fpuna.py.com.appolympos.network.MyRequest;
import fpuna.py.com.appolympos.network.NetworkQueue;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.repository.NoCompraRepository;
import fpuna.py.com.appolympos.repository.TransactionRepository;
import fpuna.py.com.appolympos.repository.VendedorRepository;
import fpuna.py.com.appolympos.utiles.AppLocationProvider;
import fpuna.py.com.appolympos.utiles.AppPreferences;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;

public class NoSaleActivity extends AppCompatActivity {

    public static final String TAG_CLASS = NoSaleActivity.class.getName();
    //Adapter
    private NoSaleSpinnerAdapter mAdapter;
    // Request class
    private NoSaleTask mNoSaleTask;
    private NetworkQueue mQueue;
    // Object instance and class
    private Transactions mTransaction;
    private long mTransactionId = 0;
    private Clientes mCliente;
    private Vendedores mVendedor;

    private CoordinatorLayout mCoordinator;
    private AppCompatSpinner mReasonSpinner;
    private TextInputEditText mInputClient;
    private TextInputEditText mInputRuc;
    private AppLocationProvider mAppLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_sale);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_no_sale);
        setSupportActionBar(toolbar);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.no_sale_coordinator);
        mReasonSpinner = (AppCompatSpinner) findViewById(R.id.no_sale_spinner_id);
        mInputClient = (TextInputEditText) findViewById(R.id.no_sale_client_name);
        mInputClient.setFocusable(false);
        mInputRuc = (TextInputEditText) findViewById(R.id.no_sale_client_ruc);
        mInputRuc.setFocusable(false);

        Button mAcceptButton = (Button) findViewById(R.id.no_sale_button_ok);
        Button mCancelButton = (Button) findViewById(R.id.no_sale_button_cancel);
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setData();
        getClienteData();
        getVendedorData();
        mQueue = new NetworkQueue(getApplicationContext());
        mAppLocationProvider = new AppLocationProvider(this);
    }


    private void setData() {
        long reasonCount = NoCompraRepository.count();
        if (reasonCount > 0) {
            mAdapter = new NoSaleSpinnerAdapter(this, R.layout.item_reason, NoCompraRepository.getAll());
            mReasonSpinner.setAdapter(mAdapter);
        } else {
            Utils.getToast(this, getString(R.string.error_need_sync_reasons));
            startActivity(new Intent(getApplicationContext(), SyncActivity.class));
            finish();
            return;
        }
    }

    private void getClienteData() {
        if (!getIntent().hasExtra(ClientsAdapter.CLIENT_ID)) {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }

        if (getIntent().getExtras().containsKey(ClientsAdapter.CLIENT_ID)) {
            long clientId = getIntent().getExtras().getLong(ClientsAdapter.CLIENT_ID);
            mCliente = ClienteRepository.getById(clientId);
            if (mCliente == null) {
                Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
                finish();
                return;
            }
            mInputClient.setText(mCliente.getNombreNegocio());
            mInputRuc.setText(mCliente.getRuc());
        } else {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }
    }

    private void getVendedorData() {
        String mUserName = AppPreferences.getAppPreferences(this).getString(AppPreferences.KEY_PREFERENCE_USUARIO, null);
        mVendedor = VendedorRepository.getSalesmanByUserName(mUserName);
        if (mVendedor == null) {
            Utils.getToast(this, getString(R.string.error_need_sync_salesman));
            startActivity(new Intent(getApplicationContext(), SyncActivity.class));
            finish();
            return;
        }
    }


    private void validateFields() {
        NoCompra reason = (NoCompra) mReasonSpinner.getSelectedItem();
        double latitude = 0;
        double longitude = 0;
        long mReasonId = reason.getId();
        long mSalesmanId = mVendedor.getId();
        long mClientId = mCliente.getId();
        String mClientDesc = mCliente.getNombreNegocio();
        if (mReasonId <= 0) {
            Utils.getToast(this, getString(R.string.error_need_selected_no_sale_reason));
            return;
        }

        Location mLocation = mAppLocationProvider.getLocation();
        if(mLocation!=null){
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        }

        mNoSaleTask = new NoSaleTask(String.valueOf(mReasonId), String.valueOf(mClientId), mClientDesc, String.valueOf(mSalesmanId), String.valueOf(latitude), String.valueOf(longitude));
        mNoSaleTask.confirm();
    }

    private class NoSaleTask extends MyRequest {
        public static final String REQUEST_TAG = "NoSaleTask";
        private String mReasonId;
        private String mClientId;
        private String mClientDesc;
        private String mSalesmanId;
        private String mLatitude;
        private String mLongitude;

        public NoSaleTask(String mReasonId, String mClientId, String mClientDesc, String mSalesmanId, String mLatitude, String mLongitude) {
            this.mReasonId = mReasonId;
            this.mClientId = mClientId;
            this.mClientDesc = mClientDesc;
            this.mSalesmanId = mSalesmanId;
            this.mLatitude = mLatitude;
            this.mLongitude = mLongitude;
        }

        @Override
        protected void confirm() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(NoSaleActivity.this);
            builder.setIcon(R.mipmap.ic_info_black_24dp);
            builder.setTitle(R.string.dialog_confirmation_title);
            builder.setMessage(R.string.dialog_confirmation_message);
            builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    execute();
                }
            });

            builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mNoSaleTask = null;
                }
            });

            AlertDialog confirmDialog = builder.create();
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.show();
        }

        @Override
        protected void execute() {
            mProgressDialog = ProgressDialogFragment.newInstance(getApplicationContext());
            mProgressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);

            if (mJsonObjectRequest != null)
                mQueue.getRequestQueue(getApplicationContext()).cancelAll(NoSaleActivity.NoSaleTask.REQUEST_TAG);


            Gson mGsonObject = new Gson();
            if (mTransaction == null) {
                mTransaction = new Transactions();
                mTransaction.setCreatedAt(Utils.trimDate(new Date()));
                mTransaction.setClientId(mClientId);
                mTransaction.setClientName(mClientDesc);
                mTransaction.setType(Constants.NO_SALE_TRANSACTION);
                mTransaction.setStatus(Constants.TRANSACTION_PENDING);
                mTransaction.setObservation(getApplication().getString(R.string.message_pending_transaction));
                mTransaction.setHttpDetail(String.valueOf(getParams()));
                mTransactionId = TransactionRepository.store(mTransaction);
                if (mTransactionId <= 0) {
                    Log.e(TAG_CLASS, "Error storing transaction data: " + mGsonObject.toJson(getParams()));
                    Utils.getSnackBar(mCoordinator, getString(R.string.error_save_transaction));
                    mProgressDialog.dismiss();
                    return;
                }
            } else {
                Log.v(TAG_CLASS, "Transaction exist, just updating on database");
                mTransaction.setCreatedAt(Utils.trimDate(new Date()));
                mTransaction.setClientId(mClientId);
                mTransaction.setClientName(mClientDesc);
                mTransaction.setType(Constants.NO_SALE_TRANSACTION);
                mTransaction.setStatus(Constants.TRANSACTION_PENDING);
                mTransaction.setObservation(getApplication().getString(R.string.message_pending_transaction));
                mTransaction.setHttpDetail(String.valueOf(getParams()));
                TransactionRepository.store(mTransaction);
            }

            mCliente.setVisitado(true);
            ClienteRepository.store(mCliente);

            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.NO_SALE_URL,
                    getParams(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            handleResponse(response);
                            mJsonObjectRequest.cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialog.dismiss();
                            String message = NetworkQueue.handleError(error, getApplicationContext());
                            Utils.getSnackBar(mCoordinator, message);
                        }
                    });
            mJsonObjectRequest.setTag(NoSaleActivity.NoSaleTask.REQUEST_TAG);
            Log.v(REQUEST_TAG, "Queueing: " + mGsonObject.toJson(getParams()));
            mQueue.addToRequestQueue(mJsonObjectRequest, getApplicationContext());
        }

        @Override
        protected JSONObject getParams() {
            mJsonParams = new JSONObject();
            try {
                mJsonParams.put("idMotivo", mReasonId);
                mJsonParams.put("idCliente", mClientId);
                mJsonParams.put("idVendedor", mSalesmanId);
                mJsonParams.put("latitud", mLatitude);
                mJsonParams.put("longitud", mLongitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mJsonParams;
        }


        @Override
        protected void handleResponse(JSONObject response) {
            String message = null;
            int status = -1;

            if (response == null) {
                Utils.updateTransaction(mTransaction, Constants.TRANSACTION_ERROR, getString(R.string.volley_default_error));
                Utils.getSnackBar(mCoordinator, getString(R.string.volley_default_error));
                return;
            }

            Log.i(TAG_CLASS, REQUEST_TAG + " | Response: " + response.toString());

            try {
                if (response.has("status")) status = response.getInt("status");
                if (response.has("message")) message = response.getString("message");

                if (status != Constants.RESPONSE_OK) {
                    Utils.updateTransaction(mTransaction, Constants.TRANSACTION_ERROR, (message == null) ? getString(R.string.volley_default_error) : message);
                    Utils.getSnackBar(mCoordinator, (message == null) ? getString(R.string.volley_default_error) : message);
                    return;
                }
                Utils.updateTransaction(mTransaction, Constants.TRANSACTION_SEND, message);
                Utils.callMarkersOptionLoader(getApplicationContext());;
                successDialog(message);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.getSnackBar(mCoordinator, getString(R.string.volley_default_error));
            }
        }
    }

    private void successDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoSaleActivity.this);
        builder.setIcon(R.mipmap.ic_done_black_24dp);
        builder.setTitle(R.string.dialog_info_title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Utils.callMarkersOptionLoader(getApplicationContext());
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
