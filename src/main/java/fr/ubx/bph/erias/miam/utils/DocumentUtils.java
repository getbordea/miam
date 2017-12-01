/**
 * 
 */
package fr.ubx.bph.erias.miam.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Georgeta Bordea
 * 
 */
public class DocumentUtils {
  
  private static Logger logger =
    Logger.getLogger(DocumentUtils.class.getName());
  
  public static final String DS = System.getProperty("file.separator");
  public static final String LS = System.getProperty("line.separator");
  
  public static final String springerLink =
      "http://www.springerlink.com/content/";

  public static String readFile(String fileName) throws IOException {
    StringBuilder contents = new StringBuilder();

    // use buffering, reading one line at a time
    // FileReader always assumes default encoding is OK!
    BufferedReader input = new BufferedReader(new FileReader(fileName));
    try {
      String line = null; // not declared within while loop
      /*
       * readLine is a bit quirky : it returns the content of a line MINUS the
       * newline. it returns null only for the END of the stream. it returns an
       * empty String if two newlines appear in a row.
       */
      while ((line = input.readLine()) != null) {
        contents.append(line);
        contents.append(System.getProperty("line.separator"));
      }
    } finally {
      input.close();
    }
    return contents.toString();
  }

  public static String convertSpringerLink(String link) {
    String pdfLink = "";
    String fullText = "/fulltext.pdf";
    pdfLink = link.substring(0, link.lastIndexOf("/"));
    return pdfLink + fullText;
  }

  public static String buildSpringerLinkFileName(String link) {
    String pdfLink = "";
    String extension = ".pdf";
    pdfLink = link.substring(0, link.lastIndexOf("/"));
    pdfLink = pdfLink.substring(pdfLink.lastIndexOf("/") + 1, pdfLink.length());
    return pdfLink + extension;
  }

  public static void deleteDirectory(File f) throws IOException {
    if (f.isDirectory()) {
      for (File c : f.listFiles()) {
        deleteDirectory(c);
      }
    }
    if (!f.delete()) {
      throw new FileNotFoundException("Failed to delete file: " + f);
    }
  }

  public static void writeToFile(String file, String content) {
    try {
      // Create file
      FileWriter fstream = new FileWriter(file);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(content);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void appendToFile(String file, String content) {
    try {
      // Create file
      FileWriter fstream = new FileWriter(file, true);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(content);
      fstream.close();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void copyfile(String srFile, String dtFile) {
    try {
      File f1 = new File(srFile);
      File f2 = new File(dtFile);
      InputStream in = new FileInputStream(f1);

      // Overwrite the file.
      OutputStream out = new FileOutputStream(f2);

      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      in.close();
      out.close();
      System.out.println("File copied.");
    } catch (FileNotFoundException ex) {
      System.out.println(ex.getMessage() + " in the specified directory.");
      System.exit(0);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public static ArrayList<File> getAllDocumentFiles(File docDir)
      throws FileNotFoundException {
    ArrayList<File> docFileList = new ArrayList<File>();

    if (!docDir.exists()) {
      logger.log(Level.FATAL, "Document directory '" + docDir.getAbsolutePath()
          + "' does not exist");
      throw new FileNotFoundException("Document directory '"
          + docDir.getAbsolutePath() + "' does not exist");
    }

    if (!docDir.canRead()) {
      logger.log(Level.ERROR, "Document '" + docDir.getAbsolutePath()
          + "' could not be read.");
      // throw new IOException("Document '" + docDir.getAbsolutePath()
      // + "' is not readable");
    } else {
      if (!docDir.getName().equals(".svn")) {

        // if docDir is a Directory search for files into it
        // Otherwise, add it into the file list
        if (docDir.isDirectory()) {

          String[] files = docDir.list();
          if (files != null) {
            for (int i = 0; i < files.length; i++) {
              docFileList
                  .addAll(getAllDocumentFiles(new File(docDir, files[i])));
            }
          }
        } else {
          docFileList.add(docDir);
        }

      }
    }

    return docFileList;
  }
  
  /**
   * Removes a file if it was already created
   * 
   * @param fileName
   *          the name of the file
   */
  public static void removeFile(String fileName) {
    // Remove the file iJf it was already created
    File f = new File(fileName);
    if (f.exists()) {
      f.delete();
    }
  }
  
  /**
   * Reads the words (stopwords) from a file
   * 
   * @param path
   *          the path of the file that contains selected words, one per line
   * @return a list with the words
   */
  public static List<String> readWordsFromFile(String path) {
    List<String> stopWords = new ArrayList<String>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(path));
      try {
        String line = null;
        while ((line = input.readLine()) != null) {
          stopWords.add(line);
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return stopWords;
  }
  
  /**
   * Reads the words (stopwords) from a file
   * 
   * @param path
   *          the path of the file that contains selected words, one per line
   * @return a list with the words
   */
  public static Set<String> readWordsFromFileInSet(String path) {
    Set<String> stopWords = new HashSet<String>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(path));
      try {
        String line = null;
        while ((line = input.readLine()) != null) {
          stopWords.add(line);
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return stopWords;
  }
}
