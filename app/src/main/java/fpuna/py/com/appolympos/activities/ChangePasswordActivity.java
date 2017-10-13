package fpuna.py.com.appolympos.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.dialogs.ProgressDialogFragment;
import fpuna.py.com.appolympos.network.MyRequest;
import fpuna.py.com.appolympos.network.NetworkQueue;
import fpuna.py.com.appolympos.utiles.AppPreferences;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;


public class ChangePasswordActivity extends AppCompatActivity {

    private CoordinatorLayout mCoordinator;
    private TextInputEditText mInputPassword1;
    private TextInputEditText mInputPassword2;
    private NetworkQueue mQueue;
    private TaskChangePassword mTaskChangePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.change_pin_coordinator);
        mInputPassword1 = (TextInputEditText) findViewById(R.id.change_pin_1);
        mInputPassword2 = (TextInputEditText) findViewById(R.id.change_pin_2);

        Button buttonSend = (Button) findViewById(R.id.change_pin_ok);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        Button buttonCancel = (Button) findViewById(R.id.change_pin_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mQueue = new NetworkQueue(getApplicationContext());
    }

    private void validateFields() {
        String mPassword1 = "";
        String mPassword2 = "";
        View mFocusView = null;

        mInputPassword1.setError(null);
        mInputPassword2.setError(null);

        mPassword1 = mInputPassword1.getText().toString().trim();
        mPassword2 = mInputPassword2.getText().toString().trim();

        if (TextUtils.isEmpty(mPassword1)) {
            mInputPassword1.setError(getString(R.string.error_field_required));
            mFocusView = mInputPassword1;
            mFocusView.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mPassword2)) {
            mInputPassword1.setError(getString(R.string.error_field_required));
            mFocusView = mInputPassword1;
            mFocusView.requestFocus();
            return;
        }

        if (!mPassword1.equals(mPassword2)) {
            mInputPassword1.setError(getString(R.string.error_pin_not_match));
            mFocusView = mInputPassword1;
            mFocusView.requestFocus();
            return;
        }

        mTaskChangePassword = new TaskChangePassword(mPassword1);
        mTaskChangePassword.confirm();
    }

    private class TaskChangePassword extends MyRequest {

        public static final String REQUEST_TAG = "TaskChangePassword";
        private String mNewPin;

        public TaskChangePassword(String newPin) {
            mNewPin = newPin;
        }

        @Override
        protected void confirm() {
            execute();
        }


        @Override
        protected void execute() {

            mProgressDialog = ProgressDialogFragment.newInstance(getApplicationContext());
            mProgressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);

            // Cancel any pending request before starting a new one
            if (mJsonObjectRequest != null)
                mQueue.getRequestQueue(getApplicationContext()).cancelAll(ChangePasswordActivity.TaskChangePassword.REQUEST_TAG);

            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.RESET_PIN_URL,
                    getParams(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            handleResponse(response);
                            mTaskChangePassword = null;
                            mJsonObjectRequest.cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialog.dismiss();
                            String message = mQueue.handleError(error, getApplicationContext());
                            Utils.getSnackBar(mCoordinator, message);
                            mJsonObjectRequest.cancel();
                        }
                    });
            mJsonObjectRequest.setTag(ChangePasswordActivity.TaskChangePassword.REQUEST_TAG);
            mQueue.addToRequestQueue(mJsonObjectRequest, getApplicationContext());
        }


        @Override
        protected JSONObject getParams() {
            mJsonParams = new JSONObject();
            try {
                mJsonParams.put("usuario", AppPreferences.getAppPreferences(getApplicationContext()).getString(AppPreferences.KEY_PREFERENCE_USUARIO, null));
                mJsonParams.put("password", mNewPin);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mJsonParams;
        }

        @Override
        protected void handleResponse(JSONObject result) {
            int status = -1;
            String message = "No Message";

            Log.v(REQUEST_TAG, "RESPONSE | " + String.valueOf(result));

            try {
                if (result.has("status"))
                    status = result.getInt("status");


                if (result.has("message"))
                    message = result.getString("message");


                if (status != Constants.RESPONSE_OK) {
                    Utils.getSnackBar(mCoordinator, (message == null || message == "") ? getString(R.string.volley_default_error) : message);
                } else {
                    Utils.endSession(getApplicationContext(), message);
                }
            } catch (JSONException jEx) {
                jEx.printStackTrace();
                Utils.getSnackBar(mCoordinator, jEx.getMessage());
            }
        }
    }
}
