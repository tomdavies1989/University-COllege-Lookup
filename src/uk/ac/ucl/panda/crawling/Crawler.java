package uk.ac.ucl.panda.crawling;

public class Crawler
{
  private static int depth;
  private static boolean spanHosts;
  private static int numberOfThreads;
  private static String baseURL;
  private static String outputDirectory;
  
  /*
   * Arguments:
   * args[0] : --depth=[N|inf]
   * args[1] : --span-hosts=[y|n]
   * args[2] : --number-of-threads=N
   * args[3] : the base URL to crawl through
   * args[4] : the directory to output the parsed webpages to
   */
  
  public Crawler(String depthin, String spanHostsin, String numberOfThreadsin, String baseURLin, String outputDirectoryin) throws Exception
  {
	  String[] arguments = new String[5];
	  arguments[0] = depthin;
	  arguments[1] = spanHostsin;
	  arguments[2] = numberOfThreadsin;
	  arguments[3] = baseURLin;
	  arguments[4] = outputDirectoryin;
	  parseArguments(arguments);
  }
  
 /* public static void main(String[] args) throws Exception
  {
    parseArguments(args);
    
    WebCrawlerMain webCrawler = new WebCrawlerMain(depth, spanHosts, numberOfThreads, baseURL, outputDirectory);
    webCrawler.startCrawling();
    webCrawler.closeLogFile();
  }*/
  
  private static void parseArguments(String[] args) throws Exception
  {
    String depthString = args[0].substring( Constants.DEPTH_ARGUMENT.length() );
    if ( depthString.compareToIgnoreCase( Constants.INFINITE_DEPTH_KEYWORD ) == 0 )
    {
      depth = Integer.MAX_VALUE;
    }
    else
    {
      depth = Integer.parseInt( depthString );
    }
    
    args[1] = args[1].substring( Constants.SPAN_HOSTS_ARGUMENT.length() );
    if ( args[1].compareToIgnoreCase( "y" ) == 0 )
    {
      spanHosts = true;
    }
    if ( args[1].compareToIgnoreCase( "n" ) == 0 )
    {
      spanHosts = false;
    }
    
    numberOfThreads = Integer.parseInt( args[2].substring( Constants.NUMBER_OF_THREADS_ARGUMENT.length() ) );
    
    baseURL = args[3];
    outputDirectory = args[4];
    
    WebCrawlerMain webCrawler = new WebCrawlerMain(depth, spanHosts, numberOfThreads, baseURL, outputDirectory);
    webCrawler.startCrawling();
    webCrawler.closeLogFile();
  }
  
}
