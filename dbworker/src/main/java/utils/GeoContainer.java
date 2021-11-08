package utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import crosby.binary.osmosis.OsmosisReader;
import loader.PbfReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import readDXF.DXFImport;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/05/21
 */
public class GeoContainer {
    public static double[] MAP_LAT_LNG = null;
    public static double[] NE_LAT_LNG = null;
    public static double[] SW_LAT_LNG = null;


    public static String BOUNDARY_FILENAME = null;
    public static String BLOCK_FILENAME = null;
    public static String CITYNAME = null;
    public static String OSM_FILENAME = null;
    public static BiMap<Long, Integer> wayId;
    public static BiMap<Long, Integer> nodeId;
    public static BiMap<Long, Integer> relationId;
    public static int wayCount;
    public static int nodeCount;
    public static int relationCount;

    public static List<Gpoi> gpois;
    public static List<GeoPoint> nodes;
    public static List<GeoPolyLine> ways;
    public static List<GeoMultiLines> relations;

    public static List<WB_Polygon> blocks;
    public static String boundary;


    public static void init() {
        gpois = new ArrayList<>();
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();
        nodeId = HashBiMap.create();
        wayId = HashBiMap.create();
        relationId = HashBiMap.create();

        wayCount = 0;
        nodeCount = 0;
        relationCount = 0;


        try {
            InputStream inputStream = new FileInputStream(GeoContainer.OSM_FILENAME);
            OsmosisReader reader = new OsmosisReader(inputStream);
            reader.setSink(new PbfReader());
            reader.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        blocks = new ArrayList<>();
        readDXF();
//        GMapLoader.loadGPoi();
        readGeoJSON();
    }


    private static void readDXF() {
        if (BLOCK_FILENAME == null) {
            System.err.println("Can't find block file, regenerate from analysis.Generate");
            return;
        }
        double[][][] polys = DXFImport.polylines_layer(BLOCK_FILENAME, "brokenLine");

        for (int i = 0; i < polys.length; ++i) {
            WB_Point[] pts = new WB_Point[polys[i].length];
            for (int j = 0; j < polys[i].length; ++j) {
                pts[j] = new WB_Point(polys[i][j]);
            }
            blocks.add(new WB_Polygon(pts));
        }

    }

    private static void readGeoJSON() {
        if (BOUNDARY_FILENAME == null) {
            System.err.println("Can't find boundary file, set as bounding box of the city");
            boundary = "ST_MakeEnvelope(" + SW_LAT_LNG[1] + ", " + SW_LAT_LNG[0] + ", "
                    + NE_LAT_LNG[1] + ", " + NE_LAT_LNG[0] + ", 4326)";
            return;
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(BOUNDARY_FILENAME));
            Gson gson = new Gson();
            JsonElement geojson = gson.fromJson(reader, JsonElement.class);

            GeometryFactory gf = new GeometryFactory();
            List<Polygon> plys = new ArrayList<>();

            for(JsonElement e : geojson.getAsJsonObject().get("features").getAsJsonArray()) {
                JsonArray g = e.getAsJsonObject().get("geometry").getAsJsonObject()
                        .get("coordinates").getAsJsonArray()
                        .get(0).getAsJsonArray();

                Coordinate[] p = new Coordinate[g.size()];
                for(int i = 0; i < g.size(); ++ i) {
                    JsonArray pt = g.get(i).getAsJsonArray();
                    p[i] = new Coordinate(pt.get(0).getAsDouble(), pt.get(1).getAsDouble());
                }
                Polygon ply = gf.createPolygon(p);
                plys.add(ply);

            }

            if(plys.size() == 1) {
                boundary = "ST_GeomFromText('" + plys.get(0).toText() + "', 4326)";
            } else {
                MultiPolygon ply = gf.createMultiPolygon(plys.toArray(new Polygon[0]));
                boundary = "ST_GeomFromText('" + ply.toText() + "', 4326)";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
