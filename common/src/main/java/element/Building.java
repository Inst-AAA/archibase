package element;

import org.locationtech.jts.geom.LineString;

import java.util.Date;

public class Building {

    Long osm_id;
    LineString ply;
    String name;
    String building;
    String s3db;
    Date timestamp;

    public Long getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(Long osm_id) {
        this.osm_id = osm_id;
    }

    public LineString getPly() {
        return ply;
    }

    public void setPly(LineString ply) {
        this.ply = ply;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getS3db() {
        return s3db;
    }

    public void setS3db(String s3db) {
        this.s3db = s3db;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Building{" +
                "osm_id=" + osm_id +
                ", ply=" + ply +
                ", name='" + name + '\'' +
                ", building='" + building + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
