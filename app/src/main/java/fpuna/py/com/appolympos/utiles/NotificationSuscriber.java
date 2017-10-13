package fpuna.py.com.appolympos.utiles;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fpuna.py.com.appolympos.services.SyncService;

/**
 * Created by Diego on 7/17/2017.
 */

public class NotificationSuscriber {
    public static final String TAG_CLASS = NotificationSuscriber.class.getName();

    private static final String SYNC_CLIENTS = "sync_client";
    private static final String SYNC_STOCK = "sync_stock";

    private PNConfiguration pnConfiguration;
    private PubNub pubnub;
    private Context context;
    public static NotificationSuscriber instance;
    private SubscribeCallback subscribeCallback;

    public static NotificationSuscriber getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationSuscriber(context);
        }
        return instance;
    }

    public NotificationSuscriber(Context context) {
        this.context = context;
        pnConfiguration = new PNConfiguration();
        getPnConfiguration().setPublishKey("pub-c-053b5e9a-5f62-4061-b783-d1d448b2ad1f");
        getPnConfiguration().setSubscribeKey("sub-c-84da7594-6aed-11e7-898a-02ee2ddab7fe");
        getPnConfiguration().setSecure(false);
        pubnub = new PubNub(getPnConfiguration());
        initListenChannel();
    }

    private void initListenChannel() {
        pubnub.subscribe()
                .channels(Arrays.asList("stock")) // subscribe to channels
                .execute();

        subscribeCallback = new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getOperation() != null) {
                    switch (status.getOperation()) {
                        // let's combine unsubscribe and subscribe handling for ease of use
                        case PNSubscribeOperation:
                        case PNUnsubscribeOperation:
                            // note: subscribe statuses never have traditional
                            // errors, they just have categories to represent the
                            // different issues or successes that occur as part of subscribe
                            switch (status.getCategory()) {
                                case PNConnectedCategory:
                                    // this is expected for a subscribe, this means there is no error or issue whatsoever
                                case PNReconnectedCategory:
                                    // this usually occurs if subscribe temporarily fails but reconnects. This means
                                    // there was an error but there is no longer any issue
                                case PNDisconnectedCategory:
                                    // this is the expected category for an unsubscribe. This means there
                                    // was no error in unsubscribing from everything
                                case PNUnexpectedDisconnectCategory:
                                    // this is usually an issue with the internet connection, this is an error, handle appropriately
                                case PNAccessDeniedCategory:
                                    // this means that PAM does allow this client to subscribe to this
                                    // channel and channel group configuration. This is another explicit error
                                default:
                                    // More errors can be directly specified by creating explicit cases for other
                                    // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                            }

                        case PNHeartbeatOperation:
                            // heartbeat operations can in fact have errors, so it is important to check first for an error.
                            // For more information on how to configure heartbeat notifications through the status
                            // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                            if (status.isError()) {
                                // There was an error with the heartbeat operation, handle here
                            } else {
                                // heartbeat operation was successful
                            }
                        default: {
                            // Encountered unknown status type
                        }
                    }
                } else {
                    // After a reconnection see status.getCategory()
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult messageResult) {
                String data = "";
                try {
                    JsonElement jEl = messageResult.getMessage();
                    data = jEl.getAsString();
                    Log.d(TAG_CLASS, "RECEIVED NOTIFICATION " + data.trim());
                    startSync(data.trim());
                } catch (Exception j) {
                    j.printStackTrace();
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        };
        pubnub.addListener(subscribeCallback);
    }

    public PNConfiguration getPnConfiguration() {
        return pnConfiguration;
    }

    public void startSync(String itemToSync) {
        List<String> urls = new ArrayList<>();
        if (itemToSync.equalsIgnoreCase(SYNC_STOCK)) {
            urls.add(Constants.SYNC_PRODUCTO_URL);
        } else if (itemToSync.equalsIgnoreCase(SYNC_CLIENTS)) {
            urls.add(Constants.SYNC_CLIENTE_URL);
        } else {
            Log.d(TAG_CLASS, "Option not match " + itemToSync);
            return;
        }

        String[] urlActions = new String[urls.size()];
        urlActions = urls.toArray(urlActions);
        final JSONObject params = new JSONObject();
        try {
            params.put(SyncService.JSON_PARAM_USUARIO, AppPreferences.getAppPreferences(context).getString(AppPreferences.KEY_PREFERENCE_USUARIO, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] paramsList = new String[urlActions.length];
        for (int i = 0; i < urlActions.length; i++) {
            paramsList[i] = params.toString();
        }
        SyncService.startActionSync(context, urlActions, paramsList);
    }

}
