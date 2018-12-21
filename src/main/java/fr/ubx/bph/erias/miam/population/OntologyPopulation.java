/**
 * 
 */
package fr.ubx.bph.erias.miam.population;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.complexible.stardog.api.Connection;

import fr.ubx.bph.erias.miam.corpora.BratAnnotation;
import fr.ubx.bph.erias.miam.corpora.BratAnnotationsReader;
import fr.ubx.bph.erias.miam.corpora.MiamAnnotationType;
import fr.ubx.bph.erias.miam.db.RDFConceptHierarchies;
import fr.ubx.bph.erias.miam.db.UMLSConceptSearch;
import fr.ubx.bph.erias.miam.utils.DocumentUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class OntologyPopulation {

  private BratAnnotationsReader bar = new BratAnnotationsReader();
  private RDFConceptHierarchies rch = new RDFConceptHierarchies();

  String[] headTerms = { "ratio", "food product", "of material" };

  // TODO load ChEBI in a database, read annotations, add in FIDEO
  public void populateAnnFromBratFileByType(Connection aConn, String bratDir,
      String annType) {

    System.out.println("Linking annotations of type " + annType);

    File docDir = new File(bratDir);

    List<File> allFiles;

    Integer countAnnotatedStrings = 0;
    Integer countMatchedStrings = 0;

    try {
      allFiles = DocumentUtils.getAllDocumentFiles(docDir);

      for (File file : allFiles) {

        if (file.getName().endsWith(".ann")) {
          System.out.println("----------------------------");
          System.out.println(file.getName());
          System.out.println("----------------------------");

          Set<String> filteredStrings =
              filterAnnByType(file.getPath(), annType);

          countAnnotatedStrings += filteredStrings.size();

          for (String filteredString : filteredStrings) {
            String conceptUri = null;
            String foundString = filteredString;

            // These characters should be removed for well formed queries
            if (filteredString.contains("'") || filteredString.contains("\"")) {
              filteredString = filteredString.replaceAll("\"", "");
              filteredString = filteredString.replaceAll("'", " ");
            }

            List<String> variants = generateConceptVariants(filteredString);

            for (String variant : variants) {

              // System.out.println("Searching for " + variant);

              String searchUri = selectConceptUriFromDB(aConn, true, variant);

              if (searchUri != null) {

                conceptUri = searchUri;
                foundString = variant;
                countMatchedStrings++;

                break;
              } else {

                searchUri = selectConceptUriFromDB(aConn, false, variant);

                if (searchUri != null) {

                  conceptUri = searchUri;
                  foundString = variant;
                  countMatchedStrings++;

                  break;
                } else {

                }
                searchUri = rch.selectSynonymConcept(aConn, variant, true);

                if (searchUri != null) {

                  conceptUri = searchUri;
                  foundString = variant;
                  countMatchedStrings++;

                  break;
                } else {

                }
                searchUri = rch.selectSynonymConcept(aConn, variant, false);

                if (searchUri != null) {

                  conceptUri = searchUri;
                  foundString = variant;
                  countMatchedStrings++;

                  break;
                }
              }
            }

            if (filteredString.equals(foundString)) {
              System.out.println(filteredString + " " + conceptUri);
            } else {
              System.out.println(
                  filteredString + " -> " + foundString + " " + conceptUri);
            }
          }
        }
      }

      double recall =
          (double) countMatchedStrings / (double) countAnnotatedStrings;
      System.out.println("Maximum Recall " + recall);

    } catch (

    FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private List<String> generateConceptVariants(String annotatedString) {
    List<String> variantsList = new ArrayList<String>();

    variantsList.add(annotatedString);

    if (annotatedString.endsWith("s")) {
      variantsList
          .add(annotatedString.substring(0, annotatedString.length() - 1));
    }

    if (!variantsList.contains(cleanSearchString(annotatedString))) {
      variantsList.add(cleanSearchString(annotatedString));
    }

    if (!variantsList.contains(annotatedString.replaceAll("-", " "))) {
      variantsList.add(annotatedString.replaceAll("-", " "));
    }

    if (!variantsList.contains(annotatedString.toLowerCase())) {
      variantsList.add(annotatedString.toLowerCase());
    }

    // upper case first letter, lower case the rest
    String upperCaseFirstLetter = annotatedString.substring(0, 1).toUpperCase()
        + annotatedString.substring(1).toLowerCase();
    if (!variantsList.contains(upperCaseFirstLetter)) {
      variantsList.add(upperCaseFirstLetter);
    }

    List<String> tmpVariants = new ArrayList<String>(variantsList);

    // find all synonyms from UMLS
    try {
      for (String variant : tmpVariants) {

        List<String> englishVariants =
            UMLSConceptSearch.selectEnglishNameVariants(variant);

        for (String umlsVariant : englishVariants) {

          // These characters should be removed for well formed queries
          if (umlsVariant.contains("'") || umlsVariant.contains("\"")) {
            umlsVariant = umlsVariant.replaceAll("\"", "");
            umlsVariant = umlsVariant.replaceAll("'", " ");

            //System.out.println("!!!!!" + umlsVariant);
          }

          variantsList.add(umlsVariant);
        }
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    tmpVariants = new ArrayList<String>(variantsList);

    for (String headTerm : headTerms) {
      for (String variant : tmpVariants) {

        String composedTerm = variant + " " + headTerm;

        if (!variantsList.contains(composedTerm)) {
          variantsList.add(composedTerm);
        }
      }
    }

    return variantsList;
  }

  public String selectConceptUriFromDB(Connection aConn, Boolean languageAnn,
      String filteredString) {
    String conceptUri;

    filteredString = cleanSearchString(filteredString);

    if (languageAnn) {
      conceptUri = rch.selectEnglishConcept(aConn, filteredString);
    } else {
      conceptUri = rch.selectConcept(aConn, filteredString);
    }
    return conceptUri;
  }

  private String cleanSearchString(String filteredString) {
    filteredString = filteredString.trim().replaceAll(" +", " ");

    filteredString = filteredString.replaceAll("\\(", "");
    filteredString = filteredString.replaceAll("\\)", "");

    return filteredString;
  }

  private Set<String> filterAnnByType(String bratFile, String type) {
    List<BratAnnotation> annList = bar.readAnnotations(bratFile);

    Set<String> outputList = new HashSet<String>();

    for (BratAnnotation bratAnn : annList) {

      if (type.equals(bratAnn.getAnnotationType())) {
        outputList.add(bratAnn.getAnnotatedText());
      }
    }

    return outputList;
  }

  // TODO load ChEBI in a database, read Stockley entities, add in FIDEO
  public void populateFromStockley(Connection aConn, String stockleyFile,
      Boolean languageAnn) {

    Integer countMatchedStrings = 0;

    List<String> foods = DocumentUtils.readWordsFromFile(stockleyFile);

    for (String food : foods) {
      String conceptUri = "";

      conceptUri = selectConceptUriFromDB(aConn, languageAnn, food);

      if (conceptUri != null) {
        countMatchedStrings++;
      }

    }

  }
}
