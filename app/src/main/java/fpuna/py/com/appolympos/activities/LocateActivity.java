package fpuna.py.com.appolympos.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.ClientsAdapter;
import fpuna.py.com.appolympos.dialogs.ProgressDialogFragment;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.network.MyRequest;
import fpuna.py.com.appolympos.network.NetworkQueue;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;

public class LocateActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG_CLASS = LocateActivity.class.getName();

    private Clientes mCliente;
    private NetworkQueue mQueue;
    private GoogleMap mMap;

    private CoordinatorLayout mCoordinator;
    private TextInputEditText inputClientName;
    private TextInputEditText inputClientRUC;
    private TextInputEditText inputClientAdress;

    private Button btAccept;
    private Button btCancel;
    private Marker mMarkerLocation;
    private Location mCLientLocation;
    private LocateTask mLocateTask;
    private ProgressDialogFragment mProgressDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialogFragment = ProgressDialogFragment.newInstance(getApplicationContext());
        mProgressDialogFragment.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.locate_coordinator);
        inputClientName = (TextInputEditText) findViewById(R.id.locate_client_name);
        inputClientName.setFocusable(false);
        inputClientRUC = (TextInputEditText) findViewById(R.id.locate_client_ruc);
        inputClientRUC.setFocusable(false);
        inputClientAdress = (TextInputEditText) findViewById(R.id.locate_client_address);
        inputClientAdress.setFocusable(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btAccept = (Button) findViewById(R.id.locate_button_ok);
        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        btCancel = (Button) findViewById(R.id.locate_button_cancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mQueue = new NetworkQueue(getApplicationContext());
        getClienteData();
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
            inputClientName.setText(mCliente.getNombreNegocio());
            inputClientRUC.setText(mCliente.getRuc());
            inputClientAdress.setText(mCliente.getDireccion());
        } else {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {

                if (mProgressDialogFragment.isAdded()) {
                    mProgressDialogFragment.dismiss();
                }

                setmCLientLocation(arg0); //Update Client Location
                MarkerOptions a = new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
                if (mMarkerLocation != null) {
                    mMarkerLocation.remove();
                }
                mMarkerLocation = mMap.addMarker(a);
                mMarkerLocation.setPosition(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
                mMarkerLocation.setTitle(mCliente.getNombreNegocio().toUpperCase());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 16.0f));
            }
        });
    }

    public Location getmCLientLocation() {
        return mCLientLocation;
    }

    public void setmCLientLocation(Location mCLientLocation) {
        this.mCLientLocation = mCLientLocation;
    }

    private void validate() {
        String mClientId = String.valueOf(mCliente.getId());
        if (getmCLientLocation() == null) {
            Utils.getSnackBar(mCoordinator, getString(R.string.error_get_location));
            return;
        }
        mLocateTask = new LocateTask(mClientId, String.valueOf(getmCLientLocation().getLatitude()), String.valueOf(getmCLientLocation().getLongitude()));
        mLocateTask.confirm();
    }

    private class LocateTask extends MyRequest {
        public static final String REQUEST_TAG = "LocateTask";

        private String mClientId;
        private String mLatitude;
        private String mLongitude;

        public LocateTask(String mCliendId, String mLatitude, String mLongitude) {
            this.mClientId = mCliendId;
            this.mLatitude = mLatitude;
            this.mLongitude = mLongitude;
        }

        @Override
        protected void confirm() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LocateActivity.this);
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
                    mLocateTask = null;
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

            // Cancel any pending request before starting a new one
            if (mJsonObjectRequest != null)
                mQueue.getRequestQueue(getApplicationContext()).cancelAll(LocateActivity.LocateTask.REQUEST_TAG);

            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.UPDATE_LOCATION_URL, getParams(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    handleResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    String message = NetworkQueue.handleError(error, getApplicationContext());
                    mJsonObjectRequest.cancel();
                    Utils.getSnackBar(mCoordinator, message);
                }
            });
            mQueue.addToRequestQueue(mJsonObjectRequest, getApplicationContext());
        }

        @Override
        protected JSONObject getParams() {
            mJsonParams = new JSONObject();
            try {
                mJsonParams.put("clientId", mClientId);
                mJsonParams.put("latitude", mLatitude);
                mJsonParams.put("longitude", mLongitude);
                mJsonParams.put("picture", "");
            } catch (JSONException e) {
                System.err.println(e);
            }
            return mJsonParams;
        }

        @Override
        protected void handleResponse(JSONObject response) {
            String message = null;
            int status = -1;
            Log.i(TAG_CLASS, REQUEST_TAG + " | Response: " + response.toString());

            try {
                if (response.has("status")) status = response.getInt("status");
                if (response.has("message")) message = response.getString("message");

                if (status != Constants.RESPONSE_OK) {
                    Utils.getSnackBar(mCoordinator, (message == null) ? getString(R.string.volley_default_error) : message);
                } else {
                    mCliente.setGeolocalizado(true);
                    mCliente.setLongitud(Double.parseDouble(mLongitude));
                    mCliente.setLatitud(Double.parseDouble(mLatitude));
                    ClienteRepository.store(mCliente);
                    successDialog(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.getSnackBar(mCoordinator, getString(R.string.volley_default_error));
            }
        }
    }

    private void successDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LocateActivity.this);
        builder.setIcon(R.mipmap.ic_done_black_24dp);
        builder.setTitle(R.string.dialog_info_title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
