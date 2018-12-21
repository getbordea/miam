/**
 * 
 */
package fr.ubx.bph.erias.miam.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Georgeta Bordea
 *
 */
public class UMLSConceptSearch {

  // SELECT DISTINCT cui FROM `MRCONSO` WHERE str = 'warfarin'
  public static String selectConceptID(String term) throws SQLException {
    String selectQuery = "SELECT DISTINCT cui FROM "
        + UMLSConceptHierarchies.CONCEPT_TABLE_NAME + " WHERE str = ?";

    PreparedStatement ps =
        JDBCConnection.getConn().prepareStatement(selectQuery);

    ps.setString(1, term);
    ResultSet rs = ps.executeQuery();
    try {

      if (rs.next()) {
        return rs.getString(1);
      }

    } finally {
      ps.close();
      if (rs != null) {
        rs.close();
      }
    }

    return null;
  }

  // SELECT DISTINCT str FROM `MRCONSO` WHERE cui = 'C0043031' AND lat = 'ENG'
  public static List<String> selectEnglishNameVariants(String term)
      throws SQLException {

    String conceptId = selectConceptID(term);
    
    String selectQuery =
        "SELECT DISTINCT str FROM " + UMLSConceptHierarchies.CONCEPT_TABLE_NAME
            + " WHERE cui = ? AND lat = 'ENG'";

    
    PreparedStatement ps =
        JDBCConnection.getConn().prepareStatement(selectQuery);

    List<String> nameVariants = new ArrayList<String>();

    ps.setString(1, conceptId);
    ResultSet rs = ps.executeQuery();
    try {

      int i = 1;

      while (rs.next()) {
        nameVariants.add(rs.getString(i));
      }

    } finally {
      ps.close();
      if (rs != null) {
        rs.close();
      }
    }

    return nameVariants;
  }
}
