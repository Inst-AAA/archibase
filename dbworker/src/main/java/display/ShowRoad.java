package display;

import Guo_Cam.Vec_Guo;
import analysis.Classifier;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2021/01/09
 */
public class ShowRoad extends PApplet {
    public static final int LEN_OF_CAMERA = 5000;
    Tools tools;
    GeoMath geoMath;
    List<WB_PolyLine> roads;
    List<WB_Polygon> blocks;

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
        tools.cam.top();
        tools.cam.getCamera().setPosition(new Vec_Guo(-200, -220, LEN_OF_CAMERA));
        tools.cam.getCamera().setLookAt(new Vec_Guo(-200, -220, 0));

        geoMath = new GeoMath(GeoContainer.MAP_LAT_LNG);

        roads = new ArrayList<>();
        for (GeoPolyLine line : GeoContainer.ways) {
            String type = Classifier.getHighwayType(line);
            if (type == null || type.equals("R1") || type.equals("S3")) continue;
            roads.add(lineStringToWB_PolyLine((LineString) line.getGeometry()));
        }

//        blocks = readDXF("./data/wien-block.dxf");
//        blocks = ShapeAnalysis.getInstance().getMapPolygon(roads);
    }

    public void draw() {
        background(255);
        stroke(120);
        tools.render.drawPolylineEdges(roads);

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
}