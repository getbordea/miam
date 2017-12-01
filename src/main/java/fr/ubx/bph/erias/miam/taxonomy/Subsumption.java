/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

import fr.ubx.bph.erias.miam.lucene.SearchDocuments;

/**
 * @author Georgeta Bordea
 * 
 */
public class Subsumption {

  private static final String dsgt =
      "note,execution,domain,reason,contrast,formulation,search,"
          + "possibility,bound,cost,procedure,type,class,alternative,selection,"
          + "communication,transformation,solution,language,research,principle,"
          + "constraint,addition,scheme,nature,memory,test,journal,code,survey,"
          + "result,limitation,architecture,framework,extension,simulation,"
          + "space,network,information,context,discussion,call,version,level,"
          + "specification,transaction,function,example,approximation,"
          + "experiment,difference,theory,comparison,acm,direction,process,"
          + "combination,relation,construction,paper,section,effect,control,"
          + "strategy,machine,proceedings,generation,support,requirement,"
          + "structure,performance,practice,development,improvement,data,"
          + "design,distribution,time,evaluation,generalization,complexity,"
          + "problem,study,implementation,method,program,model,algorithm,ctr,"
          + "approach,application,computer,system,optimal,optimization,"
          + "efficiency,analysis,base,technique,introduction,generate,support,"
          + "require,solve,apply,perform,include,develop,improve,adapt,"
          + "propose,design,distribute,provide,evaluate,study,implement,model,"
          + "compute,base";

  private static Logger logger = Logger.getLogger(Subsumption.class.getName());

  private static Double subsumbtionThreshold = 0.1;

  public static Integer computeCoocurrence(Map<String, Integer> occMap1,
      Map<String, Integer> occMap2, Integer docsNo) {
    Integer cooc = 0;

    try {
      Set<String> keys = occMap1.keySet();
      for (String key : keys) {
        if (occMap2.containsKey(key)) {
          cooc++;
        }
      }
    } catch (Exception e) {
      logger.log(Level.WARN,
          "Could not compute coocurrence, caught exception ..");
      e.printStackTrace();
    }

    return cooc;
  }

