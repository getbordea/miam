/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import fr.ubx.bph.erias.miam.DBpediaCategoryExtractor;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaFoodExtractor extends DBpediaCategoryExtractor{
  
  public String[] STOP_RDF_TYPES = { DBO + "Person", DBO + "Company",
      DBO + "Organisation", DBO + "Book", DBO + "Place", DBO + "Software",
      DBO + "Place", DBO + "Location", DBO + "Building", DBO + "Restaurant" };

  public String[] KEEP_RDF_TYPES = { DBO + "Food", DBO + "Beverage" };
}
