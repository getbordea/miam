/**
 * 
 */
package fr.ubx.bph.erias.miam.taxonomy;

/**
 * @author Georgeta Bordea
 *
 */

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
//import org.jgrapht.ext.EdgeNameProvider;
//import org.jgrapht.ext.StringNameProvider;
//import org.jgrapht.ext.VertexNameProvider;

/**
 * Exports a graph into a DOT file.
 * 
 * <p>
 * For a description of the format see <a
 * href="http://en.wikipedia.org/wiki/DOT_language">
 * http://en.wikipedia.org/wiki/DOT_language</a>.
 * </p>
 * 
 * @author Trevor Harmon
 */

public class MiamDotExporter<V, E> {

  //TODO find a more recent implementation or change version
  
  static Logger logger = Logger.getLogger(MiamDotExporter.class.getName());
  /*
  // ~ Instance fields --------------------------------------------------------

  private VertexNameProvider<V> vertexIDProvider;
  private VertexNameProvider<V> vertexLabelProvider;
  private EdgeNameProvider<E> edgeLabelProvider;

  // ~ Constructors -----------------------------------------------------------

  /**
   * Constructs a new DOTExporter object with an integer name provider for the
   * vertex IDs and null providers for the vertex and edge labels.
   */
/*  public MiamDotExporter() {
    this(new StringNameProvider<V>(), null, null);
  }

  /**
   * Constructs a new DOTExporter object with the given ID and label providers.
   * 
   * @param vertexIDProvider
   *          for generating vertex IDs. Must not be null.
   * @param vertexLabelProvider
   *          for generating vertex labels. If null, vertex labels will not be
   *          written to the file.
   * @param edgeLabelProvider
   *          for generating edge labels. If null, edge labels will not be
   *          written to the file.
   */
/*  public MiamDotExporter(VertexNameProvider<V> vertexIDProvider,
      VertexNameProvider<V> vertexLabelProvider,
      EdgeNameProvider<E> edgeLabelProvider) {
    this.vertexIDProvider = vertexIDProvider;
    this.vertexLabelProvider = vertexLabelProvider;
    this.edgeLabelProvider = edgeLabelProvider;
  }

  // ~ Methods ----------------------------------------------------------------

  /**
   * Exports a graph into a plain text file in DOT format.
   * 
   * @param writer
   *          the writer to which the graph to be exported
   * @param g
   *          the graph to be exported
   */

/*
  public void export(Writer writer, Graph<V, E> g, Map<String, Double> sumPMIMap) {
    PrintWriter out = new PrintWriter(writer);
    String indent = "  ";
    String connector;

    if (g instanceof DirectedGraph) {
      out.println("digraph G {");
      connector = " -> ";
    } else {
      out.println("graph G {");
      connector = " -- ";
    }

    for (V v : g.vertexSet()) {

      String nodeString = vertexIDProvider.getVertexName(v);

      String label = nodeString;

      out.print(indent + "\"" + label + "\"");

      if (vertexLabelProvider != null) {
        out.print(" [label = \"" + label + "\"]");
      }

      String nodeWeightString = "";

      if (sumPMIMap != null) {
        nodeWeightString = " [ weight = " + sumPMIMap.get(nodeString) + "]";
      }

      out.println(nodeWeightString + ";");
    }

    for (E e : g.edgeSet()) {

      String sourceString = vertexIDProvider.getVertexName(g.getEdgeSource(e));
      String source = "\"" + sourceString + "\"";

      String targetString = vertexIDProvider.getVertexName(g.getEdgeTarget(e));
      String target = "\"" + targetString + "\"";
      
      Double w = g.getEdgeWeight(e);
      String weightString = " [ weight = " + w.doubleValue() + "]";
      out.print(indent + source + connector + target + weightString);

      if (edgeLabelProvider != null) {
        out.print(" [label = \"" + edgeLabelProvider.getEdgeName(e) + "\"]");
      }

      out.println(";");
    }

    out.println("}");

    out.flush();
  }*/
}


// End DOTExporter.java
