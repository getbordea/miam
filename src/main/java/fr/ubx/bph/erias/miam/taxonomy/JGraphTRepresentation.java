/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.StringComponentNameProvider;


/**
 * @author Georgeta Bordea
 * 
 */
public class JGraphTRepresentation {

  public static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> convertToJGraphT(
      List<Node> nodes, AdjacencyList edgeList) {
    DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph =
        new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(
            DefaultWeightedEdge.class);

    for (Node node : nodes) {

      String nodeString = node.getTopicString();
      // nodeString = Topic.get(nodeString).getPreferredString();
      directedGraph.addVertex(nodeString);
    }

    Collection<Edge> edges = edgeList.getAllEdges();
    for (Edge edge : edges) {
      if (edge != null) {
        // System.out.println(edge.getFrom().getTopicString() + ", "
        // + edge.getTo().getTopicString());

        String edgeToString = edge.getTo().getTopicString();
        String edgeFromString = edge.getFrom().getTopicString();

        // edgeToString = Topic.get(edgeToString).getPreferredString();
        // edgeFromString = Topic.get(edgeFromString).getPreferredString();

        if ((edgeFromString != edgeToString)) { // && (edge.getWeight() >0)) {

          DefaultWeightedEdge e =
              directedGraph.addEdge(edgeFromString, edgeToString);

          if (e != null) {
            directedGraph.setEdgeWeight(e, edge.getWeight());
          }
        }
      }
    }

    return directedGraph;
  }

  public static void exportToDot(
      DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph,
      String fileName, Map<String, Double> sumPMIMap) {

    DOTExporter<String, DefaultWeightedEdge> de =
        new DOTExporter<String, DefaultWeightedEdge>(
            new StringComponentNameProvider<String>(), null, null);
    // MiamDotExporter<String, DefaultWeightedEdge> de =
    // new MiamDotExporter<String, DefaultWeightedEdge>(
    // new StringNameProvider<String>(), null, null);

    try {
      FileWriter w = new FileWriter(fileName);
      // de.export(w, graph, sumPMIMap);
      de.exportGraph(graph, w);


      w.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
