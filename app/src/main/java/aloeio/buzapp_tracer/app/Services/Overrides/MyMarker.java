package aloeio.buzapp_tracer.app.Services.Overrides;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;

import java.io.Serializable;

/**
 * Created by pablohenrique on 2/21/15.
 */
public class MyMarker
        extends Marker
        implements Cloneable, Serializable {

    public MyMarker(MapView mapView) {
        super(mapView);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public MyMarker copy() throws CloneNotSupportedException{
        return (MyMarker) this.clone();
    }
}
