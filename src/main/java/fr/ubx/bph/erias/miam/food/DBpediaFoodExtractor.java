/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaFoodExtractor {

  private static final String OUTPUT_FILE = "output/dbPediaCategories.txt";

  String[] STOP_WORDS = { "production", "people", "industry", "disease",
      "manufacturer", "companies", "restaurant", "science", "bakeries",
      "farming", "pubs_", "_pubs", "distilleries", "history", "films",
      "organisations", "breeds", "music", "plantations", "refineries",
      "organizations", "fictional", "chains", "cultivation", "cultivars",
      "regions", "crops", "breweries", "brewing", "brewers", "wineries",
      "vineyards", "festivals", "viticultural", "beer_by", "beer_in",
      "french_wine", "coats_of_arms", "founders", "franchises", "chefs",
      "pizzerias", "media", "pet_food", "shops", "parlors", "retailers",
      "cheesemakers", "characters", "food_truck", "episodes", "ice_cream_vans",
      "orchardists", "economy", "houses", "pathogens",
      "geographical_indications", "studio", "trade", "standards", "campaigns",
      "litigation", "player", "spots", "haze", "crisis", "scandal",
      "popular_culture", "flour_mills", "criticism", "books", "list_of",
      "lists_of", "brand" , "logos"};

  String[] STOP_CATEGORIES =
      { "carnivory", "alcoholic_drink_brands", "cherry_blossom" };

  String DBO = "http://dbpedia.org/ontology/";

  String[] STOP_RDF_TYPES = { DBO + "Person", DBO + "Company",
      DBO + "Organisation", DBO + "Book", DBO + "Place", DBO + "Software" };

  String[] KEEP_RDF_TYPES = { DBO + "Food", DBO + "Beverage" };

  String[] LEAF_CATEGORIES = { "wine", "beer", "whisky", "whiskey" };

  public List<String> downloadNarrowerCategories(String seedPageURI,
      FileWriter fstream) throws IOException {
    Document doc;
    Elements narrowerCategoryElements;
    Elements subjectOfElements;

    BufferedWriter out = null;

    List<String> categoryURLList = new ArrayList<String>();

    String seedCategoryName =
        seedPageURI.substring(seedPageURI.lastIndexOf(':') + 1);

    if (!isFilteredCategory(seedCategoryName)) {
      try {

        out = new BufferedWriter(fstream);

        doc = Jsoup.connect(seedPageURI).get();
        narrowerCategoryElements =
            doc.body().getElementsByAttributeValue("rev", "skos:broader");

        for (Element element : narrowerCategoryElements) {
          // System.out.print(element.text() + " ");

          String categoryURL = element.attr("href");

          String categoryName =
              categoryURL.substring(categoryURL.lastIndexOf(':') + 1);
          
          if (!isFilteredCategory(categoryURL)
              && !hasFilteredType(categoryURL)) {

            out.write("  \"Category:" + seedCategoryName + "\"" + " -> "
                + "\"Category:" + categoryName + "\";"
                + System.lineSeparator());

            System.out.println(seedCategoryName + " " + categoryName);
          } else {
            System.out.println("Filtering DBpedia resource: " + categoryName);
          }
          categoryURLList.add(categoryURL);
        }

        subjectOfElements =
            doc.body().getElementsByAttributeValue("rev", "dct:subject");

        for (Element element : subjectOfElements) {
          // System.out.print(element.text() + " ");

          String elementURL = element.attr("href");
          String elementName =
              elementURL.substring(elementURL.lastIndexOf('/') + 1);

          if (!isFilteredCategory(elementURL) && !hasFilteredType(elementURL)) {
            out.write("  \"Category:" + seedCategoryName + "\"" + " -> " + "\""
                + elementName + "\";" + System.lineSeparator());

            System.out.println(seedCategoryName + " " + elementName);
          } else {
            System.out.println("Filtering DBpedia resource: " + elementName);
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        // Close the output stream
        if (out != null) {
          out.close();
        }
      }
    }

    return categoryURLList;
  }

  public List<String> recursiveDownloadNarrowerCategories(String seedPageURI,
      List<String> allCategories) {

    FileWriter fstream;

    List<String> narrowerCategories;
    try {

      fstream = new FileWriter(OUTPUT_FILE, true);

      narrowerCategories = downloadNarrowerCategories(seedPageURI, fstream);
      for (String dbPediaConceptURI : narrowerCategories) {

        if (isFilteredCategory(dbPediaConceptURI)) {
          System.out.println("Ignoring stop category " + dbPediaConceptURI);
        } else {
          if (!allCategories.contains(dbPediaConceptURI)) {
            allCategories.add(dbPediaConceptURI);

            if (!isLeafCategory(dbPediaConceptURI)) {
              recursiveDownloadNarrowerCategories(dbPediaConceptURI,
                  allCategories);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return allCategories;
  }

  private boolean isLeafCategory(String dbPediaConceptURI) {

    String categoryName =
        dbPediaConceptURI.substring(dbPediaConceptURI.lastIndexOf(':') + 1);

    categoryName = categoryName.toLowerCase();

    if (Arrays.asList(LEAF_CATEGORIES).contains(categoryName)) {
      return true;
    }

    return false;
  }

  private boolean hasFilteredType(String dbpediaUrl) throws IOException {
    Elements typeElements;
    Document doc = Jsoup.connect(dbpediaUrl).get();
    typeElements = doc.body().getElementsByAttributeValue("rel", "rdf:type");

    Boolean hasStopType = false;

    Boolean hasKeepType = false;

    for (Element element : typeElements) {
      String typeURL = element.attr("href");
      if (Arrays.asList(STOP_RDF_TYPES).contains(typeURL)) {
        hasStopType = true;
      }

      if (Arrays.asList(KEEP_RDF_TYPES).contains(typeURL)) {
        hasKeepType = true;
      }
    }
    
    if (hasStopType && !hasKeepType) {
      return true;
    } else {
      return false; 
    }
  }

  private boolean isFilteredCategory(String dbPediaConceptURI) {

    String categoryName =
        dbPediaConceptURI.substring(dbPediaConceptURI.lastIndexOf(':') + 1);

    categoryName = categoryName.toLowerCase();

    if (Arrays.asList(STOP_CATEGORIES).contains(categoryName)) {
      return true;
    }

    for (String stopword : STOP_WORDS) {
      if (categoryName.contains(stopword)) {
        return true;
      }
    }
    return false;
  }
}
