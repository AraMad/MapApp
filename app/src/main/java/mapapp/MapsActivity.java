package mapapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.example.arina.mapapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TakeDataTask.TaskCompleteListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnCameraIdleListener {

    private static final String TAG = "MapApp_log";

    private GoogleMap mMap;
    private ClusterManager<Marker> clusterManager;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private TakeDataTask takeDataTask;
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetwork;
    private String city = "city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        setUpLocationListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FragmentManager manager = getSupportFragmentManager();

        activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()){
            OwnDialogFragment internetDialogFragment = new OwnDialogFragment();
            internetDialogFragment.setTitleAndMessage(
                    this.getString(R.string.fragment_title),
                    this.getString(R.string.fragment_text_internet));
            internetDialogFragment.show(manager, this.getString(R.string.fragment_internet_tag));
        }


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            OwnDialogFragment gpsDialogFragment = new OwnDialogFragment();
            gpsDialogFragment.setTitleAndMessage(
                    this.getString(R.string.fragment_title),
                    this.getString(R.string.fragment_text_gps));
            gpsDialogFragment.show(manager, this.getString(R.string.fragment_gps_tag));
        }

        mGoogleApiClient.connect();
    }

    private void setUpClusterer(GoogleMap googleMap) {
        clusterManager = new ClusterManager(this.getApplicationContext(), googleMap);
        clusterManager.setRenderer(new OwnRendered(
                getApplicationContext(), googleMap, clusterManager));
    }

    private void addItems(String json_string) {

        JSONArray jsonArray;

        try {
            jsonArray = new JSONObject(json_string).getJSONArray("devices");

            // Add cluster items
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                Marker marker = new Marker(item.getDouble("latitude"), item.getDouble("longitude"),
                        item.getString("fullAddressEn"));

                clusterManager.addItem(marker);
            }

        } catch (JSONException e) {
            Log.i(TAG, e.toString());
        }
    }

    private void setUpLocationListener() {

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10,
                this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpClusterer(mMap);
        mMap.setOnCameraIdleListener(this);

        try{
            LatLng last_location = new LatLng(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude(),
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(last_location, 15));
        } catch (Exception e){
            LatLng Kirovograd = new LatLng(48.5132, 32.2597);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Kirovograd, 10) );
        }
    }

    @Override
    public void onTaskCompleteCallBack(String result) {

        clusterManager.clearItems();
        addItems(result);
        clusterManager.cluster();

        takeDataTask = null;
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mMap != null){
            LatLng user_LatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_LatLng, 15) );
        }
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

    @Override
    public void onCameraIdle() {

        CameraPosition cameraPosition = mMap.getCameraPosition();

        NameCreator nameCreator = new NameCreator();

        activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()){

            Geocoder geoCoder =
                    new Geocoder(getBaseContext(), new Locale(this.getString(R.string.locale)));

            if (Geocoder.isPresent()){
                try {
                    List<Address> addresses = geoCoder
                            .getFromLocation(cameraPosition.target.latitude,
                                    cameraPosition.target.longitude, 5);
                    try {
                        if (addresses.get(0).getLocality() != null && !addresses.get(0).getLocality().equals(city)){
                            city = addresses.get(0).getLocality();
                            takeDataTask = new TakeDataTask(getApplicationContext(), this);
                            takeDataTask.execute(nameCreator.takeCitName(addresses.get(0).getLocality()));
                        } else {
                            clusterManager.cluster();
                        }
                    } catch (Exception e){
                        Log.i(TAG, e.toString());
                    }
                }
                catch (IOException e) {
                    Log.i(TAG, e.toString());
                }
            }
        } else {
            Toast.makeText(this, this.getString(R.string.toast_internet_text), Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
