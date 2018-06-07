/**
 * 
 */
package fr.ubx.bph.erias.miam.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * @author Georgeta Bordea
 *
 */
public class DocumentClassifier {

  private Classifier classifier;

  private static Logger logger =
      Logger.getLogger(DocumentClassifier.class.getName());

  public Instances loadInstances(String arffFile) {

    Instances data = null;

    try {
      BufferedReader reader = new BufferedReader(new FileReader(arffFile));
      data = new Instances(reader);

      reader.close();

      // setting class attribute
      data.setClassIndex(data.numAttributes() - 1);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return data;
  }

  /**
   * Train the classifier with the given dataset
   * 
   * @param trainingData
   *          The dataset to be given for classification
   * @throws ClassifierException
   *           If classification fails for some reason
   */
  public void trainClassifier(Instances trainingData, String pathToSaveModel,
      boolean crossValidate) throws ClassifierException {

    logger.log(Level.INFO, "Training the classifier with "
        + trainingData.numInstances() + " instances");
    SMO smoClassifier = new SMO();
    trainingData.setClass(trainingData.attribute("class"));

    try {
      String[] options = Utils.splitOptions(
          "-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers"
              + ".functions.supportVector.PolyKernel -C 250007 -E 1.0\"");

      smoClassifier.setOptions(options);
      smoClassifier.buildClassifier(trainingData);

      classifier = smoClassifier;

      if (crossValidate) {
        crossValidate(trainingData);
      }

      saveModel(new File(pathToSaveModel, "latest.model").getAbsolutePath());
      logger.log(Level.INFO, "Model built and saved");
    } catch (Exception e) {
      logger.log(Level.ERROR, "Training classifier failed.", e);
      throw new ClassifierException("Classification failed.", e);
    }
  }

  /**
   * Save the created model file to a file to be used later
   * 
   * @throws ClassifierException
   */
  private void saveModel(String pathToSaveModel) throws ClassifierException {
    
    logger.log(Level.INFO, "Saving model file...");
    
    if (classifier != null) {
      
      ObjectOutputStream oos;
      
      try {
        oos = new ObjectOutputStream(new FileOutputStream(pathToSaveModel));
        
        // todo: save a copy with datestamp
        oos.writeObject(classifier);
        oos.flush();
        oos.close();
        
        logger.log(Level.INFO, "Model file saved");
        
      } catch (IOException e) {
        logger.log(Level.ERROR, "Saving model file failed.", e);
        throw new ClassifierException("Saving model file failed", e);
      }
    } else {
      logger.log(Level.ERROR, "Model is not initiated, saving aborted");
      throw new ClassifierException("Classifier is not initiated");
    }
  }

  /**
   * Performs cross validation on the newly built model to validate it's
   * accuracy and acceptance
   * 
   * @param data
   *          training data t perform the cross validation
   * @throws ClassifierException
   *           If cross validation failed
   */
  private void crossValidate(Instances data) throws ClassifierException {
    
    logger.log(Level.INFO, "Cross validating the model...");
    
    Evaluation eval;
    
    try {
      eval = new Evaluation(data);
      eval.crossValidateModel(classifier, data, 10, new Random(1));
      
      logger.log(Level.INFO, "\n" + eval.toSummaryString(true));
      logger.log(Level.INFO, "\n" + eval.toClassDetailsString());
      logger.log(Level.INFO, "\n" + eval.toMatrixString());
      
    } catch (Exception e) {
      logger.log(Level.ERROR, "Cross validation failed.", e);
      throw new ClassifierException("Cross Validation failed", e);
    }
  }

  /**
   * Load a previously saved model
   * 
   * @throws ClassifierException
   */
  private void loadModel(String modelPath) throws ClassifierException {
    
    logger.log(Level.INFO, "Loading WEKA model...");
    
    try {
      ObjectInputStream ois =
          new ObjectInputStream(new FileInputStream(modelPath));
      
      classifier = (Classifier) ois.readObject();
      ois.close();
      
      logger.log(Level.INFO, "Weka model loaded");
      
    } catch (ClassNotFoundException e) {
      logger.log(Level.ERROR,
          "Invalid classifier object loaded, Loading ignored.", e);
      throw new ClassifierException("Invalid classifier object", e);
    } catch (FileNotFoundException e) {
      logger.log(Level.ERROR, "Model file not found at " + modelPath, e);
      throw new ClassifierException("No File found to open at " + modelPath, e);
    } catch (IOException e) {
      logger.log(Level.ERROR, "Model file read failed.", e);
      throw new ClassifierException("File can't be read", e);
    }
  }

  /**
   * Do the predictions for the given instances using the previously trained
   * classifier
   * 
   * @param instances
   *          The instances to be classified (to make the predictions on)
   * @return String array mentioning which class each instance is belongs (based
   *         on the prediction). The output array follow the same sequences as
   *         the given dataset
   * @throws ClassifierException
   *           If prediction process failed
   */
  public String[] doPredictions(Instances instances, String modelPath)
      throws ClassifierException {
    
    if (classifier == null) {
      loadModel(modelPath);
    }
    
    instances.setClass(instances.attribute("class"));
    
    String[] predictions = new String[instances.numInstances()];
    
    try {
      for (int i = 0; i < instances.numInstances(); i++) {
        
        Instance instance = instances.instance(i);
        double v = classifier.classifyInstance(instance);
        
        predictions[i] = instances.classAttribute().value((int) v);
      }
    } catch (Exception e) {
      throw new ClassifierException("Predicting Failed", e);
    }
    return predictions;
  }
}
