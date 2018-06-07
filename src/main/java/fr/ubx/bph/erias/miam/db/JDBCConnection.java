/**
 * 
 */
package fr.ubx.bph.erias.miam.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ubx.bph.erias.miam.core.Config;

/**
 * @author Georgeta Bordea
 * 
 */
public class JDBCConnection {

  private static Logger logger =
      Logger.getLogger(JDBCConnection.class.getName());

  private static Connection conn = null;

  public static Connection getConn() {
    return conn;
  }

  private static void setConn(Connection conn) {
    JDBCConnection.conn = conn;
  }

  public static void startConnection() {
    Connection conn = null;

    String dbUrl = "";
    try {

      Class.forName(Config.getProperty("driver"));

      dbUrl = Config.getProperty("databaseUrl") + "//"
          + Config.getProperty("databaseHost") + ":"
          + Config.getProperty("databasePort") + "/"
          + Config.getProperty("databaseName");

      conn =
          DriverManager.getConnection(dbUrl, Config.getProperty("databaseUser"),
              Config.getProperty("databasePassword"));

    } catch (SQLException e) {
      e.printStackTrace();
      logger.log(Level.FATAL, e.getLocalizedMessage());
      logger.log(Level.FATAL, dbUrl);
      logger.log(Level.FATAL, "could not get a connection to the database..");
      System.exit(1);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      logger.log(Level.FATAL, e.getLocalizedMessage());
      logger.log(Level.FATAL,
          "could not find the driver to connect to the database..");
      System.exit(1);
    }

    setConn(conn);
  }

  public static void closeConnection() {
    try {
      conn.close();
    } catch (SQLException e) {
      logger.log(Level.FATAL, e.getMessage());
      logger.log(Level.FATAL,
          "could not close the connection to the database..");
      System.exit(1);
    }
  }
}
