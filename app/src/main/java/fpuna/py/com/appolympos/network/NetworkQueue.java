package fpuna.py.com.appolympos.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fpuna.py.com.appolympos.R;

/**
 * Created by Diego on 5/30/2017.
 */

public class NetworkQueue {

    private static final String TAG_CLASS = NetworkQueue.class.getName();
    private static DefaultRetryPolicy mDefaultRetryPolicy;
    private final Context mContext;
    private static RequestQueue mRequestQueue;

    private static final int DEFAULT_REQUEST_TIMEOUT = 60000; //30 Seconds
    private static final int DEFAULT_RETRY_INTENTS = 0; //0 Retry

    public NetworkQueue(Context context) {
        mContext = context;
    }


    public static synchronized RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }


    public static DefaultRetryPolicy getDefaultRetryPolicy() {

        if (mDefaultRetryPolicy == null) {
            // Create a singleton Default Retry Policy
            // for the requests
            mDefaultRetryPolicy = new DefaultRetryPolicy(DEFAULT_REQUEST_TIMEOUT, DEFAULT_RETRY_INTENTS, 0);
        }

        return mDefaultRetryPolicy;

    }

    public static String handleError(VolleyError error, Context context) {
        NetworkResponse response = error.networkResponse;
        String message = "No message";
        StringBuilder sb = new StringBuilder();
        int statusCode;

        if (response == null) {
            Log.d(TAG_CLASS, "Network Error " + error);
            if (error instanceof NoConnectionError) {
                message = context.getResources().getString(R.string.volley_no_connection_error);
            } else if (error instanceof NetworkError) {
                message = context.getResources().getString(R.string.volley_network_error);
            } else if (error instanceof ServerError) {
                message = context.getResources().getString(R.string.volley_server_error);
            } else if (error instanceof AuthFailureError) {
                message = context.getResources().getString(R.string.volley_authentication_error);
            } else if (error instanceof ParseError) {
                message = context.getResources().getString(R.string.volley_parse_error);
            } else if (error instanceof TimeoutError) {
                message = context.getResources().getString(R.string.volley_time_out_error);
            }
        } else {

            try {
                JSONObject jsonObject = new JSONObject(new String(response.data));
                statusCode = response.statusCode;
                if (jsonObject.has("message")) {
                    sb.append(jsonObject.getString("message"));
                    sb.append(" code: ").append(statusCode);
                }
                message = sb.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                message = context.getString(R.string.volley_default_error);
            }
        }
        Log.w(TAG_CLASS, message);
        return message;
    }


}
