/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.ubx.bph.erias.miam.utils.WebUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class PubmedCorpusBuilder {

  private static Logger logger =
      Logger.getLogger(PubmedCorpusBuilder.class.getName());

  public static final String PUBMED_URL =
      // "https://www.ncbi.nlm.nih.gov/pubmed";
      "https://www.ncbi.nlm.nih.gov/pubmed/?term=";

  // TODO parse references file
  // find PMID by title and date
  // get abstract, full text, MESH terms, cited by

  public List<Integer> findCitedInPMIDs(String filePath,
      String outputFilePath) {
    List<Integer> citedInPMIDs = new ArrayList<Integer>();

    BufferedWriter out = null;
    FileWriter fstream;

    Elements elements;

    try {
      BufferedReader input = new BufferedReader(new FileReader(filePath));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          System.out.println("Search citations for " + line);

          String searchURL = buildCitedInSearchURL(line);

          Document doc = WebUtils.connectWith3Timeouts(searchURL);

          elements =
              doc.getElementsByAttributeValueContaining("ref", "ncbi_uid");

          for (Element element : elements) {
            String pmid = element.attr("href");

            if (pmid.startsWith("/pubmed/")) {

              pmid = pmid.substring(pmid.lastIndexOf("/") + 1);
              System.out.println(pmid);

              out.write(pmid + System.lineSeparator());
            }
          }

        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return citedInPMIDs;
  }

  public void countPubMedCitations(String pmidFilePath, String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> countPMIDS = new HashMap<String, Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(pmidFilePath));

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          if (!countPMIDS.keySet().contains(line)) {
            countPMIDS.put(line, 1);
          } else {
            Integer count = countPMIDS.get(line);

            count++;

            countPMIDS.put(line, count);
          }
        }

        Set<String> pmids = countPMIDS.keySet();

        // write PubMed ids and their counts to the output file
        fstream = new FileWriter(outputFilePath, true);
        out = new BufferedWriter(fstream);

        for (String pmid : pmids) {
          out.write(
              pmid + "\t" + countPMIDS.get(pmid) + System.lineSeparator());
        }

      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Example URL
   * 
   * https://www.ncbi.nlm.nih.gov/pubmed?linkname=
   * pubmed_pubmed_citedin&from_uid=12087344
   * <dd>elements
   * 
   * @return
   */
  private String buildCitedInSearchURL(String pmid) {

    String urlFirstPart =
        PUBMED_URL + "?linkname=" + "pubmed_pubmed_citedin&from_uid=";

    return urlFirstPart + pmid;
  }

  public List<Integer> findPMIDs(String filePath, String outputFilePath) {
    List<Integer> pmids = new ArrayList<Integer>();

    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(filePath));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          System.out.println("Parsing reference: " + line);

          String[] citationFields = line.split("\\. |\\? ");

          String title = citationFields[1];
          String venue = citationFields[2];

          if (!venue.contains("(")) {
            // title = title + venue;

            venue = citationFields[3];
          } else if (!venue.contains("(")) {
            // title = title + venue;

            venue = citationFields[4];
          } else if (!venue.contains("(")) {
            // title = title + venue;

            venue = citationFields[5];
          }

          String year =
              venue.substring(venue.indexOf("(") + 1, venue.indexOf(")"));

          // System.out.println(title + " " + year);

          String searchURL = buildPMIDsSearchURL(title, year);

          // System.out.println(searchURL);

          Document doc = WebUtils.connectWith3Timeouts(searchURL);

          if (doc != null) {
            String response = doc.toString().toLowerCase();

            if (response.contains("<id>")) {

              String pmid = response.substring(response.indexOf("<id>") + 4,
                  response.indexOf("</id>"));

              pmid = pmid.trim();

              out.write(line + "\t" + pmid + System.lineSeparator());
            } else {
              System.out.println("PMID not found for: " + line);
            }
          }
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return pmids;
  }

  /**
   * Example URL
   * 
   * https://eutils.ncbi.nlm.nih.gov/entrez/eutils/
   * esearch.fcgi?db=pubmed&term=How+to+minimize+interaction
   * +between+phenytoin+and+enteral+feedings+AND+1996[pdat]
   * 
   * @return
   */
  private String buildPMIDsSearchURL(String title, String year) {

    String urlFirstPart = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/"
        + "esearch.fcgi?db=pubmed&term=";

    title = title.replace("(", "");
    title = title.replace(")", "");

    title = title.replace(" ", "+");

    String andString = "+AND+";

    String dateString = "[pdat]";

    return urlFirstPart + title + andString + year + dateString;
  }

  public Integer findPMID(String title, String year) {
    return null;
  }

  public List<Integer> findCitedBy(Integer pmid) {
    List<Integer> pmids = new ArrayList<Integer>();

    return pmids;
  }

  public List<String> collectMeshTerms(String pmid) {
    // String pubmedPmidURL = PUBMED_URL + "/" + pmid;
    String pubmedPmidURL = PUBMED_URL + pmid;

    List<String> meshTerms = new ArrayList<String>();

    Document doc = WebUtils.connectWith3Timeouts(pubmedPmidURL);

    Elements elements;

    if (doc != null) {
      elements = doc.getElementsByAttributeValueContaining("alsec", "mesh");

      for (Element element : elements) {
        String meshTerm = element.text();

        meshTerms.add(meshTerm);
      }
    }

    return meshTerms;
  }

  public void collectAllMeshTerms(String pmidsFilePath, String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(pmidsFilePath));
      fstream = new FileWriter(outputFilePath, false);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {
          List<String> meshTerms = collectMeshTerms(line);

          String meshTermsString = "";

          for (String meshTerm : meshTerms) {
            meshTerm = meshTerm.trim();
            meshTermsString = meshTermsString + meshTerm + ";";
          }

          String output = line;

          if (meshTermsString != "") {
            output = output + ";"
                + meshTermsString.substring(0, meshTermsString.length() - 1);
          }

          out.write(output + System.lineSeparator());

          System.out.println(output);
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void generateRandomPMIDs(Integer maxPMID, String pmidsFilePath,
      String outputFilePath, Integer morePMIDs) {

    BufferedWriter out = null;
    FileWriter fstream;

    List<Integer> pmids = new ArrayList<Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(pmidsFilePath));
      fstream = new FileWriter(outputFilePath, false);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {
          pmids.add(Integer.parseInt(line));
        }

        List<Integer> randomPMIDs = new ArrayList<Integer>();

        Random rand = new Random();

        Integer i = 1;

        while (i <= pmids.size() + morePMIDs) {

          int n = rand.nextInt(maxPMID) + 1;

          if (!pmids.contains(n) && !randomPMIDs.contains(n)) {
            randomPMIDs.add(n);
            i++;
          }
        }

        for (Integer j : randomPMIDs) {
          out.write(j + System.lineSeparator());
        }

      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void downloadAbstracts(String pmidsFilePath, String outputDirectory) {

    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(pmidsFilePath));

      try {
        String line = null;

        while ((line = input.readLine()) != null) {
          String pmid = line;

          logger.log(Level.INFO, "Downloading abstract for PMID " + pmid);

          BufferedWriter out = null;
          fstream = new FileWriter(outputDirectory + pmid + ".txt", false);

          try {
            out = new BufferedWriter(fstream);

            String abstractText = downloadAbstract(pmid);

            out.write(abstractText);

          } finally {
            out.close();
          }
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private String downloadAbstract(String pmid) {

    String pubmedPmidURL = PUBMED_URL + pmid;

    String abstractText = "";

    Document doc = WebUtils.connectWith3Timeouts(pubmedPmidURL);

    Elements elements;

    if (doc != null) {
      elements = doc.getElementsByAttributeValueContaining("class", "abstr");

      for (int i = 0; i < elements.size() - 1; i++) {

        Element element = elements.get(i);
        abstractText = abstractText + getTextRecursively(element);
      }

      // Elements childElements = element.children();
      // for (Element childElement : childElements) {
      // abstractText =
      // abstractText + childElement.text() + System.lineSeparator();
      // }
    }

    return cleanText(abstractText);
  }

  private String getTextRecursively(Element element) {

    String text = "";

    if (element.ownText() != "") {
      text = element.ownText();
    }

    Elements childElements = element.children();

    for (Element childElement : childElements) {
      text = text + System.lineSeparator() + getTextRecursively(childElement);
    }

    return text;
  }

  private String cleanText(String abstractText) {

    String linkoutString = "LinkOut - more resources";

    if (abstractText.contains(linkoutString)) {
      abstractText =
          abstractText.substring(0, abstractText.indexOf(linkoutString));
    }

    return abstractText;
  }
}
