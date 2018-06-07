/**
 * 
 */
package fr.ubx.bph.erias.miam.db;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Georgeta Bordea
 *
 */
public class UMLSConceptHierarchiesTest {

  private static final String CONCEPT_NAME = "Ginkgo biloba";

  @Before
  public void setUp() {
    JDBCConnection.startConnection();
  }

  @Test
  public void selectTreeNameForConceptTest() {
    
    try {

      List<String> treeIDs =
          UMLSConceptHierarchies.selectTreeIDsForConcept(CONCEPT_NAME);

      assertTrue(treeIDs.contains("B01.650.940.800.575.400.300"));

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void isFoodRelatedConceptTest() {

    assertTrue(UMLSConceptHierarchies.isFoodRelatedConcept("Ginkgo biloba"));
  }

  @Test
  public void isDrugTest() {

    try {

      assertTrue(UMLSConceptHierarchies.isDrug("Warfarin"));

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() {
    JDBCConnection.closeConnection();
  }
}
