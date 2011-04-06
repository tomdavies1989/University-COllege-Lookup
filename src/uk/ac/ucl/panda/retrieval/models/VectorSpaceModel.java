package uk.ac.ucl.panda.retrieval.models;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class VectorSpaceModel implements Model
{
  
  @Override
  public double defaultScore( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public double getscore( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  // query --> is a query term vector, TermVector --> is a hash with term as key and frequency of the term in the document as value
  @Override
  public double getVSMscore( Vector query, HashMap TermVector )
  {
    System.out.println("query class is: " + query.getClass().toString());
    System.out.println("termvector class is: " + TermVector.getClass().toString());
    
    HashMap<String, Integer> TermVectorOwn = (HashMap<String, Integer>) TermVector;
    
    HashMap<String,Integer> queryOwn = new HashMap<String, Integer>();
    for( Object currentQuery : query )
    {
      queryOwn.put( ( String ) currentQuery, 1 );
    }
    
    long totalNumberOfTerms = 0;
    for ( String currentKey : TermVectorOwn.keySet() )
    {
      totalNumberOfTerms += (Integer) TermVectorOwn.get(currentKey);
    }
    
    HashMap<String, Double> TF_IDF = new HashMap<String, Double>();
    double currentTF;
    double currentIDF;
    for ( String currentKey : TermVectorOwn.keySet() )
    {
      currentTF = (double)TermVectorOwn.get(currentKey) / (double)totalNumberOfTerms;
      currentIDF = Math.log( (double)totalNumberOfTerms / (double)(TermVectorOwn.get(currentKey) + 1) );
      
      TF_IDF.put( currentKey, currentTF / currentIDF );
    }
    
    double returnNumerator = 0;
    for ( String key : queryOwn.keySet() )
    {
      double currentA, currentB;
      
      currentA = queryOwn.get( key );
      currentB = TermVectorOwn.get( key );
      
      returnNumerator += currentA * currentB;
    }
    
    double returnDenominatorA = 0;
    for ( String key : queryOwn.keySet() )
    {
      returnDenominatorA += Math.pow( queryOwn.get(key), 2 );
    }
    returnDenominatorA = Math.sqrt(returnDenominatorA);
    
    double returnDenominatorB = 0;
    for ( String key : TermVectorOwn.keySet() )
    {
      returnDenominatorB += Math.pow( TermVectorOwn.get(key), 2 );
    }
    returnDenominatorB = Math.sqrt(returnDenominatorB);
    
    return returnNumerator / ( returnDenominatorA * returnDenominatorB );
  }
  
  @Override
  public double defaultScore( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF, double a )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public double getscore( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF, double a )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public double getmean( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF, double a )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public double getvar( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF, double a )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public double defaultmean( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF, double a )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public double defaultvar( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF, double a )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
}
