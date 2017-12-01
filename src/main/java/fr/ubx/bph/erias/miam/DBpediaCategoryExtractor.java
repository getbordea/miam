/**
 * 
 */
package fr.ubx.bph.erias.miam;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
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
public class DBpediaCategoryExtractor {

  public static final String DBO = "http://dbpedia.org/ontology/";

  private static final String OUTPUT_FILE = "output/dbPediaCategories.txt";

  protected static final String URI_OUTPUT_FILE = "output/dbPediaURIs.txt";

  public Set<String> downloadNarrowerCategories(String dctPrefix,
      String seedPageURI, FileWriter fstream, String[] stopwords,
      String[] stopCategories, String[] stopRDFTypes, String[] keepRDFTypes)
      throws IOException {
    Document doc;
    Elements narrowerCategoryElements;
    Elements subjectOfElements;

    BufferedWriter out = null;

    Set<String> categoryURLSet = new HashSet<String>();

    String seedCategoryName =
        seedPageURI.substring(seedPageURI.lastIndexOf(':') + 1);

    if (!isFilteredCategory(seedCategoryName, stopwords, stopCategories)) {
      try {

        out = new BufferedWriter(fstream);

        doc = connectWith3Timeouts(seedPageURI);

        if (doc != null) {
          narrowerCategoryElements =
              doc.body().getElementsByAttributeValue("rev", "skos:broader");

          for (Element element : narrowerCategoryElements) {
            // System.out.print(element.text() + " ");

            String categoryURL = element.attr("href");

            String categoryName =
                categoryURL.substring(categoryURL.lastIndexOf(':') + 1);

            if (!isFilteredCategory(categoryURL, stopwords, stopCategories)
                && !hasFilteredType(categoryURL, stopRDFTypes, keepRDFTypes)) {

              out.write("  \"Category:" + seedCategoryName + "\"" + " -> "
                  + "\"Category:" + categoryName + "\" [weight=0.0];"
                  + System.lineSeparator());

              System.out.println(seedCategoryName + " " + categoryName);

              categoryURLSet.add(categoryURL);
            } else {
              System.out.println("Filtering DBpedia resource: " + categoryName);
            }
          }

          subjectOfElements = doc.body().getElementsByAttributeValue("rev",
              dctPrefix + ":subject");

          for (Element element : subjectOfElements) {
            // System.out.print(element.text() + " ");

            String elementURL = element.attr("href");
            String elementName =
                elementURL.substring(elementURL.lastIndexOf('/') + 1);

            if (!isFilteredCategory(elementURL, stopwords, stopCategories)
                && !hasFilteredType(elementURL, stopRDFTypes, keepRDFTypes)) {
              out.write("  \"Category:" + seedCategoryName + "\"" + " -> "
                  + "\"" + elementName + "\" [weight=0.0];" + System.lineSeparator());

              categoryURLSet.add(elementURL);

              System.out.println(seedCategoryName + " " + elementName);
            } else {
              System.out.println("Filtering DBpedia resource: " + elementName);
            }
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

    return categoryURLSet;
  }

  public Set<String> recursiveDownloadNarrowerCategories(String dctPrefix,
      String seedPageURI, Set<String> allCategories, String[] stopwords,
      String[] stopCategories, String[] leafCategories, String[] stopRDFTypes,
      String[] keepRDFTypes) {

    FileWriter fstream;

    Set<String> narrowerCategories;
    try {

      fstream = new FileWriter(OUTPUT_FILE, true);

      narrowerCategories = downloadNarrowerCategories(dctPrefix, seedPageURI,
          fstream, stopwords, stopCategories, stopRDFTypes, keepRDFTypes);
      for (String dbPediaConceptURI : narrowerCategories) {

        String categoryName =
            dbPediaConceptURI.substring(dbPediaConceptURI.lastIndexOf('/') + 1);

        if (categoryName.contains(":")) {
          categoryName =
              categoryName.substring(categoryName.lastIndexOf(':') + 1);
        }

        if (isFilteredCategory(categoryName, stopwords, stopCategories)) {
          System.out.println("Ignoring stop category " + categoryName);
        } else {
          if (!allCategories.contains(dbPediaConceptURI)) {
            allCategories.add(dbPediaConceptURI);

            if (!isLeafCategory(dbPediaConceptURI, leafCategories)) {
              recursiveDownloadNarrowerCategories(dctPrefix, dbPediaConceptURI,
                  allCategories, stopwords, stopCategories, leafCategories,
                  stopRDFTypes, keepRDFTypes);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return allCategories;
  }

  private boolean isLeafCategory(String dbPediaConceptURI,
      String[] leafCategories) {

    String categoryName =
        dbPediaConceptURI.substring(dbPediaConceptURI.lastIndexOf(':') + 1);

    categoryName = categoryName.toLowerCase();

    if (Arrays.asList(leafCategories).contains(categoryName)) {
      return true;
    }

    return false;
  }

  private boolean hasFilteredType(String dbpediaUrl, String[] stopRDFTypes,
      String[] keepRDFTypes) throws IOException {
    Elements typeElements;

    Document doc = connectWith3Timeouts(dbpediaUrl);

    if (doc != null) {
      typeElements = doc.body().getElementsByAttributeValue("rel", "rdf:type");

      Boolean hasStopType = false;

      Boolean hasKeepType = false;

      for (Element element : typeElements) {
        String typeURL = element.attr("href");
        if (Arrays.asList(stopRDFTypes).contains(typeURL)) {
          hasStopType = true;
        }

        if (Arrays.asList(keepRDFTypes).contains(typeURL)) {
          hasKeepType = true;
        }
      }

      if (hasStopType && !hasKeepType) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  private boolean isFilteredCategory(String dbPediaConceptURI,
      String[] stopwords, String[] stopCategories) {

    String categoryName =
        dbPediaConceptURI.substring(dbPediaConceptURI.lastIndexOf(':') + 1);

    categoryName = categoryName.toLowerCase();

    if (Arrays.asList(stopCategories).contains(categoryName)) {
      return true;
    }

    for (String stopword : stopwords) {
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
        out.write(uri);

        String frenchURI = extractFrenchURI(uri);

        if (frenchURI != null) {
          out.write("\t" + frenchURI);
        }

        out.write(System.lineSeparator());
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

  private String extractFrenchURI(String enURI) throws IOException {
    Document doc;
    Elements sameAsElements;

    doc = connectWith3Timeouts(enURI);

    if (doc != null) {
      sameAsElements =
          doc.body().getElementsByAttributeValue("rel", "owl:sameAs");

      for (Element element : sameAsElements) {
        String sameAsUri = element.attr("href");

        if (sameAsUri.contains("fr.dbpedia.org")) {
          System.out.println("Found the French URI: " + sameAsUri);
          return sameAsUri;
        }
      }
    }

    return null;
  }

  public Document connectWith3Timeouts(String url) {

    String encodedURI = URI.create(url).toASCIIString();

    Document doc = null;

    try {
      for (int i = 1; i <= 3; i++) {
        try {
          doc = Jsoup.connect(encodedURI).timeout(5000).get();
          break; // Break immediately if successful
        } catch (SocketTimeoutException e) {
          // Swallow exception and try again
          System.out.println("jsoup Timeout occurred " + i + " time(s)");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return doc;
  }
}
