/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ubx.bph.erias.miam.db.JDBCConnection;
import fr.ubx.bph.erias.miam.db.SnarlConnection;
import fr.ubx.bph.erias.miam.population.OntologyPopulationTest;

/**
 * @author Georgeta Bordea
 *
 */
public class BratAnnotationsReaderTest {

  @Test
  public void readAnnotationsTest() {
    BratAnnotationsReader bar = new BratAnnotationsReader();
    
    bar.readAnnotations(OntologyPopulationTest.BRAT_FILE);
  }
}
