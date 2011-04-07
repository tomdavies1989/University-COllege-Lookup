package uk.ac.ucl.panda.crawling;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.ListIterator;


public class WebCrawlerMain
{
  public final String startingAddress; // this is stored as a String because Strings are immutable
  public final int maximumDepth; // the maximum number of links from the root URL to delve into.
  public final int numberOfThreads; // the number of web-crawling threads that this class should spawn.
  public final String outputDirectory; // the directory where the retrieved and parsed websites should be outputted to.
  
  /*
   * Whether the crawler will follow links to different hosts or not.
   * (e.g. a link from www.cs.ucl.ac.uk to www.google.com will only be followed if
   * spanHosts is set to true).
   */
  final boolean spanHosts;
  
  LinkedHashBucket<WebAddress> visitedURLs; // to prevent doubling-back and re-visiting a URL:
  LinkedHashBucket<WebAddress> URLsToVisit; // a list of pending URLs
  
  // the logfile:
  BufferedWriter logFileWriter;
  
  // to keep track of the spawned threads:
  private LinkedList<WebCrawlerThread> threads = new LinkedList<WebCrawlerThread>();
  
  
  // constructor:
  public WebCrawlerMain( int maximumDepth, boolean spanHosts, int numberOfThreads, String startingAddress , String outputDirectory) throws Exception
  {
    this.maximumDepth = maximumDepth;
    this.spanHosts = spanHosts; // whether to crawl across different hostnames or not
    this.numberOfThreads = numberOfThreads;
    
    // ensuring that the output directory has a trailing file separator:
    if ( outputDirectory.endsWith( System.getProperty("file.separator") ) ) { this.outputDirectory = outputDirectory; }
    else { this.outputDirectory = outputDirectory + System.getProperty("file.separator"); }
    
    // testing the validity of the output directory:
    File outputDirFile = new File(outputDirectory);
    if ( ! outputDirFile.exists() )
    {
      throw new Exception("Output directory given does not exist. Path given was: " + outputDirectory);
    }
    if ( ! outputDirFile.isDirectory() )
    {
      throw new Exception("Output directory path given is not a directory. Path given was: " + outputDirectory);
    }
    
    // cleaning out the output directory:
    for ( File outputDirExistingFile : outputDirFile.listFiles() )
    {
      if
      (
        outputDirExistingFile.getName().startsWith( Constants.OUTPUT_FILE_PREFIX ) &&
        outputDirExistingFile.getName().endsWith( Constants.OUTPUT_FILE_SUFFIX )
      )
      {
        outputDirExistingFile.delete();
      }
    }
    
    // Linked-lists and Hashmaps are used both because they're both O(1) fast for most operations:
    visitedURLs = new LinkedHashBucket<WebAddress>( Constants.URL_LISTS_INITIAL_CAPACITY );
    URLsToVisit = new LinkedHashBucket<WebAddress>( Constants.URL_LISTS_INITIAL_CAPACITY );
    
    // set up the log file:
    File logFile = new File("log.txt");
    logFile.delete();
    logFileWriter = new BufferedWriter( new FileWriter( logFile ) );
    
    // create the threads (but don't run them yet):
    for ( int i = 0; i < numberOfThreads; i++ )
    {
      threads.add( new WebCrawlerThread( this ) );
    }
    
    this.startingAddress = threads.getFirst().normaliseURL( startingAddress );
    
    // add the root URL to the list of URLs to be visited to start off:
    URLsToVisit.put( new WebAddress( this.startingAddress, 0 ) );
  }
  
  
  public void startCrawling() throws InterruptedException
  {
    for ( WebCrawlerThread thread : threads )
    {
      thread.start();
      /*
       * Starting the threads all at the same time will just cause all but one of them to die immediately,
       * as only one of them will find the base URL and fetch it, whereas the others will find the list of 
       * pending URLs empty, and finish.
       */
      Thread.sleep( Constants.THREAD_START_INTERVAL );
    }
    
    int deadThreads; // number of dead threads
    int stuckThreads; // number of interrupted threads (i.e. that are in the wait() state)
    
    /*
     * Keep resurrecting dead threads unless all of them have died.
     * If all of the threads have died, then this indicates that there
     * are no more pending URLs to visit.
     */
    do
    {
      deadThreads = 0;
      stuckThreads = 0;
      ListIterator<WebCrawlerThread> threadsIterator = threads.listIterator();
      
      WebCrawlerThread thread;
      while( threadsIterator.hasNext() )
      {
        thread = threadsIterator.next();
        
        if ( ! thread.isAlive() )
        {
          deadThreads++;
          threadsIterator.remove();
          thread = new WebCrawlerThread(this);
          threadsIterator.add( thread );
        }
        
        if ( thread.isInterrupted() ) { stuckThreads++; }
      }
      
      /*
       * Only start the new threads after they've all been re-created:
       * (otherwise, an infinite loop is possible if threads die immediately after they're started
       * due to no pending URLs to visit)
       */
      for ( WebCrawlerThread currentThread : threads )
      {
        if ( ! currentThread.isAlive() )
        {
          currentThread.start();
        }
      }
      
      System.out.print( (threads.size() - deadThreads) + " threads still running\t\t");
      System.out.print( stuckThreads + " interrupted\t\t" );
      System.out.print( visitedURLs.size() + " URLs visited\t\t" );
      System.out.print( URLsToVisit.size() + " pending URLs.\r\n" );
      
      Thread.sleep( Constants.THREAD_RESURRECTION_POLLING_INTERVAL * numberOfThreads );
    }
    while ( deadThreads < numberOfThreads );
  }
  
  public void closeLogFile() throws Exception
  {
    /*
     * Ensure that the log file is only closed after all of the threads
     * have terminated:
     */
    for ( WebCrawlerThread thread : threads )
    {
      thread.join();
    }
    
    logFileWriter.close();
  }
}
