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
public class UMLSConceptHierarchies {

  private static final String TREE_TABLE_NAME = "MRHIER";

  private static final String RELATIONS_TABLE_NAME = "MRREL";

  private static final String CONCEPT_TABLE_NAME = "MRCONSO";

  public static final String[] FOOD_RELATED_TREES =
      { "B01.650", "J02", "G07.203", "B01.300", "E02.642", "D09", "A18",
          "F01.145.317.269", "D02.241.081.901.434", "J01.576.423.850",
          "D20.215.784", "B01.050.150.900.493" };

  public static final String[] DRUG_RELATED_TREES = { "D27.505", "D26", "D03",
      "D04", "D01", "D02.241.223.701", "D02", "D12", "D25", "D09"};

  private static final String[] DISEASE_RELATED_TREES =
      { "C", "G09", "F03", "E04" };

  private static final String[] INVESTIGATION_DIAGNOSIS_RELATED_TREES =
      { "E05", "E01" };

  private static final String[] COHORT_RELATED_TREES =
      { "B01.050.150.900.649.313.988.400.112.400.400", "M01", "B01.050" };

  private static final String[] COHORT_TERMS = { "Male", "Female" };

  private static final String[] ADME_RELATED_TREES = { "G03", "D08" };

  /**
   * SELECT DISTINCT hcd FROM `MRHIER` h INNER JOIN MRCONSO c ON c.cui = h.cui
   * WHERE c.sab = 'MSH' AND str = 'Ginkgo biloba'
   * 
   * @param id
   * @throws SQLException
   */
  public static List<String> selectTreeIDsForConcept(String meshTerm)
      throws SQLException {
    String selectQuery = "SELECT DISTINCT hcd FROM " + TREE_TABLE_NAME
        + " h INNER JOIN " + CONCEPT_TABLE_NAME
        + " c ON c.cui = h.cui WHERE c.sab = 'MSH' AND str = ?";

    PreparedStatement ps =
        JDBCConnection.getConn().prepareStatement(selectQuery);

    List<String> treeIDs = new ArrayList<String>();

    ps.setString(1, meshTerm);
    ResultSet rs = ps.executeQuery();
    try {

      int i = 1;

      while (rs.next()) {
        treeIDs.add(rs.getString(i));
      }

    } finally {
      ps.close();
      if (rs != null) {
        rs.close();
      }
    }

    return treeIDs;
  }

  public static Boolean isFoodRelatedConcept(String meshTerm) {

    List<String> treeIDs;

    try {
      treeIDs = selectTreeIDsForConcept(meshTerm);

      for (String treeID : treeIDs) {

        if (treeID != null) {

          for (int i = 0; i < FOOD_RELATED_TREES.length; i++) {
            if (treeID.startsWith(FOOD_RELATED_TREES[i]))
              return true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static Boolean isCohortRelatedConcept(String meshTerm) {

    List<String> treeIDs;

    for (String cohortTerm : COHORT_TERMS) {
      if (cohortTerm.equals(meshTerm)) {
        return true;
      }
    }

    try {
      treeIDs = selectTreeIDsForConcept(meshTerm);

      for (String treeID : treeIDs) {

        if (treeID != null) {

          for (int i = 0; i < COHORT_RELATED_TREES.length; i++) {
            if (treeID.startsWith(COHORT_RELATED_TREES[i]))
              return true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static Boolean isDiseaseRelatedConcept(String meshTerm) {

    List<String> treeIDs;

    try {
      treeIDs = selectTreeIDsForConcept(meshTerm);

      for (String treeID : treeIDs) {

        if (treeID != null) {

          for (int i = 0; i < DISEASE_RELATED_TREES.length; i++) {
            if (treeID.startsWith(DISEASE_RELATED_TREES[i]))
              return true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static Boolean isDrugRelatedConcept(String meshTerm) {

    List<String> treeIDs;

    try {
      treeIDs = selectTreeIDsForConcept(meshTerm);

      for (String treeID : treeIDs) {

        if (treeID != null) {

          for (int i = 0; i < DRUG_RELATED_TREES.length; i++) {
            if (treeID.startsWith(DRUG_RELATED_TREES[i]))
              return true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static Boolean isDrug(String meshTerm) throws SQLException {
    String selectQuery =
        " SELECT DISTINCT cui2 " + "FROM " + RELATIONS_TABLE_NAME + " r "
            + "INNER JOIN " + CONCEPT_TABLE_NAME + " c ON c.cui = r.cui1 "
            + "WHERE rela = 'inverse_isa' AND r.sab = 'MSH' " + "AND str = ? ";

    PreparedStatement ps =
        JDBCConnection.getConn().prepareStatement(selectQuery);

    ps.setString(1, meshTerm);
    ResultSet rs = ps.executeQuery();
    try {

      if (rs.next() || isDrugRelatedConcept(meshTerm)) {
        return true;
      }

    } finally {
      ps.close();
      if (rs != null) {
        rs.close();
      }
    }

    return false;
  }

  public static Boolean isInvestigationDiagnosisRelatedConcept(
      String meshTerm) {

    List<String> treeIDs;

    try {
      treeIDs = selectTreeIDsForConcept(meshTerm);

      for (String treeID : treeIDs) {

        if (treeID != null) {

          for (int i =
              0; i < INVESTIGATION_DIAGNOSIS_RELATED_TREES.length; i++) {
            if (treeID.startsWith(INVESTIGATION_DIAGNOSIS_RELATED_TREES[i]))
              return true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }
}
