package fr.ubx.bph.erias.miam.corpora;

/**
 * @author Georgeta Bordea
 *
 */
public class BratAnnotation {

  private String annotationID = null;
  private String annotationType = null;

  private Integer startOffset = null;
  private Integer endOffset = null;

  private Integer startOffset1 = null;
  private Integer endOffset1 = null;
  
  private Integer startOffset2 = null;
  private Integer endOffset2 = null;
  
  private Integer startOffset3 = null;
  private Integer endOffset3 = null;

  public Integer getStartOffset3() {
    return startOffset3;
  }

  public void setStartOffset3(Integer startOffset3) {
    this.startOffset3 = startOffset3;
  }

  public Integer getEndOffset3() {
    return endOffset3;
  }

  public void setEndOffset3(Integer endOffset3) {
    this.endOffset3 = endOffset3;
  }

  private String annotatedText = null;

  public BratAnnotation(String bratID, String annotationType,
      Integer startOffset, Integer endOffset, Integer startOffset1,
      Integer endOffset1, Integer startOffset2,
      Integer endOffset2, Integer startOffset3,
      Integer endOffset3, String annotatedText) {
    super();
    this.annotationID = bratID;
    this.annotationType = annotationType;
    this.startOffset = startOffset;
    this.endOffset = endOffset;
    this.startOffset1 = startOffset1;
    this.endOffset1 = endOffset1;
    this.startOffset2 = startOffset2;
    this.endOffset2 = endOffset2;
    this.startOffset3 = startOffset3;
    this.endOffset3 = endOffset3;
    this.annotatedText = annotatedText;
  }

  public String getAnnotationID() {
    return annotationID;
  }

  public void setAnnotationID(String annotationID) {
    this.annotationID = annotationID;
  }

  public Integer getStartOffset2() {
    return startOffset2;
  }

  public void setStartOffset2(Integer startOffset2) {
    this.startOffset2 = startOffset2;
  }

  public Integer getEndOffset2() {
    return endOffset2;
  }

  public void setEndOffset2(Integer endOffset2) {
    this.endOffset2 = endOffset2;
  }

  public Integer getStartOffset1() {
    return startOffset1;
  }

  public void setStartOffset1(Integer startOffset1) {
    this.startOffset1 = startOffset1;
  }

  public Integer getEndOffset1() {
    return endOffset1;
  }

  public void setEndOffset1(Integer endOffset1) {
    this.endOffset1 = endOffset1;
  }

  public String getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(String annotationType) {
    this.annotationType = annotationType;
  }

  public Integer getStartOffset() {
    return startOffset;
  }

  public void setStartOffset(Integer startOffset) {
    this.startOffset = startOffset;
  }

  public Integer getEndOffset() {
    return endOffset;
  }

  public void setEndOffset(Integer endOffset) {
    this.endOffset = endOffset;
  }

  public String getAnnotatedText() {
    return annotatedText;
  }

  public void setAnnotatedText(String annotatedText) {
    this.annotatedText = annotatedText;
  }
}
