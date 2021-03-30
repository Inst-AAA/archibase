package helper;

import dxfExporter.Constants;
import dxfExporter.DXFData;
import dxfExporter.DXFExport;
import dxfExporter.DXFLayer;
import dxfExporter.DXFPoint;
import org.locationtech.jts.geom.Coordinate;
import wblut.geom.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于输出DXF文件的方法整合
 *
 * @author 霄侠
 */
public class ExportDXF {
    /**
     * DXF图层对应数字
     */
    public static Integer MARK = 0, BROKEN = 1, DOT = 2, TEXT = 3;
    /**
     * 写数字的类
     */
    private Number number;
    /**
     * 字体高度
     */
    private double height = 2;
    /**
     * DXF输出总设置
     */
    private DXFExport wt;
    /**
     * DXF数据存储
     */
    private DXFData dt;
    /**
     * DXF图层
     */
    private DXFLayer markLine, brokenLine, dotLine, text;
    /**
     * 图层与数字的匹配关系
     */
    private Map<Integer, DXFLayer> map;

    /**
     * 初始化
     */
    public ExportDXF() {
        setLayer();
        setMap();
    }

    /**
     * 测试程序
     *
     * @param args
     */
    public static void main(String[] args) {
        WB_Point a = new WB_Point(0, 1, 0);
        WB_Point b = new WB_Point(10, 1, 0);
        WB_Point c = new WB_Point(8, 2, 0);
        WB_Point d = new WB_Point(5, 3, 0);
        WB_Polygon polygon = new WB_Polygon(a, b, c, d);
        ExportDXF dxf = new ExportDXF();
        dxf.add(polygon, ExportDXF.BROKEN);
        dxf.save("F:/562.dxf");
    }

    /**
     * 设置图层与数字的匹配关系
     */
    private void setMap() {
        map = new LinkedHashMap<>();
        map.put(MARK, markLine);
        map.put(BROKEN, brokenLine);
        map.put(DOT, dotLine);
        map.put(TEXT, text);
    }

    /**
     * 设置图层颜色，名称
     */
    private void setLayer() {
        wt = new DXFExport();
        wt.AutoCADVer = Constants.DXFVERSION_R2000;
        dt = new DXFData();

        // 画刻线
        markLine = new DXFLayer("markLine");
        markLine.setColor(Constants.convertColorRGBToDXF(Color.YELLOW));

        // 画断线
        brokenLine = new DXFLayer("brokenLine");
        brokenLine.setColor(Constants.convertColorRGBToDXF(Color.RED));

        // 画虚线
        dotLine = new DXFLayer("dotLine");
        dotLine.setColor(Constants.convertColorRGBToDXF(Color.BLUE));

        // 画字
        text = new DXFLayer("text");
        text.setColor(Constants.convertColorRGBToDXF(Color.GREEN));
    }

    /**
     * 在指定图层添加多个WB_Polygon/WB_PolyLine
     *
     * @param polys 多边形/多段线集合
     * @param layer 指定图层
     */
    public void add(List<?> polys, Integer layer) {
        String name = polys.get(0).getClass().getName();
        setEnvironment(layer);
        switch (name) {
            case "wblut.geom.WB_Polygon":
                for (int i = 0; i < polys.size(); i++) {
                    drawpolygon(wt, dt, (WB_Polygon) polys.get(i));
                }
                break;
            case "wblut.geom.WB_PolyLine":
                for (int i = 0; i < polys.size(); i++) {
                    drawWBPolyLine(wt, dt, (WB_PolyLine) polys.get(i));
                }
            default:
                break;
        }
    }

    /**
     * 添加圆
     *
     * @param centers
     * @param radius
     * @param layer
     */
    public void add(List<WB_Coord> centers, double radius, Integer layer) {
        setEnvironment(layer);
        for (int i = 0; i < centers.size(); i++) {
            drawCircle(wt, dt, centers.get(i), radius);
        }
    }

    /**
     * 在指定图层添加一个WB_Polygon
     *
     * @param poly  多边形
     * @param layer 指定图层
     */
    public void add(WB_Polygon poly, Integer layer) {
        setEnvironment(layer);
        drawpolygon(wt, dt, poly);
    }

