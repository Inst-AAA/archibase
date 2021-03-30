package creator;

import analysis.Classifier;
import db.Utils;
import helper.GeoMath;
import org.locationtech.jts.geom.*;
import utils.*;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Polygon;

import java.sql.Timestamp;

/**
 * @classname:
 * @description:
 * @author: amomorning
 * @date: 2020/05/21
 */
public class Main {
    public static void main(String[] args) {
        String cityname = "wien";

        GeoContainer.OSM_FILENAME = "./data/"+cityname+".pbf";
        GeoContainer.BLOCK_FILENAME = "./data/"+cityname+"-block.dxf";
        GeoContainer.BOUNDARY_FILENAME = "./data/"+cityname+"-boundary.geojson";
        GeoContainer.init();
        Utils db = new Utils();
        createCity(db, cityname, true);
        filterBuildings(db, true);

        filterRoads(db, true);
        filterUrbanSpace(db, true);
        filterBlocks(db, true);
    }

    public static void createCity(Utils db, String name, boolean flag) {
        if (flag) db.createTable("city", 1);
        GeoMath geoMath = new GeoMath(GeoContainer.MAP_LAT_LNG);
        geoMath.setRatio(GeoContainer.SW_LAT_LNG, GeoContainer.NE_LAT_LNG); // To get MAPRATIO;
        String[] v = new String[6];
        Timestamp time = new java.sql.Timestamp(System.currentTimeMillis());

        v[0] = "'" + name + "'";
        v[1] = Double.toString(geoMath.getLat());
        v[2] = Double.toString(geoMath.getLng());
        v[3] = Double.toString(geoMath.getRatio());
        v[4] = GeoContainer.boundary;

        v[5] = "'" + time.toString() + "'";
        db.insertFullData("city", 1, v);

    }


    public static void filterBlocks(Utils db, boolean flag) {
        if (flag) db.createTable("blocks", 1);

        GeoMath geoMath = new GeoMath(GeoContainer.MAP_LAT_LNG);
        geoMath.setRatio(GeoContainer.SW_LAT_LNG, GeoContainer.NE_LAT_LNG); // To get MAPRATIO;
        GeometryFactory gf = new GeometryFactory();
        for (WB_Polygon ply : GeoContainer.blocks) {
            LineString ls = getLineString(gf, geoMath, ply);
            String[] v = new String[2];

            v[0] = "default";
            v[1] = "ST_GeomFromText('" + ls.toText() + "', 4326)";
            db.insertFullData("blocks", 1, v);

        }
    }

    private static LineString getLineString(GeometryFactory gf, GeoMath geoMath, WB_Polygon ply) {
        WB_Coord[] pts = ply.getPoints().toArray();

        Coordinate[] coords = new Coordinate[pts.length];
        for (int i = 0; i < pts.length; ++i) {
            double[] latLng = geoMath.xyToLatLng(pts[i].xd(), pts[i].yd());
            coords[i] = new Coordinate(latLng[1], latLng[0]);
        }
        return gf.createLineString(coords);
    }

    private static void insertGeom(GeoGeom geom, Utils db, int feature_id) {
        Timestamp time = new java.sql.Timestamp(geom.getTimestamp().getTime());
        Geometry ls = geom.getGeometry();

        String[] v = new String[5];
        v[0] = "" + feature_id;
        v[1] = "" + geom.getOsm_id();
        v[2] = "ST_GeomFromText('" + ls.toText() + "', 4326)";
        if (geom.getTags().containsKey("name")) {
            v[3] = "'" + db.checkString(geom.getTags().get("name")) + "'";
        }
        v[4] = "'" + time.toString() + "'";

        db.insertFullData("urban_spaces", 2, v);
    }

