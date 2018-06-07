/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ubx.bph.erias.miam.db.UMLSConceptHierarchies;
import fr.ubx.bph.erias.miam.utils.MapUtils;
import fr.ubx.bph.erias.miam.utils.WebUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class MeshTermsStats {

  private static Logger logger =
      Logger.getLogger(MeshTermsStats.class.getName());

  public void countFreqQualifiers(String meshTermsFile, String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> meshQualifierFreq = new HashMap<String, Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            if (meshTerm.contains("/")) {

              String qualifier = meshTerm.substring(meshTerm.indexOf("/") + 1);

              if (meshQualifierFreq.containsKey(qualifier)) {
                Integer freq = meshQualifierFreq.get(qualifier);
                freq++;

                meshQualifierFreq.put(qualifier, freq);
              } else {
                meshQualifierFreq.put(qualifier, 1);
              }
            }
          }
        }

        Set<String> keys = meshQualifierFreq.keySet();

        for (String key : keys) {
          out.write(
              key + ";" + meshQualifierFreq.get(key) + System.lineSeparator());
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void countFrequencies(String meshTermsFile, String outputFilePath,
      Boolean noMajor, Boolean noQualifiers) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> meshTermFreq = new HashMap<String, Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            meshTerm = stripMeSHTerm(noMajor, noQualifiers, meshTerm);

            if (meshTermFreq.containsKey(meshTerm)) {
              Integer freq = meshTermFreq.get(meshTerm);
              freq++;

              meshTermFreq.put(meshTerm, freq);
            } else {
              meshTermFreq.put(meshTerm, 1);
            }
          }
        }

        Set<String> keys = meshTermFreq.keySet();

        for (String key : keys) {
          out.write(key + ";" + meshTermFreq.get(key) + System.lineSeparator());
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private String stripMeSHTerm(Boolean noMajor, Boolean noQualifiers,
      String meshTerm) {
    // Ignoring major topics
    if (noMajor && meshTerm.endsWith("*")) {
      meshTerm = meshTerm.substring(0, meshTerm.length() - 1);
    }

    // Discarding qualifiers
    if (noQualifiers && meshTerm.contains("/")) {
      String[] meshParts = meshTerm.split("/");

      meshTerm = meshParts[0];
    }
    return meshTerm;
  }

  public void countCoocurrences(String meshTermsFile, String meshTermsFreqFile,
      String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> meshTermCooc = new HashMap<String, Integer>();
    Map<String, Integer> meshTermFreqMap = new HashMap<String, Integer>();

    Map<String, Double> normalisedMeshTermCoocMap =
        new HashMap<String, Double>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      BufferedReader input1 =
          new BufferedReader(new FileReader(meshTermsFreqFile));

      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {
            for (int j = 1; j < values.length; j++) {

              String meshTerm1 = values[i];
              String meshTerm2 = values[j];

              if (meshTerm1 != meshTerm2) {
                String key = meshTerm1 + "+" + meshTerm2;

                if (meshTermCooc.containsKey(key)) {
                  Integer cooc = meshTermCooc.get(key);
                  cooc++;

                  meshTermCooc.put(key, cooc);
                } else {
                  meshTermCooc.put(key, 1);
                }
              }
            }
          }
        }

        while ((line = input1.readLine()) != null) {
          String[] values = line.split(";");

          meshTermFreqMap.put(values[0], Integer.parseInt(values[1]));
        }

        Set<String> keys = meshTermCooc.keySet();

        for (String key : keys) {

          String[] terms = key.split("\\+");

          String term1 = terms[0];
          String term2 = terms[1];

          Double cooc = (double) meshTermCooc.get(key);

          Integer term1Freq = meshTermFreqMap.get(term1);
          Integer term2Freq = meshTermFreqMap.get(term2);

          Double normalisedCooc = cooc / (term1Freq + term2Freq);

          normalisedMeshTermCoocMap.put(key, normalisedCooc);
        }

        normalisedMeshTermCoocMap =
            MapUtils.sortByValue(normalisedMeshTermCoocMap);

        Set<String> outputKeys = normalisedMeshTermCoocMap.keySet();

        for (String key : outputKeys) {
          out.write(key + ";" + normalisedMeshTermCoocMap.get(key)
              + System.lineSeparator());
        }

      } finally {
        input.close();
        input1.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void computeNormalisedFrequencies(String meshTermsFreqFile,
      String randomMeshTermsFreqFile, String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> meshTermFreqMap = new HashMap<String, Integer>();
    Map<String, Integer> randomMeshTermFreqMap = new HashMap<String, Integer>();

    Map<String, Double> normalisedMeshTermFreqMap =
        new HashMap<String, Double>();

    try {
      BufferedReader inputFreq =
          new BufferedReader(new FileReader(meshTermsFreqFile));
      BufferedReader inputRandomFreq =
          new BufferedReader(new FileReader(randomMeshTermsFreqFile));

      fstream = new FileWriter(outputFilePath, true);
      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = inputFreq.readLine()) != null) {

          String[] values = line.split(";");

          meshTermFreqMap.put(values[0], Integer.parseInt(values[1]));
        }

        while ((line = inputRandomFreq.readLine()) != null) {

          String[] values = line.split(";");

          randomMeshTermFreqMap.put(values[0], Integer.parseInt(values[1]));
        }

        Set<String> keys = meshTermFreqMap.keySet();

        for (String key : keys) {

          Double normalisedFreq = 0.0;

          if (randomMeshTermFreqMap.containsKey(key)) {
            normalisedFreq = (double) meshTermFreqMap.get(key)
                / ((double) randomMeshTermFreqMap.get(key));
          } else {
            normalisedFreq = (double) meshTermFreqMap.get(key);
          }

          normalisedMeshTermFreqMap.put(key, normalisedFreq);

        }

        normalisedMeshTermFreqMap =
            MapUtils.sortByValue(normalisedMeshTermFreqMap);

        Set<String> outputKeys = normalisedMeshTermFreqMap.keySet();

        for (String key : outputKeys) {
          out.write(key + ";" + normalisedMeshTermFreqMap.get(key)
              + System.lineSeparator());
        }

      } finally {
        inputFreq.close();
        inputRandomFreq.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void computeNormalisedFrequencies(String meshTermsFreqFile,
      String outputFilePath, Integer docsNo, Integer maxPubmedDocsNo,
      Boolean noMajor, Boolean noQualifiers) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> meshTermFreqMap = new HashMap<String, Integer>();

    Map<String, Integer> pubmedHitsMap = new HashMap<String, Integer>();

    Map<String, Double> normalisedMeshTermFreqMap =
        new HashMap<String, Double>();

    try {
      BufferedReader inputFreq =
          new BufferedReader(new FileReader(meshTermsFreqFile));

      fstream = new FileWriter(outputFilePath, false);
      out = new BufferedWriter(fstream);

      System.out.println("Loading file " + meshTermsFreqFile);
      
      try {
        String line = null;

        while ((line = inputFreq.readLine()) != null) {

          String[] values = line.split(";");

          meshTermFreqMap.put(values[0], Integer.parseInt(values[1]));
          
          System.out.println("Reading fraquency for: " + values[0]);
        }

        Set<String> keys = meshTermFreqMap.keySet();

        for (String key : keys) {

          Integer pubmedHits = searchPubMedHits(key);

          Double normalisedFreq = ((double) meshTermFreqMap.get(key) / docsNo)
              / ((double) (1 + pubmedHits) / maxPubmedDocsNo);

          pubmedHitsMap.put(key, pubmedHits);

          normalisedMeshTermFreqMap.put(key, normalisedFreq);

        }

        normalisedMeshTermFreqMap =
            MapUtils.sortByValue(normalisedMeshTermFreqMap);

        Set<String> outputKeys = normalisedMeshTermFreqMap.keySet();

        for (String key : outputKeys) {
          out.write(key + ";" + meshTermFreqMap.get(key) + ";"
              + pubmedHitsMap.get(key) + ";"
              + normalisedMeshTermFreqMap.get(key) + System.lineSeparator());
        }

      } finally {
        inputFreq.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Search URL:
   * https://www.ncbi.nlm.nih.gov/pubmed?term=%22Risk+Factors%22[MeSH+Terms]
   * 
   * @param meshTerm
   */
  public Integer searchPubMedHits(String meshTerm) {
    meshTerm = meshTerm.replace(" ", "+");
    String searchURL = PubmedCorpusBuilder.PUBMED_URL + meshTerm + "[mesh]";

    System.out.println(searchURL);

    Document doc = WebUtils.connectWith3Timeouts(searchURL);

    if (doc != null) {
      Elements elements =
          doc.getElementsByAttributeValueContaining("id", "resultcount");

      for (Element element : elements) {
        Integer hits = Integer.parseInt(element.attr("value"));

        System.out.println(meshTerm + " " + hits);

        return hits;
      }
    } else {
      logger.log(Level.INFO,
          "PubMed timed out, returning 0 hits for the term " + meshTerm);
    }

    return 0;
  }

  public void filterFoods(String meshTermsFile, String outputFilePath) {

    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          out.write(values[0]);

          Set<String> foods = new HashSet<String>();

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            if (meshTerm.contains("/")) {
              meshTerm = meshTerm.substring(0, meshTerm.indexOf("/"));
            }

            if (UMLSConceptHierarchies.isFoodRelatedConcept(meshTerm)) {
              foods.add(meshTerm);
            }
          }

          for (String meshTerm : foods) {
            out.write(";" + meshTerm);
          }

          out.write("\n");
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void filterCohort(String meshTermsFile, String outputFilePath) {

    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          out.write(values[0]);

          Set<String> cohortTerms = new HashSet<String>();

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            if (meshTerm.contains("/")) {
              meshTerm = meshTerm.substring(0, meshTerm.indexOf("/"));
            }

            if (UMLSConceptHierarchies.isCohortRelatedConcept(meshTerm)) {
              cohortTerms.add(meshTerm);
            }
          }

          for (String meshTerm : cohortTerms) {
            out.write(";" + meshTerm);
          }

          out.write("\n");
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void filterDrugs(String meshTermsFile, String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          out.write(values[0]);

          Set<String> foods = new HashSet<String>();

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            if (meshTerm.contains("/")) {
              meshTerm = meshTerm.substring(0, meshTerm.indexOf("/"));
            }

            if (UMLSConceptHierarchies.isDrug(meshTerm)) {
              foods.add(meshTerm);
            }
          }

          for (String meshTerm : foods) {
            out.write(";" + meshTerm);
          }

          out.write("\n");
        }
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void filterOtherThanFoodsAndDrugsAndDiseases(String meshTermsFile,
      String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          out.write(values[0]);

          Set<String> otherTerms = new HashSet<String>();

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            if (meshTerm.contains("/")) {
              meshTerm = meshTerm.substring(0, meshTerm.indexOf("/"));
            }

            if (!UMLSConceptHierarchies.isDrug(meshTerm)
                && !UMLSConceptHierarchies.isFoodRelatedConcept(meshTerm)
                && !UMLSConceptHierarchies.isDiseaseRelatedConcept(meshTerm)
                && !UMLSConceptHierarchies
                    .isInvestigationDiagnosisRelatedConcept(meshTerm)
                && !UMLSConceptHierarchies.isCohortRelatedConcept(meshTerm)) {
              otherTerms.add(meshTerm);
            }
          }

          for (String meshTerm : otherTerms) {
            out.write(";" + meshTerm);
          }

          out.write("\n");
        }
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void filterDiseases(String meshTermsFile, String outputFilePath) {

    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          out.write(values[0]);

          Set<String> diseases = new HashSet<String>();

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            if (meshTerm.contains("/")) {
              meshTerm = meshTerm.substring(0, meshTerm.indexOf("/"));
            }

            if (UMLSConceptHierarchies.isDiseaseRelatedConcept(meshTerm)) {
              diseases.add(meshTerm);
            }
          }

          for (String meshTerm : diseases) {
            out.write(";" + meshTerm);
          }

          out.write("\n");
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void filterInvestigationDiagnosisTerms(String meshTermsFile,
      String outputFilePath) {

    BufferedWriter out = null;
    FileWriter fstream;

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          out.write(values[0]);

          Set<String> foods = new HashSet<String>();

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            if (meshTerm.contains("/")) {
              meshTerm = meshTerm.substring(0, meshTerm.indexOf("/"));
            }

            if (UMLSConceptHierarchies
                .isInvestigationDiagnosisRelatedConcept(meshTerm)) {
              foods.add(meshTerm);
            }
          }

          for (String meshTerm : foods) {
            out.write(";" + meshTerm);
          }

          out.write("\n");
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
