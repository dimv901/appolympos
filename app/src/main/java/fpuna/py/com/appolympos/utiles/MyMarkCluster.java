package fpuna.py.com.appolympos.utiles;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Diego on 9/27/2017.
 */

public class MyMarkCluster implements ClusterItem {
    private final LatLng mPosition;
    private String description;

    public MyMarkCluster(double lat, double lng, String description) {
        mPosition = new LatLng(lat, lng);
        this.description = description;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}