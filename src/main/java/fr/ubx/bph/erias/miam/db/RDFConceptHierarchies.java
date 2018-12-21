/**
 * 
 */
package fr.ubx.bph.erias.miam.db;

import java.util.List;
import java.util.NoSuchElementException;

import org.openrdf.model.IRI;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.UnsupportedQueryResultFormatException;

import com.complexible.common.rdf.model.Values;
import com.complexible.common.rdf.query.Edge;
import com.complexible.common.rdf.query.Path;
import com.complexible.common.rdf.query.PathQueryResult;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.PathQuery;
import com.complexible.stardog.api.SelectQuery;

/**
 * @author Georgeta Bordea
 *
 */
public class RDFConceptHierarchies {

  private static final String PREFIX =
      "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
          + "PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#> \n";

  private static final String PATH_QUERY =
      PREFIX + "PATHS ALL START ?s = xxx END ?e = yyy VIA rdfs:subClassOf";

  private static final String SELECT_LABEL_QUERY =
      PREFIX + "SELECT ?o WHERE {?s rdfs:label ?o}";

  private static final String SELECT_CONCEPT_QUERY =
      "SELECT ?s WHERE {?s rdfs:label ?o}";

  private static final String SELECT_EN_CONCEPT_QUERY_START =
      "SELECT ?s WHERE {?s rdfs:label '";

  private static final String SELECT_EN_CONCEPT_QUERY_END = "'@en}";

  private static final String SELECT_CONCEPT_QUERY_END = "'}";

  private static final String SELECT_CONCEPT_SYNONYM_QUERY_START =
      PREFIX + "SELECT ?s WHERE {?s obo:IAO_0000118|oboInOwl:hasSynonym|"
          + "oboInOwl:hasExactSynonym|oboInOwl:hasRelatedSynonym '";

  public void queryPath(Connection aConn, String start, String end) {

    String pathQuery = PATH_QUERY;

    // TODO Find out how to parameterise path queries
    pathQuery = pathQuery.replace("xxx", start);
    pathQuery = pathQuery.replace("yyy", end);

    PathQuery pQuery = aConn.paths(pathQuery);

    PathQueryResult pathResult = pQuery.execute();

    try {
      // System.out.println("The first ten results...");
      // QueryResultIO.writeTuple(aResult, TextTableQueryResultWriter.FORMAT,
      // System.out);

      while (pathResult.hasNext()) {
        System.out.println("Path ");

        Path path = pathResult.next();

        List<Edge> edges = path.getEdges();

        for (Edge edge : edges) {

          String startId = edge.getStart().toString();
          String endId = edge.getEnd().toString();

          String startLabel = selectLabel(aConn, startId);
          String endLabel = selectLabel(aConn, endId);

          System.out.println(startLabel + " -> " + endLabel);
        }

        System.out.println();
      }
    } finally {
      // *Always* close your result sets, they hold resources which need to be
      // released.
      pathResult.close();
    }
  }

  public String selectLabel(Connection aConn, String id) {
    SelectQuery aQuery = aConn.select(SELECT_LABEL_QUERY);

    IRI aURI = Values.iri(id);
    aQuery.parameter("s", aURI);
    TupleQueryResult aResult = aQuery.execute();

    try {

      return (aResult.next().getValue("o").stringValue());
      // QueryResultIO.writeTuple(aResult, TextTableQueryResultWriter.FORMAT,
      // System.out);
    } catch (TupleQueryResultHandlerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (QueryEvaluationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnsupportedQueryResultFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      // *Always* close your result sets, they hold resources which need to be
      // released.
      aResult.close();
    }

    return "";
  }

  public String selectConcept(Connection aConn, String label) {
    SelectQuery aQuery = aConn.select(SELECT_CONCEPT_QUERY);

    aQuery.parameter("o", label);
    TupleQueryResult aResult = aQuery.execute();

    try {

      return (aResult.next().getValue("s").stringValue());
      // QueryResultIO.writeTuple(aResult, TextTableQueryResultWriter.FORMAT,
      // System.out);
    } catch (TupleQueryResultHandlerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      return null;
    } catch (QueryEvaluationException e) {
      return null;
    } catch (UnsupportedQueryResultFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      // *Always* close your result sets, they hold resources which need to be
      // released.
      aResult.close();
    }

    return null;
  }

  public String selectEnglishConcept(Connection aConn, String label) {

    String selectString =
        SELECT_EN_CONCEPT_QUERY_START + label + SELECT_EN_CONCEPT_QUERY_END;
    
    //System.out.println(selectString);
    
    SelectQuery aQuery = aConn.select(selectString);

    // aQuery.parameter("o", label);
    TupleQueryResult aResult = aQuery.execute();

    try {

      return (aResult.next().getValue("s").stringValue());
      // QueryResultIO.writeTuple(aResult, TextTableQueryResultWriter.FORMAT,
      // System.out);
    } catch (TupleQueryResultHandlerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      return null;
    } catch (QueryEvaluationException e) {
      return null;
    } catch (UnsupportedQueryResultFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      // *Always* close your result sets, they hold resources which need to be
      // released.
      aResult.close();
    }

    return null;
  }

  public String selectSynonymConcept(Connection aConn, String label,
      Boolean languageAnn) {

    String queryString = SELECT_CONCEPT_SYNONYM_QUERY_START + label;

    if (languageAnn) {
      queryString += SELECT_EN_CONCEPT_QUERY_END;
    } else {
      queryString += SELECT_CONCEPT_QUERY_END;
    }

    SelectQuery aQuery = aConn.select(queryString);

    TupleQueryResult aResult = aQuery.execute();

    try {

      return (aResult.next().getValue("s").stringValue());
      // QueryResultIO.writeTuple(aResult, TextTableQueryResultWriter.FORMAT,
      // System.out);
    } catch (TupleQueryResultHandlerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      return null;
    } catch (QueryEvaluationException e) {
      return null;
    } catch (UnsupportedQueryResultFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      // *Always* close your result sets, they hold resources which need to be
      // released.
      aResult.close();
    }

    return null;
  }
}
