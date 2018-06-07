/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import static org.junit.Assert.*;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ubx.bph.erias.miam.db.JDBCConnection;

/**
 * @author Georgeta Bordea
 *
 */
public class RelevanceDatasetBuilderTest {

  private static final String CORPUS_FILE_PATH = "src/main/resources/corpora/";

  private static final String MESHTERMS_FILE_PATH =
  // CORPUS_FILE_PATH + "stockley_all1604PMIDs_meshTerms.csv";
  // CORPUS_FILE_PATH + "all/meshTerms_2882all.csv";
  // CORPUS_FILE_PATH + "all/meshTerms_2632all.csv";
  // CORPUS_FILE_PATH + "pomelo/meshTerms_all_1104PMIDS.csv";
  // CORPUS_FILE_PATH + "pomelo/meshTerms_all639PMIDS.csv";
  //CORPUS_FILE_PATH + "all/meshTerms_2806all.csv";
  //CORPUS_FILE_PATH + "all/meshTerms_test_all.csv";
  //CORPUS_FILE_PATH + "all/meshTerms_3220all.csv";
    CORPUS_FILE_PATH + "fdi_hdi_2018/meshTerms_Stockley_fdi-hdi_random_8646PMIDs.csv";

  private static final String TEST_MESHTERMS_FILE_PATH =
      "src/main/resources/corpora/pomelo/meshTerms_all639PMIDS.csv";

  private static final String RELEVANT_PMIDS_FILE_PATH =
      // CORPUS_FILE_PATH + "stockley_802PMIDs.csv";
      // CORPUS_FILE_PATH + "all/stockley802_pomelo552_1316PMIDs.csv";
      // CORPUS_FILE_PATH + "pomelo/relevant_552PMIDs.txt";
      // CORPUS_FILE_PATH + "pomelo/relevant_620PMIDs.txt";
      // CORPUS_FILE_PATH + "all/stockley802_pomelo639_1441RandomPMIDs.csv";
      //CORPUS_FILE_PATH + "all/stockley802_pomelo639_1403PMIDs.csv";
      //CORPUS_FILE_PATH + "all/pmids_test_relevant.csv";
      //CORPUS_FILE_PATH + "all/stockley802_pomelo2018_1610PMIDs.csv";
      CORPUS_FILE_PATH + "fdi_hdi_2018/stockley802_fdi-hdi3593_relevant_4254PMIDs.csv";

  private static final String VOCABULARY_HITS_PATH =
      CORPUS_FILE_PATH +
      //"all/vocabulary_hits_min10_2882all.csv";
      "fdi_hdi_2018/vocabulary_hits_min10_4254all.csv";
  
  private static final String OUTPUT_FILE_PATH = "FDIrelevance.arff";

  private static final Integer MINIMUM_FREQUENCY = 10; //10

  RelevanceDatasetBuilder rdb;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    rdb = new RelevanceDatasetBuilder();
    PropertyConfigurator.configure("src/main/config/log.properties");

    JDBCConnection.startConnection();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    JDBCConnection.closeConnection();
  }

  // @Test
  // public void buildVocabularyTest() {
  // Set<String> vocabulary =
  // rdb.buildVocabulary(MINIMUM_FREQUENCY, MESHTERMS_FILE_PATH, false);

  // System.out.println("Minimum frequency is set to: " + MINIMUM_FREQUENCY);
  // System.out.println("Vocabulary size is " + vocabulary.size());

  // assertTrue(vocabulary.size() > 0);
  // }

  @Test
  public void outputInARFFTest() {
    rdb.outputInARFF(RELEVANT_PMIDS_FILE_PATH, MESHTERMS_FILE_PATH,
     VOCABULARY_HITS_PATH, OUTPUT_FILE_PATH, MINIMUM_FREQUENCY);

    /*rdb.outputTestDataInARFF(RELEVANT_PMIDS_FILE_PATH, MESHTERMS_FILE_PATH,
     TEST_MESHTERMS_FILE_PATH, OUTPUT_FILE_PATH, MINIMUM_FREQUENCY);

    rdb.outputTestDataInARFF(CORPUS_FILE_PATH + "pomelo/empty_meshTerms.csv",
        MESHTERMS_FILE_PATH,
        CORPUS_FILE_PATH + "pomelo/meshTerms_random_552PMIDs.csv",
        OUTPUT_FILE_PATH, MINIMUM_FREQUENCY);*/

  }
  
  //@Test
  //public void findDuplicatePMIDs() {
  //  rdb.removeDuplicatePMIDs(CORPUS_FILE_PATH + "fdi_hdi_2018/test1.csv", 
  //      CORPUS_FILE_PATH + "fdi_hdi_2018/test2.csv");
  //}
}
