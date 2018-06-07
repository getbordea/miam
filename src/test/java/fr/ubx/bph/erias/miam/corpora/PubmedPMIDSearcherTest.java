/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author gb5
 *
 */
public class PubmedPMIDSearcherTest {

  private static final String CORPUS_FILE_PATH = "src/main/resources/corpora/";
  private static final String PMIDS_FILE_PATH = CORPUS_FILE_PATH + 
      //"fdi_hdi_2018/stockley802_fdi-hdi3593_relevant_4254PMIDs.csv";
  //"stockley_802PMIDs.csv";
  "pomelo/all_639PMIDS.txt";
  
  PubmedPMIDSearcher pps = new PubmedPMIDSearcher();
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  //@Test
  //public void findPMIDsTest() {
  //  pps.findPMIDs("Drug Interactions[mesh] AND Bioavailability", 0, 100);
  //}
  
  /*@Test
  public void findAllPMIDsTest() {
    
    String meshTerm = "Drug Interactions[mesh] AND Bioavailability";
    
    MeshTermsStats mts = new MeshTermsStats();
    Integer hits = mts.searchPubMedHits(meshTerm);
        
    pps.searchAllPMIDs(meshTerm, hits);
  }*/
  
  //@Test
  //public void searchYear() {
  //  pps.searchYear("29700251");
  //}
  
  @Test
  public void countYears() {
    pps.countYears(PMIDS_FILE_PATH);
  }

}
