/**
 * 
 */
package fr.ubx.bph.erias.miam.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import fr.ubx.bph.erias.miam.utils.DocumentUtils;

/**
 * @author Georgeta Bordea
 * 
 */
public class SearchDocuments {

  static Logger logger = Logger.getLogger(SearchDocuments.class.getName());

  private File indexPath;

  public File getIndexPath() {
    return indexPath;
  }

  public void setIndexPath(File indexPath) {
    this.indexPath = indexPath;
  }

  public SearchDocuments(File indexPath) {
    super();
    this.indexPath = indexPath;
  }

  public Map<String, Integer> searchOccWithQueryParsing(List<String> topics,
      Integer docsNo) throws CorruptIndexException, IOException {
    Map<String, Integer> occurrenceMap = new HashMap<String, Integer>();

    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);

    final BooleanQuery bQuery = new BooleanQuery();
    StandardQueryParser qpHelper =
        new StandardQueryParser(new StandardAnalyzer());
    try {
      for (String topic : topics) {
        Query query =
            qpHelper.parse("\"" + topic + "\"", LuceneDocument.CONTENTS_NAME);
        bQuery.add(query, Occur.MUST);
      }

      final TopDocs docs = searcher.search(bQuery, docsNo);

      for (int i = 0; i < docs.scoreDocs.length; i++) {
        final Document d = searcher.doc(docs.scoreDocs[i].doc);
        Float score = docs.scoreDocs[i].score;
        occurrenceMap.put(d.get(LuceneDocument.UID_NAME), score.intValue());
      }

    } catch (QueryNodeException qne) {
      logger.log(Level.INFO,
          "There was a problem while parsing a query with the following message:"
              + qne.getMessage());
    } finally {
      if (reader != null) {
        reader.close();
      }
    }

