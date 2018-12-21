/**
 * 
 */
package fr.ubx.bph.erias.miam.db;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.complexible.stardog.StardogException;

import fr.ubx.bph.erias.miam.population.OntologyPopulation;
import fr.ubx.bph.erias.miam.population.OntologyPopulationTest;

/**
 * @author gb5
 *
 */
public class RDFConceptHierarchiesTest {

  private static final String CHEBI_RDF_FILE =
      "../../isped/miam/Resources/CHEBI/chebi_lite.owl";

  private static final String FOODON_RDF_FILE =
      "../../isped/miam/Resources/FOODON/foodon-master/foodon.owl";

  private static final String CONNECTION_NAME = "rdfDBConn";

  RDFConceptHierarchies rch = new RDFConceptHierarchies();

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    SnarlConnection.startStardogInstance();
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    SnarlConnection.shutdownStardog();
  }

  @Test
  public void test() throws Exception {

    // rch.queryPath(SnarlConnection.getConn(), "obo:CHEBI_28829",
    // "obo:CHEBI_24431");

    // System.out.println(rch.selectLabel(SnarlConnection.getConn(),
    // "http://purl.obolibrary.org/obo/CHEBI_28829"));

    // System.out.println(rch.selectConcept(SnarlConnection.getConn(),
    // "aminophenol"));

    // System.out.println(rch.selectConcept(SnarlConnection.getConn(),
    // "paracetamol"));

    // System.out.println(rch.selectConcept(SnarlConnection.getConn(),
    // "tetracycline"));
    
    SnarlConnection.startConnection(OntologyPopulationTest.DIDEO_RDF_FILE,
        OntologyPopulationTest.DIDEO_CONNECTION_NAME, true);

    System.out.println(rch.selectSynonymConcept(SnarlConnection.getConn(),
        "metabolism", false));

    SnarlConnection.closeConnectionAndDropTable(SnarlConnection.getAdminConn(),
        OntologyPopulationTest.DIDEO_CONNECTION_NAME);

    assertTrue(true);
  }

}
