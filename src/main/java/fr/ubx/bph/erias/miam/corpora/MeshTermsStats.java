/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Georgeta Bordea
 *
 */
public class MeshTermsStats {

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

          String[] values = line.split(",");

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
          out.write(key + "," + meshQualifierFreq.get(key) + System.lineSeparator());
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void countFrequencies(String meshTermsFile, String outputFilePath) {
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

          String[] values = line.split(",");

          for (int i = 1; i < values.length; i++) {

            String meshTerm = values[i];

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
          out.write(key + "," + meshTermFreq.get(key) + System.lineSeparator());
        }
      } finally {
        input.close();
        out.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void countCoocurrences(String meshTermsFile, String outputFilePath) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> meshTermCooc = new HashMap<String, Integer>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(meshTermsFile));
      fstream = new FileWriter(outputFilePath, true);

      out = new BufferedWriter(fstream);

      try {
        String line = null;

        while ((line = input.readLine()) != null) {

          String[] values = line.split(",");

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

        Set<String> keys = meshTermCooc.keySet();

        for (String key : keys) {
          out.write(key + "," + meshTermCooc.get(key) + System.lineSeparator());
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
