package element;

import helper.GeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.Date;

public class UrbanSpace {
    String feature;
    Long osm_id;
    Geometry geom;
    String name;
    Date timestamp;

    public WB_Point getWB_Pt(GeoMath geoMath) {
        Coordinate[] coords = geom.getCoordinates();
        WB_Point[] pts = new WB_Point[coords.length];
        for (int i = 0; i < coords.length; ++i) {
            double[] xy = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(xy);
        }
        return pts[0];
    }

    public WB_PolyLine getWB_Pl(GeoMath geoMath, LineString ls) {
        Coordinate[] coords = ls.getCoordinates();
        WB_Point[] pts = new WB_Point[coords.length];
        for (int i = 0; i < coords.length; ++i) {
            double[] xy = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(xy);
        }
        return new WB_PolyLine(pts);
    }

    public WB_Polygon getWB_Pg(GeoMath geoMath, LineString ls) {
        Coordinate[] coords = ls.getCoordinates();
        WB_Point[] pts = new WB_Point[coords.length];
        for (int i = 0; i < coords.length; ++i) {
            double[] xy = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(xy);
        }
        return new WB_Polygon(pts);
    }

    public void print() {
        System.out.println(osm_id + " " + name + " " + feature);

    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Long getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(Long osm_id) {
        this.osm_id = osm_id;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
