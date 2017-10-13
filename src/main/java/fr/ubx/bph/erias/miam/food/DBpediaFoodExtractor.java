/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

  private static final String URI_OUTPUT_FILE = "output/dbPediaURIs.txt";

  String[] STOP_WORDS = { "production", "people", "industry", "disease",
      "manufacturer", "companies", "company", "restaurant", "science",
      "bakeries", "farming", "pubs_", "_pubs", "_bars", "bars_", "distilleries",
      "distillery", "history", "films", "organisations", "breeds", "music",
      "plantations", "refineries", "organizations", "fictional", "chains",
      "cultivation", "cultivars", "regions", "crops", "breweries", "brewing",
      "brewers", "wineries", "vineyards", "festivals", "viticultural",
      "beer_by", "beer_in", "french_wine", "coats_of_arms", "founders",
      "franchises", "chefs", "pizzerias", "media", "pet_food", "shops",
      "parlors", "retailers", "cheesemakers", "characters", "food_truck",
      "episodes", "ice_cream_vans", "orchardists", "economy", "houses",
      "pathogens", "geographical_indications", "studio", "trade", "standards",
      "campaigns", "litigation", "player", "spots", "haze", "crisis", "scandal",
      "popular_culture", "flour_mills", "criticism", "books", "list_of",
      "lists_of", "brand", "producer", "video_game", "tv_series", "theory" };

  String[] STOP_CATEGORIES = { "carnivory", "alcoholic_drink_brands",
      "cherry_blossom", "halophiles", "forages", "decorative_fruits_and_seeds" };

  String[] LEAF_CATEGORIES = { "wine", "beer", "whisky", "whiskey", "rubus",
      "onions", "table_grape_varieties", "grape_varieties", "quails", "grouse",
      "geese", "swans", "ducks" };

  public Set<String> downloadNarrowerCategories(String seedPageURI,
      FileWriter fstream) throws IOException {
    Document doc;
    Elements narrowerCategoryElements;
    Elements subjectOfElements;

    BufferedWriter out = null;

    Set<String> categoryURLSet = new HashSet<String>();

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

          if (!isFilteredCategory(categoryName)) {
            out.write("  \"Category:" + seedCategoryName + "\"" + " -> "
                + "\"Category:" + categoryName + "\";"
                + System.lineSeparator());

            System.out.println(seedCategoryName + " " + categoryName);
          }
          categoryURLSet.add(categoryURL);
        }

        subjectOfElements =
            doc.body().getElementsByAttributeValue("rev", "dct:subject");

        for (Element element : subjectOfElements) {
          // System.out.print(element.text() + " ");

          String elementURL = element.attr("href");
          String elementName =
              elementURL.substring(elementURL.lastIndexOf('/') + 1);

          if (!isFilteredCategory(elementName)) {
            out.write("  \"Category:" + seedCategoryName + "\"" + " -> " + "\""
                + elementName + "\";" + System.lineSeparator());

            System.out.println(seedCategoryName + " " + elementName);
          }
          categoryURLSet.add(elementURL);
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

    return categoryURLSet;
  }

  public Set<String> recursiveDownloadNarrowerCategories(String seedPageURI,
      Set<String> allCategories) {

    FileWriter fstream;

    Set<String> narrowerCategories;
    try {

      fstream = new FileWriter(OUTPUT_FILE, true);

      narrowerCategories = downloadNarrowerCategories(seedPageURI, fstream);
      for (String dbPediaConceptURI : narrowerCategories) {

        String categoryName =
            dbPediaConceptURI.substring(dbPediaConceptURI.lastIndexOf('/') + 1);

        if (categoryName.contains(":")) {
          categoryName =
              categoryName.substring(categoryName.lastIndexOf(':') + 1);
        }

        if (isFilteredCategory(categoryName)) {
          System.out.println("Ignoring stop category " + categoryName);
        } else {
          if (!allCategories.contains(dbPediaConceptURI)) {
            allCategories.add(dbPediaConceptURI);

            if (!isLeafCategory(categoryName)) {
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

  private boolean isLeafCategory(String categoryName) {
    categoryName = categoryName.toLowerCase();

    if (Arrays.asList(LEAF_CATEGORIES).contains(categoryName)) {
      return true;
    }

    return false;
  }

  private boolean isFilteredCategory(String categoryName) {

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

  public void printURIsToFile(Set<String> uriSet) throws IOException {
    FileWriter fstream;

    BufferedWriter out = null;

    try {

      fstream = new FileWriter(URI_OUTPUT_FILE, false);
      out = new BufferedWriter(fstream);

      for (String uri : uriSet) {
        out.write(uri + System.lineSeparator());
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
}
