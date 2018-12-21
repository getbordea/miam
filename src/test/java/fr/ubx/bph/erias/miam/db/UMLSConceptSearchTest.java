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
 * @author gb5
 *
 */
public class UMLSConceptSearchTest {

  private static final String CONCEPT_NAME = "warfarin";

  @Before
  public void setUp() {
    JDBCConnection.startConnection();
  }

  @After
  public void tearDown() {
    JDBCConnection.closeConnection();
  }
  
  @Test
  public void selectTreeNameForConceptTest() {
    
    try {

      List<String> variants =
          UMLSConceptSearch.selectEnglishNameVariants(CONCEPT_NAME);
      
      for (String variant : variants) {
        System.out.println(variant);
      }

      assertTrue(variants.contains("Coumafene"));

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
