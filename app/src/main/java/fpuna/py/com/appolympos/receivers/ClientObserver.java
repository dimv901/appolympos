package fpuna.py.com.appolympos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import fpuna.py.com.appolympos.loaders.ClientsLoader;

/**
 * Created by Diego on 6/12/2017.
 */

public class ClientObserver extends BroadcastReceiver {

    public static final String ACTION_LOAD_CLIENTS = "fpuna.py.com.appolympos.receivers.LOAD_CLIENTS";
    private ClientsLoader mLoader;

    public ClientObserver(ClientsLoader loader) {
        mLoader = loader;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_LOAD_CLIENTS);
        mLoader.getContext().registerReceiver(this, mFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}
