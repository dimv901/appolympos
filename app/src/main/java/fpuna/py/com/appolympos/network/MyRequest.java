package fpuna.py.com.appolympos.network;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import fpuna.py.com.appolympos.dialogs.ProgressDialogFragment;

/**
 * Created by Diego on 5/30/2017.
 */

public abstract class MyRequest {

    protected ProgressDialogFragment mProgressDialog;
    protected JsonObjectRequest mJsonObjectRequest;
    protected JSONObject mJsonParams;

    protected abstract void confirm();

    protected abstract void execute();

    protected abstract JSONObject getParams();

    protected abstract void handleResponse(JSONObject response);

    public JsonObjectRequest getJsonObjectRequest() {
        return mJsonObjectRequest;
    }

    public JSONObject getJsonParams() {
        return mJsonParams;
    }

}
