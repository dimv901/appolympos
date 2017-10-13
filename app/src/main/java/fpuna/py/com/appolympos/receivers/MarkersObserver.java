package fpuna.py.com.appolympos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import fpuna.py.com.appolympos.loaders.MarkerLoader;

/**
 * Created by Diego on 9/28/2017.
 */

public class MarkersObserver extends BroadcastReceiver {

    public static final String ACTION_LOAD_MARKERS = "fpuna.py.com.appolympos.receivers.LOAD_MARKERS";
    private MarkerLoader mLoader;

    public MarkersObserver(MarkerLoader loader) {
        mLoader = loader;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_LOAD_MARKERS);
        mLoader.getContext().registerReceiver(this, mFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}
