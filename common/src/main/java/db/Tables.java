package db;

import java.util.HashMap;
import java.util.Map;

/**
 * All table structure use for build archibase
 */
public class Tables {
    public static final Map<String, String[][]> tableMap;

    public static String[][] city = {
            {"name", "varchar"},
            {"lat", "float"},
            {"lon", "float"},
            {"ratio", "float"},
            {"boundary", "geometry(polygon, 4326)"},
            {"timestamp", "date"}
    };

    public static String[][] roads = {
            {"id", "int8"},
            {"geom", "geometry(linestring, 4326)"},
            {"name", "varchar"},
            {"roadType", "char(2)"},
            {"highway", "varchar"},
            {"timestamp", "date"}

    };


    public static String[][] buildings = {
            {"id", "int8"},
            {"geom", "geometry(linestring, 4326)"},
            {"name", "varchar"},
            {"building_type", "varchar"},
            {"timestamp", "date"}
    };

    public static String[][] urban_spaces = {
            {"feature_id", "int references features(id)"},
            {"id", "int8"},
            {"geom", "geometry"},
            {"name", "varchar"},
            {"timestamp", "date"}
    };

    public static String[][] features = {
            {"id", "serial"},
            {"name", "varchar"},
            {"color", "int"},
            {"and_keys", "varchar"},
            {"and_values", "varchar"},
            {"not_keys", "varchar"},
            {"not_values", "varchar"},
            {"or_keys", "varchar"},
            {"or_values", "varchar"},
            {"is_closed", "bool"}
    };

    public static String[][] blocks = {
            {"id", "serial"},
            {"geom", "geometry(linestring, 4326)"}
    };

    public static String[][] change_set = {
            {"id", "int8"},
            {"geom", "geometry"},
            {"status", "int"},
            {"tags", "hstore"},
            {"timestamp", "date"}
    };

    public static String[][] functions = {
            {"placeid", "char(255)"},
            {"lat", "float"},
            {"lng", "float"},
            {"rating", "float"},
            {"user_ratings_total", "int"},
            {"name", "char(255)"},
            {"type", "char(255)"},
            {"type_detail", "char(255)"}};



    static {
        tableMap = new HashMap<>();
        tableMap.put("city", Tables.city);
        tableMap.put("roads", Tables.roads);
        tableMap.put("buildings", Tables.buildings);
        tableMap.put("urban_spaces", Tables.urban_spaces);
        tableMap.put("features", Tables.features);
        tableMap.put("blocks", Tables.blocks);
        tableMap.put("change_set", Tables.change_set);
        tableMap.put("functions", Tables.functions);
    }
}
