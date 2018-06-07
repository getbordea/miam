package fr.ubx.bph.erias.miam.classification;

import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import fr.ubx.bph.erias.miam.corpora.RelevanceDatasetBuilder;
import weka.core.Instances;

public class DocumentClassifierTest {

  private static Logger logger =
      Logger.getLogger(DocumentClassifierTest.class.getName());

  DocumentClassifier dc = new DocumentClassifier();

  private static final String CORPUS_FILE_PATH = "src/main/resources/corpora/";
  private static final String MODELS_FILE_PATH =
      "/home/gb5/work/isped/miam/Corpus/experiments/wekaModels/";

  private static final String TRAINED_MODEL_FILE_PATH =
      MODELS_FILE_PATH + "latest.model";

  private static final String TRAINING_FILE_PATH =
      //CORPUS_FILE_PATH + "all/FDIrelevance_2806all.arff";
      CORPUS_FILE_PATH + 
      //"all/FDIrelevance_3220all.arff";;
      "fdi_hdi_2018/FDIrelevance_8646all.arff";

  private static final String TEST_FILE_PATH = "featureEvalTest.arff";

  @Before
  public void setUp() throws Exception {
    PropertyConfigurator.configure("src/main/config/log.properties");
  }

  @Test
  public void doPredictionsTest() {
    //Instances trainingData = dc.loadInstances(TRAINING_FILE_PATH);

    Instances testData = dc.loadInstances(TEST_FILE_PATH);

    try {
      //dc.trainClassifier(trainingData, MODELS_FILE_PATH, true);

      String[] predictions =
          dc.doPredictions(testData, TRAINED_MODEL_FILE_PATH);

      logger.log(Level.INFO,
          "Predicting relevance for " + predictions.length + " test instances");

      Integer countPositives = 0;
      Integer countNegatives = 0;

      for (String prediction : predictions) {
        //System.out.println(prediction);
        
        if (prediction.equals("positive")) {
          countPositives++;
        } else {
          countNegatives++;
        }
      }

      Double percentagePositives =
          (countPositives * 100.0) / (countPositives + countNegatives);
      Double percentageNegatives =
          (countNegatives * 100.0) / (countPositives + countNegatives);

      System.out.println(
          "Positives and negatives: " + countPositives + " " + countNegatives);
      System.out.println("Positives and negatives percentage:\t"
          + percentagePositives + "\t" + percentageNegatives);

    } catch (ClassifierException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
