package aloeio.buzapp_tracer.app.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 9/21/15.
 */
public class DeviceInfo {

    private String uuid = "uuid";
    private String serial = "serial";
    private String macAddress = "macAddress";
    private String simSerialNumber = "simSerialNumber";
    private String simNumber = "simNumber";
    private String email="email";
    private String registrationID="";

    public DeviceInfo(String uuid, String serial, String macAddress, String simSerialNumber, String simNumber, String email,String registrationID) {
        this.uuid = uuid;
        this.serial = serial;
        this.macAddress = macAddress;
        this.simSerialNumber = simSerialNumber;
        this.simNumber = simNumber;
        this.email = email;
        this.registrationID=registrationID;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject json = new JSONObject();
        json.accumulate("uuid", this.getUuid());
        json.accumulate("serial",this.getSerial() );
        json.accumulate("macAddress",this.getMacAddress());
        json.accumulate("simSerialNumber", this.getSimSerialNumber());
        json.accumulate("simNumber",this.getSimNumber());
        json.accumulate("email",this.getEmail());
        json.accumulate("registrationId",this.getRegistrationID());
        return json;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public String getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(String simNumber) {
        this.simNumber = simNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
