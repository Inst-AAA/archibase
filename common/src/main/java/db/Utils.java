package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;

    public Utils() {
        conn = getConnection();
    }

    /**
     * Open a database connection
     * @return Connection
     */
    public Connection getConnection() {
        conn = null;

        try {
            conn = DriverManager.getConnection(Info.URL, Info.USERNAME, Info.PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * check and return a valid sql string
     * @param str any string
     * @return String
     */
    public String checkString(String str) {
        String ustr = str.replace("\"", "");
        return ustr.replace("'", "''");
    }

    /**
     * Insert into table with values
     * @param tableName table name stored in db.Tables
     * @param numOfPrimaryKey primary keys number of the table
     * @param values a string list include values of each column
     */
    public void insertFullData(String tableName, int numOfPrimaryKey, String[] values) {
        try {
            stmt = conn.createStatement();
            StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
            String[][] table = Tables.tableMap.get(tableName);
            assert values.length == table.length;

            for (int i = 0; i < table.length; ++i) {
                sql.append(table[i][0]);
                if (i == table.length - 1) sql.append(") values (");
                else sql.append(", ");
            }

            for (int i = 0; i < values.length; ++i) {
                sql.append(values[i]);
                if (i == values.length - 1) sql.append(") on conflict (");
                else sql.append(", ");
            }
            for (int i = 0; i < numOfPrimaryKey; ++i) {
                sql.append(table[i][0]);
                if (i == numOfPrimaryKey - 1) sql.append(") do nothing;");
                else sql.append(", ");
            }

            System.out.println(sql);
            stmt.executeUpdate(sql.toString());
            stmt.close();
            System.out.println("records created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Create simple table, the number of primary key should specified
     * @param tableName table name
     * @param numOfPrimaryKey number of primary key
     */
    public void createTable(String tableName, int numOfPrimaryKey) {
        try {
            stmt = conn.createStatement();
            StringBuilder sql = new StringBuilder("create table " + tableName + " (");
            String[][] table = Tables.tableMap.get(tableName);
            for (int i = 0; i < table.length; ++i) {
                sql.append(table[i][0]).append(" ").append(table[i][1]);
                if (numOfPrimaryKey == 1 && i == 0) {
                    sql.append(" primary key");
                }
                if (i != table.length - 1) {
                    sql.append(",");
                }
            }

            if (numOfPrimaryKey >= 2) {
                sql.append(", primary key (");
                for (int i = 0; i < numOfPrimaryKey; ++i) {
                    sql.append(table[i][0]);
                    if (i != numOfPrimaryKey - 1) {
                        sql.append(",");
                    }
                }
                sql.append("));");
            } else {
                sql.append(");");
            }
            System.out.println(sql.toString());

            stmt.executeUpdate(sql.toString());
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created.");
    }

    /**
     * get full request result of table
     * @param name
     * @return
     */
    public List<String[]> getTable(String name) {
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + name + " order by id;");

            String[][] table = Tables.tableMap.get(name);
            int N = table.length;

            List<String[]> ret = new ArrayList<>();

            while (rs.next()) {
                String[] r = new String[N];
                for (int i = 1; i <= N; ++i) {
                    r[i - 1] = rs.getString(i);
                }
                ret.add(r);
            }

            return ret;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}