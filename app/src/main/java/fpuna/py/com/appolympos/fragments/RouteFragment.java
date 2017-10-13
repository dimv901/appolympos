package fpuna.py.com.appolympos.fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.loaders.MarkerLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link} interface
 * to handle interaction events.
 * Use the {@link RouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteFragment extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<List<MarkerOptions>>, LocationListener {


    private View rootView;
    private GoogleMap map;
    private MapView mMapView;
    private LocationManager mLocationManager;

    public RouteFragment() {
        // Required empty public constructor
    }

    public static RouteFragment newInstance() {
        RouteFragment fragment = new RouteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_route, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (context instanceof OnMarkerSelectedListener) {
          //  mListener = (OnMarkerSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMarkerSelectedListener");
        }*/
    }


    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        setMap(googleMap);
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, false);
        Location location = getLastKnownLocation();
        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        }
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void setupMarkers(List<MarkerOptions> markers) {

        if (getMap() != null) {
            getMap().clear();
        }

        for (MarkerOptions m : markers) {
            getMap().addMarker(m);
        }
        // Position the map.
        //getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        //  mClusterManager = new ClusterManager<MyItem>(this, getMap());

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.


        // Add cluster items (markers) to the cluster manager.
        //     addItems();
    }


    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        setupMyLocation(lat, lng);
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

    private void setupMyLocation(double latitude, double longitude) {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
    }

    @Override
    public Loader<List<MarkerOptions>> onCreateLoader(int id, Bundle args) {
        return new MarkerLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<MarkerOptions>> loader, List<MarkerOptions> data) {
        setupMarkers(data);
    }

    @Override
    public void onLoaderReset(Loader<List<MarkerOptions>> loader) {

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMarkerSelectedListener {
        void onMarkerSelectedListener(Uri uri);
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
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
