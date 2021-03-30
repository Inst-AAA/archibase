/**
 *
 */
package helper;

import Guo_Cam.CameraController;
import Guo_Cam.Vec_Guo;
import controlP5.ControlP5;
import igeo.ICurve;
import igeo.IG;
import igeo.IPoint;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.buffer.BufferOp;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.math.WB_Epsilon;
import wblut.processing.WB_Render;

import java.util.List;
import java.util.Random;

/**
 * @author amo Sep 14, 2018
 *
 */
public class Tools {
    public static double EPS = Double.longBitsToDouble(971L << 52);
    public static double RATIO = (Math.sqrt(5) + 1.0) / 2;
    private static long lastTimeMillis;
    /**
     * @param testUtils
     */
    public WB_Render render;
    public CameraController cam;
    public ControlP5 cp5;
    public PApplet app;

    /**
     * Construct a useful tool for Architecture Design.
     *
     */
    public Tools(PApplet app, int len) {
        IG.init();
        render = new WB_Render(app);
        cam = new CameraController(app, len);
        cp5 = new ControlP5(app);
        cp5.setAutoDraw(false);
        this.app = app;
    }


    public static void timerStart() {
        lastTimeMillis = System.currentTimeMillis();
    }

    public static void timerShow(String msg) {
        long duration = System.currentTimeMillis() - lastTimeMillis;
        System.err.println(msg + ": " + duration);
    }

    /**
     * Change a JTS Polygon into WB_Polygon
     *
     * @param: Polygon ply
     * @return: WB_Polygon
     * @throws:
     */
    public static WB_Polygon toWB_Polygon(Polygon ply) {
        Geometry g = ply.getExteriorRing();
        Coordinate[] pts = g.getCoordinates();
        WB_Point[] polypt = new WB_Point[g.getNumPoints()];

        for (int i = 0; i < pts.length; ++i) {
            polypt[i] = new WB_Point(pts[i].x, pts[i].y);
        }
        return new WB_GeometryFactory().createSimplePolygon(polypt);
    }

    public static WB_Polygon toWB_Polygon(LineString ls, GeoMath geoMath) {
        Coordinate[] coords = ls.getCoordinates();
        WB_Point[] pts = new WB_Point[coords.length];

        for (int i = 0; i < coords.length; ++ i) {
            double[] xy = geoMath.latLngToXY(coords[i].y, coords[i].x);
            pts[i] = new WB_Point(xy);
        }
        return new WB_Polygon(pts);
    }

    public static double[][] toPoint3D(List<WB_Point> pts) {
        if (pts == null)
            return null;
        double[][] ret = new double[pts.size()][3];
        for (int i = 0; i < pts.size(); ++i) {
            ret[i][0] = pts.get(i).xd();
            ret[i][1] = pts.get(i).yd();
            ret[i][2] = pts.get(i).zd();
        }
        return ret;
    }

    /**
     *
     * @param: WB_Polygon ply
     * @return:Polygon
     * @throws:
     */
    public static Polygon toJTSPolygon(WB_Polygon ply) {
        WB_Coord[] polypt = ply.getPoints().toArray();
        Coordinate[] pts = new Coordinate[polypt.length + 1];

        for (int i = 0; i < polypt.length; ++i) {
            pts[i] = new Coordinate(polypt[i].xd(), polypt[i].yd());
        }
        pts[polypt.length] = new Coordinate(polypt[0].xd(), polypt[0].yd());
        return new GeometryFactory().createPolygon(pts);
    }

