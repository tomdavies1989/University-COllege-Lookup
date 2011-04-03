package uk.ac.ucl.panda.crawling;


public class ConcurrentIncrementer
{
  private long value;
  
  // constructor:
  public ConcurrentIncrementer()
  {
    this.value = 0L;
  }
  
  // constructor:
  public ConcurrentIncrementer(long value)
  {
    this.value = value;
  }
  
  public synchronized long getNextValue()
  {
    return ++value;
  }
  
  public synchronized void reset()
  {
    value = 0L;
  }
  
  public synchronized void set(long value)
  {
    this.value = value;
  }
}
