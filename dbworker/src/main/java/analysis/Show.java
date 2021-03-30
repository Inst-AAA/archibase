package analysis;

import Guo_Cam.Vec_Guo;
import helper.ExportDXF;
import helper.Tools;
import helper.GeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import readDXF.DXFImport;
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
 * @date: 2020/07/13
 */
public class Show extends PApplet {
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
        geoMath.setRatio(GeoContainer.SW_LAT_LNG, GeoContainer.NE_LAT_LNG);

        roads = new ArrayList<>();
        for (GeoPolyLine line : GeoContainer.ways) {
            String type = Classifier.getHighwayType(line);
            if (type == null || type.equals("R1") || type.equals("S3")) continue;
            roads.add(lineStringToWB_PolyLine((LineString) line.getGeometry()));
        }

//        blocks = readDXF("./data/wien-block.dxf");
        blocks = ShapeAnalysis.getInstance().getMapPolygon(roads);
        saveDXF("./data/wien-block.dxf");
    }

    public void draw() {
        background(255);
//        stroke(120);
//        tools.render.drawPolylineEdges(roads);

        stroke(0);
        tools.render.drawPolygonEdges(blocks);
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

    public void saveDXF(String filePath) {
        ExportDXF dxf = new ExportDXF();
        for (WB_Polygon pl : blocks) {
            dxf.add(pl, ExportDXF.BROKEN);
        }
        dxf.save(filePath);
    }

    public List<WB_Polygon> readDXF(String filePath) {
        double[][][] polys = DXFImport.polylines_layer(filePath, "brokenLine");
        List<WB_Polygon> ply = new ArrayList<>();


        for (int i = 0; i < polys.length; ++i) {
            WB_Point[] pts = new WB_Point[polys[i].length];
            for (int j = 0; j < polys[i].length; ++j) {
                pts[j] = new WB_Point(polys[i][j]);
            }
            ply.add(new WB_Polygon(pts));
        }
        return ply;
    }
}
