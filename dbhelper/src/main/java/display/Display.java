package display;

import element.*;
import helper.ColorHelper;
import helper.GeoMath;
import helper.Tools;
import loader.DBLoader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import type.OsmTypeDetail;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.Arrays;
import java.util.List;


public class Display extends PApplet {
    public static final int LEN_OF_CAMERA = 5000;
    Tools tools;
    List<Road> roads;
    List<Building> buildings;
    List<UrbanSpace> urbanSpaces;
    List<Block> blocks;
    GeoMath geoMath;

    public static void main(String[] args) {
        PApplet.main("display.Display");
    }
    public void settings() {
        size(1800, 1000, P3D);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
        DBLoader db = new DBLoader();
        City city = db.collectCity();
        roads = db.collectRoad();
        buildings = db.collectBuildings();
        urbanSpaces = db.collectUrbanSpace();
        blocks = db.collectBlock();

        geoMath = new GeoMath(city.getLat(), city.getLon());
        geoMath.setRatio(city.getRatio());
        System.out.println(Arrays.toString(getCenter()));

        System.out.println(city);
    }

    public void draw() {
        background(ColorHelper.BACKGROUNDBLUE);
        tools.cam.drawSystem(LEN_OF_CAMERA);
        for (Road road : roads) {
            int[] c = OsmTypeDetail.roadColor.get(road.getRoadType());
            stroke(c[0], c[1], c[2]);

            WB_PolyLine ply = Tools.toWB_Polygon(road.getPly(), geoMath);
            tools.render.drawPolylineEdges(ply);
        }

        for (Building building: buildings) {
            stroke(255);
            noFill();
            WB_Polygon ply = Tools.toWB_Polygon(building.getPly(), geoMath);
            tools.render.drawPolygonEdges(ply);

        }

        for (Block block : blocks) {
            stroke(255);
            noFill();
            WB_Polygon ply = Tools.toWB_Polygon(block.getPly(), geoMath);
            tools.render.drawPolygonEdges(ply);
        }


        for (UrbanSpace urbanSpace : urbanSpaces) {
            int[] c = ColorHelper.colorLighter(ColorHelper.BACKGROUNDBLUE, 0.6);
            fill(c[0], c[1], c[2]);
            noStroke();

            Geometry geom = urbanSpace.getGeom();
            if (geom.getGeometryType().equals("Point")) {
                WB_Point pt = urbanSpace.getWB_Pt(geoMath);
                tools.drawPoint(pt, 2.5, c);
            } else if (geom.getGeometryType().equals("LineString")) {
                LineString ls = (LineString) geom;
                if (ls.isClosed()) {
                    WB_Polygon ply = urbanSpace.getWB_Pg(geoMath, ls);
                    tools.render.drawPolygonEdges(ply);
                } else {
                    WB_PolyLine ply = urbanSpace.getWB_Pl(geoMath, ls);
                    stroke(c[0], c[1], c[2]);
                    tools.render.drawPolylineEdges(ply);
                }

            } else {
                for (int i = 0; i < geom.getNumGeometries(); ++i) {
                    Geometry geome = geom.getGeometryN(i);
                    LineString ls = (LineString) geome;
                    if (ls.isClosed()) {
                        WB_Polygon ply = urbanSpace.getWB_Pg(geoMath, ls);
                        tools.render.drawPolygonEdges(ply);
                    } else {
                        WB_PolyLine ply = urbanSpace.getWB_Pl(geoMath, ls);
                        stroke(c[0], c[1], c[2]);
                        tools.render.drawPolylineEdges(ply);
                    }
                }
            }
        }
    }

    private double[] getCenter() {
        double[] aabb = new double[4];
        aabb[0] = aabb[1] = Double.MAX_VALUE;
        aabb[2] = aabb[3] = -Double.MAX_VALUE;
        for (Road road : roads) {
            Coordinate[] coords = road.getPly().getCoordinates();
            for (Coordinate coord : coords) {
                aabb[0] = Math.min(aabb[0], coord.x);
                aabb[1] = Math.min(aabb[1], coord.y);
                aabb[2] = Math.max(aabb[2], coord.x);
                aabb[3] = Math.max(aabb[3], coord.y);
            }
        }
        return new double[]{(aabb[0] + aabb[2]) / 2.0, (aabb[1] + aabb[3]) / 2.0};
    }
}
