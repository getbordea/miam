package fr.ubx.bph.erias.miam;

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

    System.out.println("Extracting DBpedia subcategories for " + SEED);

    List<String> emptyList = new ArrayList<String>(); 
    dfe.recursiveDownloadNarrowerCategories(SEED, emptyList);
    
    //try {
    //  dfe.downloadNarrowerCategories(SEED);
    //} catch (IOException e) {
    //  e.printStackTrace();
    //}
    
    System.out.println("Done.");
  }
}