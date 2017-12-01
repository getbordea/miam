/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

/**
 * @author Georgeta Bordea
 *
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdjacencyList {

	private Map<Node, List<Edge>> adjacencies = new HashMap<Node, List<Edge>>();

	public void addEdge(Node source, Node target, double weight) {
		List<Edge> list;
		if (!adjacencies.containsKey(source)) {
			list = new ArrayList<Edge>();
			adjacencies.put(source, list);
		} else {
			list = adjacencies.get(source);
		}
		list.add(new Edge(source, target, weight));
		// adjacencies.put(source, list);
	}

	public List<Edge> getAdjacent(Node source) {
		return adjacencies.get(source);
	}

	public void reverseEdge(Edge e) {
		adjacencies.get(e.getFrom()).remove(e);
		addEdge(e.getTo(), e.getFrom(), e.getWeight());
	}

	public void reverseGraph() {
		adjacencies = getReversedList().adjacencies;
	}

	public AdjacencyList getReversedList() {
		AdjacencyList newlist = new AdjacencyList();
		for (List<Edge> edges : adjacencies.values()) {
			for (Edge e : edges) {
				newlist.addEdge(e.getTo(), e.getFrom(), e.getWeight());
			}
		}
		return newlist;
	}

	public Set<Node> getSourceNodeSet() {
		return adjacencies.keySet();
	}

	public List<Node> getSourceNodeList() {
		List<Node> nodesList = new ArrayList<Node>();
		Set<Node> nodesSet = adjacencies.keySet();
		for (Node node : nodesSet) {
			nodesList.add(node);
		}
		return nodesList;
	}

	public List<Node> getAllNodesList() {
		List<Node> nodesList = new ArrayList<Node>();
		Set<Node> nodesSet = adjacencies.keySet();
		for (Node node : nodesSet) {
			nodesList.add(node);

			List<Edge> edges = adjacencies.get(node);
			for (Edge edge : edges) {
				Node fromNode = edge.getFrom();
				if (!nodesList.contains(fromNode))
					nodesList.add(fromNode);

				Node toNode = edge.getTo();
				if (!nodesList.contains(toNode))
					nodesList.add(toNode);
			}
		}
		return nodesList;
	}

	public Collection<Edge> getAllEdges() {
		List<Edge> edges = new ArrayList<Edge>();
		for (List<Edge> e : adjacencies.values()) {
			edges.addAll(e);
		}
		return edges;
	}

	public void printAdjacencies() {
		Set<Node> nodes = adjacencies.keySet();

		for (Node node : nodes) {
			List<Edge> edges = adjacencies.get(node);
			for (Edge edge : edges) {
				System.out.println("\"" + node.getTopicString() + "\" -> \"" + edge.getTo().getTopicString() + "\"");
			}
		}
	}

	public Node getNode(String topicString) {
		Set<Node> nodes = adjacencies.keySet();

		for (Node node : nodes) {
			if (topicString.equals(node.getTopicString()))
				return node;

			List<Edge> edges = adjacencies.get(node);
			for (Edge edge : edges) {
				Node fromNode = edge.getFrom();
				if (topicString.equals(fromNode.getTopicString()))
					return fromNode;

				Node toNode = edge.getTo();
				if (topicString.equals(toNode.getTopicString()))
					return toNode;
			}
		}

		return null;
	}

  public static List<Edge> incomingEdges(Node node, List<Edge> adjacentEdges) {
  
    List<Edge> incoming = new ArrayList<Edge>();
  
    // System.out
    // .println("Checking incoming edges for node " + node.getTopicString());
  
    if (adjacentEdges != null) {
      for (Edge edge : adjacentEdges) {
  
        // System.out.println("Edge: " + edge.getFrom().getTopicString() + " ->
        // "
        // + edge.getTo().getTopicString());
  
        Node source = edge.getTo();
  
        if (node.getTopicString() != source.getTopicString()) {
          incoming.add(edge);
        }
      }
    }
  
    return incoming;
  }
  
  public static List<Edge> outgoingEdges(Node node, List<Edge> adjacentEdges) {
    List<Edge> outgoing = new ArrayList<Edge>();

    // System.out
    // .println("Checking outgoing edges for node " + node.getTopicString());

    for (Edge edge : adjacentEdges) {

      // System.out.println("Edge: " + edge.getFrom().getTopicString() + " -> "
      // + edge.getTo().getTopicString());

      Node dest = edge.getTo();

      if (node.getTopicString() != dest.getTopicString()) {
        outgoing.add(edge);
      }
    }

    return outgoing;
  }
}
