package uk.ac.ucl.panda.crawling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;


public class WebCrawlerThread extends Thread 
{
  protected WebCrawlerMain threadController;
  protected WebAddress currentAddress;
  private HTMLParserListener parserListener; // each thread should have its own HTMLParserListener, so as to avoid concurrency issues
  
  private String outputFilename;
  private String outputFilePath;
  protected StringWriter uncompressedOutputWriter;
  protected BufferedWriter fileWriter;
  private File outputFile;
  
  
  // constructor:
  public WebCrawlerThread( WebCrawlerMain threadController )
  {
    this.threadController = threadController;
    this.parserListener = new HTMLParserListener(this);
  }
  
  @Override
  public void run()
  {
    while ( ( currentAddress = threadController.URLsToVisit.pull() ) != null )
    {
      /*
       * Only visit a URL if it hasn't already been visited:
       * (URLsToVisit won't contain any URLs that are deeper than the
       * maximum depth, as such URLs are not stored in URLsToVisit)
       */
      if ( ! threadController.visitedURLs.contains(currentAddress) )
      {
        threadController.visitedURLs.put(currentAddress); // mark the current URL as visited now, just in case it cannot be reached (and will thus be ignored)
        
        try
        {
          // also log this visitation:
          threadController.logFileWriter.write( currentAddress.depth + "\t" + currentAddress.URL );
          threadController.logFileWriter.newLine();
          
          getLinksFromHTML( getHTMLFromURL(currentAddress) );
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  private String getHTMLFromURL( WebAddress webAddress ) throws Exception
  {
    HttpURLConnection connection;
    String URLString = webAddress.URL;
    StringBuilder returnString = new StringBuilder( Constants.ESTIMATED_SIZE_OF_WEBPAGE );
    
    try
    {
      connection = (HttpURLConnection) ( new URL(URLString).openConnection() );
      connection.setConnectTimeout( Constants.CONNECTION_TIMEOUT );
      connection.setReadTimeout( Constants.CONNECTION_TIMEOUT );
      connection.setRequestProperty( "User-agent", "crawler" );
      HttpURLConnection.setFollowRedirects(true);
      connection.connect();
      
      if ( connection.getResponseCode() != HttpURLConnection.HTTP_OK )
      {
        return null;
      }
      
      Object content = connection.getContent();
      
      // to allow some time for the website to actually be downloaded:
      Thread.sleep( Constants.WEBSITE_DOWNLOAD_MAXIMUM_TIME );
      
      // to ensure that the link being retrieved is a HTML website (as opposed to, for example, a a binary file linked from a web-page):
      if ( content instanceof java.io.InputStream )
      {
        BufferedReader contentReader = new BufferedReader( new InputStreamReader( (InputStream) content ) );
        
        String nextLineRead;
        while ( contentReader.ready() )
        {
          nextLineRead = contentReader.readLine();
          returnString.append( nextLineRead );
          returnString.append( System.getProperty("line.separator") );
        }
      }
      else
      {
        connection.disconnect();
        return null;
      }
    }
    // silently ignore these exceptions:
    catch ( MalformedURLException e ){ return null; } // the parser may well pick up some things which are not links; these should just be ignored
    catch ( SocketTimeoutException e ) { return null; }
    catch ( IOException e ) { return null; }
    
    connection.disconnect();
    return returnString.toString();
  }
  
  private void getLinksFromHTML( String rawHTML ) throws Exception
  {
    // if "null" was given, then this means that the website could not be retrieved as HTML, and so nothing else can be done:
    if ( rawHTML == null ) { return; }
    
    outputFilename = convertURLtoFilename( currentAddress.URL ) + Constants.OUTPUT_FILE_SUFFIX;
    outputFilePath = threadController.outputDirectory + outputFilename;
    
    outputFile = new File( outputFilePath );
    outputFile.delete();
    fileWriter = new BufferedWriter( new FileWriter( outputFile ) );
    
    // prepare the file:
    fileWriter.write( "<DOC>" ); fileWriter.newLine(); fileWriter.newLine();
    fileWriter.write( "<DOCNO> " + currentAddress.URL + " </DOCNO>" ); fileWriter.newLine(); fileWriter.newLine();
    fileWriter.write( "<TEXT>" ); fileWriter.newLine();
    
    StringReader rawHTMLReader = new StringReader(rawHTML);
    new ParserDelegator().parse(rawHTMLReader, parserListener, true);
    rawHTMLReader.close();
    
    fileWriter.newLine(); fileWriter.newLine();
    fileWriter.write( "</TEXT>" ); fileWriter.newLine(); fileWriter.newLine();
    fileWriter.write( "</DOC>" ); fileWriter.newLine();
    fileWriter.close();
  }
  
  void storeLink( String address )
  {
    // if we're already at the maximum depth, then we don't need to go any deeper:
    if ( currentAddress.depth == threadController.maximumDepth ) { return; }
    
    address = normaliseURL( normaliseLink( address, currentAddress.URL ) );
    
    // add the web address if it's not in the URLs to visit, and if it hasn't been already visited:
    WebAddress newWebAddress = new WebAddress( address, currentAddress.depth + 1 );
    if
    (
      ( ! threadController.URLsToVisit.contains( newWebAddress ) ) &&
      ( ! threadController.visitedURLs.contains( newWebAddress ) )
    )
    {
      if ( ! threadController.spanHosts )
      {
        if ( sameHosts( threadController.startingAddress, newWebAddress.URL ) )
        {
          threadController.URLsToVisit.put( newWebAddress );
        }
      }
      else
      {
        threadController.URLsToVisit.put( newWebAddress );
      }
    }
  }
  
  /*
   * Normalise the representation of the URL given.
   * Also extract the root and current level web addresses from the URL given.
   */
  String normaliseURL( String URL )
  {
    // "www.google.com" -> "http://www.google.com":
    if ( ! URL.startsWith(Constants.HTTP_PREFIX) )
    {
      URL = Constants.HTTP_PREFIX + URL;
    }
    
    // "http://www.google.com" -> "http://www.google.com/"
    if ( URL.indexOf('/', Constants.HTTP_LENGTH) == -1 )
    {
      URL = URL + "/";
    }
    
    return URL;
  }
  
  String normaliseLink( String linkAddress, String baseAddress )
  {
    // for absolute paths without a DNS name (i.e. without "http://www.google.com"):
    if ( linkAddress.startsWith("/") )
    {
      linkAddress = getRootWebAddress(baseAddress) + linkAddress;
    }
    // for relative paths without a DNS name (i.e. without "http://www.google.com"):
    else if ( ! linkAddress.startsWith(Constants.HTTP_PREFIX) )
    {
      linkAddress = getCurrentLevelWebAddress(baseAddress) + linkAddress;
    }
    
    // for removing placeholders within webpages (e.g. ...index.html#section):
    if ( linkAddress.contains("#") )
    {
      linkAddress = linkAddress.substring( 0, linkAddress.indexOf("#") );
    }
    
    return linkAddress;
  }
  
  String getRootWebAddress(String URL)
  {
    URL = normaliseURL(URL);
    
    // "http://www.google.com/search/searcher/index.html" -> "http://www.google.com"
    if ( URL.substring( Constants.HTTP_LENGTH ).contains("/") )
    {
      URL = URL.substring( 0, URL.indexOf('/', Constants.HTTP_LENGTH) );
    }
    // otherwise, nothing needs to be done:
    
    return URL;
  }
  
  String getCurrentLevelWebAddress(String URL)
  {
    URL = normaliseURL(URL);
    
    // "http://www.google.com/search/searcher/index.html" -> "http://www.google.com/search/searcher"
    if ( URL.substring( Constants.HTTP_LENGTH ).contains("/") )
    {
      URL = URL.substring( 0, URL.lastIndexOf('/') + 1 );
    }
    // remove the trailing slash:
    if ( URL.endsWith( "/" ) )
    {
      URL = URL.substring( 0, URL.length() -1 );
    }
    // otherwise, nothing needs to be done:
    
    return URL;
  }
  
  String getHostname(String URL)
  {
    URL = getRootWebAddress(URL);
    URL = URL.substring( Constants.HTTP_LENGTH );
    
    return URL;
  }
  
  boolean sameHosts( String URL1, String URL2 )
  {
    if ( getHostname(URL1).equals( getHostname(URL2) ) ) { return true; }
    else { return false; }
  }
  
  /*
   * Returns true if the character is in:
   * 0-9, A-Z, a-z,
   * and returns false otherwise.
   * 
   * Single characters in Java are eight-bit UTF-8 characters.
   */
  private boolean charValidInFilename(char c)
  {
    if ( c >= 0x30 && c <= 0x39 ) { return true; }
    if ( c >= 0x41 && c <= 0x5A ) { return true; }
    if ( c >= 0x61 && c <= 0x7A ) { return true; }
    
    return false;
  }
  
  private String convertURLtoFilename( String inputURL )
  {
    StringBuilder returnString = new StringBuilder( inputURL.length() );
    char[] inputURLCharArray = inputURL.toCharArray();
    
    for ( char c : inputURLCharArray )
    {
      if ( charValidInFilename(c) )
      {
        returnString.append(c);
      }
      else
      {
        returnString.append( Constants.FILENAME_SUBSTITUTION_CHAR );
      }
    }
    
    return returnString.toString();
  }
  
}


class HTMLParserListener extends HTMLEditorKit.ParserCallback
{
  
  WebCrawlerThread thread;
  
  // constructor:
  public HTMLParserListener( WebCrawlerThread thread )
  {
    this.thread = thread;
  }
  
  public void handleText( char[] data, int position )
  {
    try
    {
      if ( noDisallowedChars(data) )
      {
        /*thread.uncompressedOutputWriter.write( data );
        thread.uncompressedOutputWriter.write( System.getProperty( "line.separator" ) );*/
        
        thread.fileWriter.write( data );
        thread.fileWriter.newLine();
      }
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
  }
  
  public void handleComment( char[] data, int position ) {}
  
  public void handleStartTag( HTML.Tag tag, MutableAttributeSet attributes, int position )
  {
    handleTag(tag, attributes);
  }
  
  public void handleEndTag( HTML.Tag tag, int position ) {}
  
  public void handleSimpleTag( HTML.Tag tag, MutableAttributeSet attributes, int position )
  {
    handleTag(tag, attributes);
  }
  
  public void handleError( String errorMsg, int position ) {}
  
  /*
   * For handling both ordinary and simple tags:
   */
  private void handleTag( HTML.Tag tag, MutableAttributeSet attributes )
  {
    if
    (
      ( tag.equals( HTML.Tag.A ) ) &&
      ( attributes.isDefined( HTML.Attribute.HREF ) )
    )
    {
      thread.storeLink( (String) attributes.getAttribute( HTML.Attribute.HREF ) );
    }
  }
  
  private boolean noDisallowedChars( char[] input )
  {
    for ( char currentChar : input )
    {
      for ( char currentDisallowedChar : Constants.DISALLOWED_CHARS )
      {
        if ( currentChar == currentDisallowedChar ) { return false; }
      }
    }
    
    return true;
  }
  
}
