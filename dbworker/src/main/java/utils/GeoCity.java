package utils;

import analysis.Classifier;
import helper.GeoMath;
import helper.Tools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import processing.core.PApplet;
import processing.core.PGraphics;
import utils.*;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render2D;

import java.util.ArrayList;
import java.util.List;


public class GeoCity {
    GeoMath geoMath;

    List<WB_Polygon> plgs;
    List<WB_PolyLine> plys;
    List<WB_Point> nodes;


    String[] tags;


    public GeoCity(GeoMath geoMath) {
        this.geoMath = geoMath;
    }

    public void filter(String... tagType) {
        plgs = new ArrayList<>();
        plys = new ArrayList<>();
        nodes = new ArrayList<>();

        for (GeoPoint geo : GeoContainer.nodes) {
            boolean flag = Classifier.getGeometryType(tagType[0], geo);
            for (int i = 1; i < tagType.length; ++i) {
                flag |= Classifier.getGeometryType(tagType[1], geo);
            }
            if (flag) {
                Coordinate co = geo.getGeometry().getCoordinate();

                double[] pos = geoMath.latLngToXY(co.y, co.x);
                nodes.add(new WB_Point(pos));
            }
        }

        for (GeoPolyLine geo : GeoContainer.ways) {
            boolean flag = Classifier.getGeometryType(tagType[0], geo);
            for (int i = 1; i < tagType.length; ++i) {
                flag |= Classifier.getGeometryType(tagType[1], geo);
            }
            if (flag) {//                geo.printTag();
                LineString ls = (LineString) geo.getGeometry();
                if (ls.isClosed()) plgs.add(lineStringToWB_Polygon(ls));
                else plys.add(lineStringToWB_PolyLine(ls));
            }
        }

        for (GeoMultiLines geo : GeoContainer.relations) {

            boolean flag = Classifier.getGeometryType(tagType[0], geo);
            for (int i = 1; i < tagType.length; ++i) {
                flag |= Classifier.getGeometryType(tagType[1], geo);
            }
            if (flag) {
                MultiLineString mls = (MultiLineString) geo.getGeometry();
                for (int i = 0; i < mls.getNumGeometries(); ++i) {
                    LineString ls = (LineString) mls.getGeometryN(i);
                    if (ls.isClosed()) plgs.add(lineStringToWB_Polygon(ls));
                    else plys.add(lineStringToWB_PolyLine(ls));
                }

            }
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

    public void drawFilter(Tools tools) {

        for (String tag : tags) {
            filter(tag);
            int[] c = Classifier.buildingType.get(tag);
            int r = c[0];
            int g = c[1];
            int b = c[2];

            if(tag.equals("building")) tools.app.stroke(255);
            else tools.app.stroke(r, g, b);

            tools.app.fill(r, g, b);
            tools.render.drawPolygonEdges(plgs);
            tools.render.drawPolylineEdges(plys);
//            for (WB_Point pt : nodes) {
//                tools.drawPoint(pt, 2.5, new int[]{r, g, b});
//            }
        }
    }

    public void draw(PGraphics pg) {
        System.out.println("Filtered");
        WB_Render2D render = new WB_Render2D(pg);
        for (String tag : tags) {
            filter(tag);
            System.out.println("DrawTag: " + tag);
            int[] c = Classifier.buildingType.get(tag);


            if(tag.equals("building")) pg.stroke(255);
            else pg.stroke(c[0], c[1], c[2]);
            pg.fill(c[0], c[1], c[2]);
            for (WB_Polygon plg : plgs) {
                render.drawPolygonEdges2D(plg);
            }

            pg.noFill();
            pg.stroke(c[0], c[1], c[2]);

            for (WB_PolyLine ply : plys) {
                render.drawPolyLine2D(ply);
            }

            pg.fill(c[0], c[1], c[2]);
            pg.stroke(c[0], c[1], c[2]);

        }
        System.out.println("Draw block... ");
        System.out.println(GeoContainer.blocks.size());
        pg.noFill();
        pg.stroke(0);
        for(WB_Polygon block: GeoContainer.blocks) {
            render.drawPolygonEdges2D(block);
        }
        System.out.println("Finish");
    }

    public void setTags(String... tagType) {
        tags = tagType;
    }

    public void save(PApplet app, int w, int h, String filename) {
        System.out.println("Saving....");
        PGraphics pg = app.createGraphics(w, h);
        pg.beginDraw();
        pg.translate(w / 2.0f, h / 2.0f);
        pg.background(255);
        pg.scale(1, -1);
        this.draw(pg);
        pg.endDraw();
        System.out.println("End draw");
        pg.save(filename);
        System.out.println("Saved.");
    }
}
