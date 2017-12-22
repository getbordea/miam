/**
 * 
 */
package fr.ubx.bph.erias.miam.utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Georgeta Bordea
 *
 */
public class WebUtils {

  private static Logger logger = Logger.getLogger(WebUtils.class.getName());

  public static Document connectWith3Timeouts(String url) {

    Document doc = null;

    try {
      String encodedURI = URI.create(url).toASCIIString();

      try {
        for (int i = 1; i <= 3; i++) {
          try {
            doc = Jsoup.connect(encodedURI).timeout(5000).get();
            break; // Break immediately if successful
          } catch (SocketTimeoutException e) {
            // Swallow exception and try again
            System.out.println("jsoup Timeout occurred " + i + " time(s)");
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IllegalArgumentException iae) {
      logger.log(Level.WARN, "Illegal argument in URL " + url);
    }
    return doc;
  }
}
