/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.ubx.bph.erias.miam.utils.DocumentUtils;

/**
 * @author Georgeta Bordea
 *
 */
public class BratAnnotationsReader {

  public List<BratAnnotation> readAnnotations(String fileName) {

    List<BratAnnotation> bratAnnList = new ArrayList<BratAnnotation>();

    if (fileName.endsWith(".ann")) {
      try {
        String annotationsString = DocumentUtils.readFile(fileName);

        String[] lines = annotationsString.split(System.lineSeparator());

        for (String line : lines) {

          if (line.startsWith("T")) {
            String[] fields = line.split("\t");

            String[] annOffset = fields[1].split(" |;");

            BratAnnotation ba = null;

            switch (annOffset.length) {
            case 3:
              ba = new BratAnnotation(fields[0], annOffset[0],
                  Integer.parseInt(annOffset[1]),
                  Integer.parseInt(annOffset[2]), null, null, null, null, null,
                  null, fields[2]);
              break;
            case 5:
              ba = new BratAnnotation(fields[0], annOffset[0],
                  Integer.parseInt(annOffset[1]),
                  Integer.parseInt(annOffset[2]),
                  Integer.parseInt(annOffset[3]),
                  Integer.parseInt(annOffset[4]), null, null, null, null,
                  fields[2]);
              break;
            case 7:
              ba = new BratAnnotation(fields[0], annOffset[0],
                  Integer.parseInt(annOffset[1]),
                  Integer.parseInt(annOffset[2]),
                  Integer.parseInt(annOffset[3]),
                  Integer.parseInt(annOffset[4]),
                  Integer.parseInt(annOffset[5]),
                  Integer.parseInt(annOffset[6]), null, null, fields[2]);
              break;
            case 9:
              ba = new BratAnnotation(fields[0], annOffset[0],
                  Integer.parseInt(annOffset[1]),
                  Integer.parseInt(annOffset[2]),
                  Integer.parseInt(annOffset[3]),
                  Integer.parseInt(annOffset[4]),
                  Integer.parseInt(annOffset[5]),
                  Integer.parseInt(annOffset[6]),
                  Integer.parseInt(annOffset[7]),
                  Integer.parseInt(annOffset[8]), fields[2]);
              break;
            }

            bratAnnList.add(ba);
          }
        }

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return bratAnnList;
    }

    return bratAnnList;
  }

  // TODO Implement method
  public List<BratRelation> readRelations(String fileName) {
    List<BratRelation> bratRelList = new ArrayList<BratRelation>();

    return bratRelList;
  }
}
