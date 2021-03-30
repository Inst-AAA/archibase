package utils;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GeoPolyLine extends GeoGeom {
    private Date timestamp;
    private long osm_id;
    private LineString ply;
    public Map<String, String> tags;


    public GeoPolyLine(LineString polyline) {
        setPly(polyline);
        tags = new HashMap<>();
    }


    public Geometry getGeometry() {
        return ply;
    }

    public void setPly(LineString ply) {
        this.ply = ply;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(long id) {
        this.osm_id = id;


    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public void addTag(String key, String value) {
        getTags().put(key, value);
    }

    public void printTag() {
        for (String key : getTags().keySet()) {
//            if(key == "building") continue;
            System.out.println("Way # " + osm_id + " key = " + key + ", value = " + getTags().get(key));
        }
        System.out.println("---------------------------------------------");
    }
}
