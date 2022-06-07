package utils;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;

public class DBConnector {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection connection = null;
    private static String connStr = "jdbc:mysql://localhost:3306/calculations" + "?serverTimezone = UTC";

    public static void dbConnect() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            throw e;
        }

        System.out.println("JDBC Driver has been registered!");

        try{
            connection = DriverManager.getConnection(connStr, "root", "12345");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console" + e);
            throw e;
        }
    }

    public static void dbDisconnect() throws SQLException {
        try{
            if(connection != null && !connection.isClosed()) connection.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void dbExecuteQuery(String sqlStmt) throws ClassNotFoundException, SQLException {
        Statement stmt = null;
        try {
            dbConnect();
            stmt = connection.createStatement();
            stmt.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            System.out.println("Problem occured at dbExecuteQuery operation" + e);
        } finally {
            if(stmt != null) stmt.close();
            dbDisconnect();
        }
    }

    public static ResultSet dbExecute(String sqlQuery) throws ClassNotFoundException, SQLException {
        Statement statement = null;
        ResultSet rs = null;
        CachedRowSetImpl crs = null;

        try {
            dbConnect();
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlQuery);
            crs = new CachedRowSetImpl();
            crs.populate(rs);
        } catch (SQLException e) {
            System.out.println("Error occured is dbExecute operation" + e);
        } finally {
            if(rs != null) rs.close();
            if(statement != null) statement.close();
            dbDisconnect();
        }
        return crs;
    }
}