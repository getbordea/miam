/**
 * 
 */
package fr.ubx.bph.erias.miam.classification;

/**
 * @author Georgeta Bordea
 *
 */
public class ClassifierException extends Exception {
  public ClassifierException() {
  }

  public ClassifierException(String message) {
    super(message);
  }

  public ClassifierException(String message, Throwable cause) {
    super(message, cause);
  }
}
