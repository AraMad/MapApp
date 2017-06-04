package mapapp.utils;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import mapapp.models.Marker;

/**
 * Created by Arina on 29.12.2016
 */

public class OwnRendered extends DefaultClusterRenderer<Marker> {

    public OwnRendered(Context context, GoogleMap map,
                       ClusterManager<Marker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Marker item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        markerOptions.title(item.getTitle());
    }
}
