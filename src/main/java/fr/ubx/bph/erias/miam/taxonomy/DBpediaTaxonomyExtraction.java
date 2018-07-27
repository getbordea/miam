/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import fr.ubx.bph.erias.miam.lucene.SearchDocuments;

/**
 * @author Georgeta Bordea
 *
 */
public class DBpediaTaxonomyExtraction {

  static Logger logger =
      Logger.getLogger(DBpediaTaxonomyExtraction.class.getName());

  /**
   * Read existing category structure from dot file, compute edge weights and
   * prune the graph and output to a dot file
   */
  public void extractDBpediaTaxonomy(String rootName, String graphPath,
      Integer spanSize, String luceneIndexPath) {
    List<Node> nodes = MiamDotImporter.importNodes(graphPath);
    logger.log(Level.INFO,
        "Loaded the following number of nodes: " + nodes.size());

    AdjacencyList dbPediaEdges = MiamDotImporter.importEdges(graphPath, nodes);
    logger.log(Level.INFO, "Loaded the following number of edges: "
        + dbPediaEdges.getAllEdges().size());

    List<TmpEdge> tmpEdges = new ArrayList<TmpEdge>();
    try {

      logger.log(Level.INFO, "Constructing similarity edges");
      tmpEdges =
          constructEdgesWithoutDirections(nodes, 0.0, 1, luceneIndexPath);

      Map<String, List<TmpEdge>> organisedEdges = organiseEdges(tmpEdges);

      logger.log(Level.INFO,
          "Compute SumPMI for all the topics based on existing edges");
      Map<String, Double> sumPMIMap = new HashMap<String, Double>();

      List<String> topics = extractNodeNames(nodes);

      sumPMIMap =
          computeSumPMI(topics, organisedEdges, spanSize, luceneIndexPath);

      logger.log(Level.INFO, "Construct directed edges based on SumPMI");
      AdjacencyList edges = constructDirectedEdges(tmpEdges, sumPMIMap);

      // edges = normaliseEdgeWeights(edges);

      logger.log(Level.INFO, "Exporting graph to DOT format");
      JGraphTRepresentation.exportToDot(
          JGraphTRepresentation.convertToJGraphT(nodes, edges),
          "similarityGraph.dot", sumPMIMap);

      AdjacencyList dbpediaGraphWithWeights = updateEdgeWeights(dbPediaEdges,
          new ArrayList<Edge>(edges.getAllEdges()));

      JGraphTRepresentation
          .exportToDot(
              JGraphTRepresentation.convertToJGraphT(nodes,
                  dbpediaGraphWithWeights),
              "weightedDBpediaGraph.dot", sumPMIMap);

      logger.log(Level.INFO, "Prune the graph");
      AdjacencyList prunedEdges = GraphPruning.pruneGraph(rootName, nodes,
          dbpediaGraphWithWeights, sumPMIMap);

      // Remove edges for nodes that have more than one parent, parallel
      // edges and self referring edges
      // TODO Check this line of code
      prunedEdges = GraphPruning.pruneMultipleParents(nodes, prunedEdges);

      DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> jgrapht =
          JGraphTRepresentation.convertToJGraphT(nodes, prunedEdges);

      logger.log(Level.INFO, "Export graph to DOT");
      JGraphTRepresentation.exportToDot(jgrapht, "prunedGraph.dot", sumPMIMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private AdjacencyList updateEdgeWeights(AdjacencyList dbpediaEdges,
      List<Edge> edges) {

    List<Edge> edgesList = new ArrayList<Edge>(dbpediaEdges.getAllEdges());

    AdjacencyList outputList = new AdjacencyList();

    for (Edge dbpediaEdge : edgesList) {

      Boolean foundEdge = false;

      for (Edge edge : edges) {
        if (dbpediaEdge.getFrom().getTopicString()
            .equals(edge.getFrom().getTopicString())
            && dbpediaEdge.getTo().getTopicString()
                .equals(edge.getTo().getTopicString())) {
          outputList.addEdge(dbpediaEdge.getFrom(), dbpediaEdge.getTo(),
              edge.getWeight());

          foundEdge = true;
          break;
        }
      }

      if (!foundEdge) {
        outputList.addEdge(dbpediaEdge.getFrom(), dbpediaEdge.getTo(),
            dbpediaEdge.getWeight());
      }

    }

    return outputList;
  }

  private List<TmpEdge> convertEdgesToTmp(List<Edge> edgeList) {

    List<TmpEdge> tmpEdges = new ArrayList<TmpEdge>();

    for (Edge edge : edgeList) {
      TmpEdge tmpEdge =
          new TmpEdge(edge.getFrom(), edge.getTo(), edge.getWeight());

      tmpEdges.add(tmpEdge);
    }

    return tmpEdges;
  }

  public Map<String, List<TmpEdge>> organiseEdges(List<TmpEdge> tmpEdges) {

    Map<String, List<TmpEdge>> organisedEdges =
        new HashMap<String, List<TmpEdge>>();

    for (TmpEdge tmpEdge : tmpEdges) {
      String node1 = tmpEdge.getNode1().getTopicString();
      String node2 = tmpEdge.getNode2().getTopicString();

      if (!organisedEdges.containsKey(node1)
          && !organisedEdges.containsKey(node2)) {
        List<TmpEdge> edges = new ArrayList<TmpEdge>();
        edges.add(tmpEdge);
        organisedEdges.put(node1, edges);
        organisedEdges.put(node2, edges);
      } else if (!organisedEdges.containsKey(node1)) {
        List<TmpEdge> edges = new ArrayList<TmpEdge>();
        edges.add(tmpEdge);
        organisedEdges.put(node1, edges);
        List<TmpEdge> edges2 = organisedEdges.get(node2);
        edges2.add(tmpEdge);
        organisedEdges.put(node2, edges2);
      } else if (!organisedEdges.containsKey(node2)) {
        List<TmpEdge> edges = new ArrayList<TmpEdge>();
        edges.add(tmpEdge);
        organisedEdges.put(node2, edges);
        List<TmpEdge> edges1 = organisedEdges.get(node1);
        edges1.add(tmpEdge);
        organisedEdges.put(node1, edges1);
      } else {
        List<TmpEdge> edges1 = organisedEdges.get(node1);
        List<TmpEdge> edges2 = organisedEdges.get(node2);
        edges1.add(tmpEdge);
        edges2.add(tmpEdge);
        organisedEdges.put(node1, edges1);
        organisedEdges.put(node2, edges2);
      }
    }

    return organisedEdges;
  }

  public Map<String, Double> computeSumPMI(List<String> topics,
      Map<String, List<TmpEdge>> organisedEdges, Integer spanSlop,
      String luceneIndexPath) throws IOException {

    Map<String, Long> topicsMap = new HashMap<String, Long>();
    SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));
    IndexReader reader = DirectoryReader
        .open(FSDirectory.open(new File(luceneIndexPath).toPath()));

    Integer docsCount = reader.numDocs();

    Map<String, Double> pmiMap = new HashMap<String, Double>();

    // use all top topics
    for (String topic : topics) {

      Long overallFreq = sd.computeFrequency(topic, docsCount);

      logger.log(Level.INFO,
          "Topic " + topic + " has frequency " + overallFreq);
      topicsMap.put(topic, new Long(overallFreq));
    }

    for (String topic : topics) {

      // TODO replace max integer with number of tokens
      Double pmi = sumPMI(sd, topic, topicsMap, organisedEdges, docsCount,
          Integer.MAX_VALUE, spanSlop);

      logger.log(Level.INFO, "Sum PMI for topic " + topic + " is: " + pmi);

      pmiMap.put(topic, pmi);
    }

    // return MapUtils.sortByDoubleValue(pmiMap);
    return pmiMap;
  }

  private String removeDisambiguationString(String topicName) {

    if (topicName.contains("(")) {
      return topicName.substring(0, topicName.indexOf("(") - 1);
    } else
      return topicName;

  }

  private List<String> extractNodeNames(List<Node> nodes) {
    List<String> nodeNames = new ArrayList<String>();

    for (Node node : nodes) {
      nodeNames.add(node.getTopicString());
    }

    return nodeNames;
  }

  private AdjacencyList constructDirectedEdges(List<TmpEdge> tmpEdges,
      Map<String, Double> sumPMIMap) {
    AdjacencyList directedEdges = new AdjacencyList();

    for (TmpEdge tmpEdge : tmpEdges) {
      if ((tmpEdge.getNode1().getTopicString() != tmpEdge.getNode2()
          .getTopicString())
          && (tmpEdge.getNode1().getName() != tmpEdge.getNode2().getName())) {

        directedEdges = addEdge(sumPMIMap, directedEdges, tmpEdge.getWeight(),
            tmpEdge.getNode1(), tmpEdge.getNode2());
      }
    }
    return directedEdges;
  }

  /**
   * @param pmiMap
   * @param edges
   * @param weight
   * @param nodei
   * @param nodej
   */
  public static AdjacencyList addEdge(Map<String, Double> pmiMap,
      AdjacencyList edges, Double weight, Node nodei, Node nodej) {

    Double pmiI = pmiMap.get(nodei.getTopicString());
    Double pmiJ = pmiMap.get(nodej.getTopicString());

    // Idea was to connect to nodes of similar generality, doesn't work
    // Double squareDiff = (pmiI - pmiJ) * (pmiI - pmiJ);
    // if (squareDiff != 0) {
    // weight = weight / squareDiff;
    // }

    if (pmiI != null && pmiJ != null
        && nodei.getTopicString() != nodej.getTopicString()) {

      if (// (nodej.getTopicString().contains(nodei.getTopicString()))
      (nodej.getTopicString().endsWith(" " + nodei.getTopicString()))
          || (pmiI > pmiJ)) {

        // if (ti.computeOverallOccurrence() >
        // tj.computeOverallOccurrence()) {

        // logger.log(Level.INFO, "Adding edge " + nodei.getTopicString() + "->"
        // + nodej.getTopicString() + " " + weight);
        edges.addEdge(nodei, nodej, weight);
      } else {
        // logger.log(Level.INFO, "Adding edge " + nodej.getTopicString() + "->"
        // + nodei.getTopicString() + " " + weight);
        edges.addEdge(nodej, nodei, weight);
      }
    }
    return edges;
  }

  public static AdjacencyList normaliseEdgeWeights(AdjacencyList edges) {

    AdjacencyList normEdges = new AdjacencyList();
    Collection<Edge> edgeCollection = edges.getAllEdges();
    List<Edge> edgeList = new ArrayList<Edge>(edgeCollection);
    Double minWeight = edgeList.get(0).getWeight();
    Double maxWeight = edgeList.get(0).getWeight();
    for (Edge edge : edgeCollection) {
      Double weight = edge.getWeight();

      if (weight < minWeight) {
        minWeight = weight;
      }

      if ((!weight.isInfinite()) && (weight > maxWeight)) {
        maxWeight = weight;
      }
    }

    minWeight = Math.log(1 + minWeight);
    maxWeight = Math.log(1 + maxWeight);

    for (Edge edge : edgeCollection) {
      Double w = edge.getWeight();

      if (w.isInfinite()) {
        w = maxWeight;
      } else {
        w = Math.log(1 + edge.getWeight());
      }

      Double normWeight = 10000 * (w - minWeight) / (maxWeight - minWeight);
      Integer intWeight = normWeight.intValue() / 10;
      normEdges.addEdge(edge.getFrom(), edge.getTo(), intWeight);
    }

    return normEdges;
  }

  public Double sumPMI(SearchDocuments sd, String topic,
      Map<String, Long> topics, Map<String, List<TmpEdge>> organisedEdges,
      Integer docsCount, Integer totalTokensNo, Integer spanSlop) {

    Double contextWordsRank = 0.0;
    try {

      List<TmpEdge> edges = organisedEdges.get(topic);

      Long overallFreq = sd.computeFrequency(topic, docsCount);

      if (edges != null && totalTokensNo > 0) {
        for (TmpEdge edge : edges) {

          String otherTopic;
          if (edge.getNode1().getTopicString().equals(topic)) {
            otherTopic = edge.getNode2().getTopicString();
          } else {
            otherTopic = edge.getNode1().getTopicString();
          }

          Long spanFreq = new Long(0);
          // TODO use all MV strings not just the preferred string
          // String searchString = otherTopic.replace("-", " ").replace("_", "
          // ");
          String searchString1 = topic.replace("-", " ").replace("_", " ");

          if (otherTopic != null) {
            String searchString2 =
                otherTopic.replace("-", " ").replace("_", " ");
            spanFreq = sd.computeSpanOccurrence(searchString1, searchString2,
                docsCount, spanSlop);
          }

          double pxy = (double) spanFreq / totalTokensNo;
          double px = (double) topics.get(otherTopic) / totalTokensNo;
          double py = (double) overallFreq / totalTokensNo;

          if (spanFreq > 0 && px != 0 && py != 0) {
            contextWordsRank += Math.log(pxy / (px * py));
          }
        }
      }
    } catch (CorruptIndexException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return contextWordsRank; // topics.size();
  }

  public List<TmpEdge> constructEdgesWithoutDirections(List<Node> nodes,
      Double simThreshold, Integer minCommonDocs, String luceneIndexPath)
      throws CorruptIndexException, IOException {

    List<TmpEdge> edgeList = new ArrayList<TmpEdge>();

    SearchDocuments sd = new SearchDocuments(new File(luceneIndexPath));

    Map<String, Map<String, Integer>> occMaps =
        new HashMap<String, Map<String, Integer>>();

    IndexReader reader = DirectoryReader
        .open(FSDirectory.open(new File(luceneIndexPath).toPath()));

    Integer docsCount = reader.numDocs();

    for (Node nodei : nodes) {
      String topic = nodei.getTopicString();
      try {
        Map<String, Integer> occMap = sd.searchOccurrence(topic, docsCount);

        logger.log(Level.INFO, "Searching frequency for " + topic);
        occMaps.put(topic, occMap);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    for (int i = 0; i < nodes.size(); i++) {
      for (int j = 0; j < nodes.size(); j++) {
        // compute only for the triangular matrix
        if ((nodes.get(i) != nodes.get(j)) && (i >= j)) {

          String topic1ID = nodes.get(i).getTopicString();
          String topic2ID = nodes.get(j).getTopicString();

          // Add substring edges with maximum weight
          if (topic1ID.endsWith(" " + topic2ID)
              || topic2ID.endsWith(" " + topic1ID)
          // || topic2ID.startsWith(topic1ID + " ")
          // || topic1ID.startsWith(topic2ID + " ")
          // || topic2ID.contains(" " + topic1ID + " ")
          // || topic1ID.contains(" " + topic2ID + " ")
          ) {
            edgeList.add(new TmpEdge(nodes.get(i), nodes.get(j), 0.5));

            logger.log(Level.INFO,
                "Adding edge: " + nodes.get(i).getTopicString() + " -> "
                    + nodes.get(j).getTopicString() + " " + 0.5);
          } else {

            // compute similarity based on co-occurrence
            Map<String, Integer> occMap1 = occMaps.get(topic1ID);
            Map<String, Integer> occMap2 = occMaps.get(topic2ID);

            // check first if at least a minimum number of documents mention
            // them
            // together
            Integer cooc =
                Subsumption.computeCoocurrence(occMap1, occMap2, docsCount);
            if (cooc >= minCommonDocs) {

              Integer t1Docs = occMap1.size();
              Integer t2Docs = occMap2.size();

              // TODO in previous experiments we used t1Docs + t2Docs
              Double weight = new Double(cooc) / (1 + t1Docs + t2Docs);
              // Double weight = new Double(cooc) / (1 + t1Docs * t2Docs);

              if (weight > simThreshold) {
                // weight =
                // weight
                // * weight
                // * (1 / (pmiMap.get(nodei.getTopicString()) - pmiMap
                // .get(nodej.getTopicString())));

                edgeList.add(new TmpEdge(nodes.get(i), nodes.get(j), weight));

                logger.log(Level.INFO,
                    "Adding edge: " + nodes.get(i).getTopicString() + " -> "
                        + nodes.get(j).getTopicString() + " " + weight);
              }
            }
          }
        }
      }
    }

    return edgeList;
  }
}
