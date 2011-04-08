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
    
    long TermVectorOwn_totalNumberOfTerms = 0;
    for ( String currentKey : TermVectorOwn.keySet() )
    {
      TermVectorOwn_totalNumberOfTerms += (Integer) TermVectorOwn.get(currentKey);
    }
    
    double currentTF;
    double currentIDF;
    
    HashMap<String, Double> TermVectorOwn_TFIDF = new HashMap<String, Double>();
    for ( String currentKey : TermVectorOwn.keySet() )
    {
      currentTF = (double)TermVectorOwn.get(currentKey) / (double)TermVectorOwn_totalNumberOfTerms;
      currentIDF = Math.log( (double)TermVectorOwn_totalNumberOfTerms / (double)(TermVectorOwn.get(currentKey) + 1) );
      
      TermVectorOwn_TFIDF.put( currentKey, currentTF / currentIDF );
    }
    
    long queryOwn_totalNumberOfTerms = 0;
    for ( String currentKey : queryOwn.keySet() )
    {
      queryOwn_totalNumberOfTerms += (Integer) queryOwn.get(currentKey);
    }
    
    HashMap<String, Double> queryOwn_TFIDF = new HashMap<String, Double>();
    for ( String currentKey : queryOwn.keySet() )
    {
      currentTF = (double)queryOwn.get(currentKey) / (double)queryOwn_totalNumberOfTerms;
      currentIDF = Math.log( (double)queryOwn_totalNumberOfTerms / (double)(queryOwn.get(currentKey) + 1) );
      
      queryOwn_TFIDF.put( currentKey, currentTF / currentIDF );
    }
    
    double returnNumerator = 0;
    for ( String key : queryOwn_TFIDF.keySet() )
    {
      Double currentA, currentB;
      
      currentA = queryOwn_TFIDF.get( key );
      if ( currentA == null ) { currentA = 0.0d; }
      
      currentB = TermVectorOwn_TFIDF.get( key );
      if ( currentB == null ) { currentB = 0.0d; }
      
      returnNumerator += currentA * currentB;
    }
    
    double returnDenominatorA = 0;
    for ( String key : queryOwn_TFIDF.keySet() )
    {
      returnDenominatorA += Math.pow( queryOwn_TFIDF.get(key), 2 );
    }
    returnDenominatorA = Math.sqrt(returnDenominatorA);
    
    double returnDenominatorB = 0;
    for ( String key : TermVectorOwn_TFIDF.keySet() )
    {
      returnDenominatorB += Math.pow( TermVectorOwn_TFIDF.get(key), 2 );
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
