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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ubx.bph.erias.miam.db.UMLSConceptHierarchies;

/**
 * @author Georgeta Bordea
 *
 */
public class RelevanceDatasetBuilder {

  private static Logger logger =
      Logger.getLogger(RelevanceDatasetBuilder.class.getName());

  public Set<String> buildVocabulary(Integer minimumFrequency,
      String meshTermsFile, Boolean withQualifiers) {
    Set<String> vocabulary = new HashSet<String>();

    Map<String, Integer> meshTermFreq =
        countMeshTermsFrequencies(meshTermsFile, withQualifiers);

    Set<String> meshTermFreqSet = meshTermFreq.keySet();

    for (String meshTerm : meshTermFreqSet) {
      if (meshTermFreq.get(meshTerm) > minimumFrequency) {
        vocabulary.add(meshTerm);
      }
    }

    return vocabulary;
  }

  public Set<String> buildQualifierVocabulary(Integer minimumFrequency,
      String meshTermsFile) {
    Set<String> vocabulary = new HashSet<String>();

    Map<String, Integer> qualifierFreq =
        countMeshQualifiersFrequencies(meshTermsFile);

    Set<String> qualifierFreqSet = qualifierFreq.keySet();

    for (String qualifier : qualifierFreqSet) {
      if (qualifierFreq.get(qualifier) > minimumFrequency) {
        vocabulary.add(qualifier);
      }
    }

    return vocabulary;
  }

  public Set<String> loadPMIDs(String pmidsFile) {
    Set<String> pmids = new HashSet<String>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(pmidsFile));
      try {
        String line = null;

        while ((line = input.readLine()) != null) {
          pmids.add(line);
        }

      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return pmids;
  }

  public Map<String, Integer> loadVocabularyHits(String vocabularyHitsPath) {
    Map<String, Integer> vocabularyHits = new HashMap<String, Integer>();

    try {
      BufferedReader input =
          new BufferedReader(new FileReader(vocabularyHitsPath));
      try {
        String line = null;

        while ((line = input.readLine()) != null) {
          String[] values = line.split(";");

          vocabularyHits.put(values[0], Integer.parseInt(values[1]));
        }

      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return vocabularyHits;
  }

  public Map<String, Integer> loadPubmedHits(Set<String> vocabulary) {
    Map<String, Integer> pubmedHits = new HashMap<String, Integer>();

    MeshTermsStats mts = new MeshTermsStats();

    for (String meshTerm : vocabulary) {
      pubmedHits.put(meshTerm, mts.searchPubMedHits(meshTerm));
    }

    return pubmedHits;
  }

  public Map<String, Integer> countMeshTermsFrequencies(String meshTermsFile,
      Boolean withQualifiers) {

    Map<String, Integer> meshTermFreq = new HashMap<String, Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            meshTerm = removeMeshAnnotations(withQualifiers, meshTerm);

            if (meshTermFreq.containsKey(meshTerm)) {
              Integer freq = meshTermFreq.get(meshTerm);
              freq++;

              meshTermFreq.put(meshTerm, freq);
            } else {
              meshTermFreq.put(meshTerm, 1);
            }
          }
        }
      } finally {
        input.close();
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return meshTermFreq;
  }

