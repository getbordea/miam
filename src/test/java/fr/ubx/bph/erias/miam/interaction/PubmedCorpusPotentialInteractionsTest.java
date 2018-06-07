/**
 * 
 */
package fr.ubx.bph.erias.miam.interaction;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ubx.bph.erias.miam.corpora.MeshTermsStatsTest;
import fr.ubx.bph.erias.miam.db.JDBCConnection;

/**
 * @author Georgeta Bordea
 *
 */
public class PubmedCorpusPotentialInteractionsTest {

  public static final String PUBMED_MESH_INTERACTIONS_FILE_PATH =
      "src/main/resources/corpora/pubmed_MeSH_interactions.csv";

  PubmedCorpusPotentialInteractions pcpi =
      new PubmedCorpusPotentialInteractions();

  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
    JDBCConnection.startConnection();
  }

  @Test
  public void test() {
    pcpi.generateInteractionsFromMeSH(
        MeshTermsStatsTest.FOOD_MESH_TERMS_NO_STAR_FILE_PATH,
        MeshTermsStatsTest.DRUG_MESH_TERMS_NO_STAR_FILE_PATH,
        PUBMED_MESH_INTERACTIONS_FILE_PATH);
    assertTrue(true);
  }

  @After
  public void tearDown() {
    JDBCConnection.closeConnection();
  }
}
