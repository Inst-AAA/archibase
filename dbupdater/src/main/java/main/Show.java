package main;

import Guo_Cam.Vec_Guo;
import utils.ChangeSet;
import element.Building;
import helper.GeoMath;
import helper.Tools;
import loader.DBLoader;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.List;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/06
 */
public class Show extends PApplet {
    public static final int LEN_OF_CAMERA = 7000;
    Tools tools;
    List<ChangeSet> changeSets;
    List<Building> buildings;
    GeoMath geoMath;

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
        geoMath = new GeoMath(new double[]{43.87815, 11.08385});

        DBLoader db = new DBLoader();
        changeSets = db.collectChangeSet();
        buildings = db.collectBuildings();
        tools.cam.top();
        tools.cam.getCamera().setPosition(new Vec_Guo(-200, -920, LEN_OF_CAMERA));
        tools.cam.getCamera().setLookAt(new Vec_Guo(-200, -920, 0));
    }

    public void draw() {
        background(255);

        for (Building building : buildings) {
            fill(223, 223, 223);
            noStroke();
            WB_Polygon ply = Tools.toWB_Polygon(building.getPly(), geoMath);
            tools.render.drawPolygonEdges(ply);
        }

        int nPt, nL, nP;
        nPt = nL = nP = 0;
        for (ChangeSet changeSet : changeSets) {
            int[] c;
            if (changeSet.getStatus() == 0) c = new int[]{0, 255, 0};
            else if (changeSet.getStatus() == 1) c = new int[]{0, 0, 255};
            else c = new int[]{255, 0, 0};


            fill(c[0], c[1], c[2]);
            noStroke();

            Geometry geom = changeSet.getGeom();

            if (geom.getGeometryType().equals("Point")) {
                WB_Point pt = changeSet.getWB_Pt(geoMath);
                tools.drawPoint(pt, 10, c);
                nPt++;

            } else if (geom.getGeometryType().equals("LineString")) {
                LineString ls = (LineString) geom;
                if (ls.isClosed()) {
                    WB_Polygon ply = changeSet.getWB_Pg(geoMath, ls);
                    int[] lightC = new int[3];
                    for (int i = 0; i < 3; ++i) lightC[i] = (c[i] + 200) / 2;
                    fill(lightC[0], lightC[1], lightC[2]);
                    tools.render.drawPolygonEdges(ply);
                    nP++;
                } else {
                    WB_PolyLine ply = changeSet.getWB_Pl(geoMath, ls);
                    stroke(c[0], c[1], c[2]);
                    strokeWeight(1.5f);
                    tools.render.drawPolylineEdges(ply);
                    nL++;
                }

            }
        }

        System.out.println("Pt = " + nPt + ", L = " + nL + ", P" + nP);

    }

}
