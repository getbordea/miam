/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
public class DBpediaFoodExtractor {

  private static final String OUTPUT_FILE = "output/dbPediaCategories.txt";

  private static final String URI_OUTPUT_FILE = "output/dbPediaURIs.txt";

  String DCT_PREFIX = "dct";
  String DCT_PREFIX_FR = "dcterms";

  String[] STOP_WORDS = { "production", "people", "industry", "disease",
      "manufacturer", "companies", "company", "restaurant", "science",
      "bakeries", "farming", "pubs_", "_pubs", "distilleries", "distillery",
      "history", "films", "organisations", "breeds", "music", "plantations",
      "refineries", "organizations", "fictional", "chains", "cultivation_in",
      "regions", "crops", "breweries", "brewing", "brewers", "wineries",
      "vineyards", "festivals", "viticultural", "beer_by", "beer_in",
      "french_wine", "coats_of_arms", "founders", "franchises", "chefs",
      "pizzerias", "pet_food", "shops", "parlors", "retailers", "cheesemakers",
      "characters", "food_truck", "episodes", "ice_cream_vans", "orchardists",
      "economy", "houses", "pathogens", "geographical_indications", "studio",
      "trade", "standards", "campaigns", "litigation", "player", "spots",
      "haze", "crisis", "scandal", "popular_culture", "flour_mills",
      "criticism", "books", "list_of", "lists_of", "brand", "producer",
      "video_game", "tv_series", "theory", "logos" };

  String[] STOP_WORDS_FR = { "court_métrage", "élevage", "viticulture",

      "race_de", "donald_duck", "toponyme", "collaborateur", "production",
      "personnes", "publication", "industrie", "maladie", "fabricant",
      "dans_la_culture", "entreprises", "société", "restaurant", "science",
      "boulangeries", "agriculture", "_pubs", "pubs_", "distilleries",
      "distillerie", "histoire", "films", "musique", "plantations",
      "raffineries", "organisations", "fictives", "chaînes", "culture dans",
      "régions", "cultures", "brasseries", "brassage", "brasseurs", "caves",
      "vignobles", "festivals", "viticoles", "blasons", "fondateurs",
      "franchises", "chefs", "aliments pour animaux", "boutiques", "salons",
      "détaillants", "fromagers", "personnages", "épisodes", "fourgons à glace",
      "verger", "économie", "maisons", "pathogènes",
      "indications géographiques", "commerce", "normes", "campagnes", "litiges",
      "joueur", "brume", "crise", "scandale", "culture populaire", "moulins",
      "books", "liste de", "listes de", "marque", "producteur", "jeu vidéo",
      "séries télévisées", "théorie", "logos", "série" };

  String[] STOP_CATEGORIES =
      { "carnivory", "alcoholic_drink_brands", "cherry_blossom", "halophiles",
          "forages", "decorative_fruits_and_seeds" };

  String[] STOP_CATEGORIES_FR = { "vinification" };

  String DBO = "http://dbpedia.org/ontology/";

  String[] STOP_RDF_TYPES = { DBO + "Person", DBO + "Company",
      DBO + "Organisation", DBO + "Book", DBO + "Place", DBO + "Software",
      DBO + "Place", DBO + "Location", DBO + "Building", DBO + "Restaurant" };

  String[] KEEP_RDF_TYPES = { DBO + "Food", DBO + "Beverage" };

  String[] LEAF_CATEGORIES = { "wine", "beer", "whisky", "whiskey", "rubus",
      "onions", "table_grape_varieties", "grape_varieties", "quails", "grouse",
      "geese", "swans", "ducks" };

  String[] LEAF_CATEGORIES_FR = { "vin" };

  public Set<String> downloadNarrowerCategories(String seedPageURI,
      FileWriter fstream) throws IOException {
    Document doc;
    Elements narrowerCategoryElements;
    Elements subjectOfElements;

    BufferedWriter out = null;

    Set<String> categoryURLSet = new HashSet<String>();

    String seedCategoryName =
        seedPageURI.substring(seedPageURI.lastIndexOf(':') + 1);

    String encodedseedPageURI = URI.create(seedPageURI).toASCIIString();

    if (!isFilteredCategory(seedCategoryName)) {
      try {

        out = new BufferedWriter(fstream);

        doc = Jsoup.connect(encodedseedPageURI).get();
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

            categoryURLSet.add(categoryURL);
          } else {
            System.out.println("Filtering DBpedia resource: " + categoryName);
          }
        }

        subjectOfElements = doc.body().getElementsByAttributeValue("rev",
            DCT_PREFIX + ":subject");

        for (Element element : subjectOfElements) {
          // System.out.print(element.text() + " ");

          String elementURL = element.attr("href");
          String elementName =
              elementURL.substring(elementURL.lastIndexOf('/') + 1);

          if (!isFilteredCategory(elementURL) && !hasFilteredType(elementURL)) {
            out.write("  \"Category:" + seedCategoryName + "\"" + " -> " + "\""
                + elementName + "\";" + System.lineSeparator());

            categoryURLSet.add(elementURL);

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

    String encodedURL = URI.create(dbpediaUrl).toASCIIString();

    Document doc = Jsoup.connect(encodedURL).get();
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
          out.write("," + frenchURI);
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

  private String extractFrenchURI(String enURI) {
    Document doc;
    Elements sameAsElements;

    String encodedENURI = URI.create(enURI).toASCIIString();

    try {
      doc = Jsoup.connect(encodedENURI).get();

      sameAsElements =
          doc.body().getElementsByAttributeValue("rel", "owl:sameAs");

      for (Element element : sameAsElements) {
        String sameAsUri = element.attr("href");

        if (sameAsUri.contains("fr.dbpedia.org")) {
          System.out.println("------->" + sameAsUri);
          return sameAsUri;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
