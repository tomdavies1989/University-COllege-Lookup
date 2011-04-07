package uk.ac.ucl.panda.crawling;

/*
 * A class to hold all constant values used in the program:
 */
public class Constants
{
  
  // private constructor (to prevent this class from being instantiated, as there's no point):
  private Constants() { }
  
  // arguments for the program itself:
  public static final String DEPTH_ARGUMENT = "--depth=";
  public static final String SPAN_HOSTS_ARGUMENT = "--span-hosts=";
  public static final String NUMBER_OF_THREADS_ARGUMENT = "--number-of-threads=";
  public static final String INFINITE_DEPTH_KEYWORD = "inf";
  
  public static final String HTTP_PREFIX = "http://";
  
  // calculating and storing it once now is faster than re-calculating it each time:
  public static final int HTTP_LENGTH = HTTP_PREFIX.length();
  
  /*
   * How long to wait before starting another thread at startup.
   * 
   * If all of the threads were started at startup, then all but one of them would die,
   * as there is only one URL to get at the very start of parsing.
   */
  public static final long THREAD_START_INTERVAL = 500; // in milliseconds
  
  /*
   * The thread resurrecting loop will wait for the following number of milliseconds
   * multiplied by the maximum number of threads specified to the program.
   * This value should be larger than the value for THREAD_START_INTERVAL.
   */
  public static final long THREAD_RESURRECTION_POLLING_INTERVAL = 600; // in milliseconds, per thread
  
  // the maximum amount of time to wait for a website to be downloaded before beginning to parse it:
  public static final long WEBSITE_DOWNLOAD_MAXIMUM_TIME = 1000; // in milliseconds
  
  public static final int URL_LISTS_INITIAL_CAPACITY = 10000;
  public static final int ESTIMATED_SIZE_OF_WEBPAGE = 10000; // in characters
  
  public static final int CONNECTION_TIMEOUT = 20000; // in milliseconds
  
  // the list of characters which disqualifies a string from being text:
  public static final char[] DISALLOWED_CHARS = { '#', '{', '}', '|', '\\', '<', '>', '~' };
  
  // the prefix and suffix which should be applied to all output files:
  public static final String OUTPUT_FILE_PREFIX = "Panda Crawler output ";
  public static final String OUTPUT_FILE_SUFFIX = ".txt";
  
  // the character to substitute in filenames in place of invalid characters:
  public static final char FILENAME_SUBSTITUTION_CHAR = '_';
  
}
