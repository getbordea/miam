/**
 * 
 */
package fr.ubx.bph.erias.miam.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Georgeta Bordea
 *
 */
public class Config {

  private static Properties configFile;

  private static Config instance;

  private Config() {
    configFile = new Properties();
    try {
      //configFile.load(this.getClass().getClassLoader().getResourceAsStream(
      //    "/home/gb5/work/eclipse-workspace/miam/src/main/config/config.properties"));
      

      File file = new File("src/main/config/config.properties");
      FileInputStream fileInput = new FileInputStream(file);
      configFile.load(fileInput);
      fileInput.close();
      
    } catch (Exception eta) {
      eta.printStackTrace();
    }
  }

  private String getValue(String key) {
    return configFile.getProperty(key);
  }

  public static String getProperty(String key) {
    if (instance == null)
      instance = new Config();
    
    return instance.getValue(key);
  }
}