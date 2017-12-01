/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

/**
 * implementation from 
 * http://algowiki.net/wiki/index.php?title=Edmonds%27s_algorithm
 * 
 * @author Georgeta Bordea
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Edmonds {

  private ArrayList<Node> cycle;

  public AdjacencyList getMinBranching(Node root, AdjacencyList list) {
    AdjacencyList reverse = list.getReversedList();
    // remove all edges entering the root
    if (reverse.getAdjacent(root) != null) {
      reverse.getAdjacent(root).clear();
    }
    AdjacencyList outEdges = new AdjacencyList();
    // for each node, select the edge entering it with smallest weight
    for (Node n : reverse.getSourceNodeSet()) {
      List<Edge> inEdges = reverse.getAdjacent(n);
      if (inEdges.isEmpty())
        continue;
      Edge min = inEdges.get(0);
      for (Edge e : inEdges) {
        if (e.getWeight() < min.getWeight()) {
          min = e;
        }
      }
      outEdges.addEdge(min.getTo(), min.getFrom(), min.getWeight());
    }

    // detect cycles
    ArrayList<ArrayList<Node>> cycles = new ArrayList<ArrayList<Node>>();
    cycle = new ArrayList<Node>();
    getCycle(root, outEdges);
    cycles.add(cycle);
    for (Node n : outEdges.getSourceNodeSet()) {
      if (!n.isVisited()) {
        cycle = new ArrayList<Node>();
        getCycle(n, outEdges);
        cycles.add(cycle);
      }
    }

    // for each cycle formed, modify the path to merge it into another part of
    // the graph
    AdjacencyList outEdgesReverse = outEdges.getReversedList();

    for (ArrayList<Node> x : cycles) {
      if (x.contains(root))
        continue;
      mergeMaxCycles(x, list, reverse, outEdges, outEdgesReverse);
    }
    return outEdges;
  }

  public AdjacencyList getMaxBranching(Node root, AdjacencyList list) {
    AdjacencyList reverse = list.getReversedList();
    // remove all edges entering the root
    if (reverse.getAdjacent(root) != null) {
      reverse.getAdjacent(root).clear();
    }
    AdjacencyList outEdges = new AdjacencyList();
    // for each node, select the edge entering it with largest weight
    for (Node n : reverse.getSourceNodeSet()) {
      List<Edge> inEdges = reverse.getAdjacent(n);
      if (inEdges.isEmpty())
        continue;
      Edge max = inEdges.get(0);
      for (Edge e : inEdges) {
        if (e.getWeight() > max.getWeight()) {
          max = e;
        }
      }
      outEdges.addEdge(max.getTo(), max.getFrom(), max.getWeight());
    }

    // detect cycles
    ArrayList<ArrayList<Node>> cycles = new ArrayList<ArrayList<Node>>();
    cycle = new ArrayList<Node>();
    getCycle(root, outEdges);

    if (cycle.size() > 1) {
      cycles.add(cycle);
    }

    for (Node n : outEdges.getSourceNodeSet()) {
      if (!n.isVisited()) {
        cycle = new ArrayList<Node>();
        getCycle(n, outEdges);
        if (cycle.size() > 1) {
          cycles.add(cycle);
        }
      }
    }

    // for each cycle formed, modify the path to merge it into another part of
    // the graph
    AdjacencyList outEdgesReverse = outEdges.getReversedList();

    for (ArrayList<Node> x : cycles) {
      if (x.contains(root))
        continue;
      mergeMaxCycles(x, list, reverse, outEdges, outEdgesReverse);
    }
    return outEdges;
  }

  private void mergeMaxCycles(ArrayList<Node> cycle, AdjacencyList list,
      AdjacencyList reverse, AdjacencyList outEdges,
      AdjacencyList outEdgesReverse) {
    ArrayList<Edge> cycleAllInEdges = new ArrayList<Edge>();
    Edge maxInternalEdge = null;
    // find the maximum internal edge weight
    for (Node n : cycle) {
      if ((reverse != null) && (reverse.getAdjacent(n) != null)) {
        for (Edge e : reverse.getAdjacent(n)) {
          if (cycle.contains(e.getTo())) {
            if (maxInternalEdge == null
                || maxInternalEdge.getWeight() < e.getWeight()) {
              maxInternalEdge = e;
              continue;
            }
          } else {
            cycleAllInEdges.add(e);
          }
        }
      }
    }

    // find the incoming edge with maximum modified cost
    Edge maxExternalEdge = null;
    double maxModifiedWeight = 0;
    for (Edge e : cycleAllInEdges) {

      double w = 0;

      try {
        w =
            e.getWeight()
                - (outEdgesReverse.getAdjacent(e.getFrom()).get(0).getWeight() - maxInternalEdge
                    .getWeight());

      } catch (NullPointerException ne) {
        System.out.println("Null exception");
      }
      if (maxExternalEdge == null || maxModifiedWeight < w) {
        maxExternalEdge = e;
        maxModifiedWeight = w;
      }
    }

    // add the incoming edge and remove the inner-circuit incoming edge
    if ((maxExternalEdge != null) && (maxExternalEdge.getFrom() != null)
        && (outEdgesReverse.getAdjacent(maxExternalEdge.getFrom()) != null)) {
      Edge removing =
          outEdgesReverse.getAdjacent(maxExternalEdge.getFrom()).get(0);
      outEdgesReverse.getAdjacent(maxExternalEdge.getFrom()).clear();
      outEdgesReverse.addEdge(maxExternalEdge.getTo(),
          maxExternalEdge.getFrom(), maxExternalEdge.getWeight());
      List<Edge> adj = outEdges.getAdjacent(removing.getTo());
      for (Iterator<Edge> i = adj.iterator(); i.hasNext();) {
        if (i.next().getTo() == removing.getFrom()) {
          i.remove();
          break;
        }
      }
      outEdges.addEdge(maxExternalEdge.getTo(), maxExternalEdge.getFrom(),
          maxExternalEdge.getWeight());
    }
  }

  private void mergeCycles(ArrayList<Node> cycle, AdjacencyList list,
      AdjacencyList reverse, AdjacencyList outEdges,
      AdjacencyList outEdgesReverse) {
    ArrayList<Edge> cycleAllInEdges = new ArrayList<Edge>();
    Edge minInternalEdge = null;
    // find the minimum internal edge weight
    for (Node n : cycle) {
      for (Edge e : reverse.getAdjacent(n)) {
        if (cycle.contains(e.getTo())) {
          if (minInternalEdge == null
              || minInternalEdge.getWeight() > e.getWeight()) {
            minInternalEdge = e;
            continue;
          }
        } else {
          cycleAllInEdges.add(e);
        }
      }
    }
    // find the incoming edge with minimum modified cost
    Edge minExternalEdge = null;
    double minModifiedWeight = 0;
    for (Edge e : cycleAllInEdges) {
      double w =
          e.getWeight()
              - (outEdgesReverse.getAdjacent(e.getFrom()).get(0).getWeight() - minInternalEdge
                  .getWeight());
      if (minExternalEdge == null || minModifiedWeight > w) {
        minExternalEdge = e;
        minModifiedWeight = w;
      }
    }
    // add the incoming edge and remove the inner-circuit incoming edge
    Edge removing =
        outEdgesReverse.getAdjacent(minExternalEdge.getFrom()).get(0);
    outEdgesReverse.getAdjacent(minExternalEdge.getFrom()).clear();
    outEdgesReverse.addEdge(minExternalEdge.getTo(), minExternalEdge.getFrom(),
        minExternalEdge.getWeight());
    List<Edge> adj = outEdges.getAdjacent(removing.getTo());
    for (Iterator<Edge> i = adj.iterator(); i.hasNext();) {
      if (i.next().getTo() == removing.getFrom()) {
        i.remove();
        break;
      }
    }
    outEdges.addEdge(minExternalEdge.getTo(), minExternalEdge.getFrom(),
        minExternalEdge.getWeight());
  }

  private void getCycle(Node n, AdjacencyList outEdges) {
    n.setVisited(true);
    cycle.add(n);
    if (outEdges.getAdjacent(n) == null)
      return;
    for (Edge e : outEdges.getAdjacent(n)) {
      if (!e.getTo().isVisited()) {
        getCycle(e.getTo(), outEdges);
      }
    }
  }
}
