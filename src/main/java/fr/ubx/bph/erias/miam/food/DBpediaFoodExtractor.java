/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

  public List<String> downloadNarrowerCategories(String seedPageURI)
      throws IOException {
    Document doc;
    Elements narrowerCategoryElements;

    FileWriter fstream;
    BufferedWriter out = null;

    List<String> categoryURLList = new ArrayList<String>();

    String seedCategoryName =
        seedPageURI.substring(seedPageURI.lastIndexOf(':') + 1);

    try {

      fstream = new FileWriter(OUTPUT_FILE, false);
      out = new BufferedWriter(fstream);

      doc = Jsoup.connect(seedPageURI).get();
      narrowerCategoryElements =
          doc.body().getElementsByAttributeValue("rev", "skos:broader");

      for (Element element : narrowerCategoryElements) {
        // System.out.print(element.text() + " ");

        String categoryURL = element.attr("href");
        String categoryName =
            categoryURL.substring(categoryURL.lastIndexOf(':') + 1);
        out.write("  \"" + seedCategoryName + "\"" + " -> " + "\""
            + categoryName + "\";" + System.lineSeparator());

        System.out.println(seedCategoryName + " " + categoryName);

        categoryURLList.add(categoryURL);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // Close the output stream
      if (out != null) {
        out.close();
      }
    }

    return categoryURLList;
  }

  public List<String> recursiveDownloadNarrowerCategories(String seedPageURI,
      List<String> allCategories) {

    List<String> narrowerCategories;
    try {
      narrowerCategories = downloadNarrowerCategories(seedPageURI);

      for (String dbPediaConceptURI : narrowerCategories) {
        if (!allCategories.contains(dbPediaConceptURI)) {
          allCategories.add(dbPediaConceptURI);
          recursiveDownloadNarrowerCategories(dbPediaConceptURI, allCategories);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return allCategories;
  }
}
