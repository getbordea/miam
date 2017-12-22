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
      "src/main/resources/testReference.csv";

  private static final String OUTPUT_FILE_PATH = "pmids.txt";

  //private static final String CITED_IN_OUTPUT_FILE_PATH = "citedIn_pmids.txt";
  private static final String CITED_IN_OUTPUT_FILE_PATH = "citedIn_3rd_pmids.txt";

  private static final String PMIDS_FILE_PATH =
      "src/main/resources/corpora/pmidsFound.txt";
  
  private static final String TEST_PMIDS_FILE_PATH =
      "src/main/resources/corpora/testPMIDS.csv";

  private static final String CITED_IN_PMIDS_FILE_PATH =
      "src/main/resources/corpora/citedIn_pmids.csv";
  
  private static final String CITED_IN_UNIQUE_PMIDS_FILE_PATH =
      "src/main/resources/corpora/citedIn_2nd_pmids_unique.csv";

  private static final String CITED_IN_PMIDS_COUNTS_FILE_PATH =
      //"src/main/resources/corpora/citedIn_pmids_counts.csv";
      "src/main/resources/corpora/citedIn_3rd_pmids_counts.csv";

  PubmedCorpusBuilder pcb = new PubmedCorpusBuilder();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  //@Test
  //public void findPMIDsTest() {
  //  pcb.findPMIDs(FILE_PATH, OUTPUT_FILE_PATH);
  //}

  //@Test
  //public void findCitedInPMIDsTest() {
    //pcb.findCitedInPMIDs(PMIDS_FILE_PATH, CITED_IN_OUTPUT_FILE_PATH);
    //pcb.findCitedInPMIDs(CITED_IN_UNIQUE_PMIDS_FILE_PATH, 
    //    CITED_IN_OUTPUT_FILE_PATH);
  //}

  //@Test
  //public void countPubMedCitationsTest() {
  //  pcb.countPubMedCitations(CITED_IN_OUTPUT_FILE_PATH,
  //      CITED_IN_PMIDS_COUNTS_FILE_PATH);
  //}

  @Test
  public void collectMeshTermsTest() {
    List<String> meshTerms = pcb.collectMeshTerms("21254874");
    
    assertTrue(meshTerms.get(0).equals("Animals"));
    assertTrue(meshTerms.get(1).equals("Beverages*/adverse effects"));
  }
  
  @Test
  public void collectAllMeshTermsTest() {
    pcb.collectAllMeshTerms(TEST_PMIDS_FILE_PATH, "meshTerms.csv");
    
    assertTrue(true);
  }
}
