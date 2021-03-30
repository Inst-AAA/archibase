package analysis;

import helper.Tools;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/07/13
 */
public class ShapeAnalysis {
    public static ShapeAnalysis getInstance() {
        return AnalysisLoader.instance;
    }

    public List<WB_Polygon> getMapPolygon(List<WB_PolyLine> plys) {
        LineMerger lineMerger = new LineMerger();

        Polygonizer polygonizer = new Polygonizer();
        Geometry ls = null;
        for (WB_PolyLine ply : plys) {
            GeometryFactory gf = new GeometryFactory();
            int n = ply.getNumberOfPoints();
            Coordinate[] pts = new Coordinate[n];
            for (int i = 0; i < n; ++i) {
                WB_Point pt = ply.getPoint(i);
                pts[i] = new Coordinate(pt.xd(), pt.yd(), pt.zd());
            }
            ls = gf.createLineString(pts);
            lineMerger.add(ls);
        }
        Collection<LineString> mergedLineStrings = lineMerger.getMergedLineStrings();

        int cnt = 0;
        for (LineString line : mergedLineStrings) {
            assert ls != null;
            ls = ls.union(line);
            System.out.println("Line #" + (cnt++) + "/" + mergedLineStrings.size() + " is merged.");
        }

        polygonizer.add(ls);
//
        Collection<Polygon> polygons = polygonizer.getPolygons();


        polygons = polygonizer.getPolygons();

        List<WB_Polygon> ret = new ArrayList<>();
        for (Polygon polygon : polygons) {
            Geometry g = polygon.buffer(-7);
            g = g.buffer(4.5);
            if (g.getGeometryType().equals("Polygon")) {

                WB_Polygon pl = Tools.toWB_Polygon((Polygon) g);
                if (pl != null && Math.abs(pl.getSignedArea()) > 650) {
                    ret.add(pl);
                }

            } else if (g.getGeometryType().equals("MultiPolygon")) {
                MultiPolygon mp = (MultiPolygon) g;
                for (int i = 0; i < mp.getNumGeometries(); ++i) {
                    WB_Polygon pl = Tools.toWB_Polygon((Polygon) mp.getGeometryN(i));

                    if (pl != null && Math.abs(pl.getSignedArea()) > 650) {
                        ret.add(pl);
                    }
                }
            }
        }
        return ret;
    }

    private static class AnalysisLoader {
        private static final ShapeAnalysis instance = new ShapeAnalysis();
    }

}