    /**
     *
     * @param:
     * @return:WB_Polygon
     * @throws:
     */
    @SuppressWarnings("deprecation")
    public static WB_Polygon JTSOffset(WB_Polygon ply, double r, double distance) {
        try {
            BufferOp b1 = new BufferOp(toJTSPolygon(ply));
            b1.setEndCapStyle(BufferOp.CAP_ROUND);
            Geometry g1 = b1.getResultGeometry(-r);

            BufferOp b2 = new BufferOp(g1);
            b2.setEndCapStyle(BufferOp.CAP_ROUND);
            Geometry g2 = b2.getResultGeometry(r - distance);
            return toWB_Polygon((Polygon) g2);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param: WB_Polygon ply
     * @return:double
     * @throws:
     */
    public static double calcPlyPerimeter(WB_Polygon ply) {
        double ret = 0;
        int n = ply.getNumberOfPoints();
        for (int i = 0; i < n; ++i) {
            ret += ply.getd(i, (i + 1) % n);
        }
        return ret;
    }

    /**
     *
     * @param:
     * @return:WB_Segment []
     * @throws:
     */
    public static WB_Segment[] getSegmentsFromPolygon(WB_Polygon ply) {
        int n = ply.getNumberOfPoints();
        WB_Segment[] segs = new WB_Segment[n];
        for (int i = 0; i < n; ++i) {
            segs[i] = new WB_Segment(ply.getPoint(i), ply.getPoint((i + 1) % n));
        }
        return segs;
    }

    public static WB_Polygon[] extrudePolygon(WB_Polygon ply, WB_Vector vec) {
        WB_Coord[] pts = ply.getPoints().toArray();
        WB_Polygon[] ret = new WB_Polygon[pts.length + 2];
        ret[0] = ply;
        ret[1] = movePolygon(ply, vec);
        WB_Point[] newPts = new WB_Point[4];
        for (int i = 0; i < pts.length; ++i) {
            newPts[0] = new WB_Point(pts[i]);
            newPts[1] = new WB_Point(pts[(i + 1) % pts.length]);
            newPts[2] = new WB_Point(vec.add(pts[(i + 1) % pts.length]));
            newPts[3] = new WB_Point(vec.add(pts[i]));
            ret[i + 2] = new WB_Polygon(newPts);
        }
        return ret;
    }

    public static WB_Polygon movePolygon(WB_Polygon ply, WB_Vector vec) {
        WB_Coord[] pts = ply.getPoints().toArray();
        WB_Point[] newPts = new WB_Point[pts.length];
        for (int i = 0; i < pts.length; ++i) {
            newPts[i] = new WB_Point(vec.add(pts[i]));
        }
        return new WB_Polygon(newPts);
    }

    public static boolean isIntersect(WB_AABB aabb, WB_Segment seg) {

        WB_Coord aabb_max = aabb.getMax();
        WB_Coord aabb_min = aabb.getMin();
        WB_Coord o = seg.getOrigin();
        WB_Coord d = seg.getDirection();

        double tmin = 0;
        double tmax = 1;

        for (int i = 0; i < 3; ++i) {
            if (Math.abs(d.getd(i)) < WB_Epsilon.EPSILON) {
                if (o.getd(i) < aabb_min.getd(i) || o.getd(i) > aabb_max.getd(i)) {
                    return false;
                }
            } else {
                double od = 1.0 / d.getd(i);
                double tmp = 0;
                double t1 = (aabb_min.getd(i) - o.getd(i)) * od;
                double t2 = (aabb_max.getd(i) - o.getd(i)) * od;
                if (t1 > t2) {
                    tmp = t1;
                    t1 = t2;
                    t2 = tmp;
                }
                tmin = Math.max(tmin, t1);
                tmax = Math.min(tmax, t2);

                if (tmin > tmax)
                    return false;
            }
        }
        return true;
    }

    public static WB_Point[] IPointstoWB_Points(IPoint[] pts) {
        WB_Point[] points = new WB_Point[pts.length];
        for (int i = 0; i < pts.length; i++) {
            points[i] = IPointtoWB_Point(pts[i]);
        }
        return points;
    }

    public static IPoint[] WB_PointstoIPoints(WB_CoordCollection pts) {
        IPoint[] points = new IPoint[pts.size()];
        for (int i = 0; i < pts.size(); ++i) {
            points[i] = WB_PointtoIPoint(pts.get(i));
        }
        return points;
    }

    public static WB_Point IPointtoWB_Point(IPoint p) {
        return new WB_Point(p.x(), -p.y(), p.z());
    }

    public static IPoint WB_PointtoIPoint(WB_Coord p) {
        return new IPoint(p.xd(), p.yd(), p.zd());
    }

    public static ICurve[] WB_PolylinestoIPolyline(WB_PolyLine[] plys) {
        ICurve[] crvs = new ICurve[plys.length];
        for (int i = 0; i < plys.length; ++i) {
            crvs[i] = WB_PolylinetoIPolyline(plys[i]);
        }
        return crvs;
    }

    public static ICurve WB_PolylinetoIPolyline(WB_PolyLine ply) {
        WB_CoordCollection pts = ply.getPoints();
        IPoint[] points = WB_PointstoIPoints(pts);
        ICurve crv = new ICurve(points, 1);
        return crv;
    }

    public static void saveWB_Polyline(WB_PolyLine[] plys, String filename) {
        ICurve[] crvs = WB_PolylinestoIPolyline(plys);
        IG.save(filename);
    }

    public static WB_Polygon toWB_Polygon(WB_PolyLine ply) {
        return new WB_Polygon(ply.getPoints());
    }

    public static void toCSV(String filename, String[] in) {

    }


    /**
     * @param: WB_Polygon a
     * @param: WB_Polygon b
     * @param: double     distance
     * @return: a is within b or not;
     */
    public boolean JTSwithin(WB_Polygon a, WB_Polygon b, double distance) {
        Polygon g1 = toJTSPolygon(a);
        Polygon g2 = toJTSPolygon(b);
        return g1.isWithinDistance(g2, distance);
    }

    /**
     *
     * @param:
     * @return:WB_Polygon[]
     * @throws:
     */
    public WB_Polygon[] getRectangles(WB_Polygon ply, double distance, int N, int seed) {

        return null;
    }

    public WB_Point[] optimizedPoint(WB_Polygon ply, double distance, int N, int seed) {
        WB_Point[] resultPoint = new WB_Point[N];

        return resultPoint;

    }

    public WB_Point[] getRandomPoint(WB_Polygon ply, double distance, int N, int seed) {
        WB_Point[] pts = new WB_Point[N];
        Random random = new Random(seed);
        if (Math.abs(ply.getSignedArea()) < 1)
            return null;
        Polygon g1 = toJTSPolygon(JTSOffset(ply, 0, distance));

        for (int i = 0; i < N; ) {
            // Point g2 = new GeometryFactory().createPoint(new Coordinate(100, 100));
            Point g2 = new GeometryFactory().createPoint(new Coordinate(
                    ply.getAABB().getMinX() + random.nextDouble() * (ply.getAABB().getMaxX() - ply.getAABB().getMinX()),
                    ply.getAABB().getMinY()
                            + random.nextDouble() * (ply.getAABB().getMaxY() - ply.getAABB().getMinY())));

            // System.out.println(g2);
            if (g2.within(g1)) {
                pts[i] = new WB_Point(g2.getX(), g2.getY(), 0);
                i++;
            }
        }
        return pts;
    }

    public WB_Polygon getMinimumRectangle(WB_Polygon ply) {
        Polygon ret = (Polygon) (new MinimumDiameter(toJTSPolygon(ply))).getMinimumRectangle();
        return toWB_Polygon(ret);
    }

    public double getRectangularRatio(WB_Polygon ply) {
        WB_Polygon rect = getMinimumRectangle(ply);
        return Math.abs(ply.getSignedArea() / rect.getSignedArea());
    }

    public double getHWRatio(WB_Polygon ply) {
        WB_Polygon rect = getMinimumRectangle(ply);
        return rect.getSegment(0).getLength() / rect.getSegment(1).getLength();
    }

    public double[] getRectangleEdge(WB_Polygon ply) {
        double[] ret = new double[2];
        WB_Polygon rect = getMinimumRectangle(ply);
        ret[0] = Math.min(rect.getSegment(0).getLength(), rect.getSegment(1).getLength());
        ret[1] = Math.max(rect.getSegment(0).getLength(), rect.getSegment(1).getLength());
        return ret;
    }

    /**
     * Draw a box with two Vectors and a base Point
     *
     * @param: WB_Point  pt left bottom vertex of Box
     * @param: WB_Vector vec from pt to right up of Box
     * @param: WB_Vector Xaxis axis of Z
     * @return: void
     * @throws:
     */
    public void drawBox(WB_Point pt, WB_Vector vec, WB_Vector Xaxis) {
    }

    public void drawBox(WB_Point pt, WB_Vector vec) {
        WB_Vector Xaxis = new WB_Vector(1, 0, 0);
        drawBox(pt, vec, Xaxis);
    }

    /**
     * Draw ControlP5 Panel in a 2D canvas;
     */
    public void drawCP5() {
        this.cam.begin2d();
        this.cp5.update();
        this.cp5.draw();
        this.cam.begin3d();
    }

    /**
     * print string on (x, y) with size
     *
     * @param: str  strings need to print
     * @param: font size
     * @param: (x, y)
     * @return: void
     * @throws:
     */
    public void printOnScreen(String str, int size, float x, float y) {
        cam.begin2d();
        app.textSize(size);
        app.text(str, x, y);
        cam.begin3d();
    }

    public void printOnScreen3D(String str, int size, double x, double y, double z) {
        Vec_Guo vg = cam.getCoordinateOnScreen(x, y, z);
        printOnScreen(str, size, (float) vg.x, (float) vg.y);
    }

    public void drawCircle(PApplet app, double radius) {
        Vec_Guo v = cam.getCoordinateFromScreenOnXYPlane(app.mouseX, app.mouseY);
        WB_Circle c = new WB_Circle(v.x, v.y, radius);
        app.fill(230, 200, 180);
        app.noStroke();
        render.drawCircle(c);
    }

    public void drawPoint(final WB_Coord pts, final double r, int[] co) {
        WB_Circle c = new WB_Circle(new WB_Point(pts.xd(), pts.yd(), 0.1), r);
//		WB_Circle c = new WB_Circle(pts.xd(), pts.yd(), r);
        app.fill(co[0], co[1], co[2]);
        render.drawCircle(c);
    }
}