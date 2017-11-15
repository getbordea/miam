/**
 * 
 */
package fr.ubx.bph.erias.miam.drug;

import fr.ubx.bph.erias.miam.DBpediaCategoryExtractor;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaDrugExtractor extends DBpediaCategoryExtractor {

  public String[] STOP_RDF_TYPES =
      { DBO + "Person", DBO + "Company", DBO + "Organisation", DBO + "Book",
          DBO + "Place", DBO + "Place", DBO + "Location", DBO + "Building" };

  public String[] KEEP_RDF_TYPES = { DBO + "ChemicalSubstance", DBO + "Drug",
      DBO + "Plant", DBO + "Species"};
}
