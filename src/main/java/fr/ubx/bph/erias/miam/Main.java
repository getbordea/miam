package fr.ubx.bph.erias.miam;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.ubx.bph.erias.miam.food.DBpediaFoodExtractor;
import fr.ubx.bph.erias.miam.food.FrenchDBpediaFoodExtractor;

public class Main {
  /**
   * @param args
   */
  public static void main(String[] args) {
    //DBpediaFoodExtractor dfe = new DBpediaFoodExtractor();
    FrenchDBpediaFoodExtractor dfe = new FrenchDBpediaFoodExtractor();

    //final String SEED = "http://dbpedia.org/page/Category:Foods";
    final String SEED = "http://fr.dbpedia.org/page/Cat%C3%A9gorie:Aliment";
    //final String SEED = "http://fr.dbpedia.org/resource/Catégorie:Épice";
    //final String SEED = "http://dbpedia.org/page/Category:Popcorn";
    //final String SEED = "http://dbpedia.org/page/Category:Apple_products";
    
    System.out.println("Extracting DBpedia subcategories for " + SEED);

    Set<String> categSet = new HashSet<String>();
    categSet = dfe.recursiveDownloadNarrowerCategories(SEED, categSet);
    
    try {
      dfe.printURIsToFile(categSet);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println("Done.");
  }
}