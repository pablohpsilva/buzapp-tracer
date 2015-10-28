package aloeio.buzapp_tracer.app.Services;

import aloeio.buzapp_tracer.app.Models.BusInfoSingleton;
import aloeio.buzapp_tracer.app.R;
import aloeio.buzapp_tracer.app.Fragments.MapFragment;
import aloeio.buzapp_tracer.app.Services.Overrides.MyLocationProvider;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;

public class MapManagerService {
    private final MapFragment fragment;
    private MyLocationProvider buzappMyLocationProvider;
    private MapView mapView;
    private MyLocationService myLocationService;

    final private int CAMERA_ZOOM = 16;


    public MapManagerService(final MapFragment fragment){
        this.fragment = fragment;
        this.mapView = (MapView) fragment.getActivity().findViewById(R.id.home_mapview);
        this.setOSMDefaults();
    }

    public void drawUserLocation(){
        if(buzappMyLocationProvider == null) {
            buzappMyLocationProvider = new MyLocationProvider(fragment, BusInfoSingleton.getInstance().getRoute(), BusInfoSingleton.getInstance().getPlate());
        }

        if(this.myLocationService == null) {
            this.myLocationService = new MyLocationService(fragment, buzappMyLocationProvider);
        } else {
            this.myLocationService.centerMyLocation();
        }

        BackgroundService.setLocationProvider(buzappMyLocationProvider);

        this.myLocationService.followUser();
    }

    private void setOSMDefaults(){
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

        mapView.postInvalidate();
    }
}