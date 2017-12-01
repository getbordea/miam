package fr.ubx.bph.erias.miam;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.ubx.bph.erias.miam.drug.EnglishDBpediaDrugExtractor;
import fr.ubx.bph.erias.miam.food.EnglishDBpediaFoodExtractor;
import fr.ubx.bph.erias.miam.food.FrenchDBpediaFoodExtractor;

public class Main {
  /**
   * @param args
   */
  public static void main(String[] args) {
    // DBpediaFoodExtractor de = new DBpediaFoodExtractor();
    // FrenchDBpediaFoodExtractor de = new FrenchDBpediaFoodExtractor();
    // EnglishDBpediaDrugExtractor de = new EnglishDBpediaDrugExtractor();
    EnglishDBpediaFoodExtractor de = new EnglishDBpediaFoodExtractor();
    
    // final String SEED = "http://dbpedia.org/page/Category:Foods";
    // final String SEED = "http://dbpedia.org/page/Category:Drugs";
    // final String SEED = "http://fr.dbpedia.org/page/Cat%C3%A9gorie:Aliment";
    // final String SEED = "http://fr.dbpedia.org/resource/Catégorie:Épice";
    final String SEED = "http://dbpedia.org/page/Category:Popcorn";
    // final String SEED = "http://dbpedia.org/page/Category:Apple_products";
    //final String SEED = "http://dbpedia.org/page/Category:Spices";

    System.out.println("Extracting DBpedia subcategories for " + SEED);

    Set<String> categSet = new HashSet<String>();
    categSet = de.recursiveDownloadNarrowerCategories(de.DCT_PREFIX, SEED,
        categSet, de.STOP_WORDS, de.STOP_CATEGORIES, de.LEAF_CATEGORIES,
        de.STOP_RDF_TYPES, de.KEEP_RDF_TYPES);

    try {
      de.printURIsToFile(categSet);
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Done.");
  }
}