    return occurrenceMap;
  }

  /**
   * Compute occurrence using lucene for a list of topics that must occur
   * 
   * @param topic
   *          the topics string
   * @param docsNo
   *          maximum documents number
   * @return the map with occurrences for all documents where the topic is
   *         mentioned
   * @throws CorruptIndexException
   * @throws IOException
   * @throws ParseException
   */
  public Map<String, Integer> searchOccurrence(List<String> topics,
      Integer docsNo) throws CorruptIndexException, IOException {

    Map<String, Integer> occurrenceMap = new HashMap<String, Integer>();
    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);

    final BooleanQuery bQuery = new BooleanQuery();

    try {
      for (String topic : topics) {

        final PhraseQuery query = new PhraseQuery();

        // topic = topic.replaceAll("-", " ");
        // topic = topic.replaceAll("\\s+", " ");

        List<String> topicWords = extractWords(topic);
        for (String word : topicWords) {
          query.add(new Term(LuceneDocument.CONTENTS_NAME, word));
        }

        bQuery.add(query, Occur.MUST);
      }

      final TopDocs docs = searcher.search(bQuery, docsNo);

      for (int i = 0; i < docs.scoreDocs.length; i++) {
        final Document d = searcher.doc(docs.scoreDocs[i].doc);
        Float score = docs.scoreDocs[i].score;
        occurrenceMap.put(d.get(LuceneDocument.UID_NAME), score.intValue());
      }
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
    return occurrenceMap;
  }

  public Integer searchOccurrence(String topic, Set<String> stopwords)
      throws IOException {

    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);

    StandardQueryParser qpHelper =
        new StandardQueryParser(new StandardAnalyzer());
    Query query;
    try {
      query = qpHelper.parse("\"" + topic + "\"", LuceneDocument.CONTENTS_NAME);

      TotalHitCountCollector results = new TotalHitCountCollector();
      searcher.search(query, results);

      Integer hits = results.getTotalHits();

      return hits;

    } catch (QueryNodeException qne) {
      logger.log(Level.INFO, "There was a problem while parsing the query "
          + topic + " with the following message:" + qne.getMessage());
    } finally {
      if (reader != null) {
        reader.close();
      }
    }

    return 0;
  }

  /**
   * Compute occurrence using lucene for a given topic
   * 
   * @param topic
   *          the topics string
   * @param docsNo
   *          maximum documents number
   * @return the map with occurrences for all documents where the topic is
   *         mentioned
   * @throws CorruptIndexException
   * @throws IOException
   * @throws ParseException
   */
  public Map<String, Integer> searchOccurrence(String topic, Integer docsNo,
      Set<String> stopwords) throws CorruptIndexException, IOException {

    Map<String, Integer> occurrenceMap = new HashMap<String, Integer>();
    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);

    // QueryParser qp =
    // new QueryParser(Version.LUCENE_CURRENT, LuceneDocument.CONTENTS_NAME,
    // new StandardAnalyzer(Version.LUCENE_CURRENT));

    // Query query = qp.parse("\"" + QueryParser.escape(topic) + "\"");

    final PhraseQuery query = new PhraseQuery();

    // topic = topic.replaceAll("-", " "); // topic =
    // topic.replaceAll("\\s+", " ");

    List<String> topicWords = extractWords(topic, stopwords);
    for (String word : topicWords) {
      query.add(new Term(LuceneDocument.CONTENTS_NAME, word));
    }

    /*
     * Using a query parser, for some reason is much slower StandardQueryParser
     * qpHelper = new StandardQueryParser(new
     * StandardAnalyzer(Version.LUCENE_43)); Query query; try { query =
     * qpHelper.parse(topic, LuceneDocument.CONTENTS_NAME);
     * 
     * final TopDocs docs = searcher.search(query, docsNo);
     * 
     * for (int i = 0; i < docs.scoreDocs.length; i++) { final Document d =
     * searcher.doc(docs.scoreDocs[i].doc); Float score =
     * docs.scoreDocs[i].score;
     * occurrenceMap.put(d.get(LuceneDocument.UID_NAME), score.intValue()); }
     * 
     * } catch (QueryNodeException qne) { logger.log(Level.INFO,
     * "There was a problem while parsing the query " + topic +
     * " with the following message:" + qne.getMessage()); }
     */

    final TopDocs docs = searcher.search(query, docsNo);

    for (int i = 0; i < docs.scoreDocs.length; i++) {
      final Document d = searcher.doc(docs.scoreDocs[i].doc);
      Float score = docs.scoreDocs[i].score;
      occurrenceMap.put(d.get(LuceneDocument.UID_NAME), score.intValue());
    }

    reader.close();
    return occurrenceMap;
  }

  /**
   * Compute occurrence using lucene for a given topic
   * 
   * @param topic
   *          the topics string
   * @param docsNo
   *          maximum documents number
   * @return the map with occurrences for all documents where the topic is
   *         mentioned
   * @throws CorruptIndexException
   * @throws IOException
   * @throws ParseException
   */
  public Map<String, Integer> searchOccurrence(String topic, Integer docsNo)
      throws CorruptIndexException, IOException {

    Map<String, Integer> occurrenceMap = new HashMap<String, Integer>();
    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);

    // QueryParser qp =
    // new QueryParser(Version.LUCENE_CURRENT, LuceneDocument.CONTENTS_NAME,
    // new StandardAnalyzer(Version.LUCENE_CURRENT));

    // Query query = qp.parse("\"" + QueryParser.escape(topic) + "\"");

    String[] topicWords = extractWords(topic).toArray(new String[0]);

    final PhraseQuery query =
        new PhraseQuery(LuceneDocument.CONTENTS_NAME, topicWords);

    // topic = topic.replaceAll("-", " "); // topic =
    // topic.replaceAll("\\s+", " ");

    /*
     * StandardQueryParser qpHelper = new StandardQueryParser(new
     * StandardAnalyzer(Version.LUCENE_43)); Query query; try { query =
     * qpHelper.parse(topic, LuceneDocument.CONTENTS_NAME);
     * 
     * final TopDocs docs = searcher.search(query, docsNo);
     * 
     * for (int i = 0; i < docs.scoreDocs.length; i++) { final Document d =
     * searcher.doc(docs.scoreDocs[i].doc); Float score =
     * docs.scoreDocs[i].score;
     * occurrenceMap.put(d.get(LuceneDocument.UID_NAME), score.intValue()); }
     * 
     * } catch (QueryNodeException qne) { logger.log(Level.INFO,
     * "There was a problem while parsing the query " + topic +
     * " with the following message:" + qne.getMessage()); }
     */

    final TopDocs docs = searcher.search(query, docsNo);

    for (int i = 0; i < docs.scoreDocs.length; i++) {
      final Document d = searcher.doc(docs.scoreDocs[i].doc);
      Float score = docs.scoreDocs[i].score;
      occurrenceMap.put(d.get(LuceneDocument.UID_NAME), score.intValue());
    }

    reader.close();
    return occurrenceMap;
  }

  public Integer searchOverallOccurrence(String topic, Integer docsNo)
      throws CorruptIndexException, IOException {
    // Map<String, Integer> occMap = searchOccurrence(topic, docsNo);
    Map<String, Integer> occMap = searchOccurrence(topic, docsNo, DocumentUtils
        .readWordsFromFileInSet("src/main/resources/stopwords.txt"));

    Set<String> keySet = occMap.keySet();

    Integer overallOccurrence = 0;

    for (String key : keySet) {
      overallOccurrence += occMap.get(key);
    }

    return overallOccurrence;
  }

  /**
   * Retrieve the content of a document given the uid
   * 
   * @param uid
   * @return the full text of the document
   * @throws CorruptIndexException
   * @throws IOException
   */
  public String searchDocumentContent(String uid)
      throws CorruptIndexException, IOException {

    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);

    final TermQuery query =
        new TermQuery(new Term(LuceneDocument.UID_NAME, uid));

    final TopDocs docs = searcher.search(query, 1);

    final Document d = searcher.doc(docs.scoreDocs[0].doc);

    reader.close();
    return d.get(LuceneDocument.CONTENTS_NAME);
  }

  /**
   * Compute the occurrence of two terms with a given window size
   * 
   * @param term1
   *          the first term
   * @param term2
   *          the first term
   * @param docsNo
   *          the number of indexed documents
   * @param spanSlop
   *          the size of the window the win
   * @return the occurrence in each file
   * @throws CorruptIndexException
   * @throws IOException
   * @throws ParseException
   */
  public Map<String, Integer> searchSpanOccurrence(String term1, String term2,
      Integer docsNo, Integer spanSlop)
      throws CorruptIndexException, IOException {

    Map<String, Integer> occurrenceMap = new HashMap<String, Integer>();
    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);

    List<String> term1Words = extractWords(term1);
    List<String> term2Words = extractWords(term2);

    SpanQuery snq1;

    if (term1Words.size() > 1) {
      SpanTermQuery[] query1 = new SpanTermQuery[term1Words.size()];
      for (int i = 0; i < term1Words.size(); i++) {
        query1[i] = new SpanTermQuery(
            new Term(LuceneDocument.CONTENTS_NAME, term1Words.get(i)));
      }
      snq1 = new SpanNearQuery(query1, 0, true);
    } else {
      snq1 = new SpanTermQuery(
          new Term(LuceneDocument.CONTENTS_NAME, term1Words.get(0)));
    }

    SpanQuery snq2;

    if (term2Words.size() > 1) {
      SpanTermQuery[] query2 = new SpanTermQuery[term2Words.size()];
      for (int i = 0; i < term2Words.size(); i++) {
        query2[i] = new SpanTermQuery(
            new Term(LuceneDocument.CONTENTS_NAME, term2Words.get(i)));
      }
      snq2 = new SpanNearQuery(query2, 0, true);
    } else {
      snq2 = new SpanTermQuery(
          new Term(LuceneDocument.CONTENTS_NAME, term2Words.get(0)));
    }

    SpanQuery[] snqArray = new SpanQuery[2];
    snqArray[0] = snq1;
    snqArray[1] = snq2;

    SpanNearQuery snq = new SpanNearQuery(snqArray, spanSlop, false);

    final TopDocs docs = searcher.search(snq, docsNo);

    logger.log(Level.INFO,
        "Span search for " + term1 + " and " + term2 + " is " + docs.totalHits);

    for (int i = 0; i < docs.scoreDocs.length; i++) {
      final Document d = searcher.doc(docs.scoreDocs[i].doc);
      Float score = docs.scoreDocs[i].score;
      occurrenceMap.put(d.get(LuceneDocument.UID_NAME), score.intValue());
    }

    reader.close();
    return occurrenceMap;
  }

  public List<String> extractWords(String phrase) {
    List<String> list = new ArrayList<String>();

    phrase = phrase.replace("-", " ");
    phrase = phrase.toLowerCase();

    if (!phrase.contains(" ")) {
      list.add(phrase);
    } else {
      list.add(phrase.substring(0, phrase.indexOf(" ")));
      List<String> sublist =
          extractWords(phrase.substring(phrase.indexOf(" ") + 1));

      list.addAll(sublist);
    }
    return list;
  }

  public List<String> extractWords(String phrase, Set<String> stopwords) {
    List<String> list = new ArrayList<String>();

    phrase = phrase.replace("-", " ");
    phrase = phrase.toLowerCase();

    if (!phrase.contains(" ")) {
      list.add(phrase);
    } else {

      String word = phrase.substring(0, phrase.indexOf(" "));

      if (!stopwords.contains(word)) {
        list.add(word);
      }

      List<String> sublist =
          extractWords(phrase.substring(phrase.indexOf(" ") + 1));

      list.addAll(sublist);
    }
    return list;
  }

  public Long computeSpanOccurrence(String term1, String term2,
      Integer docCount, Integer spanSlop)
      throws CorruptIndexException, IOException {

    Long occurrence = new Long(0);

    Map<String, Integer> occMap =
        this.searchSpanOccurrence(term1, term2, docCount, spanSlop);

    Set<String> keySet = occMap.keySet();

    for (String docId : keySet) {
      occurrence += occMap.get(docId);
    }

    return occurrence;
  }

  /**
   * Compute overall frequency for a given keyphrase in all indexed documents
   * 
   * @param topic
   * @param countDocs
   * @return
   * @throws CorruptIndexException
   * @throws IOException
   * @throws ParseException
   */
  public Long computeFrequency(String topic, Integer docCount,
      Set<String> stopwords) throws CorruptIndexException, IOException {

    Long frequency = new Long(0);

    Map<String, Integer> occMap =
        searchOccurrence(topic.toLowerCase(), docCount, stopwords);
    Set<String> keySet = occMap.keySet();

    for (String docId : keySet) {
      frequency += occMap.get(docId);
    }

    return frequency;
  }

  /**
   * Compute overall frequency for a given keyphrase in all indexed documents
   * 
   * @param topic
   * @param countDocs
   * @return
   * @throws CorruptIndexException
   * @throws IOException
   * @throws ParseException
   */
  public Long computeFrequency(String topic, Integer docCount)
      throws CorruptIndexException, IOException {

    Long frequency = new Long(0);

    Map<String, Integer> occMap =
        searchOccurrence(topic.toLowerCase(), docCount);
    Set<String> keySet = occMap.keySet();

    for (String docId : keySet) {
      frequency += occMap.get(docId);
    }

    return frequency;
  }

  /**
   * Compute tfidf using lucene for a given topic using all morphological
   * variations to calculate the tfidf.
   * 
   * @param topic
   *          the topic
   * @param docsNo
   *          maximum documents number
   * @return the map with tfidf values for all documents where the topic is
   *         mentioned
   * @throws CorruptIndexException
   * @throws IOException
   * @throws ParseException
   */
  public Map<String, Float> searchTFIDF(String topic, Integer docsNo,
      String retrievedField) throws CorruptIndexException, IOException {

    Map<String, Float> tfidfMap = new HashMap<String, Float>();
    IndexReader reader =
        DirectoryReader.open(FSDirectory.open(indexPath.toPath()));
    final IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new OnlyTFIDFSimilarity());

    List<String> topicWords = extractWords(topic);
    PhraseQuery query = new PhraseQuery();

    for (String word : topicWords) {
      query.add(new Term(LuceneDocument.CONTENTS_NAME, word));
    }

    final TopDocs docs = searcher.search(query, docsNo);

    for (int i = 0; i < docs.scoreDocs.length; i++) {
      final Document d = searcher.doc(docs.scoreDocs[i].doc);
      Float score = docs.scoreDocs[i].score;
      tfidfMap.put(d.get(retrievedField), score);
    }

    return tfidfMap;
  }

}
