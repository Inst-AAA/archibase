package display;

import Guo_Cam.Vec_Guo;
import analysis.Classifier;
import helper.ColorHelper;
import helper.GeoMath;
import helper.Tools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import type.GmapsTypeDetail;
import utils.GeoContainer;
import utils.GeoPolyLine;
import utils.Gpoi;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/08
 */
public class ShowGmaps extends PApplet {
    public static final int LEN_OF_CAMERA = 7000;
    Tools tools;
    List<WB_Polygon> plgs;
    List<WB_PolyLine> plys;
    List<WB_Point> nodes;

    List<WB_PolyLine> highways;
    List<WB_Polygon> buildings;
    List<String> wayTypes;
    GmapsTypeDetail.Types[] types;
    int[][] co;

    Map<String, Integer> map;
    GeoMath geoMath;


    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
        geoMath = new GeoMath(GeoContainer.MAP_LAT_LNG);
        getBuildingHighways();
        getGmapsColorMap();
        tools.cam.top();
        tools.cam.getCamera().setPosition(new Vec_Guo(-200, -225, LEN_OF_CAMERA));
        tools.cam.getCamera().setLookAt(new Vec_Guo(-200, -225, 0));
        // city center {565,-225} 500
    }

    public void draw() {
        background(255);
//        tools.cam.drawSystem(LEN_OF_CAMERA);

        noStroke();
        fill(223);
        tools.render.drawPolygonEdges(buildings);

        stroke(180, 120, 50);
        for (Gpoi p : GeoContainer.gpois) {
            if(!p.isChinese()) continue;
            double[] xy = geoMath.latLngToXY(p.getLat(), p.getLng());
            WB_Point pt = new WB_Point(xy);

            int[] c = new int[] {210, 180, 50};
            tools.drawPoint(pt, 30, c);
        }

        // 绘制地块
//        stroke(255);
//        noStroke();
//        fill(223, 223, 223);
//        tools.render.drawPolygonEdges(GeoContainer.blocks);
//


//        for(int i = 0; i < highways.size(); ++ i) {
//            int[] c = OsmTypeDetail.roadColor.get(wayTypes.get(i));
//            stroke(c[0], c[1], c[2]);
//
//            tools.render.drawPolylineEdges(highways.get(i));
//        }

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


    public void getGmapsColorMap() {
        types = GmapsTypeDetail.Types.values();
        System.out.println(types.length);
        co = ColorHelper.createGradientHue(types.length, ColorHelper.RED, ColorHelper.BLUE);

        map = new HashMap<>();
        for (int i = 0; i < co.length; ++i) {
            map.put(types[i].toString(), i);
        }

    }

    public void getBuildingHighways() {
        buildings = new ArrayList<>();
        highways = new ArrayList<>();
        wayTypes = new ArrayList<>();

        for (GeoPolyLine geo : GeoContainer.ways) {
            if (Classifier.getGeometryType("building", geo)) {
                LineString ls = (LineString) geo.getGeometry();
                buildings.add(lineStringToWB_Polygon(ls));
            }
        }

        for (GeoPolyLine geo : GeoContainer.ways) {
            if (Classifier.getHighwayType(geo) != null) {
                LineString ls = (LineString) geo.getGeometry();
                highways.add(lineStringToWB_PolyLine(ls));
                wayTypes.add(Classifier.getHighwayType(geo));
            }
        }
    }

}
