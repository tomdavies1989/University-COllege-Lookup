package uk.ac.ucl.panda.crawling;

import java.util.HashSet;
import java.util.LinkedList;


/*
 * This class functions as a "bucket" of Objects. Duplicate objects are not allowed, and any attempt
 * to put them in will be silently ignored.
 * 
 * All of this class' operations are O(1) (constant time). 
 * 
 * This class is thread-safe.
 */
public class LinkedHashBucket<E>
{
  /*
   * These two data structures are kept synchronised, and whichever
   * will give the best performance is used for each operation.
   */
  private HashSet<E> hashBucket;
  private LinkedList<E> linkedBucket;
  
  // constructor:
  public LinkedHashBucket()
  {
    hashBucket = new HashSet<E>();
    linkedBucket = new LinkedList<E>();
  }
  
  // constructor:
  public LinkedHashBucket(int initialCapacity)
  {
    hashBucket = new HashSet<E>(initialCapacity);
    linkedBucket = new LinkedList<E>(); // LinkedLists do not need to know an initialCapacity
  }
  
  public synchronized void put(E object)
  {
    if ( ! hashBucket.contains( object ) )
    {
      hashBucket.add(object);
      linkedBucket.add(object);
    }
    // otherwise, silently drop the object
  }
  
  // returns "null" if the bucket is empty:
  public synchronized E pull()
  {
    E returnObject = linkedBucket.poll();
    hashBucket.remove( returnObject );
    return returnObject;
  }
  
  public synchronized boolean contains(E object)
  {
    return hashBucket.contains( object );
  }
  
  public synchronized boolean isEmpty()
  {
    return hashBucket.isEmpty();
  }
  
  public synchronized int size()
  {
    return hashBucket.size();
  }
}
