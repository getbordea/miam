/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Georgeta Bordea
 *
 */
public class PubmedCorpusBuilderTest {

  private static final String FILE_PATH =
      // "src/main/resources/stockleyFoodReferences.csv";
      // "src/main/resources/stockleyFoodReferences_noPMID.csv";
      // "src/main/resources/testReference.csv";
      "src/main/resources/stockleyGenericFoodReferences.csv";

  private static final String CORPUS_FILE_PATH = "src/main/resources/corpora/";

  private static final String OUTPUT_FILE_PATH = "pmids.txt";

  // private static final String CITED_IN_OUTPUT_FILE_PATH =
  // "citedIn_pmids.txt";
  private static final String CITED_IN_OUTPUT_FILE_PATH =
      "citedIn_pmids_foodEffect.csv";

  private static final String PMIDS_FILE_PATH =
      // "src/main/resources/corpora/pmidsFound.txt";
      // "src/main/resources/corpora/pmids_foodEffect.csv";
      // "src/main/resources/corpora/stockley_802PMIDs.csv";
      // "src/main/resources/corpora/all/stockley802_pomelo552_1316PMIDs.csv";
      // "src/main/resources/corpora/pomelo/relevant_552PMIDs.txt";
      // "src/main/resources/corpora/pomelo/all_639PMIDS.txt";
      // "src/main/resources/corpora/all/stockley802_pomelo639_1441PMIDs.csv";
      // CORPUS_FILE_PATH + "all/stockley802_pomelo639_1403PMIDs.csv";
      // CORPUS_FILE_PATH + "all/stockley802_pomelo2018_1610PMIDs.csv";
      //CORPUS_FILE_PATH + "fdi_hdi_2018/query2018-fdi-hdi_3593PMIDS.csv";
      CORPUS_FILE_PATH + "fdi_hdi_2018/stockley802_fdi-hdi3593_relevant_4254PMIDs.csv";

  private static final String TEST_PMIDS_FILE_PATH =
      "src/main/resources/corpora/testPMIDS.csv";

  private static final String CITED_IN_PMIDS_FILE_PATH =
      "src/main/resources/corpora/citedIn_pmids.csv";

  private static final String CITED_IN_UNIQUE_PMIDS_FILE_PATH =
      "src/main/resources/corpora/citedIn_2nd_pmids_unique.csv";

  private static final String CITED_IN_PMIDS_COUNTS_FILE_PATH =
      // "src/main/resources/corpora/citedIn_pmids_counts.csv";
      "src/main/resources/corpora/citedIn_3rd_pmids_foodEffect_counts.csv";

  private static final String RANDOM_PMIDS_FILE_PATH =
      // "src/main/resources/corpora/stockley_802PMIDs_RandomPMIDs.csv";
      // "src/main/resources/corpora/all/stockley802_pomelo552_1316RandomPMIDs.csv";
      // "src/main/resources/corpora/pomelo/relevant_pomelo552_RandomPMIDs.txt";
      // "src/main/resources/corpora/all/stockley802_pomelo639_1441RandomPMIDs.csv";
      // CORPUS_FILE_PATH + "all/stockley802_pomelo639_1403RandomPMIDs.csv";
      CORPUS_FILE_PATH + "all/stockley802_pomelo2018_1610RandomPMIDs.csv";

  private static final String OUTPUT_ABSTRACTS_FILE_PATH =
      "output/corpora/abstracts/";

  PubmedCorpusBuilder pcb = new PubmedCorpusBuilder();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  // @Test
  // public void findPMIDsTest() {
  // pcb.findPMIDs(FILE_PATH, OUTPUT_FILE_PATH);
  // }

  // @Test
  // public void findCitedInPMIDsTest() {
  // pcb.findCitedInPMIDs(PMIDS_FILE_PATH, CITED_IN_OUTPUT_FILE_PATH);
  // pcb.findCitedInPMIDs(CITED_IN_UNIQUE_PMIDS_FILE_PATH,
  // CITED_IN_OUTPUT_FILE_PATH);
  // }

  // @Test
  // public void countPubMedCitationsTest() {
  // pcb.countPubMedCitations(CITED_IN_OUTPUT_FILE_PATH,
  // CITED_IN_PMIDS_COUNTS_FILE_PATH);
  // }

  // @Test
  // public void collectMeshTermsTest() {
  // List<String> meshTerms = pcb.collectMeshTerms("21254874");

  // assertTrue(meshTerms.get(0).equals("Animals"));
  // assertTrue(meshTerms.get(1).equals("Beverages*/adverse effects"));
  // }

  // @Test
  // public void collectAllMeshTermsTest() {
  /* pcb.collectAllMeshTerms(PMIDS_FILE_PATH, "meshTerms.csv"); */
  // pcb.collectAllMeshTerms(RANDOM_PMIDS_FILE_PATH, "meshTerms.csv");
  // assertTrue(true);
  // }

  // @Test
  // public void generateRandomPMIDsTest() {
  // update max id based on latest PubMed PMIDs
  // pcb.generateRandomPMIDs(29666118, PMIDS_FILE_PATH,
  // RANDOM_PMIDS_FILE_PATH, 250);
  // }

  //@Test
  //public void collectAllMeshTermsTest() {
  //  pcb.collectAllMeshTerms(PMIDS_FILE_PATH, "meshTerms.csv");

    // pcb.collectAllMeshTerms(RANDOM_PMIDS_FILE_PATH,
    // "meshTerms_random.csv");

  //  assertTrue(true);
  //}

  @Test
  public void downloadAbstractsTest() {
    pcb.downloadAbstracts(PMIDS_FILE_PATH, OUTPUT_ABSTRACTS_FILE_PATH);
  }
}
