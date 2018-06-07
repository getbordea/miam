/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ubx.bph.erias.miam.db.JDBCConnection;

/**
 * @author gb5
 *
 */
public class MeshTermsStatsTest {

  private static final String CORPUS_PATH = "src/main/resources/corpora/";

  private static final String MESH_TERMS_FILE_PATH =
      // "src/main/resources/corpora/meshTerms.csv";
      //CORPUS_PATH + "all/meshTerms_relevant_1441.csv";
      //CORPUS_PATH + "all/meshTerms_relevant_1610.csv";
      CORPUS_PATH + "fdi_hdi_2018/tmp.csv";

  private static final String RANDOM_MESH_TERMS_FILE_PATH =
      "src/main/resources/corpora/meshTermsRandomPMIDs.csv";

  private static final String MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/meshTerms_noStar.csv";

  private static final String RANDOM_MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/meshTermsRandomPMIDs_noStar.csv";

  private static final String MESH_TERMS_FREQ_FILE_PATH =
      // "src/main/resources/corpora/all/meshTermsFreq_relevant_1441.csv";
      // CORPUS_PATH + "all/meshTermsFreq_relevant_1441_min10.csv";
      //CORPUS_PATH + "all/meshTermsFreq_relevant_1610_min10.csv";
      CORPUS_PATH + "fdi_hdi_2018/stockley_fdi-hdi_freq_min10.csv";

  private static final String RANDOM_MESH_TERMS_FREQ_FILE_PATH =
      "src/main/resources/corpora/randomMeshTermsFreq.csv";

  private static final String MESH_TERMS_FREQ_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/meshTermsFreq_noStar.csv";

  private static final String RANDOM_MESH_TERMS_FREQ_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/meshTermsRandomPMIDsFreq_5323.csv";

  public static final String FOOD_MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/food_meshTerms_noStar.csv";

  public static final String DRUG_MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/drug_meshTerms_noStar.csv";

  private static final String DISEASE_MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/disease_meshTerms_noStar.csv";

  private static final String OTHER_MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/other_meshTerms_noStar.csv";

  private static final String INVESTIGATION_DIAGNOSIS_MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/investigationDiagnosis_meshTerms_noStar.csv";

  private static final String COHORT_MESH_TERMS_NO_STAR_FILE_PATH =
      "src/main/resources/corpora/cohort_meshTerms_noStar.csv";

  MeshTermsStats mts = new MeshTermsStats();

  @Before
  public void setUp() {
    PropertyConfigurator.configure("src/main/config/log.properties");

    JDBCConnection.startConnection();
  }

  // @Test
  // public void countFrequenciesTest() {
  // mts.countFrequencies(MESH_TERMS_FILE_PATH, MESH_TERMS_FREQ_FILE_PATH);
  // }

  //@Test
  //public void countFrequenciesTest() {
  //  mts.countFrequencies(MESH_TERMS_FILE_PATH, MESH_TERMS_FREQ_FILE_PATH, true, 
  //      true);
  //}

  // @Test
  // public void countFrequenciesTest() {
  // mts.countFrequencies(MESH_TERMS_NO_STAR_FILE_PATH,
  // MESH_TERMS_FREQ_NO_STAR_FILE_PATH);
  // mts.countFrequencies(RANDOM_MESH_TERMS_NO_STAR_FILE_PATH,
  // RANDOM_MESH_TERMS_FREQ_NO_STAR_FILE_PATH);
  // }

  // @Test
  // public void computeNormalisedFrequenciesTest() {
  // mts.computeNormalisedFrequencies(MESH_TERMS_FREQ_NO_STAR_FILE_PATH,
  // RANDOM_MESH_TERMS_FREQ_NO_STAR_FILE_PATH,
  // "normalisedMeshTermsFreq_5323Contrast.csv");
  // }

  // @Test
  // public void countCoocurrencesTest() {
  // mts.countCoocurrences(MESH_TERMS_NO_STAR_FILE_PATH,
  // MESH_TERMS_FREQ_NO_STAR_FILE_PATH, "meshTermsCooc.csv");
  // }

  // @Test
  // public void countFreqQualifiersTest() {
  // mts.countFreqQualifiers(MESH_TERMS_FILE_PATH, "meshQualifierFreq.csv");
  // }

  // @Test
  // public void searchPubMedHitsTest() {
  // Integer hits = mts.searchPubMedHits("Risk Factors");

  // assertEquals(new Integer(707957), hits);
  // }

  @Test 
  public void computeNormalisedFrequenciesTest() {
    mts.computeNormalisedFrequencies(MESH_TERMS_FREQ_FILE_PATH,
        "meshTermScores.csv", 4395, 21254874, true, true);
  }

  // @Test
  // public void filterFoodsTest() {
  // mts.filterFoods(MESH_TERMS_NO_STAR_FILE_PATH,
  // FOOD_MESH_TERMS_NO_STAR_FILE_PATH);
  // }

  // @Test
  // public void filterDrugsTest() {
  // mts.filterDrugs(MESH_TERMS_NO_STAR_FILE_PATH,
  // DRUG_MESH_TERMS_NO_STAR_FILE_PATH);
  // }

  // @Test
  // public void filterDiseasesTest() {
  // mts.filterDiseases(MESH_TERMS_NO_STAR_FILE_PATH,
  // DISEASE_MESH_TERMS_NO_STAR_FILE_PATH);
  // }

  // @Test
  // public void filterInvestigationDiagnosisTermsTest() {
  // mts.filterInvestigationDiagnosisTerms(MESH_TERMS_NO_STAR_FILE_PATH,
  // INVESTIGATION_DIAGNOSIS_MESH_TERMS_NO_STAR_FILE_PATH);
  // }

  // @Test
  // public void filterFoodsTest() {
  // mts.filterCohort(MESH_TERMS_NO_STAR_FILE_PATH,
  // COHORT_MESH_TERMS_NO_STAR_FILE_PATH);
  // }

  // @Test
  // public void filterOtherTest() {
  // mts.filterOtherThanFoodsAndDrugsAndDiseases(MESH_TERMS_NO_STAR_FILE_PATH,
  // OTHER_MESH_TERMS_NO_STAR_FILE_PATH);
  // }

  @After
  public void tearDown() {
    JDBCConnection.closeConnection();
  }
}