    /**
     * 添加圆
     *
     * @param center 圆心
     * @param radius 半径
     * @param layer  图层
     */
    public void add(WB_Coord center, double radius, Integer layer) {
        setEnvironment(layer);
        drawCircle(wt, dt, center, radius);
    }

    /**
     * 添加多段线
     *
     * @param line  多段线
     * @param layer 图层
     */
    public void add(WB_PolyLine line, Integer layer) {
        setEnvironment(layer);
        drawWBPolyLine(wt, dt, line);
    }


    /**
     * 设置指定图层的绘图环境 s
     *
     * @param layer 指定图层
     */
    private void setEnvironment(Integer layer) {
        wt.setCurrentLayer(map.get(layer));
        dt = new DXFData();
        dt.LayerName = map.get(layer).getName();
        dt.Color = map.get(layer).getColor();
    }

    /**
     * 按指定路径存储DXF文件
     *
     * @param filePath 文件路径
     */
    public void save(String filePath) {
        try {
            wt.saveToFile(filePath);
        } catch (Exception excpt) {
        } finally {
            wt.finalize();
            System.out.println("dxf saved");
        }
        System.out.println("finish-export");
    }

    /**
     * 画直线
     *
     * @param wt
     * @param dt
     * @param a
     * @param b
     */
    @SuppressWarnings("unused")
    private void drawline(DXFExport wt, DXFData dt, WB_Coord a, WB_Coord b) {
        dt.Point = new DXFPoint(a.xf(), a.yf(), 0);
        dt.Point1 = new DXFPoint(b.xf(), b.yf(), 0);
        wt.addLine(dt);
    }

    /**
     * 画圆
     *
     * @param wt
     * @param dt
     * @param p
     * @param radius
     */
    private void drawCircle(DXFExport wt, DXFData dt, WB_Coord p, double radius) {
        dt.Point = new DXFPoint(p.xf(), p.yf(), 0);
        dt.Radius = (float) radius;
        wt.addCircle(dt);
    }

    /**
     * 画多边形
     *
     * @param wt
     * @param dt
     * @param polygon
     */
    private void drawpolygon(DXFExport wt, DXFData dt, WB_Polygon polygon) {
        WB_CoordCollection coords = polygon.getPoints();
        drawWB_CoordCollection(wt, dt, coords, true);
    }

    /**
     * 画多段线
     *
     * @param wt
     * @param dt
     * @param polyLine 多段线
     */
    private void drawWBPolyLine(DXFExport wt, DXFData dt, WB_PolyLine polyLine) {
        WB_CoordCollection coords = polyLine.getPoints();
        drawWB_CoordCollection(wt, dt, coords, false);
    }

    /**
     * 按点集绘制多段线
     *
     * @param wt
     * @param dt
     * @param coords 点集
     * @param close  该多段线是否闭合
     */
    private void drawWB_CoordCollection(DXFExport wt, DXFData dt, WB_CoordCollection coords, boolean close) {
        dt.Count = coords.size();
        if (close)
            dt.Count++;
        dt.Points = new ArrayList<DXFPoint>();
        for (int i = 0; i < coords.size(); i++) {
            addPoint(dt, coords.get(i));
        }
        if (close)
            addPoint(dt, coords.get(0));
        wt.addPolyline(dt);
    }

    /**
     * 添加点
     *
     * @param dt
     * @param coord
     */
    @SuppressWarnings("unchecked")
    private void addPoint(DXFData dt, WB_Coord coord) {
        dt.Points.add(new DXFPoint(coord.xf(), coord.yf(), 0));
    }

    /**
     * 添加Coordinate数组
     *
     * @param wt
     * @param dt
     * @param coords
     */
    @SuppressWarnings("unchecked")
    private void drawCoords(DXFExport wt, DXFData dt, Coordinate[] coords) {
        dt.Count = coords.length;
        dt.Points = new ArrayList<DXFPoint>();
        for (int i = 0; i < coords.length; i++) {
            dt.Points.add(new DXFPoint((float) coords[i].x, (float) coords[i].y, 0));
        }
        wt.addPolyline(dt);
    }

}