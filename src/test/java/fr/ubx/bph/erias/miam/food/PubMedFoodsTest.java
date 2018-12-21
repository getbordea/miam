/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.index.CorruptIndexException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Georgeta Bordea
 *
 */
public class PubMedFoodsTest {

  private static final String LUCENE_INDEX_PATH = "output/index/fdiAbstracts";
    //"../taxCo/lucene/index/wiki2015";

  private static final String CORPUS_PATH = "output/corpora/abstracts/";

  private static final String INPUT_FOODS_FILEPATH =
      //"output/Foods/DBPediaFoods_v1_15686.csv";
      "/home/gb5/work/isped/miam/Approach/stockleyFoodsListUnique.csv";
  

  private static final String DBPEDIA_FOODS_OUTPUT_FILEPATH =
      //"output/DBPediaFoods_FDI_Freqs.csv";
      "output/FIDEO_FDI_Freqs.csv";

  PubMedFoods pmf = new PubMedFoods();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  //@Test
  //public void indexAllDocsTest()
  //  throws CorruptIndexException, IOException, SQLException {
  //  pmf.indexAllDocs(LUCENE_INDEX_PATH, CORPUS_PATH);
  //}

  // @Test
  // public void countAllDocsTest() {
  // System.out.println(pmf.countAllDocs(CORPUS_PATH));
  // }

  //@Test
  //public void filterFoodsInFDIAbstractsTest() {
  //  pmf.filterFoodsInFDIAbstracts(INPUT_FOODS_FILEPATH,
  //      DBPEDIA_FOODS_OUTPUT_FILEPATH, LUCENE_INDEX_PATH, CORPUS_PATH);
  //}
  
  @Test
  public void retrieveDocsTest() {
    pmf.retrieveDocs("Cheshire", LUCENE_INDEX_PATH, CORPUS_PATH);
  }

}
