package fpuna.py.com.appolympos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import fpuna.py.com.appolympos.loaders.NoSaleLoader;

/**
 * Created by Diego on 6/28/2017.
 */

public class NoSaleObserver extends BroadcastReceiver {

    public static final String ACTION_LOAD_NO_SALE = "fpuna.py.com.appolympos.receivers.LOAD_NO_SALE";
    private NoSaleLoader mLoader;

    public NoSaleObserver(NoSaleLoader loader) {
        mLoader = loader;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_LOAD_NO_SALE);
        mLoader.getContext().registerReceiver(this, mFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}