    public static void filterUrbanSpace(Utils db, boolean flag) {
        int cnt = 0;

        String[] features = {"park",
                "piazza",
                "school",
                "green",
                "blue",
                "industry",
                "religious",
                "shop",
                "health",
                "transport"
        };

        if (flag) db.createTable("urban_spaces", 2);
        for (int i = 0; i < features.length; ++i) {

            for (GeoPoint geom : GeoContainer.nodes) {
                if (Classifier.getGeometryType(features[i], geom)) {
                    insertGeom(geom, db, i + 1);
                }
            }

            for (GeoPolyLine geom : GeoContainer.ways) {
                if (Classifier.getGeometryType(features[i], geom)) {
                    insertGeom(geom, db, i + 1);
                }
            }

            for (GeoMultiLines geom : GeoContainer.relations) {
                if (Classifier.getGeometryType(features[i], geom)) {
                    insertGeom(geom, db, i + 1);
                }
            }
        }


    }


    public static void filterBuildings(Utils db, boolean flag) {
        if (flag) db.createTable("buildings", 1);
        int cnt = 0;
        for (GeoPolyLine line : GeoContainer.ways) {
            if (Classifier.getGeometryType("building", line)) {
                Timestamp time = new java.sql.Timestamp(line.getTimestamp().getTime());
                LineString ls = (LineString) line.getGeometry();

                String[] v = new String[5];
                v[0] = "" + line.getOsm_id();
                v[1] = "ST_GeomFromText('" + ls.toText() + "', 4326)";
                if (line.getTags().containsKey("name")) {
                    v[2] = "'" + db.checkString(line.getTags().get("name")) + "'";
                }
                v[3] = "'" + line.getTags().get("building") + "'";
                v[4] = "'" + time.toString() + "'";

                db.insertFullData("buildings", 1, v);
                cnt++;

            }
        }
        System.out.println("Total = " + cnt);

    }

    public static void filterRoads(Utils db, boolean flag) {
        if (flag) db.createTable("roads", 1);
        int cnt = 0;
        for (GeoPolyLine line : GeoContainer.ways) {
            String roadtype = Classifier.getHighwayType(line);
            if (roadtype != null) {

                Timestamp time = new java.sql.Timestamp(line.getTimestamp().getTime());
                LineString ls = (LineString) line.getGeometry();

                String[] v = new String[6];

                v[0] = "" + line.getOsm_id();
                v[1] = "ST_GeomFromText('" + ls.toText() + "', 4326)";
                if (line.getTags().containsKey("name")) {
                    v[2] = "'" + db.checkString(line.getTags().get("name")) + "'";
                }
                v[3] = "'" + roadtype + "'";
                v[4] = "'" + line.getTags().get("highway") + "'";
                v[5] = "'" + time.toString() + "'";


                db.insertFullData("roads", 1, v);
            }
        }
        System.out.println("Total = " + cnt);

    }

    public static void filterUrbanSpace_Old() {
        int cnt = 0;
        for (GeoPolyLine geoPolyLine : GeoContainer.ways) {
            if (geoPolyLine.getTags().containsKey("highway")) {
                if (geoPolyLine.getTags().containsKey("area") && geoPolyLine.getTags().containsKey("name")) {
                    String name = geoPolyLine.getTags().get("name");
                    if (name.contains("Via")) continue;
                    geoPolyLine.printTag();
                    cnt++;
                }
                LineString ls = (LineString) geoPolyLine.getGeometry();
                System.out.println(ls.toText());
//                System.out.println(WKTWriter.toLineString(ls.getCoordinates()));
            }
        }

        for (GeoMultiLines gm : GeoContainer.relations) {
            if (gm.getTags().containsKey("highway")) {
                if (gm.getTags().containsKey("area") && gm.getTags().containsKey("name")) {
                    gm.printTag();
                    MultiLineString ls = gm.getPly();
                    System.out.println(ls.toText());
                    cnt++;
                }
            }
        }

        for (GeoPolyLine geoPolyLine : GeoContainer.ways) {
            if (Classifier.isPark(geoPolyLine)) {
                geoPolyLine.printTag();
                cnt++;
                LineString ls = (LineString) geoPolyLine.getGeometry();
                System.out.println(ls.toText());
            }
        }

        for (GeoMultiLines gm : GeoContainer.relations) {
            if (Classifier.isPark(gm)) {
                gm.printTag();
                MultiLineString ls = gm.getPly();
                System.out.println(ls.toText());
                cnt++;
            }
        }
        System.out.println("Total = " + cnt);
    }
}
