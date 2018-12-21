/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author gb5
 *
 */
public class DBpediaFoodExtractorTest {

  private static final String DBPEDIA_URIS_FILEPATH =
      "output/Foods/DBPediaFoods_v1_15686.csv";

  DBpediaFoodExtractor dfe = new DBpediaFoodExtractor();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  @Test
  public void filterFoodsOnPubMedTest() {
    dfe.filterFoodsOnPubMed(DBPEDIA_URIS_FILEPATH, "test.csv");
  }

}
