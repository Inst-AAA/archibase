package analysis;

import org.locationtech.jts.geom.LineString;
import type.OsmTypeDetail;
import utils.GeoGeom;

import java.util.HashMap;
import java.util.Map;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/05/24
 */
public class Classifier {

    public static final Map<String, int[]> buildingType;

    static {
        buildingType = new HashMap<>();
        buildingType.put("highway", new int[]{0, 0, 0});
        buildingType.put("building", new int[]{0, 0, 0});
        buildingType.put("park", new int[]{225, 200, 140});
        buildingType.put("piazza", new int[]{225, 200, 140});
        buildingType.put("blue", new int[]{100, 120, 150});
        buildingType.put("green", new int[]{112, 122, 80});
        buildingType.put("industry", new int[]{160, 130, 110});
        buildingType.put("block", new int[]{223, 223, 223});
        buildingType.put("school", new int[]{173, 113, 133});
        buildingType.put("religious", new int[]{180, 40, 45});
        buildingType.put("shop", new int[]{210, 120, 90});
        buildingType.put("health", new int[]{255, 210, 220});
        buildingType.put("transport", new int[]{25, 55, 205});

    }


    public static boolean getGeometryType(String type, GeoGeom geom) {
        boolean flag;
        switch (type) {
            case "building":
                flag = geom.getTags().containsKey("building");
                if (geom.getGeometry().getGeometryType().equals("LineString")) {
                    LineString ls = (LineString) geom.getGeometry();
                    flag &= ls.isClosed();
                }
                return flag;
            case "highway":
                flag = geom.getTags().containsKey("highway");
                if (geom.getGeometry().getGeometryType().equals("LineString")) {
                    LineString ls = (LineString) geom.getGeometry();
                    flag &= (!ls.isClosed());
                }
                return flag;
            case "park":
                flag = geom.getTags().containsKey("leisure");
                flag &= geom.getTags().containsKey("name");
                flag &= geom.getTags().containsValue("park");
                return flag;
            case "piazza":
                flag = geom.getTags().containsKey("highway");
                flag &= geom.getTags().containsKey("area");
                flag &= geom.getTags().containsKey("name");
                if (!flag) return false;
                String name = geom.getTags().get("name");
                return !name.contains("Via");
            case "school":
                flag = geom.getTags().containsKey("amenity");
                if (!flag) return false;
                String value = geom.getTags().get("amenity");
                return (value.equals("school")
                        || value.equals("college")
                        || value.equals("library")
                        || value.equals("kindergarten")
                        || value.equals("university"));
            case "green":
                flag = geom.getTags().containsValue("grass");
                flag |= geom.getTags().containsValue("basin");
                flag |= geom.getTags().containsValue("forest");
                flag |= geom.getTags().containsValue("farmland");
                flag |= geom.getTags().containsValue("plant_nursery");
                flag |= geom.getTags().containsValue("meadow");
                flag |= geom.getTags().containsValue("orchard");
                flag |= geom.getTags().containsValue("cemetery");
                flag |= geom.getTags().containsValue("allotments");
                flag |= geom.getTags().containsValue("greenfield");
                flag |= geom.getTags().containsValue("garden");
                flag |= geom.getTags().containsValue("stadium");
                flag |= geom.getTags().containsValue("tree");
                flag |= geom.getTags().containsValue("wetland");
                return flag;
            case "blue":
                flag = geom.getTags().containsKey("waterway");
                flag |= geom.getTags().containsValue("reservoir");
                flag |= geom.getTags().containsValue("salt_pond");
                flag |= geom.getTags().containsValue("swimming_pool");
                flag |= geom.getTags().containsValue("water_park");
                flag |= geom.getTags().containsValue("water");
                flag |= geom.getTags().containsValue("wetland");
                flag |= geom.getTags().containsValue("glacier");
                flag |= geom.getTags().containsValue("bay");
                flag |= geom.getTags().containsValue("strait");
                flag |= geom.getTags().containsValue("cape");
                flag |= geom.getTags().containsValue("beach");
                flag |= geom.getTags().containsValue("coastline");
                flag |= geom.getTags().containsValue("reef");
                flag |= geom.getTags().containsValue("spring");
                flag |= geom.getTags().containsValue("hot_spring");
                flag |= geom.getTags().containsValue("geyser");
                flag |= geom.getTags().containsValue("blowhole");
                return flag;
            case "industry":
                return geom.getTags().containsValue("industrial");
            case "religious":
                flag = geom.getTags().containsValue("place_of_worship");
                flag |= geom.getTags().containsValue("church");
                flag |= geom.getTags().containsValue("cathedral");
                flag |= geom.getTags().containsValue("chapel");
                flag |= geom.getTags().containsValue("religious");
                flag |= geom.getTags().containsValue("temple");
                flag |= geom.getTags().containsValue("synagogue");
                flag |= geom.getTags().containsValue("shrine");
                flag |= geom.getTags().containsValue("regional");
                return flag;
            case "shop":
                flag = geom.getTags().containsKey("shop");
                flag |= geom.getTags().containsValue("retail");
                flag |= geom.getTags().containsValue("commercial");
                flag |= geom.getTags().containsValue("kiosk");
                flag |= geom.getTags().containsValue("office");
                flag |= geom.getTags().containsValue("supermarket");
                flag |= geom.getTags().containsValue("warehouse");
                flag |= geom.getTags().containsValue("bar");
                flag |= geom.getTags().containsValue("bbq");
                flag |= geom.getTags().containsValue("cafe");
                flag |= geom.getTags().containsValue("food_court");
                flag |= geom.getTags().containsValue("ice_cream");
                flag |= geom.getTags().containsValue("restaurant");
                flag |= geom.getTags().containsValue("atm");
                flag |= geom.getTags().containsValue("cinema");
                flag |= geom.getTags().containsValue("bank");
                return flag;
            case "health":
                flag = geom.getTags().containsValue("hospital");
                flag |= geom.getTags().containsValue("doctors");
                flag |= geom.getTags().containsValue("clinic");
                flag |= geom.getTags().containsValue("dentist");
                flag |= geom.getTags().containsValue("pharmacy");
                flag |= geom.getTags().containsValue("nursing_home");
                return flag;
            case "transport":
                flag = geom.getTags().containsKey("public_transport");
                flag |= geom.getTags().containsKey("railway");
                flag |= geom.getTags().containsKey("route");
                flag |= geom.getTags().containsValue("transportation");
                flag |= geom.getTags().containsValue("bicycle_parking");
                flag |= geom.getTags().containsValue("bicycle_repair_station");
                flag |= geom.getTags().containsValue("bicycle_rental");
                flag |= geom.getTags().containsValue("boat_rental");
                flag |= geom.getTags().containsValue("boat_sharing");
                flag |= geom.getTags().containsValue("bus_station");
                flag |= geom.getTags().containsValue("bus_stop");
                flag |= geom.getTags().containsValue("car_rental");
                flag |= geom.getTags().containsValue("car_sharing");
                flag |= geom.getTags().containsValue("car_wash");
                flag |= geom.getTags().containsValue("vehicle_inspection");
                flag |= geom.getTags().containsValue("charging_station");
                flag |= geom.getTags().containsValue("ferry_terminal");
                flag |= geom.getTags().containsValue("fuel");
                flag |= geom.getTags().containsValue("grit_bin");
                flag |= geom.getTags().containsValue("motorcycle_parking");
                flag |= geom.getTags().containsValue("parking");
                flag |= geom.getTags().containsValue("taxi");
                flag |= geom.getTags().containsValue("parking_space");
                flag |= geom.getTags().containsValue("ticket");
                return flag;
            default:
                System.err.println("Error: Type not support!");
                return false;
        }
    }


