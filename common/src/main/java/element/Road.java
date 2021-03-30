package element;

import helper.GeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;

import java.util.Date;

public class Road {
    Long osm_id;
    LineString ply;
    String name;
    String roadType;
    String highway;
    Date timestamp;

    public Long getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(Long osm_id) {
        this.osm_id = osm_id;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getHighway() {
        return highway;
    }

    public void setHighway(String highway) {
        this.highway = highway;
    }

    public void print() {
        System.out.println(osm_id + " " + name + " " + roadType + " " + highway);
    }

    @Override
    public String toString() {
        return "Road{" +
                "osm_id=" + osm_id +
                ", ply=" + ply +
                ", name='" + name + '\'' +
                ", roadType='" + roadType + '\'' +
                ", highway='" + highway + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
