package mapapp.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mapapp.interfaces.TaskCompleteListener;
import mapapp.models.City;
import mapapp.models.Marker;
import mapapp.providers.TakeMarkersInfo;
import mapapp.singletons.MainHandler;
import mapapp.utils.MessageDisplayer;
import mapapp.utils.NameCreator;
import mapapp.utils.OwnDialogFragment;
import mapapp.utils.OwnRendered;
import mapapp.R;

import static mapapp.BuildConfig.DEBUG;
import static mapapp.settings.Constants.FRAGMENT_GPS_TAG;
import static mapapp.settings.Constants.LATLNG_ZOOM;
import static mapapp.settings.Constants.LOCALE;
import static mapapp.settings.Constants.SNACKBAR_DURATION;

public class MapsActivity extends BasicGeoActivity implements OnMapReadyCallback,
        TaskCompleteListener, GoogleMap.OnCameraIdleListener {

    private final String TAG = getClass().getSimpleName();

    private final int LOCATION_PERMISSIONS_REQUEST_CODE = 0;

    private GoogleMap map;
    private ClusterManager<Marker> clusterManager;
    private LocationManager locationManager;
    private TakeMarkersInfo takeInfo;
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetwork;
    private FragmentManager manager;
    private String city;
    private MessageDisplayer messageDisplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        city = "city";

        messageDisplayer = new MessageDisplayer();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        manager = getSupportFragmentManager();

        setUpLocationListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {

            messageDisplayer.showSnackbarMessageWithAction(findViewById(R.id.map),
                    this.getString(R.string.snackbar_internet_text),
                    SNACKBAR_DURATION,
                    this.getString(R.string.snackbar_button_text),
                    (View v) ->
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setUpClusterer(map);
        map.setOnCameraIdleListener(this);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OwnDialogFragment gpsDialogFragment = new OwnDialogFragment();
            gpsDialogFragment.setTitleAndMessage(
                    this.getString(R.string.fragment_gps_title),
                    this.getString(R.string.fragment_gps_text));
            gpsDialogFragment.show(manager, FRAGMENT_GPS_TAG);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setStartCameraPosition();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (map != null){
            LatLng user_LatLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(user_LatLng, LATLNG_ZOOM) );
        }
    }

    @Override
    public void onCameraIdle() {

        CameraPosition cameraPosition = map.getCameraPosition();
        NameCreator nameCreator = new NameCreator();

        activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()){

            Geocoder geoCoder =
                    new Geocoder(getBaseContext(), new Locale(LOCALE));

            if (Geocoder.isPresent()){
                try {
                    List<Address> addresses = geoCoder
                            .getFromLocation(cameraPosition.target.latitude,
                                    cameraPosition.target.longitude, 5);

                    if (addresses.get(0).getLocality() != null && !addresses.get(0).getLocality().equals(city)){

                        city = addresses.get(0).getLocality();

                        takeInfo = new TakeMarkersInfo(this, nameCreator
                                .takeCityName(addresses.get(0).getLocality()));
                        takeInfo.start();

                    } else {
                        clusterManager.cluster();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            messageDisplayer.showToastMessage(this.getString(R.string.toast_internet_text));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE){
            if (grantResults.length != 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpLocationListener();
            } else {
                messageDisplayer.showSnackbarMessageWithAction(findViewById(R.id.map),
                        getString(R.string.snackbar_permission_text),
                        SNACKBAR_DURATION,
                        getString(R.string.snackbar_button_text),
                        (View v) ->
                                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS)));
            }
        }

    }

    @Override
    protected void onPause() {
        if (takeInfo != null){
            takeInfo.interrupt();
        }
        MainHandler.getInstance().removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void setUpClusterer(GoogleMap googleMap) {
        clusterManager = new ClusterManager(this.getApplicationContext(), googleMap);
        clusterManager.setRenderer(new OwnRendered(
                getApplicationContext(), googleMap, clusterManager));
    }

    private void setUpLocationListener() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void setStartCameraPosition(){

        LatLng location;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            location =
                    new LatLng(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            .getLatitude(),
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                    .getLongitude());
        } else {
            location = new LatLng(48.5132, 32.2597);
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, LATLNG_ZOOM));
    }

    private void addItems(String json_string) {

        if (json_string == null){
            messageDisplayer.showToastMessage(this.getString(R.string.toast_no_data_text));
            return;
        }

        Gson gson = (new GsonBuilder()).create();
        City city = gson.fromJson(json_string, City.class);

        for (int i = 0; i < city.getDevices().size(); i++) {

            Marker marker = new Marker(Double.parseDouble(city.getDevices().get(i).getLatitude()),
                    Double.parseDouble(city.getDevices().get(i).getLongitude()),
                    city.getDevices().get(i).getFullAddressEn());

            clusterManager.addItem(marker);
        }
    }

    @Override
    public void onTaskCompleteCallBack(String result) {

        clusterManager.clearItems();
        addItems(result);
        clusterManager.cluster();

        takeInfo = null;
    }
}