  public Map<String, Integer> countMeshTermsCoocurrences(Integer minFreq,
      String meshTermsFile, Boolean withQualifiers,
      Map<String, Integer> meshTermFreqs) {

    Map<String, Integer> meshTermCooc = new HashMap<String, Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {

              String meshTerm1 = values[i];
              String meshTerm2 = values[j];

              meshTerm1 = removeMeshAnnotations(withQualifiers, meshTerm1);
              meshTerm2 = removeMeshAnnotations(withQualifiers, meshTerm2);

              if ((meshTermFreqs.get(meshTerm1) > minFreq)
                  && (meshTermFreqs.get(meshTerm2) > minFreq)) {
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
      } finally {
        input.close();
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return meshTermCooc;
  }

  /**
   * Method used to clean major topics annotations and MeSH qualifiers
   * 
   * @param withQualifiers
   * @param meshTerm
   * @return the cleaned term
   */
  private String removeMeshAnnotations(Boolean withQualifiers,
      String meshTerm) {
    // ignoring major topics
    if (meshTerm.endsWith("*")) {
      meshTerm = meshTerm.substring(0, meshTerm.length() - 1);
    }

    if (!withQualifiers) {
      meshTerm = meshTerm.split("/")[0];
    }
    return meshTerm;
  }

  public Map<String, Integer> countMeshQualifiersFrequencies(
      String meshTermsFile) {

    Map<String, Integer> qualifierFreq = new HashMap<String, Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];
            String qualifier = "";

            // ignoring major topics
            if (meshTerm.endsWith("*")) {
              meshTerm = meshTerm.substring(0, meshTerm.length() - 1);
            }

            if (meshTerm.contains("/")) {
              qualifier = meshTerm.split("/")[1];

              if (qualifierFreq.containsKey(qualifier)) {
                Integer freq = qualifierFreq.get(qualifier);
                freq++;

                qualifierFreq.put(qualifier, freq);
              } else {
                qualifierFreq.put(qualifier, 1);
              }
            }
          }
        }
      } finally {
        input.close();
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return qualifierFreq;
  }

  public void outputInARFF(String positivePMIDsFile, String meshTermsFile,
      String vocabularyHitsPath, String outputFilePath, Integer minFreq) {

    Set<String> positivePMIDs = loadPMIDs(positivePMIDsFile);

    Set<String> vocabulary = buildVocabulary(minFreq, meshTermsFile, false);
    List<String> vocabularyList = new ArrayList<>(vocabulary);

    logger.log(Level.INFO, "Minimum frequenc is set to: " + minFreq);
    logger.log(Level.INFO, "Vocabulary size is " + vocabulary.size());

    Set<String> qualifVocabulary =
        buildQualifierVocabulary(minFreq, meshTermsFile);
    List<String> qualifVocabularyList = new ArrayList<>(qualifVocabulary);

    logger.log(Level.INFO,
        "Qualifier vocabulary size is " + qualifVocabulary.size());

    logger.log(Level.INFO, "Loading PubMed hits for vocabulary MeSH terms..");

    //Map<String, Integer> pubmedHits = loadPubmedHits(vocabulary);
    Map<String, Integer> pubmedHits = loadVocabularyHits(vocabularyHitsPath);

    BufferedWriter out = null;
    FileWriter fstream;

    try {

      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));

      fstream = new FileWriter(outputFilePath, false);
      out = new BufferedWriter(fstream);

      try {
        out.write("@RELATION FDIRelevance" + System.lineSeparator()
            + System.lineSeparator());

        for (String meshTerm : vocabularyList) {

          meshTerm = meshTerm.replace(" ", "_");
          meshTerm = meshTerm.replace(",", "--");

          out.write(
              "@ATTRIBUTE " + meshTerm + " NUMERIC" + System.lineSeparator());
        }

        for (String qualifier : qualifVocabularyList) {

          qualifier = qualifier.replace(" ", "_");
          qualifier = qualifier.replace(",", "--");

          out.write("@ATTRIBUTE Q_" + qualifier + " NUMERIC"
              + System.lineSeparator());
        }

        out.write("@ATTRIBUTE M_FOOD NUMERIC" + System.lineSeparator());
        out.write("@ATTRIBUTE M_DRUG NUMERIC" + System.lineSeparator());
        out.write(
            "@ATTRIBUTE M_FOOD_AND_DRUG NUMERIC" + System.lineSeparator());

        out.write("@ATTRIBUTE class {positive,negative}"
            + System.lineSeparator() + System.lineSeparator());

        out.write("@DATA" + System.lineSeparator());

        String line = null;

        while ((line = input.readLine()) != null) {

          Map<String, Integer> dataVector1 = initDataVector(vocabulary);
          Map<String, Integer> dataVector2 = initDataVector(qualifVocabulary);

          // Build data vectors
          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            // Ignoring major topics
            if (meshTerm.endsWith("*")) {
              meshTerm = meshTerm.substring(0, meshTerm.length() - 1);
            }

            if (meshTerm.contains("/")) {
              String[] meshParts = meshTerm.split("/");

              meshTerm = meshParts[0];

              dataVector2.put(meshParts[1], 1);
            }

            dataVector1.put(meshTerm, pubmedHits.get(meshTerm));
          }

          // Output data vectors
          for (String term : vocabularyList) {
            out.write(dataVector1.get(term) + ",");
          }

          for (String qualifier : qualifVocabularyList) {
            out.write(dataVector2.get(qualifier) + ",");
          }

          Set<String> valuesSet =
              Arrays.stream(values).collect(Collectors.toSet());

          writeFoodDrugFeatures(out, valuesSet);

          if (positivePMIDs.contains(values[0])) {
            out.write("positive" + System.lineSeparator());
          } else
            out.write("negative" + System.lineSeparator());
        }
      } finally {
        out.close();
        input.close();
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    logger.log(Level.INFO, "Done.");
  }

  // Test data should use the same features set as training data
  public void outputTestDataInARFF(String positivePMIDsFile,
      String trainingMeshTermsFile, String testMeshTermsFile,
      String outputFilePath, Integer minFreq) {

    Set<String> positivePMIDs = loadPMIDs(positivePMIDsFile);

    Set<String> vocabulary =
        buildVocabulary(minFreq, trainingMeshTermsFile, false);
    List<String> vocabularyList = new ArrayList<>(vocabulary);

    logger.log(Level.INFO, "Minimum frequenc is set to: " + minFreq);
    logger.log(Level.INFO, "Vocabulary size is " + vocabulary.size());

    Set<String> qualifVocabulary =
        buildQualifierVocabulary(minFreq, trainingMeshTermsFile);
    List<String> qualifVocabularyList = new ArrayList<>(qualifVocabulary);

    logger.log(Level.INFO,
        "Qualifier vocabulary size is " + qualifVocabulary.size());

    logger.log(Level.INFO, "Loading PubMed hits for vocabulary MeSH terms..");

    Map<String, Integer> pubmedHits = loadPubmedHits(vocabulary);

    BufferedWriter out = null;
    FileWriter fstream;

    try {

      BufferedReader input =
          new BufferedReader(new FileReader(testMeshTermsFile));

      fstream = new FileWriter(outputFilePath, true);
      out = new BufferedWriter(fstream);

      try {
        out.write("@RELATION FDIRelevance" + System.lineSeparator()
            + System.lineSeparator());

        for (String meshTerm : vocabularyList) {

          meshTerm = meshTerm.replace(" ", "_");
          meshTerm = meshTerm.replace(",", "--");

          out.write(
              "@ATTRIBUTE " + meshTerm + " NUMERIC" + System.lineSeparator());
        }

        for (String qualifier : qualifVocabularyList) {

          qualifier = qualifier.replace(" ", "_");
          qualifier = qualifier.replace(",", "--");

          out.write("@ATTRIBUTE Q_" + qualifier + " NUMERIC"
              + System.lineSeparator());
        }

        out.write("@ATTRIBUTE M_FOOD NUMERIC" + System.lineSeparator());
        out.write("@ATTRIBUTE M_DRUG NUMERIC" + System.lineSeparator());
        out.write(
            "@ATTRIBUTE M_FOOD_AND_DRUG NUMERIC" + System.lineSeparator());

        out.write("@ATTRIBUTE class {positive,negative}"
            + System.lineSeparator() + System.lineSeparator());

        out.write("@DATA" + System.lineSeparator());

        String line = null;

        while ((line = input.readLine()) != null) {

          Map<String, Integer> dataVector1 = initDataVector(vocabulary);
          Map<String, Integer> dataVector2 = initDataVector(qualifVocabulary);

          // Build data vectors
          String[] values = line.split(";");

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

            // Ignoring major topics
            if (meshTerm.endsWith("*")) {
              meshTerm = meshTerm.substring(0, meshTerm.length() - 1);
            }

            if (meshTerm.contains("/")) {
              String[] meshParts = meshTerm.split("/");

              meshTerm = meshParts[0];

              dataVector2.put(meshParts[1], 1);
            }

            dataVector1.put(meshTerm, pubmedHits.get(meshTerm));
          }

          // Output data vectors
          for (String term : vocabularyList) {
            out.write(dataVector1.get(term) + ",");
          }

          for (String qualifier : qualifVocabularyList) {
            out.write(dataVector2.get(qualifier) + ",");
          }

          Set<String> valuesSet =
              Arrays.stream(values).collect(Collectors.toSet());

          writeFoodDrugFeatures(out, valuesSet);

          if (positivePMIDs.contains(values[0])) {
            out.write("positive" + System.lineSeparator());
          } else
            out.write("negative" + System.lineSeparator());
        }
      } finally {
        out.close();
        input.close();
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    logger.log(Level.INFO, "Done.");
  }

  public void outputTestDataInARFF(String trainingMeshTermsFile,
      Map<String, Set<String>> testMeshTerms, String outputFilePath,
      String vocabularyHitsPath, Integer minFreq) {

    Set<String> vocabulary =
        buildVocabulary(minFreq, trainingMeshTermsFile, false);
    List<String> vocabularyList = new ArrayList<>(vocabulary);

    logger.log(Level.INFO, "Minimum frequenc is set to: " + minFreq);
    logger.log(Level.INFO, "Vocabulary size is " + vocabulary.size());

    Set<String> qualifVocabulary =
        buildQualifierVocabulary(minFreq, trainingMeshTermsFile);
    List<String> qualifVocabularyList = new ArrayList<>(qualifVocabulary);

    logger.log(Level.INFO,
        "Qualifier vocabulary size is " + qualifVocabulary.size());

    logger.log(Level.INFO, "Loading PubMed hits for vocabulary MeSH terms..");

    // Map<String, Integer> pubmedHits = loadPubmedHits(vocabulary);
    Map<String, Integer> pubmedHits = loadVocabularyHits(vocabularyHitsPath);

    BufferedWriter out = null;
    FileWriter fstream;

    try {
      fstream = new FileWriter(outputFilePath, false);
      out = new BufferedWriter(fstream);

      try {
        out.write("@RELATION FDIRelevance" + System.lineSeparator()
            + System.lineSeparator());

        for (String meshTerm : vocabularyList) {

          meshTerm = meshTerm.replace(" ", "_");
          meshTerm = meshTerm.replace("'", "_");
          meshTerm = meshTerm.replace(",", "--");

          out.write(
              "@ATTRIBUTE " + meshTerm + " NUMERIC" + System.lineSeparator());
        }

        for (String qualifier : qualifVocabularyList) {

          qualifier = qualifier.replace(" ", "_");
          qualifier = qualifier.replace(",", "--");

          out.write("@ATTRIBUTE Q_" + qualifier + " NUMERIC"
              + System.lineSeparator());
        }

        out.write("@ATTRIBUTE M_FOOD NUMERIC" + System.lineSeparator());
        out.write("@ATTRIBUTE M_DRUG NUMERIC" + System.lineSeparator());
        out.write(
            "@ATTRIBUTE M_FOOD_AND_DRUG NUMERIC" + System.lineSeparator());

        out.write("@ATTRIBUTE class {positive,negative}"
            + System.lineSeparator() + System.lineSeparator());

        out.write("@DATA" + System.lineSeparator());

        Set<String> keySet = testMeshTerms.keySet();

        for (String keyTerm : keySet) {

          Map<String, Integer> dataVector1 = initDataVector(vocabulary);
          Map<String, Integer> dataVector2 = initDataVector(qualifVocabulary);

          // Build data vectors
          Set<String> values = testMeshTerms.get(keyTerm);

          for (String meshTerm : values) {

            // Ignoring major topics
            if (meshTerm.endsWith("*")) {
              meshTerm = meshTerm.substring(0, meshTerm.length() - 1);
            }

            if (meshTerm.contains("/")) {
              String[] meshParts = meshTerm.split("/");

              meshTerm = meshParts[0];

              dataVector2.put(meshParts[1], 1);
            }

            if (pubmedHits.containsKey(meshTerm)) {
              dataVector1.put(meshTerm, pubmedHits.get(meshTerm));
            }
          }

          // Output data vectors
          for (String term : vocabularyList) {
            out.write(dataVector1.get(term) + ",");
          }

          for (String qualifier : qualifVocabularyList) {
            out.write(dataVector2.get(qualifier) + ",");
          }

          writeFoodDrugFeatures(out, values);

          out.write("positive" + System.lineSeparator());

        }
      } finally {
        out.close();
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    logger.log(Level.INFO, "Done.");
  }

  private void writeFoodDrugFeatures(BufferedWriter out, Set<String> values)
      throws IOException {
    Boolean hasFood = containsFood(values);
    Boolean hasDrug = containsDrug(values);

    if (hasFood) {
      out.write("1" + ",");
    } else {
      out.write("0" + ",");
    }

    if (hasDrug) {
      out.write("1" + ",");
    } else {
      out.write("0" + ",");
    }

    if (hasFood && hasDrug) {
      out.write("1" + ",");
    } else {
      out.write("0" + ",");
    }
  }

  private Boolean containsFood(Set<String> meshTerms) {
    for (String meshTerm : meshTerms) {
      if (UMLSConceptHierarchies.isFoodRelatedConcept(meshTerm)) {
        return true;
      }
    }
    return false;
  }

  private Boolean containsDrug(Set<String> meshTerms) {
    for (String meshTerm : meshTerms) {
      try {
        if (UMLSConceptHierarchies.isDrug(meshTerm)) {
          return true;
        }
      } catch (SQLException e) {
        logger.log(Level.INFO, e.getMessage());
        e.printStackTrace();
      }
    }
    return false;
  }

  private Map<String, Integer> initDataVector(Set<String> vocabulary) {

    Map<String, Integer> dataVector = new HashMap<String, Integer>();

    for (String term : vocabulary) {
      dataVector.put(term, 0);
    }

    return dataVector;
  }

  public void findDuplicatePMIDs(String filePath1, String filePath2) {
    Set<String> pmids1 = loadPMIDs(filePath1);

    Set<String> pmids2 = loadPMIDs(filePath2);
    
    System.out.println("Duplicate PMIDs:");

    for (String pmid : pmids1) {
      if (pmids2.contains(pmid)) {
        System.out.println(pmid);
      }
    }
  }
  
  public void removeDuplicatePMIDs(String filePath1, String filePath2) {
    Set<String> pmids1 = loadPMIDs(filePath1);

    Set<String> pmids2 = loadPMIDs(filePath2);
    
    Set<String> finalSet = pmids1;
    
    System.out.println("Unique PMIDs:");

    for (String pmid : pmids2) {
      if (!finalSet.contains(pmid)) {
        finalSet.add(pmid);
      }
    }
    
    for (String pmid : finalSet) {
      System.out.println(pmid);
    }
  }
}
