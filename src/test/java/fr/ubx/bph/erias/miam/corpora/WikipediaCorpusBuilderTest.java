/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Georgeta Bordea
 *
 */
public class WikipediaCorpusBuilderTest extends WikipediaCorpusBuilder {

  WikipediaCorpusBuilder wcb = new WikipediaCorpusBuilder();
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  @Test
  public void testContainsSection() {

    assertTrue(wcb.containsSection("https://en.wikipedia.org/wiki/Grapefruit",
        "Drug interactions"));

    assertFalse(wcb.containsSection("https://en.wikipedia.org/wiki/Apple",
        "Drug interactions"));
  }

  @Test
  public void countFoodsWithDrugInteractions() {

    // wcb.countFoodsWithDrugInteractions("output/Foods/DBPediaFoods_v1_15686.csv");

    // wcb.countDrugsWithInteractions("output/Drugs/dbPediaURIs.csv");
    
    assertTrue(true);
  }
}
