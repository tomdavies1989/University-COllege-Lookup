package uk.ac.ucl.panda.crawling;

/*
 * Holds a URL as a string, and its depth in the crawl tree.
 * 
 * The equals(), compareTo(), and hashCode() methods are just passed to the corresponding methods in the String class
 * (i.e. it is assumed that each WebAddress class can be uniquely identified by its URL string alone).
 */
public class WebAddress implements Comparable<WebAddress>
{
  
  public String URL;
  public final int depth;
  
  public WebAddress( String URL, int depth )
  {
    this.URL = URL;
    this.depth = depth;
  }

  @Override
  public int compareTo( WebAddress other )
  {
    return this.URL.compareTo( other.URL );
  }
  
  @Override
  public boolean equals( Object other )
  {
    if ( other.getClass().getName().equals( this.getClass().getName() ) )
    {
      return this.URL.equals( ((WebAddress) other).URL );
    }
    return false;
  }
  
  @Override
  public int hashCode()
  {
    return this.URL.hashCode();
  }
  
}
