/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Georgeta Bordea
 *
 */
public class GraphPruningTest {

  static Logger logger = Logger.getLogger(GraphPruningTest.class.getName());

  private static final String GRAPH_PATH =
      "src/test/resources/prunedGraph_spices.dot";

  @Before
  public void setupConfig() {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  @Test
  public void testPruneMultipleParents() {
    List<Node> nodes = MiamDotImporter.importNodes(GRAPH_PATH);
    logger.log(Level.INFO,
        "Loaded the following number of nodes: " + nodes.size());

    AdjacencyList edges = MiamDotImporter.importEdges(GRAPH_PATH, nodes);
    logger.log(Level.INFO,
        "Loaded the following number of edges: " + edges.getAllEdges().size());

    AdjacencyList prunedEdges = GraphPruning.pruneMultipleParents(nodes, edges);

    assertTrue(prunedEdges.getAllEdges().size() < nodes.size());
  }

}
