package download;


import db.Info;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/06
 */
public class DBUpdater {
    private Connection conn = null;
    private Statement stmt = null;

    public DBUpdater() {
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

}
