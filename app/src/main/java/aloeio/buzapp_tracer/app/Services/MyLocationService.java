package aloeio.buzapp_tracer.app.Services;

import aloeio.buzapp_tracer.app.Models.BusInfoSingleton;
import aloeio.buzapp_tracer.app.R;
import aloeio.buzapp_tracer.app.Fragments.MapFragment;
import aloeio.buzapp_tracer.app.Services.Overrides.MyLocationProvider;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Created by pablohenrique on 2/6/15.
 */
public class MyLocationService {
    private MapView map = null;
    private MyLocationNewOverlay myLocationOverlay = null;
    private IMyLocationProvider myLocationProvider;
    private final MapFragment fragment;
    private boolean isSearching = false;

    public MyLocationService(final MapFragment fragment, IMyLocationProvider mMyLocationProvider){
        this.fragment = fragment;
        this.myLocationProvider = mMyLocationProvider;
        map = (MapView) fragment.getActivity().findViewById(R.id.home_mapview);
        drawMyLocation();
        followUser();
    }

    public void drawMyLocation(){
        enableUserLocation();
    }

    public MyLocationNewOverlay getMyLocationOverlay(){
        return myLocationOverlay;
    }

    public void centerMyLocation(){
        if(myLocationOverlay.getMyLocation() != null) {
            map.getController().animateTo(myLocationOverlay.getMyLocation());
        } else if(!isSearching) {
            enableUserLocation();
        }
    }

    public void followUser(){
        myLocationOverlay.enableFollowLocation();
        map.postInvalidate();
    }

    private void enableUserLocation(){
        myLocationOverlay = (myLocationProvider == null) ?
                new MyLocationNewOverlay(fragment.getActivity(), map) :
                new MyLocationNewOverlay(fragment.getActivity(), new MyLocationProvider(this.fragment, BusInfoSingleton.getInstance().getRoute(), BusInfoSingleton.getInstance().getPlate()), map);

        map.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();

        isSearching = true;

        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                if (getMyLocationOverlay() == null) {
                    isSearching = false;
                    myLocationOverlay = new MyLocationNewOverlay(fragment.getActivity(), map);
                    return;
                }

                if(myLocationOverlay.getMyLocation() != null) {
                    map.getController().animateTo(myLocationOverlay.getMyLocation());
                    isSearching = false;
                }
            }
        });
    }
}
