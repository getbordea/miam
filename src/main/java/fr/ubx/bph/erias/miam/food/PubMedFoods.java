/**
 * 
 */
package fr.ubx.bph.erias.miam.food;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

import fr.ubx.bph.erias.miam.lucene.IndexFiles;
import fr.ubx.bph.erias.miam.lucene.SearchDocuments;
import fr.ubx.bph.erias.miam.utils.DocumentUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class PubMedFoods {
  // TODO index all abstracts, search DBpedia foods frequencies

  static Logger logger = Logger.getLogger(PubMedFoods.class.getName());

  public static IndexWriter writer = null;

  public void indexAllDocs(String luceneIndexPath, String corpusPath)
      throws CorruptIndexException, IOException, SQLException {

    // init the Lucene index

    writer = IndexFiles.createLuceneIndex(luceneIndexPath, 0);
    final File file = new File(corpusPath);

    indexDocs(writer, file);
    writer.commit();
    writer.close();
  }

  public void indexDocs(IndexWriter writer, File file) {
    // do not try to index files that cannot be read
    if (file.canRead()) {
      if (file.isDirectory()) {
        String[] files = file.list();
        // an IO error could occur
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            indexDocs(writer, new File(file, files[i]));
          }
        }
      } else {

        String fileName = file.getName();
        String docId = fileName.substring(0, fileName.indexOf("."));

        // logger.log(Level.INFO, "Indexing abstract with PMID " + docId);

        IndexFiles.indexDoc(writer, docId, file.getAbsolutePath(), 2000);
      }
    }
  }

  public Integer countAllDocs(String corpusPath) {
    final File file = new File(corpusPath);

    return countDocs(file, 0);
  }

  public Integer countDocs(File file, Integer counter) {
    // do not try to index files that cannot be read
    if (file.canRead()) {
      if (file.isDirectory()) {
        String[] files = file.list();
        // an IO error could occur
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            countDocs(new File(file, files[i]), counter++);
          }
        }
      }
    }

    return counter;
  }

  public void filterFoodsInFDIAbstracts(String inputFile, String outputFile,
      String luceneIndexPath, String corpusPath) {

    FileWriter fstream;

    BufferedWriter out = null;
    BufferedReader input = null;

    SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));

    Integer docsCount = countAllDocs(corpusPath);

    try {

      try {
        input = new BufferedReader(new FileReader(inputFile));

        fstream = new FileWriter(outputFile, false);

        out = new BufferedWriter(fstream);

        String line = null;

        while ((line = input.readLine()) != null) {

          String foodName = line.substring(line.lastIndexOf("/") + 1);

          if (foodName.startsWith("Category:")) {
            foodName = line.substring(line.lastIndexOf(":") + 1);
          }

          foodName = foodName.replace("_", " ");// .toLowerCase();

          // Integer hits = sd.searchOverallOccurrence(foodName, docsCount);
          Integer hits = sd.searchOccurrence(foodName, DocumentUtils
              .readWordsFromFileInSet("src/main/resources/stopwords.txt"));

          logger.log(Level.INFO, "Occurence for " + foodName + " is " + hits);

          //if (hits > 0) {
            out.write(line + "\t" + hits);
            out.write(System.lineSeparator());
          //}
        }

      } finally {

        // Close the input stream
        if (input != null) {
          input.close();
        }

        // Close the output stream
        if (out != null) {
          out.close();
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void retrieveDocs(String topic, String luceneIndexPath,
      String corpusPath) {
    SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));

    Integer docsCount = countAllDocs(corpusPath);

    List<String> topicList = new ArrayList<String>();

    topicList.add(topic);

    try {
      Map<String, Integer> hits =
          sd.searchOccWithQueryParsing(topicList, docsCount);
      
      Set<String> keySet = hits.keySet();
      
      for (String key : keySet) {
        System.out.println(key);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
