package fr.ubx.bph.erias.miam.population;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ubx.bph.erias.miam.corpora.MiamAnnotationType;
import fr.ubx.bph.erias.miam.db.JDBCConnection;
import fr.ubx.bph.erias.miam.db.SnarlConnection;

public class OntologyPopulationTest {

  OntologyPopulation op = new OntologyPopulation();

  private static final String CHEBI_RDF_FILE =
      "../../isped/miam/Resources/CHEBI/chebi.owl";

  private static final String DRON_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String DRON_CHEBI_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String DRON_HAND_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String DRON_INGREDIENT_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String DRON_NDC_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String DRON_PRO_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String DRON_RXNORM_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String DRON_UPPER_RDF_FILE =
      "../../isped/miam/Resources/DRON/dron.owl";

  private static final String FOODON_RDF_FILE =
      // "../../isped/miam/Resources/FOODON/foodon-master/foodon.owl";
      "../../isped/miam/Resources/FOODON/foodon-master/imports"
          + "/foodon_product_import.owl";

  private static final String FOODON_ENVO_RDF_FILE =
      "../../isped/miam/Resources/FOODON/foodon-master/imports"
          + "/envo_import.owl";

  public static final String DIDEO_RDF_FILE =
      "../../isped/miam/Resources/DIDEO-master/dideo.owl";

  private static final String FIDEO_RDF_FILE =
      "../../isped/miam/Resources/MIAM/miam_lite.owl";

  public static final String BRAT_FILE =
      // "../../isped/miam/Corpus/20articles/brat/MIAM_20pmids-V2/CNHIM/630792.ann";
      "../../isped/miam/Corpus/pomelo/POMELO_TST"
          + "/brat_output-DrugFoodInteractions-MH-AdvEff-328.ann";

  private static final String BRAT_DIR =
      // "../../isped/miam/Corpus/20articles/brat/MIAM_20pmids-V2/CNHIM/"; //
      // CRPV/"; //
      "/home/gb5/work/isped/miam/Corpus/pomelo/POMELO_TST";

  private static final String FIDEO_CONNECTION_NAME = "fideoRDFDB";
  private static final String FOODON_CONNECTION_NAME = "foodonRDFDB";
  private static final String CHEBI_CONNECTION_NAME = "chebiRDFDB";
  private static final String DRON_CONNECTION_NAME = "dronRDFDB";
  public static final String DIDEO_CONNECTION_NAME = "dideoRDFDB";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    SnarlConnection.startStardogInstance();
    JDBCConnection.startConnection();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    SnarlConnection.shutdownStardog();
    JDBCConnection.closeConnection();
  }

  @Test
  public void populateDrugsFromBratFileTest() throws Exception {
    SnarlConnection.startConnection(CHEBI_RDF_FILE, CHEBI_CONNECTION_NAME,
        true);

    System.out.println("Linking annotated drugs with ChEBI.. ");
    /*
     * SnarlConnection.startConnection(DRON_RDF_FILE, DRON_CONNECTION_NAME,
     * true);
     * 
     * SnarlConnection.addData(DRON_CHEBI_RDF_FILE);
     * SnarlConnection.addData(DRON_HAND_RDF_FILE);
     * SnarlConnection.addData(DRON_INGREDIENT_RDF_FILE);
     * SnarlConnection.addData(DRON_NDC_RDF_FILE);
     * SnarlConnection.addData(DRON_PRO_RDF_FILE);
     * SnarlConnection.addData(DRON_RXNORM_RDF_FILE);
     * SnarlConnection.addData(DRON_UPPER_RDF_FILE);
     */

    op.populateAnnFromBratFileByType(SnarlConnection.getConn(), BRAT_DIR,
        MiamAnnotationType.DRUG_ANNOTATION_STRING);

    //op.populateAnnFromBratFileByType(SnarlConnection.getConn(), BRAT_DIR,
    //    MiamAnnotationType.DRUG_CLASS_ANNOTATION_STRING);

    SnarlConnection.closeConnectionAndDropTable(SnarlConnection.getAdminConn(),
        CHEBI_CONNECTION_NAME);
  }

  /*
   * @Test public void populateFoodsFromBratFileTest() throws Exception {
   * SnarlConnection.startConnection(FOODON_RDF_FILE, FOODON_CONNECTION_NAME,
   * true);
   * 
   * SnarlConnection.addData(FOODON_ENVO_RDF_FILE);
   * 
   * SnarlConnection.addData(CHEBI_RDF_FILE);
   * 
   * op.populateAnnFromBratFileByType(SnarlConnection.getConn(), BRAT_DIR,
   * MiamAnnotationType.FOOD_ANNOTATION_STRING);
   * 
   * op.populateAnnFromBratFileByType(SnarlConnection.getConn(), BRAT_DIR,
   * MiamAnnotationType.FOOD_COMPONENT_ANNOTATION_STRING);
   * 
   * SnarlConnection.closeConnectionAndDropTable(SnarlConnection.getAdminConn(),
   * FOODON_CONNECTION_NAME); }
   */

  /*
   * @Test public void populatePKFromBratFileTest() throws Exception {
   * SnarlConnection.startConnection(DIDEO_RDF_FILE, DIDEO_CONNECTION_NAME,
   * true);
   * 
   * op.populateAnnFromBratFileByType(SnarlConnection.getConn(), BRAT_DIR,
   * MiamAnnotationType.PHARMACOKINETICS_ANNOTATION_STRING);
   * 
   * SnarlConnection.closeConnectionAndDropTable(SnarlConnection.getAdminConn(),
   * FOODON_CONNECTION_NAME); }
   */

}
