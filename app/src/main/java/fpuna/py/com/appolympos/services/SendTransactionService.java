package fpuna.py.com.appolympos.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.network.MyRequest;
import fpuna.py.com.appolympos.network.NetworkQueue;
import fpuna.py.com.appolympos.repository.TransactionRepository;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class SendTransactionService extends IntentService {
    private static final String TAG_CLASS = SendTransactionService.class.getName();
    private static final String EXTRA_PARAM_REQUEST = "PARAMS";
    private static final String EXTRA_PARAM_URL = "URL";
    private static final String EXTRA_PARAM_ID_TRANSACTION = "ID_TRANSACTION";
    private static final String ACTION_SEND = "SEND";

    private NetworkQueue mQueue;
    private TaskSendTransaction mTaskSendTransaction;
    private static Context mContext;

    public SendTransactionService() {
        super("SendTransactionService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSend(Context context, String paramRequest, String urlRequest, long transactioId) {
        mContext = context;
        Intent intent = new Intent(context, SendTransactionService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_PARAM_REQUEST, paramRequest);
        intent.putExtra(EXTRA_PARAM_URL, urlRequest);
        intent.putExtra(EXTRA_PARAM_ID_TRANSACTION, transactioId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND.equals(action)) {
                final String params = intent.getStringExtra(EXTRA_PARAM_REQUEST);
                final String url = intent.getStringExtra(EXTRA_PARAM_URL);
                final long transactionId = intent.getLongExtra(EXTRA_PARAM_ID_TRANSACTION, 0);
                mQueue = new NetworkQueue(mContext);
                mTaskSendTransaction = new TaskSendTransaction(params, url, transactionId);
                mTaskSendTransaction.execute();
            }
        }
    }

    private class TaskSendTransaction extends MyRequest {
        public static final String REQUEST_TAG = "TaskSendTransaction";
        private String requestParams;
        private String url;
        private long idTransaction;


        public TaskSendTransaction(String requestParams, String url, long idTransaction) {
            this.requestParams = requestParams;
            this.url = url;
            this.idTransaction = idTransaction;
        }


        @Override
        protected void confirm() {

        }

        @Override
        protected void execute() {
            if (mJsonObjectRequest != null)
                mQueue.getRequestQueue(getApplicationContext()).cancelAll(SendTransactionService.TaskSendTransaction.REQUEST_TAG);

            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url,
                    getParams(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            handleResponse(response);
                            mJsonObjectRequest.cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String message = NetworkQueue.handleError(error, getApplicationContext());
                            Utils.getToast(mContext, message);
                        }
                    });
            Log.d(TAG_CLASS, "In Queue " + String.valueOf(getParams()));
            mJsonObjectRequest.setTag(SendTransactionService.TaskSendTransaction.REQUEST_TAG);
            mQueue.addToRequestQueue(mJsonObjectRequest, getApplicationContext());
        }

        @Override
        protected JSONObject getParams() {
            try {
                mJsonParams = new JSONObject(requestParams);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mJsonParams;
        }

        @Override
        protected void handleResponse(JSONObject response) {
            int status = -1;
            String message = "No Message";
            Log.v(REQUEST_TAG, "RESPONSE | " + String.valueOf(response));

            try {
                if (response.has("status"))
                    status = response.getInt("status");
                if (response.has("message"))
                    message = response.getString("message");
                if (status == Constants.RESPONSE_OK) {
                    Transactions transactions = TransactionRepository.getById(idTransaction);
                    transactions.setStatus(Constants.TRANSACTION_SEND);
                    transactions.setObservation(message);
                    transactions.setUpdatedAt(new Date());
                    TransactionRepository.store(transactions);
                } else {
                    Transactions transactions = TransactionRepository.getById(idTransaction);
                    transactions.setStatus(Constants.TRANSACTION_ERROR);
                    transactions.setObservation(message);
                    transactions.setUpdatedAt(new Date());
                    TransactionRepository.store(transactions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