    public static String getHighwayType(GeoGeom geom) {
        if (!geom.getTags().containsKey("highway")) return null;

        String key = geom.getTags().get("highway");
        if (OsmTypeDetail.roadMap.containsKey(key)) {
            OsmTypeDetail.Road road = OsmTypeDetail.roadMap.get(key);
            return road.name();
        }
        return null;
    }

    public static boolean isPark(GeoGeom geom) {
        boolean flag = geom.getTags().containsKey("leisure");
        flag &= geom.getTags().containsKey("name");
        flag &= geom.getTags().containsValue("park");
        return flag;
    }

    public static boolean isPiazza(GeoGeom geom) {
        boolean flag = geom.getTags().containsKey("highway");
        flag &= geom.getTags().containsKey("area");
        flag &= geom.getTags().containsKey("name");
        if (!flag) return false;
        String name = geom.getTags().get("name");
        return !name.contains("Via");
    }

    public static boolean isSchool(GeoGeom geom) {
        boolean flag = geom.getTags().containsKey("amenity");
        if (!flag) return false;
        String value = geom.getTags().get("amenity");
        return (value.equals("school")
                || value.equals("college")
                || value.equals("library")
                || value.equals("kindergarten")
                || value.equals("university"));
    }

    public static boolean isBuilding(GeoGeom geom) {
        boolean flag = geom.getTags().containsKey("building");
        LineString ls = (LineString) geom.getGeometry();
        flag &= ls.isClosed();
        return flag;
    }

    public static boolean isIndustry(GeoGeom geom) {
        return geom.getTags().containsValue("industrial");
    }

    public static boolean isGreen(GeoGeom geom) {
        boolean flag = geom.getTags().containsValue("grass");
        flag |= geom.getTags().containsValue("basin");
        flag |= geom.getTags().containsValue("forest");
        flag |= geom.getTags().containsValue("farmland");
        flag |= geom.getTags().containsValue("plant_nursery");
        flag |= geom.getTags().containsValue("meadow");
        flag |= geom.getTags().containsValue("orchard");
        flag |= geom.getTags().containsValue("cemetery");
        flag |= geom.getTags().containsValue("allotments");
        flag |= geom.getTags().containsValue("greenfield");
        flag |= geom.getTags().containsValue("garden");
        flag |= geom.getTags().containsValue("stadium");
        flag |= geom.getTags().containsValue("tree");
        flag |= geom.getTags().containsValue("wetland");
        return flag;
    }

    public static boolean isBlue(GeoGeom geom) {
        boolean flag = geom.getTags().containsKey("waterway");
        flag |= geom.getTags().containsValue("reservoir");
        flag |= geom.getTags().containsValue("salt_pond");
        flag |= geom.getTags().containsValue("swimming_pool");
        flag |= geom.getTags().containsValue("water_park");
        flag |= geom.getTags().containsValue("water");
        flag |= geom.getTags().containsValue("wetland");
        flag |= geom.getTags().containsValue("glacier");
        flag |= geom.getTags().containsValue("bay");
        flag |= geom.getTags().containsValue("strait");
        flag |= geom.getTags().containsValue("cape");
        flag |= geom.getTags().containsValue("beach");
        flag |= geom.getTags().containsValue("coastline");
        flag |= geom.getTags().containsValue("reef");
        flag |= geom.getTags().containsValue("spring");
        flag |= geom.getTags().containsValue("hot_spring");
        flag |= geom.getTags().containsValue("geyser");
        flag |= geom.getTags().containsValue("blowhole");
        return flag;
    }


}
