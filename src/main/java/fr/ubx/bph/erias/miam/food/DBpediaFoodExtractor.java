/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.ubx.bph.erias.miam.DBpediaCategoryExtractor;
import fr.ubx.bph.erias.miam.corpora.PubmedCorpusBuilder;
import fr.ubx.bph.erias.miam.utils.WebUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaFoodExtractor extends DBpediaCategoryExtractor {

  private static Logger logger =
      Logger.getLogger(DBpediaFoodExtractor.class.getName());

  public String[] STOP_RDF_TYPES = { DBO + "Person", DBO + "Company",
      DBO + "Organisation", DBO + "Book", DBO + "Place", DBO + "Software",
      DBO + "Location", DBO + "Building", DBO + "Restaurant" };

  public String[] KEEP_RDF_TYPES = { DBO + "Food", DBO + "Beverage" };

  public void filterFoodsOnPubMed(String inputFile, String outputFile) {
    FileWriter fstream;

    BufferedWriter out = null;
    BufferedReader input = null;

    try {

      try {
        input = new BufferedReader(new FileReader(inputFile));

        fstream = new FileWriter(outputFile, false);

        out = new BufferedWriter(fstream);

        String line = null;

        while ((line = input.readLine()) != null) {

          String foodName = line.substring(line.lastIndexOf("/") + 1);

          if (foodName.startsWith("Category:")) {
            foodName = line.substring(line.lastIndexOf(":") + 1);
          }

          Integer hits = searchPubMedHits(foodName);

          if (hits > 0) {
            out.write(line + " " + hits);
            out.write(System.lineSeparator());
          }
        }

      } finally {

        // Close the input stream
        if (input != null) {
          input.close();
        }

        // Close the output stream
        if (out != null) {
          out.close();
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Integer searchPubMedHits(String foodName) {
    foodName = foodName.replace("_", "+");
    String searchURL = PubmedCorpusBuilder.PUBMED_URL + "%22" + foodName + "%22";

    System.out.println(searchURL);

    Document doc = WebUtils.connectWith3Timeouts(searchURL);

    if (doc != null) {
      
      Elements messageElements =
          doc.getElementsByAttributeValueContaining("class", "messages");
      
      for (Element element : messageElements) {
        
        String text = element.text();
        
        if (text.contains("Quoted phrase not found.")) {         
          System.out.println(text);
          
          return 0;
        }     
      }
      
      Elements elements =
          doc.getElementsByAttributeValueContaining("id", "resultcount");

      for (Element element : elements) {
        Integer hits = Integer.parseInt(element.attr("value"));

        System.out.println(foodName + " " + hits);

        return hits;
      }
    } else {
      logger.log(Level.INFO,
          "PubMed timed out, returning 0 hits for the term " + foodName);
    }

    return 0;
  }

}
