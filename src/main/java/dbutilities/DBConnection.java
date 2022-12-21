package dbutilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class DBConnection
{
    static String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    Logger logger;
    
    
    public DBConnection() {
    	
    	logger= LogManager.getLogger("DB Connection Logs");
    	
    }

    /**
     * This method will take one parameter and create database server connection.
     * @param dbServer  String DB Server Name
     * @return  Object of connection class
     */

    public Connection getDBConnection(String dbServer)
    {
        // Declare the JDBC objects.
        Connection conn = null;
        String url = null;
        try
        {
            url = "jdbc:sqlserver://" + dbServer + ";integratedSecurity=true";
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            logger.error(e.getMessage());
        }
        return conn;
    }

    /**
     * This will take 3 parameter dbserver, username and password to create database server connection.
     * @param dbServer  String DB Server Name
     * @param username  String username
     * @param password  String db Password
     * @return  Object of connection class
     */
    public Connection getDBConnection(String dbServer, String username, String password)
    {
        Connection conn = null;
        String url = null;
        try
        {
            url = "jdbc:sqlserver://" + dbServer + ";username=" + username + ";password=" + password;
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            logger.error(e.getMessage());
        }
        return conn;
    }

    /**
     * This method will take 2 parameters dbserver and dbname to create database database connection.
     * @param dbServer  String DB Server Name
     * @param dbName  String DB Name
     * @return - Object of connection class
     */
    public Connection getDBConnection(String dbServer, String dbName)
    {
        Connection conn = null;
        String url = null;
        try
        {
            url = "jdbc:sqlserver://" + dbServer + ";databaseName=" + dbName + ";integratedSecurity=true";
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            logger.error(e.getMessage());
        }
        return conn;
    }

    /**
     *
     * This method will take 4 paramete dbserver, dbname, username and password to create database connection.
     * @param dbServer  String DB Server Name
     * @param dbName  String DB Name
     * @param username String DB Username Name
     * @param password  String DB Password
     * @return  Object of connection class
     */

    public Connection getDBConnection(String dbServer, String dbName, String username, String password)
    {
        Connection conn = null;
        String url = null;
        try
        {
            url = "jdbc:sqlserver://" + dbServer + ";databaseName=" + dbName + ";username=" + username + ";password=" + password;
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            logger.error(e.getMessage());
        }
        return conn;
    }
}