  public static Boolean subsumes(String t1, String t2, Integer docsCount,
      String luceneIndexPath) throws SQLException {

    SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));

    try {
      Map<String, Integer> occMap1 = sd.searchOccurrence(t1, docsCount);
      Map<String, Integer> occMap2 = sd.searchOccurrence(t2, docsCount);

      Integer cooc =
          Subsumption.computeCoocurrence(occMap1, occMap2, docsCount);

      Integer t1Docs = occMap1.size();
      Integer t2Docs = occMap2.size();

      double pt1t2 = (double) cooc / t1Docs;
      double pt2t1 = (double) cooc / t2Docs;

      System.out.println(pt1t2 + " " + pt2t1);
      if ((pt1t2 >= subsumbtionThreshold) && (pt2t1 < subsumbtionThreshold)) {
        return true;
      } else {
        return false;
      }

    } catch (CorruptIndexException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  public static Map<String, Double> computePMIValues(SearchDocuments sd,
      String topic, Map<String, Long> topics, Integer docsCount,
      Integer totalTokensNo, Integer spanSize,
      Map<String, Map<String, Double>> pmiMaps) {

    Map<String, Double> pmiMap = new HashMap<String, Double>();

    try {
      Set<String> topisKeys = topics.keySet();

      Long overallFreq = sd.computeFrequency(topic, docsCount);

      for (String kp : topisKeys) {
        Long spanFreq = new Long(0);

        // only compute pmi once for a pair of terms
        if (topic != kp) {
          spanFreq = sd.computeSpanOccurrence(topic, kp, docsCount, spanSize);

          double pxy = (double) spanFreq / totalTokensNo;
          double px = (double) topics.get(kp) / totalTokensNo;
          double py = (double) overallFreq / totalTokensNo;

          if (spanFreq > 0) {
            pmiMap.put(kp, Math.log(pxy / (px * py)));
          }
        } else {
          if (pmiMaps.get(kp) != null && pmiMaps.get(kp).get(topic) != null) {
            pmiMap.put(kp, pmiMaps.get(kp).get(topic));
          }
        }
      }

    } catch (CorruptIndexException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return pmiMap;
  }

  public static Double sumPMI(Map<String, Double> pmiMap) {
    Double contextWordsRank = 0.0;

    Set<String> keys = pmiMap.keySet();

    for (String key : keys) {
      contextWordsRank += pmiMap.get(key);
    }

    return contextWordsRank;
  }

  public static Double sumPMI(SearchDocuments sd, String topic,
      Map<String, Long> topics, Integer docsCount, Integer totalTokensNo) {

    Double contextWordsRank = 0.0;
    try {

      Set<String> topisKeys = topics.keySet();

      Long overallFreq = sd.computeFrequency(topic, docsCount);
      for (String kp : topisKeys) {
        // check first at least 1 document mention them together

        Long spanFreq = new Long(0);
        if (kp != null) {
          spanFreq = sd.computeSpanOccurrence(topic, kp, docsCount, 5);
        } else {
          spanFreq = sd.computeSpanOccurrence(topic, kp, docsCount, 5);
        }

        double pxy = (double) spanFreq / totalTokensNo;
        double px = (double) topics.get(kp) / totalTokensNo;
        double py = (double) overallFreq / totalTokensNo;

        if (spanFreq > 0) {
          contextWordsRank += Math.log(pxy / (px * py));
        }
      }
    } catch (CorruptIndexException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return contextWordsRank; // topics.size();
  }

  public static Map<String, Double> computeSumPMIOptimised(List<String> topics,
      Map<String, Map<String, Double>> pmiMaps) {

    Map<String, Double> sumPMIMap = new HashMap<String, Double>();

    for (String topic : topics) {

      Subsumption.logger.log(Level.INFO,
          "Computing sum PMI for topic " + topic);
      Double sumPMI = sumPMI(pmiMaps.get(topic));

      sumPMIMap.put(topic, sumPMI);
    }

    // return MapUtils.sortByDoubleValue(pmiMap);
    return sumPMIMap;
  }

  public static Map<String, Map<String, Double>> computePMIMaps(
      List<String> topics, Integer spanSize, Integer docsCount,
      String luceneIndexPath) {

    SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));

    Map<String, Map<String, Double>> pmiMaps =
        new HashMap<String, Map<String, Double>>();

    try {

      Map<String, Long> topicsMap = new HashMap<String, Long>();

      for (String topic : topics) {
        Long overallFreq = sd.computeFrequency(topic, docsCount);
        topicsMap.put(topic, new Long(overallFreq));
      }

      for (String topic : topics) {

        logger.log(Level.INFO,
            "Computing PMI values for topic " + topic + "..");
        Map<String, Double> pmiMap = computePMIValues(sd, topic, topicsMap,
            docsCount, Integer.MAX_VALUE, spanSize, pmiMaps);
        pmiMaps.put(topic, pmiMap);
      }
    } catch (CorruptIndexException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return pmiMaps;
  }

  public static Map<String, Double> computeSumPMI(List<String> topics,
      Boolean topKP, String outputFile, Integer docsCount,
      String luceneIndexPath) throws IOException {

    Map<String, Long> topicsMap = new HashMap<String, Long>();
    SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));
    BufferedWriter out = null;

    Map<String, Double> pmiMap = new HashMap<String, Double>();

    try {
      // Create file
      FileWriter fstream = new FileWriter(outputFile, false);
      out = new BufferedWriter(fstream);

      if (topKP) {
        // use all top topics
        for (String topic : topics) {
          Long overallFreq = sd.computeFrequency(topic, docsCount);
          topicsMap.put(topic, new Long(overallFreq));
        }
      } else {
        // use DSGT
        List<String> dsgts = extractDSGT();
        for (String dsgt : dsgts) {
          topicsMap.put(dsgt, sd.computeFrequency(dsgt, docsCount));
        }
      }

      for (String topic : topics) {

        Subsumption.logger.log(Level.INFO,
            "Computing sum PMI for topic " + topic);

        // Should use the total number of tokens instead of max integer
        Double pmi = sumPMI(sd, topic, topicsMap, docsCount, Integer.MAX_VALUE);

        pmiMap.put(topic, pmi);
        out.write(topic + "," + pmi + "\n");
      }
    } catch (CorruptIndexException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // Close the output stream
      if (out != null) {
        out.close();
      }
    }

    // return MapUtils.sortByDoubleValue(pmiMap);
    return pmiMap;
  }

  /**
   * 
   */
  private static List<String> extractDSGT() {
    List<String> dsgtList = new ArrayList<String>();

    String[] dsgts = dsgt.split(",");
    for (String context : dsgts) {

      if (context != null) {
        dsgtList.add(context);
      }
    }

    return dsgtList;
  }
}
