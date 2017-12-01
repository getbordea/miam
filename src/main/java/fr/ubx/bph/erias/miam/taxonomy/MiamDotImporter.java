/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * This class is used to read a graph from a .dot file
 * 
 * @author Georgeta Bordea
 */
public class MiamDotImporter {

  public static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> importGraph(
      String path) {

    DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph =
        new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(
            DefaultWeightedEdge.class);

    try {
      BufferedReader input = new BufferedReader(new FileReader(path));
      try {
        String line = null;
        input.readLine();
        while ((line = input.readLine()) != null) {

          if (!line.contains("}")) {
            if (!line.contains("->")) {
              String nodeString = line.trim();
              nodeString = nodeString.substring(1, nodeString.length() - 2);
              directedGraph.addVertex(nodeString);
            } else {
              String edgeString = line.trim();
              String[] nodesWeight = edgeString.split(" -> ");

              String from =
                  nodesWeight[0].substring(1, nodesWeight[0].length() - 1);
              String to =
                  nodesWeight[1].substring(1, nodesWeight[1].lastIndexOf("\""));
              String weight = nodesWeight[1].substring(
                  nodesWeight[1].indexOf("=") + 2, nodesWeight[1].length() - 2);

              // Ignore self referring edges
              if (from != to) {
                Double weightDouble = Double.parseDouble(weight);
                if (!directedGraph.containsVertex(from)) {
                  directedGraph.addVertex(from);
                }
                if (!directedGraph.containsVertex(to)) {
                  directedGraph.addVertex(to);
                }

                DefaultWeightedEdge e = directedGraph.addEdge(from, to);
                if (e != null) {
                  directedGraph.setEdgeWeight(e, weightDouble);
                }
              }

            }
          }

        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return directedGraph;
  }

  public static AdjacencyList importEdges(String path, List<Node> nodes) {
    AdjacencyList edges = new AdjacencyList();

    try {
      BufferedReader input = new BufferedReader(new FileReader(path));
      try {
        String line = null;
        input.readLine();
        while ((line = input.readLine()) != null) {

          if (!line.contains("}")) {
            if (line.contains("->")) {
              String edgeString = line.trim();
              String[] nodesWeight = edgeString.split(" -> ");

              String from = nodesWeight[0]
                  .substring(1, nodesWeight[0].length() - 1).replace("_", " ");

              if (from.startsWith("Category:")) {
                from = from.substring(from.indexOf(":") + 1);
              }

              String to =
                  nodesWeight[1].substring(1, nodesWeight[1].lastIndexOf("\""))
                      .replace("_", " ");

              if (to.startsWith("Category:")) {
                to = to.substring(to.indexOf(":") + 1);
              }

              // System.out.println(from + " -> " + to);

              if (from != to) {
                String weight =
                    nodesWeight[1].substring(nodesWeight[1].indexOf("=") + 2,
                        nodesWeight[1].length() - 2);

                Node fromNode = null;
                Node toNode = null;

                for (Node node : nodes) {

                  String nodeName = node.getTopicString();
                  if (nodeName.equals(from)) {
                    fromNode = node;
                  } else if (node.getTopicString().equals(to)) {
                    toNode = node;
                  }
                }

                Double weightDouble = Double.parseDouble(weight);

                if (fromNode != null && toNode != null
                    && weightDouble != null) {
                  edges.addEdge(fromNode, toNode, weightDouble);
                }
                
                //System.out.println("Loaded edge " + from + " -> " + to);
              }
            }
          }

        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return edges;
  }

  public static List<Node> importNodes(String path) {
    Map<String, Node> nodes = new HashMap<String, Node>();

    try {
      BufferedReader input = new BufferedReader(new FileReader(path));
      try {
        String line = null;
        input.readLine();

        Integer i = 0;
        while ((line = input.readLine()) != null) {

          if (!line.contains("}")) {
            if (line.contains("->")) {
              String edgeString = line.trim();
              String[] nodesWeight = edgeString.split(" -> ");

              String from = nodesWeight[0].trim().replace("_", " ");
              from = from.replace("\"", "");

              if (from.startsWith("Category:")) {
                from = from.substring(from.indexOf(":") + 1);
              }

              String to =
                  nodesWeight[1].substring(0, nodesWeight[1].length() - 1)
                      .trim().replace("_", " ");

              to = to.replace("\"", "");
              
              if (to.startsWith("Category:")) {
                to = to.substring(to.indexOf(":") + 1);
              }

              if (to.contains("[")) {
                to = to.substring(0, to.indexOf("[") - 1);
              }

              if (!nodes.containsKey(from)) {
                nodes.put(from, new Node(i, from));
                i++;
              }

              if (!nodes.containsKey(to)) {
                nodes.put(to, new Node(i, to));
                i++;
              }
              
              //System.out.println("Loaded nodes " + from + "; " + to);
            }
          }
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return new ArrayList<Node>(nodes.values());
  }
}
