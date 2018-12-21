/**
 * 
 */
package fr.ubx.bph.erias.miam.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.rio.RDFFormat;

import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;

/**
 * @author Georgeta Bordea
 *
 */
public class SnarlConnection {

  private static Logger logger =
      Logger.getLogger(SnarlConnection.class.getName());

  private static Connection conn = null;
  private static AdminConnection aAdminConnection = null;

  private static Stardog aStardog = null;

  public static Connection getConn() {
    return conn;
  }

  private static void setConn(Connection conn) {
    SnarlConnection.conn = conn;
  }

  public static AdminConnection getAdminConn() {
    return aAdminConnection;
  }

  private static void setAdminConnection(AdminConnection aAdminConnection) {
    SnarlConnection.aAdminConnection = aAdminConnection;
  }

  /**
   * This method will start a connection and create a temporary database with
   * data from the file provided
   * 
   * @param rdfFile
   * @throws StardogException
   * @throws FileNotFoundException
   */
  public static void startConnection(String rdfFile, String connectionName,
      Boolean dropDatabase) throws StardogException, FileNotFoundException {

    // With our admin connection, we're able to see if the database for this
    // example already exists, and
    // if it does, we want to drop it and re-create so that we can run the
    // example from a clean database.
    if (dropDatabase && aAdminConnection.list().contains(connectionName)) {
      aAdminConnection.drop(connectionName);
    }

    // Create a disk-based database with default settings
    aAdminConnection.disk(connectionName).create();

    // Using the SNARL API
    // -------------------
    // Now that we've created our database for the example, let's open a
    // connection to it. For that we use the
    // [ConnectionConfiguration](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/ConnectionConfiguration.html)
    // to configure and open a new connection to a database.
    //
    // We'll use the configuration to specify which database we want to
    // connect to as well as our login information,
    // then we can obtain a new connection.

    conn = ConnectionConfiguration.to(connectionName)
        .credentials("admin", "admin").connect();
    // All changes to a database *must* be performed within a transaction.
    // We want to add some data to the database
    // so we can begin firing off some queries, so first, we'll start a
    // new transaction.
    conn.begin();

    // The SNARL API provides fluent objects for adding & removing data
    // from a database. Here we'll use the
    // [Adder](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/Adder.html)
    // to read in an N3 file
    // from disk containing the 10k triples SP2B dataset. Actually, for
    // RDF data coming from a stream or from
    // disk, we'll use the helper class
    // [IO](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/IO.html)
    // for this task. `IO` will automatically close the stream once the
    // data has been read.
    conn.add().io().format(RDFFormat.RDFXML)
        .stream(new FileInputStream(rdfFile));

    // You're not restricted to adding, or removing, data from a file. You
    // can create `Model` objects
    // containing information you want to add or remove from the database
    // and make the modification wit
    // that graph. Here we'll create a new Model and add a statement that
    // we want added to our database.
    Model aGraph = Models2.newModel(Values.statement(Values.iri("urn:subj"),
        Values.iri("urn:pred"), Values.iri("urn:obj")));

    Resource aContext = Values.iri("urn:test:context");

    // With our newly created `Graph`, we can easily add that to the
    // database as well. You can also
    // easily specify the context the data should be added to. This will
    // insert all of the statements
    // in the `Graph` into the given context.
    conn.add().graph(aGraph, aContext);

    // Now that we're done adding data to the database, we can go ahead
    // and commit the transaction.
    conn.commit();

    setConn(conn);
    setAdminConnection(aAdminConnection);
  }

  public static void addData(String rdfFile) {
    conn.begin();
    conn.add().io().file(Paths.get(rdfFile));
    conn.commit();
  }

  public static void startStardogInstance() {
    // First need to initialize the Stardog instance which will automatically
    // start the embedded server.
    aStardog = Stardog.builder().create();

    aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
        .credentials("admin", "admin").connect();
  }

  public static void closeConnectionAndDropTable(
      AdminConnection aAdminConnection, String connectionName) {
    // remove the database
    if (aAdminConnection.list().contains(connectionName)) {
      aAdminConnection.drop(connectionName);
    }

    conn.close();
  }

  public static void closeConnection(AdminConnection aAdminConnection,
      String connectionName) {
    conn.close();

    shutdownStardog();
  }

  public static void shutdownStardog() {
    aStardog.shutdown();
  }
}
