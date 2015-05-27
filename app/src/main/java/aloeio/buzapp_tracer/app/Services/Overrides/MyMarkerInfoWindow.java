package aloeio.buzapp_tracer.app.Services.Overrides;

import aloeio.buzapp_tracer.app.R;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * Created by pablohenrique on 5/6/15.
 */
public class MyMarkerInfoWindow extends MarkerInfoWindow implements Cloneable {
    public MyMarkerInfoWindow(MapView mapView) {
        super(R.layout.template_bonuspack_bubble, mapView);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public MyMarkerInfoWindow copy() throws CloneNotSupportedException{
        return (MyMarkerInfoWindow) this.clone();
    }
}
