package uk.ac.ucl.panda.retrieval.models;

import java.util.HashMap;
import java.util.Vector;


public class VectorSpaceModel implements Model
{

  @Override
  public double defaultScore( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF )
  {
    // TODO Auto-generated method stub
    return 0.0d;
  }

  @Override
  public double getscore( double tf, double df, double idf, double DL, double avgDL, int DocNum, double CL, int CTF )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  //query --> is a query term vector, TermVector --> is a hash with term as key and frequency of the term in the document as value
  @Override
  public double getVSMscore( Vector query, HashMap TermVector )
  {
    // TODO Auto-generated method stub
    
    System.out.println("\r\nquery.getClass().toString() is: " + query.getClass().toString());
    System.out.println("\r\nTermVector.getClass().toString() is: " + TermVector.getClass().toString());
    
    return 0;
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
