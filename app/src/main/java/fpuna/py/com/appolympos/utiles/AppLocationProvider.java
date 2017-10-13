package fpuna.py.com.appolympos.utiles;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import fpuna.py.com.appolympos.dialogs.GpsDialogFragment;


/**
 * Created by Diego on 4/27/2017.
 */

public class AppLocationProvider implements LocationListener {
    private static final String TAG_CLASS = AppLocationProvider.class.getName();
    private Context mContext;
    private Activity mActivity;
    private LocationManager mLocationManager;

    // Minimum time fluctuation for next update
    private static final long TIME = 0; //1 Minute
    // Minimum distance fluctuation for next update
    private static final long DISTANCE = 0; // 10 meters


    public AppLocationProvider(Activity activity) {
        mContext = activity.getApplicationContext();
        mActivity = activity;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        getLocation();
    }

    public Location getLocation() {
        Location mlocation = null;

        try {
            // Check GPS status
            boolean isGpsLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGpsLocationEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME, DISTANCE, this);
                mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (mlocation != null) {
                    return mlocation;
                }
            }

            if (isNetworkLocationEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME, DISTANCE, this);
                mlocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (mlocation != null) {
                    return mlocation;
                }
            }

            if (!isGpsLocationEnabled) {
                activateGpsDialog();
                return null;
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }
        Log.w(TAG_CLASS, "Retreive null location");
        return mlocation;
    }

    private void activateGpsDialog() {
        FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
        GpsDialogFragment newFragment = GpsDialogFragment.newInstance();
        newFragment.show(ft, GpsDialogFragment.TAG_CLASS);
    }

    public void stopGPS() {
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(this);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
