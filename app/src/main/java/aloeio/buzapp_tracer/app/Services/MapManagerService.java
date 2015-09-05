package aloeio.buzapp_tracer.app.Services;

import aloeio.buzapp_tracer.app.MainActivity;
import aloeio.buzapp_tracer.app.Models.BusInfo;
import aloeio.buzapp_tracer.app.R;
import aloeio.buzapp_tracer.app.Fragments.MapFragment;
import aloeio.buzapp_tracer.app.Services.Overrides.MyLocationProvider;
import aloeio.buzapp_tracer.app.Services.Overrides.MyMarker;
import android.graphics.drawable.Drawable;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Created by pablohenrique on 4/16/15.
 */
public class MapManagerService {
    private final MapFragment fragment;
    private MyMarker busMarker, busStopMarker, stepMarker;
    private MyLocationProvider buzappMyLocationProvider;
    private MapView mapView;
    private MyLocationService myLocationService;

    final private int CAMERA_ZOOM = 16;
//    final private int CAMERA_ZOOM = 15;
    final private String MAPQUEST_API_KEY = "Fmjtd%7Cluu82quznu%2C2w%3Do5-94tgg4";


    public MapManagerService(final MapFragment fragment){
        this.fragment = fragment;
        this.mapView = (MapView) fragment.getActivity().findViewById(R.id.home_mapview);
        this.setOSMDefaults();
    }

    public MyLocationNewOverlay getUserLocationOverlay(){
        return this.myLocationService.getMyLocationOverlay();
    }

    public void drawUserLocation(){
        if(buzappMyLocationProvider == null)
            buzappMyLocationProvider = new MyLocationProvider(fragment, BusInfo.getInstance().getRoute());

        if(this.myLocationService == null)
            this.myLocationService = new MyLocationService(fragment,buzappMyLocationProvider);
        else
            this.myLocationService.centerMyLocation();

        BackgroundService.setLocationProvider(buzappMyLocationProvider);
        this.myLocationService.followUser();
    }

    private void setOSMDefaults(){
        setOSMDefaults(new GeoPoint(-18.9106433, -48.3239163));
    }

    private void setOSMDefaults(GeoPoint startingPoint){
        mapView.setBuiltInZoomControls(false);
        mapView.setKeepScreenOn(true);
        mapView.setSaveEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(false);
        mapView.setTileSource(new XYTileSource("MapQuest",
                ResourceProxy.string.mapquest_osm, 0, 17, 256, ".jpg", new String[]{
                "http://otile1.mqcdn.com/tiles/1.0.0/map/",
                "http://otile2.mqcdn.com/tiles/1.0.0/map/",
                "http://otile3.mqcdn.com/tiles/1.0.0/map/",
                "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));

        mapView.getController().setZoom(CAMERA_ZOOM);
        setOSMCenter(startingPoint);

        mapView.postInvalidate();
    }

    private void setOSMCenter(GeoPoint startingPoint){
        mapView.getController().setCenter(startingPoint);
    }

    /**
     * Private methods that handles Buzapp objects
     */


    private void createStopsMarker(){
        if(this.busStopMarker == null)
            createStopMarker();
    }

    private void createBusMarker(){
        busMarker = new MyMarker(this.mapView);
        createMarkersDefault(busMarker, fragment.getResources().getDrawable(R.mipmap.ic_bus));
        System.gc();
    }

    private void createStopMarker(){
        busStopMarker = new MyMarker(this.mapView);
        createMarkersDefault(busStopMarker, fragment.getResources().getDrawable(R.mipmap.ic_stop_sign));
        System.gc();
    }

    private void createStepMarker(){
        stepMarker = new MyMarker(this.mapView);
        createMarkersDefault(stepMarker, fragment.getResources().getDrawable(R.mipmap.ic_step));
        System.gc();
    }

    private void createMarkersDefault(MyMarker marker, Drawable icon){
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(icon);
    }
}
