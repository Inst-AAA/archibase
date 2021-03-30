package loader;

import db.Info;
import utils.GeoContainer;
import utils.Gpoi;

import java.sql.*;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/01
 */
public class GMapLoader {
    public static void loadGPoi() {
        Connection connection = null;
        try {
            Class.forName(Info.DRIVE);
            connection = DriverManager.getConnection(Info.URL, Info.USERNAME, Info.PASSWORD);
            System.out.println("Connected");

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from function;");

            while (rs.next()) {
                Gpoi g = new Gpoi();
                g.setPlaceid(rs.getString("placeid").trim());
                g.setLat(rs.getFloat("lat"));
                g.setLng(rs.getFloat("lng"));
                g.setRating(rs.getFloat("rating"));
                g.setUserRatingsTotal(rs.getInt("user_ratings_total"));
                g.setChinese(rs.getBoolean("is_chinese"));
                g.setName(rs.getString("name").trim());
                g.setType(rs.getString("type").trim());
                g.setTypeDetail(rs.getString("type_detail").trim());

                GeoContainer.gpois.add(g);
            }
            rs.close();
            stmt.close();

            System.out.println("gpois = " + GeoContainer.gpois.size());

        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
