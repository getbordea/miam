/**
 * 
 */
package fr.ubx.bph.erias.miam.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import fr.ubx.bph.erias.miam.utils.DocumentUtils;

/**
 * @author Georgeta Bordea
 * 
 */
public class IndexFiles {

  private static Logger logger = Logger.getLogger(IndexFiles.class.getName());

  private File indexPath;

  public File getIndexPath() {
    return indexPath;
  }

  public void setIndexPath(File indexPath) {
    this.indexPath = indexPath;
  }

  public IndexFiles(File indexPath) {
    super();
    this.indexPath = indexPath;
  }

  public static IndexWriter createLuceneIndex(String indexPath,
      Integer emptyIndex) {
    try {
      // check if the lucene index is suppose to be empty
      if (emptyIndex == 1) {
        return IndexFiles.createEmptyLuceneIndex(indexPath);
      } else {
        return IndexFiles.createLuceneIndex(indexPath);
      }
    } catch (IOException ioex) {
      logger.log(Level.FATAL, ioex.getMessage());
      logger.log(Level.FATAL, "Problem creating the lucene index..");
      System.exit(1);
    }

    return null;
  }

  private static IndexWriter createLuceneIndex(String indexPath)
      throws IOException {
    IndexFiles indexFiles = new IndexFiles(new File(indexPath));
    File indexFile = indexFiles.getIndexPath();

    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

    IndexWriter writer = null;
    try {
      writer = new IndexWriter(FSDirectory.open(indexFile.toPath()), iwc);

    } catch (IOException e) {
      logger.log(Level.FATAL,
          " caught a " + e.getClass() + "\n with message: " + e.getMessage());
    }

    return writer;
  }

  private static IndexWriter createEmptyLuceneIndex(String indexPath)
      throws IOException {

    IndexFiles indexFiles = new IndexFiles(new File(indexPath));

    File indexFile = indexFiles.getIndexPath();
    if (indexFile.exists()) {
      DocumentUtils.deleteDirectory(indexFile);
    }

    IndexWriter writer = null;
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

    try {
      writer = new IndexWriter(FSDirectory.open(indexFile.toPath()), iwc);

    } catch (IOException e) {
      logger.log(Level.FATAL,
          " caught a " + e.getClass() + "\n with message: " + e.getMessage());
    }

    return writer;
  }

  public static void indexDoc(IndexWriter writer, String uid, String filePath,
      Integer year) {
    logger.log(Level.INFO, "Adding document " + uid + " to index..");

    try {
      if (uid != null) {
        
        Document document = LuceneDocument.Document(uid,
            DocumentUtils.readFile(filePath), year);
        
        writer.addDocument(document);
      }
    } catch (Exception e) {
      logger.log(Level.FATAL,
          " caught a " + e.getClass() + "\n with message: " + e.getMessage());
    }
  }
}
