/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;

/**
 * @author Georgeta Bordea
 *
 */
public class QuerySelectionEvaluation {

  private static Logger logger =
      Logger.getLogger(QuerySelectionEvaluation.class.getName());

  public static AttributeSelection GainRatioAttributeEval(Instances data, int n)
      throws Exception {
    AttributeSelection filter = new AttributeSelection();
    GainRatioAttributeEval evaluator = new GainRatioAttributeEval();
    filter.setEvaluator(evaluator);

    Ranker search = new Ranker();
    search.setNumToSelect(n);

    filter.setSearch(search);
    // filter.setInputFormat(data);

    return filter;
  }

  private void buildTestData(String[] selectedFeatures) {

  }

  public void buildTestData(String featureTerm, Integer topDocs,
      String trainingMeshTermsFile, String vocabularyHitsPath,
      String outputFilePath, Integer minFreq) {

    // search pmids
    PubmedPMIDSearcher pps = new PubmedPMIDSearcher();
    PubmedCorpusBuilder pcb = new PubmedCorpusBuilder();

    logger.log(Level.INFO, "Retrieveing relevant documents for " + featureTerm);
    List<Integer> pmids = pps.findPMIDs(featureTerm, 0, topDocs);

    Map<String, Set<String>> testMeshTerms = new HashMap<String, Set<String>>();

    // search MeSH terms
    for (Integer pmid : pmids) {

      logger.log(Level.INFO, "Searching MeSH terms for PMID: " + pmid);

      List<String> meshTerms = pcb.collectMeshTerms(pmid.toString());

      Set<String> meshTermsSet = new HashSet<String>(meshTerms);

      testMeshTerms.put(pmid.toString(), meshTermsSet);
    }

    // predictions
    RelevanceDatasetBuilder rdb = new RelevanceDatasetBuilder();

    logger.log(Level.INFO, "Printing test data to " + outputFilePath);
    rdb.outputTestDataInARFF(trainingMeshTermsFile, testMeshTerms,
        outputFilePath, vocabularyHitsPath, minFreq);
  }

  public void computePrecisionRecall(String relevantPMIDsPath, String searchTerm,
      Integer limit) {
    PubmedPMIDSearcher pps = new PubmedPMIDSearcher();

    MeshTermsStats mts = new MeshTermsStats();

    Integer hits = mts.searchPubMedHits(searchTerm);
    
    if ((limit == null) || (limit > hits)){
      limit = hits;
    }

    List<Integer> retrievedPMIDs = pps.searchAllPMIDs(searchTerm, limit);

    RelevanceDatasetBuilder rdb = new RelevanceDatasetBuilder();

    Set<String> relevantDocs = rdb.loadPMIDs(relevantPMIDsPath);

    Integer matches = 0;
    for (Integer pmid : retrievedPMIDs) {
      if (relevantDocs.contains(pmid.toString())) {
        matches++;
      }
    }

    Double recall = ((double) matches) / relevantDocs.size();
    Double precision = ((double) matches) / retrievedPMIDs.size();

    logger.log(Level.INFO, "Matched " + matches);

    logger.log(Level.INFO, "Precision is " + precision);
    logger.log(Level.INFO, "Recall is " + recall);
  }
}
