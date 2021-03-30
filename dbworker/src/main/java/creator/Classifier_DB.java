package creator;

import db.Utils;
import helper.ColorHelper;
import org.locationtech.jts.geom.LineString;
import utils.GeoGeom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/27
 */
public class Classifier_DB {
    public static final Map<String, int[]> featureColor;
    public static final List<String[]> tableContent;
    public static final Map<String, Integer> featureID;

    static {
        featureColor = new HashMap<>();
        featureID = new HashMap<>();
        Utils db = new Utils();
        tableContent = db.getTable("features");


        for (String[] row : tableContent) {

            featureID.put(row[1], Integer.parseInt(row[0]) - 1);
            featureColor.put(row[1], ColorHelper.hexToRGB(Integer.parseInt(row[2])));
        }

    }

    public static boolean getGeometryType(String type, GeoGeom geom) {
//        System.out.println("Type: " + type);


        int id = featureID.get(type);
        String[] row = tableContent.get(id);
//        System.out.println("id = " + id + "row = " + row[1] + " id = " + row[0]);

        String[] and_keys = row[3] == null ? new String[0] : row[3].split(";");
        String[] and_values = row[4] == null ? new String[0] : row[4].split(";");
        String[] not_keys = row[5] == null ? new String[0] : row[5].split(";");
        String[] not_values = row[6] == null ? new String[0] : row[6].split(";");
        String[] or_keys = row[7] == null ? new String[0] : row[7].split(";");
        String[] or_values = row[8] == null ? new String[0] : row[8].split(";");


        for (String key : and_keys) {
//            System.out.println(key);
            if (!geom.getTags().containsKey(key)) return false;
        }

        for (String value : and_values) {
//            System.out.println(value);
            if (!geom.getTags().containsValue(value)) return false;
        }

        for (String key : not_keys) {
//            System.out.println(key);
            if (geom.getTags().containsKey(key)) return false;
        }

        for (String value : not_values) {
//            System.out.println(value);
            if (geom.getTags().containsValue(value)) return false;
        }

        if (row[9] != null && geom.getGeometry().getGeometryType().equals("LineString")) {
            LineString ls = (LineString) geom.getGeometry();
            return ls.isClosed() ^ Boolean.parseBoolean(row[9]);
        }

        for (String key : or_keys) {
//            System.out.println(key);
            if (geom.getTags().containsKey(key)) return true;
        }

        for (String value : or_values) {
//            System.out.println(value);
            if (geom.getTags().containsValue(value)) return true;
        }

        return false;
    }


}
