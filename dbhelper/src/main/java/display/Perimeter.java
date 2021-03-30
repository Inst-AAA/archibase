package display;

import Guo_Cam.Vec_Guo;
import element.Block;
import element.Building;
import element.Road;
import helper.GeoMath;
import helper.Tools;
import loader.DBLoader;
import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.geom.*;
import wblut.processing.WB_Render2D;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Perimeter extends PApplet {
    public static final int LEN_OF_CAMERA = 500;
    public static final int MODE = 3; // 0 - block , 1 - all, 2 - all without block, 3 - line
    Tools tools;
    List<Road> roads;
    List<Building> buildings;
    List<Block> blocks;
    GeoMath geoMath;
    DBLoader db;
    Random rand;

    int id;
    int time;
    int[] vis;
    boolean save = false;

    public void settings() {
        size(500, 500, P3D);
        smooth(8);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
        tools.cam.top();
        tools.cam.setFovy(1);
        db = new DBLoader();
        blocks = db.collectBlock();

        rand = new Random(123);
        geoMath = new GeoMath(48.217000000000006, 16.3885);
        geoMath.setRatio(new double[]{48.11000000000001, 16.152}, new double[]{48.324000000000005, 16.625});

        id = 0;
        if (MODE == 0 || MODE == 3) {
            while (!nextBlock(id)) {
                id += 1;
                nextBlock(id);
            }
        } else {

            while (!nextAll(id)) {
                id += 1;
                nextAll(id);
            }
        }
        time = 1;
        vis = new int[blocks.size()];

    }


    public void draw() {
        if (id < blocks.size()) {
            if (MODE == 0) {
                drawCityBlocks(this.g);
            } else if(MODE == 1) {
                drawAll(this.g);
            } else if(MODE == 2){
                drawAllBlank(this.g);
            } else if(MODE == 3){
                drawLine(this.g);
            }


            if (vis[id] == 0 && save) {
                save(this);
                vis[id] = 1;
            }

            if (time % 10 == 0) {
                id += 1;

                if (MODE == 0 || MODE == 3) {
                    while (id < blocks.size() && !nextBlock(id)) {
                        id += 1;
                        nextBlock(id);
                    }
                } else {

                    while (id < blocks.size() && !nextAll(id)) {
                        id += 1;
                        nextAll(id);
                    }

                }
            }
            time++;
        }
    }

    public void drawAll(PGraphics app) {
        app.background(255);
        app.stroke(0);
        WB_Render2D render = new WB_Render2D(app);
        WB_Polygon block = Tools.toWB_Polygon(blocks.get(id).getPly(), geoMath);

        save = false;
        for (Building building : buildings) {
            WB_Polygon ply = Tools.toWB_Polygon(building.getPly(), geoMath);
            WB_Point pt = ply.getCenter();
            if (WB_GeometryOp2D.contains2D(pt, block)) {
                app.fill(0);
                app.stroke(255);
                render.drawPolygonEdges2D(ply);
                save = true;
            } else {
                app.fill(200);
                app.stroke(255);
                render.drawPolygonEdges2D(ply);
            }
        }
        for (Road road : roads) {
            app.noFill();
            app.stroke(0);

            WB_PolyLine ply = Tools.toWB_Polygon(road.getPly(), geoMath);
            render.drawPolyLine2D(ply);
        }
    }

    public void drawAllBlank(PGraphics app) {
        app.background(255);
        app.stroke(0);
        WB_Render2D render = new WB_Render2D(app);
        WB_Polygon block = Tools.toWB_Polygon(blocks.get(id).getPly(), geoMath);

        save = false;
        for (Building building : buildings) {
            WB_Polygon ply = Tools.toWB_Polygon(building.getPly(), geoMath);
            WB_Point pt = ply.getCenter();
            if (WB_GeometryOp2D.contains2D(pt, block)) {
                save = true;
            } else {
                app.fill(200);
                app.stroke(255);
                render.drawPolygonEdges2D(ply);
            }
        }
        for (Road road : roads) {
            app.noFill();
            app.stroke(0);

            WB_PolyLine ply = Tools.toWB_Polygon(road.getPly(), geoMath);
            render.drawPolyLine2D(ply);
        }
    }
    public void drawLine(PGraphics app) {
        app.background(255);
        app.stroke(0);
        app.noFill();
        WB_Render2D render = new WB_Render2D(app);
        WB_Polygon block = Tools.toWB_Polygon(blocks.get(id).getPly(), geoMath);
        render.drawPolygonEdges2D(block);


        save = false;
        for (Building building : buildings) {
            app.fill(0);
            app.stroke(255);

            WB_Polygon ply = Tools.toWB_Polygon(building.getPly(), geoMath);
            WB_Point pt = ply.getCenter();
            if (WB_GeometryOp2D.contains2D(pt, block)) {
                save = true;
            }

        }
    }

    public void drawCityBlocks(PGraphics app) {
        app.background(255);
        app.stroke(0);
        app.noFill();
        WB_Render2D render = new WB_Render2D(app);
        WB_Polygon block = Tools.toWB_Polygon(blocks.get(id).getPly(), geoMath);
        render.drawPolygonEdges2D(block);


        save = false;
        for (Building building : buildings) {
            app.fill(0);
            app.stroke(255);

            WB_Polygon ply = Tools.toWB_Polygon(building.getPly(), geoMath);
            WB_Point pt = ply.getCenter();
            if (WB_GeometryOp2D.contains2D(pt, block)) {
                save = true;
                render.drawPolygonEdges2D(ply);
            }

        }
    }

    public void save(PApplet app) {
        System.out.println("# " + id + "/" + blocks.size() + " :");
        String filename = blocks.get(id).getID() + ".jpg";
        System.out.println(filename + " saved.");
        if(MODE==1) {
            app.saveFrame("./fig/street/" + filename);
        } else if(MODE == 0) {
            app.saveFrame("./fig/block/" + filename);
        } else if(MODE == 2){
            app.saveFrame("./fig/blank/" + filename);
        } else if(MODE == 3) {
            app.saveFrame("./fig/line/" + filename);
        }
    }

    private double[] getAABB(WB_Polygon ply) {
        WB_AABB ab = ply.getAABB();

        double[] min = ab.getMin().coords();
        double[] max = ab.getMax().coords();

        double[] lmin = geoMath.xyToLatLng(min[0], min[1]);
        double[] lmax = geoMath.xyToLatLng(max[0], max[1]);

        return new double[]{lmin[1], lmax[0], lmax[1], lmin[0]};
    }

    @Override
    public void keyPressed() {
        if (key == 'n') {
            id = rand.nextInt(blocks.size());
            nextAll(id);
        }
        if (key == 's') {
            save(this);
        }
    }

    public boolean nextAll(int blockID) {
        id = blockID;
        WB_Polygon block = Tools.toWB_Polygon(blocks.get(id).getPly(), geoMath);
        double[] aabb = getAABB(block);

        System.out.println(Arrays.toString(aabb));
        double[] cet = new double[]{(aabb[0] + aabb[2]) / 2., (aabb[1] + aabb[3]) / 2.};
        double max = Math.max(aabb[2] - aabb[0], aabb[3] - aabb[1]);

        buildings = db.collectBuildings(cet[0] - max, cet[1] - max, cet[0] + max, cet[1] + max, 4326);
        roads = db.collectRoad(cet[0] - max, cet[1] - max, cet[0] + max, cet[1] + max, 4326);
        System.out.println("ID " + id + " get buildings " + buildings.size());

        if (buildings.size() > 500) return false;
        if (buildings.size() == 0) return false;

        // move camera
        moveCamera(block);
        return true;
    }

    public boolean nextBlock(int blockID) {
        id = blockID;
        WB_Polygon block = Tools.toWB_Polygon(blocks.get(id).getPly(), geoMath);
        double[] aabb = getAABB(block);
        buildings = db.collectBuildings(aabb[0], aabb[1], aabb[2], aabb[3], 4326);
        System.out.println("ID " + id + " get buildings " + buildings.size());
        if (buildings.size() > 100) return false;
        if (buildings.size() == 0) return false;

        // move camera
        moveCamera(block);
        return true;
    }

    public void moveCamera(WB_Polygon block) {
        WB_Point pt = block.getCenter();
        WB_AABB ab = block.getAABB();
        double len = Math.max(ab.getWidth(), ab.getHeight());
        tools.cam.getCamera().setPosition(new Vec_Guo(pt.xd() - 250, pt.yd() - 250, len * 2.5));
        tools.cam.getCamera().setLookAt(new Vec_Guo(pt.xd() - 250, pt.yd() - 250, 0));
        tools.cam.getCamera().updateProjectionMatrix();

    }
    public static void main(String[] args) {
        PApplet.main("display.Perimeter");
    }
}
