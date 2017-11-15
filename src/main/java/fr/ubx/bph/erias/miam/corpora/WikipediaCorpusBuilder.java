/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.ubx.bph.erias.miam.DBpediaCategoryExtractor;

/**
 * @author Georgeta Bordea
 *
 */
public class WikipediaCorpusBuilder {

  // https://en.wikipedia.org/w/api.php?action=parse&format=json
  // &prop=sections&page=Grapefruit&redirects

  private static final String FOOD_DRUG_INTERACTIONS_SECTION =
      "Drug interactions";

  private static final String DRUG_INTERACTIONS_SECTION = "Interactions";

  public void countFoodsWithDrugInteractions(String path) {

    Integer count = 0;
    Set<String> foodUris = readDBpediaUrisFromFile(path);

    for (String foodUri : foodUris) {

      //System.out.println("Checking sections for " + foodUri);

      if ((!foodUri.contains("Category:"))
          && (containsSection(foodUri, FOOD_DRUG_INTERACTIONS_SECTION))) {
        
        System.out.println(foodUri);
        count++;
      }
    }

    System.out.println("Number of foods with interactions: " + count);
  }

  public void countDrugsWithInteractions(String path) {

    Integer count = 0;
    Set<String> drugUris = readDBpediaUrisFromFile(path);

    for (String drugUri : drugUris) {

      //System.out.println("Checking sections for " + drugUri);

      if ((!drugUri.contains("Category:"))
          && (containsSection(drugUri, DRUG_INTERACTIONS_SECTION))) {
        
        System.out.println(drugUri);
        count++;
      }
    }

    System.out.println("Number of drugs with interactions: " + count);
  }

  
  private void saveFiles() {

  }

  private Set<String> readDBpediaUrisFromFile(String path) {

    Set<String> uriSet = new HashSet<String>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(path));
      try {
        String line = null;
        input.readLine();

        while ((line = input.readLine()) != null) {
          String[] uris = line.split("\t");

          uriSet.add(uris[0]);
        }

      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return uriSet;
  }

  public Boolean containsSection(String uri, String sectionName) {

    DBpediaCategoryExtractor dce = new DBpediaCategoryExtractor();

    String part1 = "https://en.wikipedia.org/w/api.php?action=parse&format=json"
        + "&prop=sections&page=";
    String part2 = "&redirects";
    String pageName = uri.substring(uri.lastIndexOf("/") + 1);

    String jsonRequest = part1 + pageName + part2;

    String jsonResponse = getJSON(jsonRequest, 5000);

    JsonElement jsonElement = new JsonParser().parse(jsonResponse);

    if (jsonElement != null) {

      JsonElement parseElement = jsonElement.getAsJsonObject().get("parse");

      if (parseElement != null) {

        JsonElement sections = parseElement
            .getAsJsonObject().get("sections");

        String sectionsText = sections.toString();

        if (sectionsText.contains(sectionName)) {
          return true;
        }
      }
    }
    return false;

  }

  private String getJSON(String url, int timeout) {
    HttpURLConnection c = null;
    try {
      URL u = new URL(url);
      c = (HttpURLConnection) u.openConnection();
      c.setRequestMethod("GET");
      c.setRequestProperty("Content-length", "0");
      c.setUseCaches(false);
      c.setAllowUserInteraction(false);
      c.setConnectTimeout(timeout);
      c.setReadTimeout(timeout);
      c.connect();
      int status = c.getResponseCode();

      switch (status) {
      case 200:
      case 201:
        BufferedReader br =
            new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
      }

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      // Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      ex.printStackTrace();
      // Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (c != null) {
        try {
          c.disconnect();
        } catch (Exception ex) {
          ex.printStackTrace();
          // Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return null;
  }
}
