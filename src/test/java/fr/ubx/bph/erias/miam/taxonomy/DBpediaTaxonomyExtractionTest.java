/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import fr.ubx.bph.erias.miam.pruning.KnowledgeGraphPruning;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaTaxonomyExtractionTest {

  private static final String LUCENE_INDEX_PATH =
      "../taxCo/lucene/index/wiki2015";

  private static final String FDI_INDEX_PATH =
      // "../taxCo/lucene/index/wiki2015";
      "output/index/fdiAbstracts";

  private static final String GRAPH_PATH =
      "output/Foods/DBPediaFoods_v1_15686.dot";
  // "src/test/resources/dbPedia_testGraph.dot";
  // "output/dbPedia_popcorn.dot";

  private static final String CATEGORIES_GRAPH_PATH =
      //"output/Foods/DBPediaFoods_v1_15686_test_categoriesOnly.dot";
      //"output/Foods/DBPediaFoods_v1_15686_FDICorpus.dot";
      "output/Foods/DBPediaFoods_v1_15686.dot";

  private static final String FDI_GRAPH_PATH =
      //"output/Foods/DBPediaFoods_v1_15686_FDIFilteredGraph.dot";
      "output/Foods/DBPediaFoods_v1_15686_FDIFilteredGraph_v2.dot";

  @Before
  public void setupConfig() {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  @Test
  public void testExtractDBTaxonomy() {

    DBpediaTaxonomyExtraction dte = new DBpediaTaxonomyExtraction();

    KnowledgeGraphPruning kgp = new KnowledgeGraphPruning();

    //kgp.extractCategoriesOnly(GRAPH_PATH, CATEGORIES_GRAPH_PATH);

    //dte.extractDBpediaTaxonomy("Foods", CATEGORIES_GRAPH_PATH, 100,
    //    LUCENE_INDEX_PATH);
    
    //dte.extractDBpediaTaxonomy("Foods", FDI_GRAPH_PATH, 100,
    //    LUCENE_INDEX_PATH);

    kgp.filterFDICategories(FDI_INDEX_PATH, CATEGORIES_GRAPH_PATH,
        FDI_GRAPH_PATH);

    assertTrue(true);
  }
}
