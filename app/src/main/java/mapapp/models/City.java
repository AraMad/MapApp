package mapapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("devices")
    @Expose
    private List<mapapp.models.Device> devices = null;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<mapapp.models.Device> getDevices() {
        return devices;
    }

    public void setDevices(List<mapapp.models.Device> devices) {
        this.devices = devices;
    }

}
