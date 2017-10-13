package fpuna.py.com.appolympos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import fpuna.py.com.appolympos.loaders.ProductsLoader;

/**
 * Created by Diego on 7/15/2017.
 */

public class ProductObserver extends BroadcastReceiver {

    public static final String ACTION_LOAD_PRODUCTS = "fpuna.py.com.appolympos.receivers.LOAD_PRODUCTS";
    private ProductsLoader mLoader;

    public ProductObserver(ProductsLoader loader) {
        mLoader = loader;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_LOAD_PRODUCTS);
        mLoader.getContext().registerReceiver(this, mFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {


    }
}
