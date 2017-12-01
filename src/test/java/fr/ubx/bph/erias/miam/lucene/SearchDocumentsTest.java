/**
 * 
 */
package fr.ubx.bph.erias.miam.lucene;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Georgeta Bordea
 *
 */
public class SearchDocumentsTest {

  private static final String LUCENE_INDEX_PATH =
      "../taxCo/lucene/index/wiki2015";

  private static final Integer SPAN_SLOP = 50;

  private static final String SEARCH_STRING_1 = "Food";
  //private static final String SEARCH_STRING_1 = "Dessert";

  private static final String SEARCH_STRING_2 = "Chocolate";

  @Before
  public void setupConfig() {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  @Test
  public void test() {

    SearchDocuments sd = new SearchDocuments(new File(LUCENE_INDEX_PATH));

    IndexReader reader;
    
    Long spanFreq = new Long (0);
    
    try {
      reader = DirectoryReader
          .open(FSDirectory.open(new File(LUCENE_INDEX_PATH).toPath()));

      Integer docsCount = reader.numDocs();

      spanFreq = sd.computeSpanOccurrence(SEARCH_STRING_1, SEARCH_STRING_2,
          docsCount, SPAN_SLOP);

      System.out.println(spanFreq);

    } catch (IOException e) {
      e.printStackTrace();
    }

    assertTrue(spanFreq == 8452);
  }

}
