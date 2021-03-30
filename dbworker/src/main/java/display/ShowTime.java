package display;

import Guo_Cam.Vec_Guo;
import analysis.Classifier;
import helper.ColorHelper;
import helper.GeoMath;
import helper.Tools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import utils.GeoContainer;
import utils.GeoPolyLine;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.*;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/05/24
 */
public class ShowTime extends PApplet {
    public static final int LEN_OF_CAMERA = 5000;
    Tools tools;
    List<WB_PolyLine> highways;
    List<WB_Polygon> buildings;

    GeoMath geoMath;
    int[][] buildingColorSet;
    int[][] highwayColorSet;

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
        geoMath = new GeoMath(GeoContainer.MAP_LAT_LNG);

        getBuildingsAndHighways();
        tools.cam.top();
        tools.cam.getCamera().setPosition(new Vec_Guo(-200, -220, LEN_OF_CAMERA));
        tools.cam.getCamera().setLookAt(new Vec_Guo(-200, -220, 0));
    }

    public void draw() {
        background(255);
        // 绘制地块
        stroke(255);
//        noStroke();
//        fill(223, 223, 223);
//        tools.render.drawPolygonEdges(GeoContainer.blocks);

//        for (int i = 0; i < buildings.size(); ++i) {
//            noStroke();
////            stroke(223, 223, 223);
//            fill(buildingColorSet[i][0], buildingColorSet[i][1], buildingColorSet[i][2]);
//            tools.render.drawPolygonEdges(buildings.get(i));
//        }

        for (int i = 0; i < highways.size(); ++i) {
            stroke(highwayColorSet[i][0], highwayColorSet[i][1], highwayColorSet[i][2]);
            strokeWeight(1);
            noFill();
            tools.render.drawPolylineEdges(highways.get(i));
        }

    }

    public WB_Polygon lineStringToWB_Polygon(LineString line) {
        Coordinate[] coords = line.getCoordinates();
        int num = line.isClosed() ? coords.length - 1 : coords.length;
        WB_Point[] pts = new WB_Point[num];
        for (int i = 0; i < num; ++i) {
            double[] pos = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(pos);
        }
        return new WB_Polygon(pts);
    }

    public WB_PolyLine lineStringToWB_PolyLine(LineString line) {
        Coordinate[] coords = line.getCoordinates();
        int num = line.isClosed() ? coords.length - 1 : coords.length;
        WB_Point[] pts = new WB_Point[num];
        for (int i = 0; i < num; ++i) {
            double[] pos = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(pos);
        }
        return new WB_PolyLine(pts);
    }

    public void getBuildingsAndHighways() {
        buildings = new ArrayList<>();
        highways = new ArrayList<>();


        Set<Integer> st = new TreeSet<>(Comparator.naturalOrder());
        for (GeoPolyLine geo : GeoContainer.ways) {
            if (Classifier.getGeometryType("building", geo)) {
                LineString ls = (LineString) geo.getGeometry();
                buildings.add(lineStringToWB_Polygon(ls));
                st.add(geo.getTimestamp().getYear() + 1900);
            }

            if (Classifier.getGeometryType("highway", geo)) {
                LineString ls = (LineString) geo.getGeometry();
                highways.add(lineStringToWB_PolyLine(ls));
                st.add(geo.getTimestamp().getYear() + 1900);
            }
        }


        int[][] c = ColorHelper.createGradientBright(st.size(), 0xcc9966);
        Iterator<Integer> iter = st.iterator();
        Integer first = iter.next();
        for (Integer year : st) {
            System.out.println(year);
        }
        int cntBuilding = 0;
        int cntHighway = 0;
        buildingColorSet = new int[buildings.size()][];
        highwayColorSet = new int[highways.size()][];
        for (GeoPolyLine geo : GeoContainer.ways) {
            if (Classifier.getGeometryType("building", geo)) {
                int yearid = geo.getTimestamp().getYear() + 1900 - first;
                buildingColorSet[cntBuilding++] = c[yearid];
            }

            if (Classifier.getGeometryType("highway", geo)) {
                int yearid = geo.getTimestamp().getYear() + 1900 - first;
                highwayColorSet[cntHighway++] = c[yearid];
            }
        }

        System.out.println("Buildings = " + cntBuilding);
        System.out.println("Highways = " + cntHighway);
    }
}
