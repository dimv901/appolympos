package fpuna.py.com.appolympos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import fpuna.py.com.appolympos.loaders.OrdersLoader;

/**
 * Created by Diego on 7/16/2017.
 */

public class OrderObserver extends BroadcastReceiver {

    public static final String ACTION_LOAD_ORDERS = "fpuna.py.com.appolympos.receivers.LOAD_ORDERS";
    private OrdersLoader mLoader;

    public OrderObserver(OrdersLoader loader) {
        mLoader = loader;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_LOAD_ORDERS);
        mLoader.getContext().registerReceiver(this, mFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}
