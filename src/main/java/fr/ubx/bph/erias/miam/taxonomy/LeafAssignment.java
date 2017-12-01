/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Georgeta Bordea
 *
 */
public class LeafAssignment {

  public void attachLeaves() {

  }

  public List<Node> identifyLeaves(AdjacencyList taxonomy) {

    List<Node> nodes = taxonomy.getAllNodesList();
    List<Node> leaves = new ArrayList<Node>();

    for (Node node : nodes) {
      List<Edge> outgoingEdges =
          AdjacencyList.outgoingEdges(node, taxonomy.getAdjacent(node));
      
      if (outgoingEdges.size() == 0) {
        leaves.add(node);
      }
    }

    return leaves;
  }

  /**
   * Returns null if the node was not found
   * 
   * @param searchNode
   * @param currentNode
   * @param depth
   * @param taxonomy
   * @return
   */
  public Integer findDepth(Node searchNode, Node currentNode, Integer depth,
      AdjacencyList taxonomy) {

    if (currentNode.getTopicString().equals(searchNode.getTopicString())) {
      return depth;
    }

    depth++;

    List<Edge> edges = taxonomy.getAdjacent(currentNode);

    for (Edge edge : edges) {
      if (edge.getTo().getTopicString().equals(searchNode.getTopicString())) {
        return depth;
      } else {
        findDepth(searchNode, edge.getTo(), depth, taxonomy);
      }
    }

    return null;
  }
}
