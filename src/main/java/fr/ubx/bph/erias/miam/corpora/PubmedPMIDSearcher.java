/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.ubx.bph.erias.miam.utils.MapUtils;
import fr.ubx.bph.erias.miam.utils.WebUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class PubmedPMIDSearcher {

  private static Logger logger =
      Logger.getLogger(PubmedPMIDSearcher.class.getName());

  /**
   * Example URL
   * 
   * https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi
   * ?db=pubmed&term=Drug+Interactions[mesh]
   * +AND+Food[mesh]&retmax=1000&retstart=1000
   * 
   * @return
   */
  private String buildPMIDsSearchURL(String term, Integer start,
      Integer limit) {

    String urlFirstPart = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/"
        + "esearch.fcgi?db=pubmed&term=";

    term = term.replace(" ", "+");

    String meshString = "[mesh]";
    String limitString = "&retmax=" + limit;
    String startString = "&retstart=" + start;

    return urlFirstPart + term + meshString + limitString + startString;
  }

  public List<Integer> findPMIDs(String term, Integer start, Integer limit) {
    List<Integer> pmids = new ArrayList<Integer>();

    String searchURL = buildPMIDsSearchURL(term, start, limit);

    logger.log(Level.INFO, searchURL);

    Document doc = WebUtils.connectWith3Timeouts(searchURL);

    if (doc != null) {
      String response = doc.toString().toLowerCase();

      while (response.contains("<id>")) {

        String pmid = response.substring(response.indexOf("<id>") + 4,
            response.indexOf("</id>"));

        pmid = pmid.trim();

        pmids.add(Integer.parseInt(pmid));

        response = response.substring(response.indexOf("</id>") + 5);
      }
    }

    return pmids;
  }

  public List<Integer> searchAllPMIDs(String term, Integer hits) {

    logger.log(Level.INFO,
        "Searching PMIDs for term " + term + " that has " + hits + " hits");

    List<Integer> pmids = new ArrayList<Integer>();

    Integer loop = hits / 1000;

    for (int i = 0; i <= loop; i++) {
      List<Integer> tmpPMIDs = findPMIDs(term, i * 1000, 1000);

      pmids.addAll(tmpPMIDs);
    }

    logger.log(Level.INFO, "Number of retrieved pmids: " + pmids.size());

    return pmids;
  }

  // https://www.ncbi.nlm.nih.gov/pubmed/?term=29700251%5Buid%5D
  private String buildYearSearchURL(String pmid) {
    String url = "https://www.ncbi.nlm.nih.gov/pubmed/?term=";

    url = url + pmid;
    url = url + "%5Buid%5D";

    return url;
  }

  // id="absdate" value="2018/4/28"
  public String searchYear(String pmid) {

    logger.log(Level.INFO, "Searching year for pmid " + pmid);

    String year = "0";

    String searchURL = buildYearSearchURL(pmid);

    logger.log(Level.INFO, searchURL);

    Document doc = WebUtils.connectWith3Timeouts(searchURL);

    Elements sameAsElements;

    if (doc != null) {
      sameAsElements = doc.body().getElementsByAttributeValue("id", "absdate");

      for (Element element : sameAsElements) {
        String date = element.attr("value");

        year = date.substring(0, date.indexOf("/"));

        logger.log(Level.INFO, year);

        return year;
      }
    }

    return year;
  }

  public void countYears(String pmidsFilePath) {
    RelevanceDatasetBuilder rdb = new RelevanceDatasetBuilder();

    Map<String, Integer> yearsMap = new HashMap<String, Integer>();

    Set<String> pmids = rdb.loadPMIDs(pmidsFilePath);

    for (String pmid : pmids) {
      String year = searchYear(pmid);

      if (yearsMap.containsKey(year)) {
        Integer value = yearsMap.get(year);
        value++;
        
        yearsMap.put(year, value);
      } else {
        yearsMap.put(year, 1);
      }
    }
    
    Set<String> keys = yearsMap.keySet();
    
    for (String key : keys) {
      System.out.println(key + ";" + yearsMap.get(key));
    }
  }
}
