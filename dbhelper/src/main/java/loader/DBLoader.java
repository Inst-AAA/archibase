package loader;

import db.Info;
import element.*;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import utils.ChangeSet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBLoader {
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;

    public DBLoader() {
        conn = getConnection();
    }

    public Connection getConnection() {
        conn = null;

        try {
            conn = DriverManager.getConnection(Info.URL, Info.USERNAME, Info.PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public City collectCity() {
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select name, lat, lon, ratio, st_astext(boundary), timestamp from city");
            WKTReader reader = new WKTReader();

            while(rs.next()) {
                City c = new City();
                c.setName(rs.getString(1).trim());
                c.setLat(rs.getDouble(2));
                c.setLon(rs.getDouble(3));
                c.setRatio(rs.getDouble(4));
                c.setBoundary((Polygon) reader.read(rs.getString(5)));
                c.setTimestamp(rs.getTimestamp(6));

                return c;
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Road> collectRoad() {
        List<Road> roads = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id, st_astext(geom), name, roadtype, highway, timestamp from roads;");

            WKTReader reader = new WKTReader();

            while (rs.next()) {
                Road r = new Road();
                r.setOsm_id(rs.getLong(1));
                r.setPly((LineString) reader.read(rs.getString(2)));
                if (rs.getString(3) != null)
                    r.setName(rs.getString(3).trim());
                r.setRoadType(rs.getString(4).trim());
                r.setHighway(rs.getString(5));
                r.setTimestamp(rs.getTimestamp(6));

                roads.add(r);
            }
            System.out.println("Finish collecting roads");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return roads;
    }

    public List<Road> collectRoad(double xmin, double ymin, double xmax, double ymax, int srid) {
        List<Road> roads = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            String query = String.format("select id, st_astext(geom), name, roadtype, highway, timestamp from roads where roads.geom && st_makeenvelope(%f,%f,%f,%f, %d)", xmin, ymin, xmax, ymax, srid);
            ResultSet rs = stmt.executeQuery(query);

            WKTReader reader = new WKTReader();

            while (rs.next()) {
                Road r = new Road();
                r.setOsm_id(rs.getLong(1));
                r.setPly((LineString) reader.read(rs.getString(2)));
                if (rs.getString(3) != null)
                    r.setName(rs.getString(3).trim());
                r.setRoadType(rs.getString(4).trim());
                r.setHighway(rs.getString(5));
                r.setTimestamp(rs.getTimestamp(6));

                roads.add(r);
            }
            System.out.println("Finish collecting roads");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return roads;
    }

    public List<Building> collectBuildings(double xmin, double ymin, double xmax, double ymax, int srid) {
        List<Building> buildings = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            String query = String.format("select id, st_astext(geom), name, building_type, timestamp from buildings where buildings.geom && st_makeenvelope(%f,%f,%f,%f, %d)", xmin, ymin, xmax, ymax, srid);
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);

            WKTReader reader = new WKTReader();

            while (rs.next()) {
                Building r = new Building();
                r.setOsm_id(rs.getLong(1));
                r.setPly((LineString) reader.read(rs.getString(2)));
                if (rs.getString(3) != null)
                    r.setName(rs.getString(3).trim());
                r.setBuilding(rs.getString(4).trim());
                r.setTimestamp(rs.getTimestamp(5));

                buildings.add(r);
            }
            System.out.println("Finish collecting buildings");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return buildings;
    }

    public List<Building> collectBuildings() {
        List<Building> buildings = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id, st_astext(geom), name, building_type, timestamp from buildings;");

            WKTReader reader = new WKTReader();

            while (rs.next()) {
                Building r = new Building();
                r.setOsm_id(rs.getLong(1));
                r.setPly((LineString) reader.read(rs.getString(2)));
                if (rs.getString(3) != null)
                    r.setName(rs.getString(3).trim());
                r.setBuilding(rs.getString(4).trim());
                r.setTimestamp(rs.getTimestamp(5));

                buildings.add(r);
            }
            System.out.println("Finish collecting buildings");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return buildings;
    }

    public List<ChangeSet> collectChangeSet() {
        List<ChangeSet> changeSets = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id, st_astext(geom), status from change_set;");

            WKTReader reader = new WKTReader();

            while (rs.next()) {
                ChangeSet r = new ChangeSet();
                r.setOsm_id(rs.getLong(1));
                r.setGeom(reader.read(rs.getString(2)));
                r.setStatus(rs.getInt(3));

                changeSets.add(r);
            }
            System.out.println("Finish collecting changeset");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return changeSets;
    }

    public List<UrbanSpace> collectUrbanSpace() {
        List<UrbanSpace> urbanSpaces = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select features.name, urban_spaces.id, st_astext(geom), urban_spaces.name, timestamp from urban_spaces, features where feature_id = features.id;");

            WKTReader reader = new WKTReader();

            while (rs.next()) {
                UrbanSpace r = new UrbanSpace();
                r.setFeature(rs.getString(1));
                r.setOsm_id(rs.getLong(2));
                r.setGeom(reader.read(rs.getString(3)));
                if (rs.getString(4) != null)
                    r.setName(rs.getString(4).trim());
                r.setTimestamp(rs.getTimestamp(5));

                urbanSpaces.add(r);
            }
            System.out.println("Finish collecting urban spaces");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return urbanSpaces;
    }

    public List<Block> collectBlock() {
        List<Block> blocks = new ArrayList<>();

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id, st_astext(geom) from blocks;");

            WKTReader reader = new WKTReader();

            while (rs.next()) {
                Block b = new Block();
                b.setID(rs.getLong(1));
                b.setPly((LineString) reader.read(rs.getString(2)));
                blocks.add(b);
            }

            System.out.println("Finish collecting blocks");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return blocks;
    }
}
