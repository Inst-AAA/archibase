package utils;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/05/23
 */
public class GeoMultiLines extends GeoGeom {

    private Date timestamp;
    private long osm_id;
    private MultiLineString ply;
    private Map<String, String> tags;

    public GeoMultiLines(MultiLineString ply) {
        this.ply = ply;
        tags = new HashMap<>();
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

    public void setOsm_id(long osm_id) {
        this.osm_id = osm_id;
    }

    public MultiLineString getPly() {
        return ply;
    }

    public void setPly(MultiLineString ply) {
        this.ply = ply;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public void printTag() {
        for (String key : getTags().keySet()) {
//            if(key == "building") continue;
            System.out.println("Relation # " + osm_id + " key = " + key + ", value = " + getTags().get(key));
        }
        System.out.println("---------------------------------------------");
    }

    public void addTag(String key, String value) {
        getTags().put(key, value);
    }

    @Override
    public Geometry getGeometry() {
        return ply;
    }

}
