package utils;

import helper.GeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/08
 */
public class ChangeSet {
    Long osm_id;
    Geometry geom;
    int status; //0-create 1-modify 2-delete

    public WB_Point getWB_Pt(GeoMath geoMath) {
        Coordinate[] coords = geom.getCoordinates();
        WB_Point[] pts = new WB_Point[coords.length];
        for (int i = 0; i < coords.length; ++i) {
//            System.out.println(coords[i].x + " " + coords[i].y);
            double[] xy = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(xy);
        }
        return pts[0];
    }

    public WB_PolyLine getWB_Pl(GeoMath geoMath, LineString ls) {
        Coordinate[] coords = ls.getCoordinates();
        WB_Point[] pts = new WB_Point[coords.length];
        for (int i = 0; i < coords.length; ++i) {
//            System.out.println(coords[i].x + " " + coords[i].y);
            double[] xy = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(xy);
        }
        return new WB_PolyLine(pts);
    }

    public WB_Polygon getWB_Pg(GeoMath geoMath, LineString ls) {
        Coordinate[] coords = ls.getCoordinates();
        WB_Point[] pts = new WB_Point[coords.length];
        for (int i = 0; i < coords.length; ++i) {
//            System.out.println(coords[i].x + " " + coords[i].y);
            double[] xy = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(xy);
        }
        return new WB_Polygon(pts);
    }

    public void print() {
        System.out.println(osm_id + " " + geom.getGeometryType() + " " + status);

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
