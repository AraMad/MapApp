package mapapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Arina on 28.12.2016
 */

public class Marker implements ClusterItem {

    private LatLng mPosition;
    private String title;

    public Marker(double lat, double lng, String title) {
        mPosition = new LatLng(lat, lng);
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
