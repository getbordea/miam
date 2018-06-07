package fr.ubx.bph.erias.miam.db;

import static org.junit.Assert.assertNotEquals;

import java.sql.Connection;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import fr.ubx.bph.erias.miam.db.JDBCConnection;

public class JDBCConnectionTest {

  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  @Test
  public void startConnectionTest() {
    JDBCConnection.startConnection();

    Connection conn = JDBCConnection.getConn();

    assertNotEquals(null, conn);

    System.out.println("Successfully connected to the dabase.");

    JDBCConnection.closeConnection();
  }

}
