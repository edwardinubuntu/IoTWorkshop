package tw.soleil.pwrs.object;

import java.util.HashMap;
import java.util.List;

/**
 * Created by edward_chiang on 6/17/16.
 */
public class DataStream {
    private String id;
    private String current_value;
    private String at;
    private String max_value;
    private String min_value;
    private HashMap<String, String> unit;
    private List<HashMap<String, String>> datapoints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(String current_value) {
        this.current_value = current_value;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getMax_value() {
        return max_value;
    }

    public void setMax_value(String max_value) {
        this.max_value = max_value;
    }

    public String getMin_value() {
        return min_value;
    }

    public void setMin_value(String min_value) {
        this.min_value = min_value;
    }

    public HashMap<String, String> getUnit() {
        return unit;
    }

    public void setUnit(HashMap<String, String> unit) {
        this.unit = unit;
    }

    public List<HashMap<String, String>> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<HashMap<String, String>> datapoints) {
        this.datapoints = datapoints;
    }

    @Override
    public String toString() {
        return "DataStream{" +
                "id='" + id + '\'' +
                ", current_value='" + current_value + '\'' +
                ", at='" + at + '\'' +
                ", max_value='" + max_value + '\'' +
                ", min_value='" + min_value + '\'' +
                ", unit=" + unit +
                ", datapoints=" + datapoints +
                '}';
    }
}
