/**
 * 
 */
package fr.ubx.bph.erias.miam.corpora;

/**
 * @author Georgeta Bordea
 *
 */
public class BratRelation {
  private String relationID = null;

  private String argument1 = null;

  private String argument2 = null;
  
  public BratRelation(String relationID, String argument1, String argument2) {
    super();
    this.relationID = relationID;
    this.argument1 = argument1;
    this.argument2 = argument2;
  }  

  public String getRelationID() {
    return relationID;
  }

  public void setRelationID(String relationID) {
    this.relationID = relationID;
  }

  public String getArgument1() {
    return argument1;
  }

  public void setArgument1(String argument1) {
    this.argument1 = argument1;
  }

  public String getArgument2() {
    return argument2;
  }

  public void setArgument2(String argument2) {
    this.argument2 = argument2;
  }
}
