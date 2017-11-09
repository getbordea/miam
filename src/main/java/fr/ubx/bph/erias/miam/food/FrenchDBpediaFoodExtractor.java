/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Georgeta Bordea
 *
 */
public class FrenchDBpediaFoodExtractor extends DBpediaFoodExtractor {

  public final String DCT_PREFIX = "dcterms";

  public String[] STOP_WORDS = { "variétés", "fiction", "personnalité",
      "famille", "pâtissier", "filiale", "entreprise", "chocolaterie",
      "fabrication", "musée", "ravageur", "chocolatier", "court_métrage",
      "élevage", "viticulture", "race_d", "donald_duck", "toponyme",
      "collaborateur", "production", "personnes", "publication", "industrie",
      "maladie", "fabricant", "dans_la_culture", "entreprises", "société",
      "restaurant", "science", "boulangeries", "agriculture", "_pubs", "pubs_",
      "distilleries", "distillerie", "histoire", "film", "films", "musique",
      "plantations", "raffineries", "organisations", "fictives", "chaînes",
      "culture dans", "régions", "cultures", "brasseries", "brassage",
      "brasseurs", "caves", "vignobles", "festivals", "viticoles", "blasons",
      "fondateurs", "franchises", "chefs", "aliments pour animaux", "boutiques",
      "salons", "détaillants", "fromagers", "personnages", "épisodes",
      "fourgons à glace", "verger", "économie", "maisons", "pathogènes",
      "indications géographiques", "commerce", "normes", "campagnes", "litiges",
      "joueur", "brume", "crise", "scandale", "culture populaire", "moulins",
      "books", "liste de", "listes de", "marque", "producteur", "jeu vidéo",
      "séries télévisées", "théorie", "logos", "série" };

  public String[] STOP_CATEGORIES = { "vinification" };

  public String[] LEAF_CATEGORIES_FR = { "vin" };

  public void printURIsToFile(Set<String> uriSet) throws IOException {
    FileWriter fstream;

    BufferedWriter out = null;

    try {

      fstream = new FileWriter(URI_OUTPUT_FILE, false);
      out = new BufferedWriter(fstream);

      for (String uri : uriSet) {
        out.write(uri);

        String frenchURI = extractEnglishURI(uri);

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

  private String extractEnglishURI(String enURI) {
    Document doc;
    Elements sameAsElements;

    String encodedENURI = URI.create(enURI).toASCIIString();

    try {
      doc = Jsoup.connect(encodedENURI).get();

      sameAsElements =
          doc.body().getElementsByAttributeValue("rel", "owl:sameAs");

      for (Element element : sameAsElements) {
        String sameAsUri = element.attr("href");

        if (sameAsUri.contains("commons.dbpedia.org")) {
          System.out.println("Found the English URI: " + sameAsUri);
          return sameAsUri;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
