package fr.ubx.bph.erias.miam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.ubx.bph.erias.miam.food.DBpediaFoodExtractor;

public class Main {
  /**
   * @param args
   */
  public static void main(String[] args) {
    DBpediaFoodExtractor dfe = new DBpediaFoodExtractor();

    final String SEED = "http://dbpedia.org/page/Category:Foods";
    //final String SEED = "http://dbpedia.org/page/Category:Popcorn";
    
    System.out.println("Extracting DBpedia subcategories for " + SEED);

    List<String> emptyList = new ArrayList<String>(); 
    dfe.recursiveDownloadNarrowerCategories(SEED, emptyList);
    
    System.out.println("Done.");
  }
}