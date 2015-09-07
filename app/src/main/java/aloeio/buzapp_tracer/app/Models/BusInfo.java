package aloeio.buzapp_tracer.app.Models;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

/**
 * Created by pablohenrique on 7/27/15.
 */
public class BusInfo {
    private static BusInfo instance = new BusInfo();
    private String route;
    private String plate;
    private String busNumber;
    private String type;
    private boolean accessibility;

    private BusInfo(){}

    public static BusInfo getInstance(){
        return instance;
    }

    public void setAll(EditText route, EditText plate, EditText busNumber, Spinner type, Switch accessibility){
        this.setAll(route.getText().toString(), plate.getText().toString(), busNumber.getText().toString(), type.getSelectedItem().toString(), accessibility.isChecked());
    }

    public void setAll(String route, String plate, String busNumber, String type, boolean accessibility){
        setRoute(route);
        setPlate(plate);
        setBusNumber(busNumber);
        setType(type);
        setAccessibility(accessibility);
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAccessibility() {
        return accessibility;
    }

    public void setAccessibility(boolean accessibility) {
        this.accessibility = accessibility;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }
}
