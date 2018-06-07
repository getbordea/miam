/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ubx.bph.erias.miam.db.JDBCConnection;

/**
 * @author Georgeta Bordea
 *
 */
public class QuerySelectionEvaluationTest {

  private static final String CORPUS_FILE_PATH = "src/main/resources/corpora/";

  private static final String MESHTERMS_FILE_PATH =
      // CORPUS_FILE_PATH + "all/meshTerms_2806all.csv";
      CORPUS_FILE_PATH + 
      //"all/meshTerms_3220all.csv";
      "fdi_hdi_2018/meshTerms_Stockley_fdi-hdi_random_8646PMIDs.csv";

  private static final String VOCABULARY_HITS_PATH =
      CORPUS_FILE_PATH +
      //"all/vocabulary_hits_min10_2882all.csv";
      "fdi_hdi_2018/vocabulary_hits_min10_4254all.csv";

  private static final String RELEVANT_PMIDS_FILE_PATH =
      CORPUS_FILE_PATH + "all/stockley802_pomelo2018_1610PMIDs.csv";
      //CORPUS_FILE_PATH + "fdi_hdi_2018/stockley802_fdi-hdi3593_relevant_"
      //    + "4254PMIDs.csv";

  private QuerySelectionEvaluation qse = new QuerySelectionEvaluation();
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");

    JDBCConnection.startConnection();
  }

  @Test
  public void buildTestDataTest() {

    qse.buildTestData("Food-Drug_Interactions", 100, MESHTERMS_FILE_PATH, VOCABULARY_HITS_PATH,
        "featureEvalTest.arff", 10);
  }
  
  //@Test
  //public void computePrecisionRecallTest() {
  //  qse.computePrecisionRecall(RELEVANT_PMIDS_FILE_PATH, "Adolescent", 16100);//42540);
  //}
  
  @After
  public void tearDown() {
    JDBCConnection.closeConnection();
  }
}
