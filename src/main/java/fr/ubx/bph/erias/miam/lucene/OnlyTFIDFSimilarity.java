/**
 * 
 */
package fr.ubx.bph.erias.miam.lucene;

import java.io.IOException;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;

/**
 * @author Georgeta Bordea
 * 
 */
public class OnlyTFIDFSimilarity extends Similarity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public float lengthNorm(String fieldName, int numTerms) {
    return 1;
  }

  public float queryNorm(float sumOfSquaredWeights) {
    return 1;
  }

  /** Implemented as <code>sqrt(freq)</code>. */
  public float tf(float freq) {
    return (float) Math.sqrt(freq);

  }

  public float sloppyFreq(int distance) {
    return 1;
  }

  /** Implemented as <code>log(numDocs/(docFreq+1)) + 1</code>. */
  public float idf(int docFreq, int numDocs) {
    return (float) (Math.log(numDocs / (double) (docFreq + 1)) + 1.0);
  }

  public float coord(int overlap, int maxOverlap) {
    return 1;
  }

  @Override
  public long computeNorm(FieldInvertState state) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public SimWeight computeWeight(float queryBoost,
      CollectionStatistics collectionStats, TermStatistics... termStats) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimScorer simScorer(SimWeight arg0, LeafReaderContext arg1)
      throws IOException {
    // TODO Auto-generated method stub
    return null;
  }
}
