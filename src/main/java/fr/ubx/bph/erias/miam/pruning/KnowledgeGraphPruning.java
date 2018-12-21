/**
 * 
 */
package fr.ubx.bph.erias.miam.pruning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ubx.bph.erias.miam.lucene.SearchDocuments;
import fr.ubx.bph.erias.miam.utils.DocumentUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class KnowledgeGraphPruning {

  static Logger logger =
      Logger.getLogger(KnowledgeGraphPruning.class.getName());

  public void extractCategoriesOnly(String inputGraphFile,
      String outputCategoriesGraphFile) {

    BufferedWriter out = null;

    try {
      BufferedReader input = new BufferedReader(new FileReader(inputGraphFile));
      try {

        // Create file
        FileWriter fstream = new FileWriter(outputCategoriesGraphFile, false);
        out = new BufferedWriter(fstream);

        String line = null;

        while ((line = input.readLine()) != null) {

          if (line.contains("{")) {
            out.write(line + System.lineSeparator());
          }

          if (line.contains("}")) {
            out.write(line);
          }

          if (line.contains("->")) {
            String edgeString = line.trim();
            String[] nodesWeight = edgeString.split(" -> ");

            String from = nodesWeight[0]
                .substring(1, nodesWeight[0].length() - 1).replace("_", " ");
            String to =
                nodesWeight[1].substring(1, nodesWeight[1].lastIndexOf("\""))
                    .replace("_", " ");

            if (from.contains("Category:") && to.contains("Category:")) {
              out.write(line + System.lineSeparator());
            }
          }
        }

      } finally {
        input.close();
        out.close();
      }
    } catch (FileNotFoundException fnfex) {

      logger.log(Level.FATAL,
          "Please provide a valid path to the file containing the DBpedia category graph");
      System.exit(0);

    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }

  public void filterFDICategories(String luceneIndexPath, String inputGraphFile,
      String outputCategoriesGraphFile) {

    BufferedWriter out = null;

    try {
      BufferedReader input = new BufferedReader(new FileReader(inputGraphFile));
      try {

        // Create file
        FileWriter fstream = new FileWriter(outputCategoriesGraphFile, false);
        out = new BufferedWriter(fstream);

        String line = null;

        while ((line = input.readLine()) != null) {

          if (line.contains("{")) {
            out.write(line + System.lineSeparator());
          }

          if (line.contains("}")) {
            out.write(line);
          }

          if (line.contains("->")) {
            String edgeString = line.trim();
            String[] nodesWeight = edgeString.split(" -> ");

            String from = nodesWeight[0]
                .substring(1, nodesWeight[0].length() - 1).replace("_", " ");
            String to =
                nodesWeight[1].substring(1, nodesWeight[1].lastIndexOf("\""))
                    .replace("_", " ");

            String fromTopic = from.substring(from.indexOf(":") + 1);

            String toTopic = to.substring(to.indexOf(":") + 1);
            
            SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));
            
            Integer fromHits = sd.searchOccurrence(fromTopic, DocumentUtils
                .readWordsFromFileInSet("src/main/resources/stopwords.txt"));
            
            Integer toHits = sd.searchOccurrence(toTopic, DocumentUtils
                .readWordsFromFileInSet("src/main/resources/stopwords.txt"));

            if (toHits > 0) {//(fromHits > 0 || toHits > 0) {

              out.write(line + System.lineSeparator());
            }
          }
        }

      } finally {
        input.close();
        out.close();
      }
    } catch (FileNotFoundException fnfex) {

      logger.log(Level.FATAL,
          "Please provide a valid path to the file containing the DBpedia category graph");
      System.exit(0);

    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }
}
