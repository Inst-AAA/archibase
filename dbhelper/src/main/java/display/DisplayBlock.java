package display;

import Guo_Cam.Vec_Guo;
import element.Block;
import element.Building;
import element.City;
import helper.GeoMath;
import helper.Tools;
import loader.DBLoader;
import processing.core.PApplet;
import wblut.geom.WB_AABB;
import wblut.geom.WB_GeometryOp2D;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render2D;

import java.util.*;

public class DisplayBlock extends PApplet {
    public static final int LEN_OF_CAMERA = 500;
    public static final String CITYNAME = "athens";

    Tools tools;
    DBLoader db;
    Block block;
    WB_Polygon ply;

    List<Building> buildings;

    GeoMath geoMath;

    Map<String, GeoMath> mp = new HashMap<>();
    public static void main(String[] args) {
        PApplet.main("display.DisplayBlock");
    }

    private double[] getAABB(WB_Polygon ply) {
        WB_AABB ab = ply.getAABB();

        double[] min = ab.getMin().coords();
        double[] max = ab.getMax().coords();

        double[] lmin =  geoMath.xyToLatLng(min[0], min[1]);
        double[] lmax =  geoMath.xyToLatLng(max[0], max[1]);

        return new double[]{lmin[1], lmax[0], lmax[1], lmin[0]};
    }


    public void settings() {
        size(500, 500, P3D);
        smooth(8);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
        tools.cam.top();
        tools.cam.setFovy(1);

        db = new DBLoader();

        block= db.collectBlock(28807);

        List<City> cities = db.collectCity();

        for (City city : cities) {

            GeoMath gm = new GeoMath(city.getLat(), city.getLon());
            gm.setRatio(city.getRatio());
            mp.put(city.getName(), gm);

            System.out.println(city);
            System.out.println(city.getName());
            System.out.println(gm);

            System.out.println(mp);
        }

        geoMath = mp.get(CITYNAME);
        System.out.println(geoMath);

//        geoMath = new GeoMath(37.9660499995, 23.7197999995);
//        geoMath.setRatio(0.78837506932504);

        ply = Tools.toWB_Polygon(block.getPly(), geoMath);
        double[] aabb = getAABB(ply);

        buildings = db.collectBuildings(aabb[0], aabb[1], aabb[2], aabb[3], 4326);

        moveCamera(ply);
    }

    public void draw() {
        background(255);
        stroke(0);
        noFill();
        WB_Render2D render = new WB_Render2D(this);
        render.drawPolygonEdges2D(ply);


        for (Building building : buildings) {
            fill(0);
            stroke(255);

            WB_Polygon ply = Tools.toWB_Polygon(building.getPly(), geoMath);
            render.drawPolygonEdges2D(ply);

        }
    }

    public void moveCamera(WB_Polygon block) {
        WB_Point pt = block.getCenter();
        WB_AABB ab = block.getAABB();
        double len = Math.max(ab.getWidth(), ab.getHeight());
        tools.cam.getCamera().setPosition(new Vec_Guo(pt.xd() - 250, pt.yd() - 250, len*2.5));
        tools.cam.getCamera().setLookAt(new Vec_Guo(pt.xd() - 250, pt.yd() - 250, 0));
        tools.cam.getCamera().updateProjectionMatrix();

    }


}
