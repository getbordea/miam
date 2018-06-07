/**
 * 
 */
package fr.ubx.bph.erias.miam.interaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ubx.bph.erias.miam.db.UMLSConceptHierarchies;

/**
 * @author Georgeta Bordea
 *
 */
public class PubmedCorpusPotentialInteractions {

  private static Logger logger =
      Logger.getLogger(PubmedCorpusPotentialInteractions.class.getName());

  /**
   * Each cooccurrence of a drug term with a food term will be considered as a
   * potential interaction
   */
  public void generateInteractionsFromMeSH(String foodTermsFile,
      String drugTermsFile, String interactionsFile) {
    BufferedWriter out = null;
    FileWriter fstream;

    Map<String, Integer> interactionsMap = new HashMap<String, Integer>();
    Integer pmidsWithInteractions = 0;
    Integer pmidsWithoutInteractions = 0;

    try {
      BufferedReader inputFoods =
          new BufferedReader(new FileReader(foodTermsFile));
      BufferedReader inputDrugs =
          new BufferedReader(new FileReader(drugTermsFile));

      fstream = new FileWriter(interactionsFile, true);

      out = new BufferedWriter(fstream);

      try {
        String foodsLine = null;
        String drugsLine = null;

        while ((foodsLine = inputFoods.readLine()) != null) {
          drugsLine = inputDrugs.readLine();

          String[] foods = foodsLine.split(";");

          String[] drugs = drugsLine.split(";");

          if ((foods.length > 1) && (drugs.length > 1)) {

            pmidsWithInteractions++;

            for (int i = 1; i < foods.length; i++) {
              for (int j = 1; j < drugs.length; j++) {

                if (!foods[i].equals(drugs[j])) {

                  String key = foods[i] + ";" + drugs[j];

                  if (!interactionsMap.containsKey(key)) {
                    interactionsMap.put(key, 1);
                  } else {
                    Integer value = interactionsMap.get(key);

                    value = value + 1;

                    interactionsMap.put(key, value);

                    // logger.log(Level.INFO,
                    // "Increased value for " + key + " to " + value);
                  }
                }
              }
            }

          } else {
            pmidsWithoutInteractions++;
          }
        }

        Set<String> keys = interactionsMap.keySet();

        for (String key : keys) {

          String[] values = key.split(";");

          String food = values[0];

          String drug = values[1];

          try {
            List<String> foodTrees =
                UMLSConceptHierarchies.selectTreeIDsForConcept(food);
            List<String> drugTrees =
                UMLSConceptHierarchies.selectTreeIDsForConcept(drug);

            String foodTreesString = constructTreesString(foodTrees,
                UMLSConceptHierarchies.FOOD_RELATED_TREES);
            String drugTreesString = constructTreesString(drugTrees,
                UMLSConceptHierarchies.DRUG_RELATED_TREES);

            out.write(food + ";" + foodTreesString + ";" + drug + ";"
                + drugTreesString + ";" + interactionsMap.get(key)
                + System.lineSeparator());

          } catch (SQLException e) {
            e.printStackTrace();
          }
        }

        logger.log(Level.INFO, "Number of PMIDs without interactions: "
            + pmidsWithoutInteractions);
        logger.log(Level.INFO,
            "Number of PMIDs with interactions: " + pmidsWithInteractions);

      } finally {
        inputFoods.close();
        inputDrugs.close();
        out.close();
      }
    } catch (

    IOException ex) {
      ex.printStackTrace();
    }
  }

  private String constructTreesString(List<String> trees,
      String[] filterTrees) {

    String foodTreesString = "";

    for (String treeID : trees) {

      if (treeID != null) {

        for (int i = 0; i < filterTrees.length; i++) {
          if (treeID.startsWith(filterTrees[i])) {
            foodTreesString += treeID + ",";
          }

        }
      }
    }

    if (foodTreesString != "") {
      return foodTreesString.substring(0, foodTreesString.length() - 1);
    } else {
      return "";
    }
  }
}
