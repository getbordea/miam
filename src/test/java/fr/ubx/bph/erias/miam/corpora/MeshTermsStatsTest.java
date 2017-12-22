/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author gb5
 *
 */
public class MeshTermsStatsTest {

  private static final String MESH_TERMS_FILE_PATH =
      "src/main/resources/corpora/meshTerms.csv";
  
  MeshTermsStats mts = new MeshTermsStats();
  
  @Test
  public void countFrequenciesTest() {
    mts.countFrequencies(MESH_TERMS_FILE_PATH, "meshTermsFreq.csv");
  }
  
  @Test
  public void countCoocurrencesTest() {
    mts.countCoocurrences(MESH_TERMS_FILE_PATH, "meshTermsCooc.csv");
  }
  
  @Test
  public void countFreqQualifiersTest() {
    mts.countFreqQualifiers(MESH_TERMS_FILE_PATH, "meshQualifierFreq.csv");
  }
}
