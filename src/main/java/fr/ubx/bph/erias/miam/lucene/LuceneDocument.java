package fr.ubx.bph.erias.miam.lucene;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.xml.sax.SAXException;

/**
 * A utility for making Lucene Documents.
 * 
 * @author Georgeta Bordea
 * 
 */
public class LuceneDocument {

  private static Logger logger = Logger.getLogger(LuceneDocument.class
      .getName());

  // Fields defined for the Wikipedia index built by Renaud
  public static final String UID_NAME = "docid";
  public static final String YEAR_NAME = "docdate";
  public static final String CONTENTS_NAME = "body";
  public static final String TITLE_NAME = "doctitle";

  /**
   * Makes a document.
   * <p>
   * The document has three fields:
   * <ul>
   * <li><code>uid</code>--containing the uid of the document, as a stored,
   * untokenized field;
   * <li><code>year</code>--containing the year of the document as a field and
   * <li><code>contents</code>--containing the full contents of the file;
   * 
   * 
   * @throws IOException
   * @throws SAXException
   */
  public static Document Document(String uid, String content, Integer year)
      throws IOException, SAXException {

    // make a new, empty document
    Document doc = new Document();

    // Add the uid of the file as a field named "uid". Use a field that is
    // indexed (i.e. searchable), but don't tokenize the field into words.
    doc.add(new StringField(UID_NAME, uid, Field.Store.YES));

    // Add the year of the file as a field named "year". Use
    // a field that is indexed (i.e. searchable), but don't tokenize the field
    // into words.
    doc.add(new StringField(YEAR_NAME, year.toString(), Field.Store.YES));

    // Add the contents of the file to a field named "contents". Specify a
    // Reader,
    // so that the text of the file is tokenized and indexed, but not stored.
    // Note that FileReader expects the file to be in the system's default
    // encoding.
    // If that's not the case searching for special characters will fail.

    Field contents = null;
    contents = new TextField(CONTENTS_NAME, content, Field.Store.YES);
    doc.add(contents);

    // return the document
    return doc;
  }

  private LuceneDocument() {
  }
}
