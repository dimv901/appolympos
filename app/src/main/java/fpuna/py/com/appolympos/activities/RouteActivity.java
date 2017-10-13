package fpuna.py.com.appolympos.activities;

import android.content.Context;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.ClientsAdapter;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.utiles.Utils;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private Clientes mCliente;
    private Marker mMarkOrigin;
    private Marker mMarkDestination;
    private LatLng mClientLatLng;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setData();
    }

    private void setData() {
        if (!getIntent().hasExtra(ClientsAdapter.CLIENT_ID)) {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }

        if (getIntent().getExtras().containsKey(ClientsAdapter.CLIENT_ID)) {
            long clientId = getIntent().getExtras().getLong(ClientsAdapter.CLIENT_ID);
            mCliente = ClienteRepository.getById(clientId);
            if (mCliente == null) {
                Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
                finish();
                return;
            }
            mClientLatLng = new LatLng(mCliente.getLatitud(), mCliente.getLongitud());
        } else {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, false);
        Location location = getLastKnownLocation();
        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        }
    }

    public void draw(LatLng mOrigin, LatLng mDestination) {
        DrawRouteMaps.getInstance(this).draw(mOrigin, mDestination, mMap);
        mMarkOrigin = mMap.addMarker(new MarkerOptions()
                .position(mOrigin)
                .title(getString(R.string.label_you)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMarkOrigin.setTag(0);

        mMarkDestination = mMap.addMarker(new MarkerOptions()
                .position(mDestination)
                .title(mCliente.getNombreNegocio()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMarkDestination.setTag(0);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(mOrigin)
                .include(mDestination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 16.0f));
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        draw(new LatLng(lat, lng), mClientLatLng);
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


    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
