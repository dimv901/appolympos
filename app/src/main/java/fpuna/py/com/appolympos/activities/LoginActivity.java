package fpuna.py.com.appolympos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
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

/**
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG_CLASS = LoginActivity.class.getName();

    // UI references.
    private TextInputEditText mInputUser;
    private TextInputEditText mInputPassword;
    private CoordinatorLayout mCoordinator;
    private ImageView mLogo;
    private NetworkQueue mQueue;
    private LoginTask mLoginTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean isLogged = AppPreferences.getAppPreferences(this).getBoolean(AppPreferences.KEY_PREFERENCE_LOGGED_IN, false);
        if (isLogged) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            setupView();
        }
    }

    private void setupView() {
        // Set up the login form.
        mCoordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        mInputUser = (TextInputEditText) findViewById(R.id.inputUser);
        mInputPassword = (TextInputEditText) findViewById(R.id.inputPassword);
        mLogo = (ImageView) findViewById(R.id.logo);

        Button mButtonLogin = (Button) findViewById(R.id.buttonLogin);
        mButtonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mQueue = new NetworkQueue(getApplicationContext());

        slide(mLogo);
    }

    public void slide(View image) {
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        image.startAnimation(animation1);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mInputUser.setError(null);
        mInputPassword.setError(null);

        // Store values at the time of the login attempt.
        String user = mInputUser.getText().toString();
        String password = mInputPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid user, if the user entered one.
        if (TextUtils.isEmpty(user)) {
            mInputUser.setError(getString(R.string.error_empty_field));
            focusView = mInputUser;
            cancel = true;
        }


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mInputPassword.setError(getString(R.string.error_empty_field));
            focusView = mInputPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginTask = new LoginTask(user, password);
            mLoginTask.execute();
        }
    }

    private class LoginTask extends MyRequest {
        public static final String REQUEST_TAG = "LoginTask";
        String user;
        String password;

        private LoginTask(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        protected void confirm() {

        }

        @Override
        protected void execute() {
            mProgressDialog = ProgressDialogFragment.newInstance(getApplicationContext());
            mProgressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);

            // Cancel any pending request before starting a new one
            if (mJsonObjectRequest != null)
                mQueue.getRequestQueue(getApplicationContext()).cancelAll(LoginActivity.LoginTask.REQUEST_TAG);

            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOGIN_URL, getParams(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    handleResponse(response);
                }
            }, new ErrorListener() {
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
                mJsonParams.put("usuario", user);
                mJsonParams.put("password", password);
            } catch (JSONException e) {
                System.err.println(e);
            }
            return mJsonParams;
        }

        @Override
        protected void handleResponse(JSONObject response) {
            String message = null;
            String user = null;
            int status = -1;
            Log.i(TAG_CLASS, REQUEST_TAG + " | Response: " + response.toString());

            try {
                if (response.has("status")) status = response.getInt("status");
                if (response.has("message")) message = response.getString("message");

                if (status != Constants.RESPONSE_OK) {
                    Utils.getSnackBar(mCoordinator, (message == null) ? getString(R.string.volley_default_error) : message);
                } else {
                    if (response.has("usuario")) user = response.getString("usuario");
                    AppPreferences.getAppPreferences(getApplicationContext()).edit().putBoolean(AppPreferences.KEY_PREFERENCE_LOGGED_IN, true).apply();
                    AppPreferences.getAppPreferences(getApplicationContext()).edit().putString(AppPreferences.KEY_PREFERENCE_USUARIO, user).apply();
                    Utils.getToast(getApplicationContext(), message);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.getSnackBar(mCoordinator, getString(R.string.volley_default_error));
            }

        }
    }


}

