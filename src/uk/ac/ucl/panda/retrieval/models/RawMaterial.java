/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ucl.panda.retrieval.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import uk.ac.ucl.panda.GetDocTermStats;

import uk.ac.ucl.panda.indexing.ExtraInformation;
import uk.ac.ucl.panda.indexing.io.IndexReader;
import uk.ac.ucl.panda.retrieval.query.Query;
import uk.ac.ucl.panda.utility.structure.Normalise;
import uk.ac.ucl.panda.utility.structure.Term;
import uk.ac.ucl.panda.utility.structure.TermDocs;
import uk.ac.ucl.panda.utility.structure.TermPositions;

/**
 *
 * @author xxms
 */
public class RawMaterial {

    private IndexReader reader;
  private TermDocs termDocs;
  private double tf;
  private double df;
  private double idf;
  private double DL;
  private double CL;
  private double aveDL;
  private int DocNum;
 private String index;
 private int doc;
 private byte[] norms;
  private final int[] docs = new int[32];         // buffered doc numbers
  private final int[] freqs = new int[32];        // buffered term freqs
  private int pointer;
  private int pointerMax;
  ModelParser model;

  public int getDocNum(){
  
    return DocNum;
  
  }


    public RawMaterial(String index, int m) throws FileNotFoundException, IOException, ClassNotFoundException {
        this.index = index;
        this.reader=IndexReader.open(index);
        this.aveDL=ExtraInformation.ReadAvgDL(index);
        this.CL = ExtraInformation.ReadCL(index);
        this.model=new ModelParser(m);
    }

    public boolean getDocSet(Term term, HashMap c) throws IOException{
        termDocs = reader.termDocs(term);

        if (termDocs == null){
            return false;
        }

        next();

    while (doc < Integer.MAX_VALUE) {                           // for docs in window
      int f = freqs[pointer];
  
        c.put(doc, doc);

      if (++pointer >= pointerMax) {
        pointerMax = termDocs.read(docs, freqs);  // refill buffers
        if (pointerMax != 0) {
          pointer = 0;
        } else {
          termDocs.close();                       // close stream
          doc = Integer.MAX_VALUE;                // set to sentinel value
          return false;
        }
      }
      doc = docs[pointer];
    }


 return true;


    }

/** Returns the current document number matching the query.
   * Initially invalid, until {@link #next()} is called the first time.
   */
  public int doc() { return doc; }


      /** Advances to the next document matching the query.
   * <br>The iterator over the matching documents is buffered using
   * {@link TermDocs#read(int[],int[])}.
   * @return true iff there is another document matching the query.
   */
  public boolean next() throws IOException {
    pointer++;
    if (pointer >= pointerMax) {
      pointerMax = termDocs.read(docs, freqs);    // refill buffer
      if (pointerMax != 0) {
        pointer = 0;
      } else {
        termDocs.close();                         // close stream
        doc = Integer.MAX_VALUE;                  // set to sentinel value
        return false;
      }
    }
    doc = docs[pointer];
    return true;
  }










  /** Returns a string representation of this <code>TermScorer</code>. */
  public String toString() { return ""; }



  public boolean process(Term term, int num,Collection DocList, HashMap queryscore, double a) throws IOException{
    if(a!=0){

    termDocs = reader.termDocs(term);
        HashMap tempscore = new HashMap();
        if (termDocs == null){

            df=idf=tf=DL=0.0d;
            return false;
        }

        norms=reader.norms(term.field());
        next();

        float[] normDecoder = Normalise.getNormDecoder();
    while (doc < Integer.MAX_VALUE) {                           // for docs in window
      int f = freqs[pointer];
      double log2toe = 1.0d / Math.log(2.0d);
      tf=f;
      DL=(normDecoder[norms[doc] & 0xFF]) * 1.0d;
      df=reader.docFreq(term) * 1.0d;
      DocNum = reader.numDocs();
      idf=Math.log(DocNum/df) * log2toe;

      double score = 0;
      //  System.out.println("Term"+reader.terms(term).CTF());
         score = model.getscore(tf, df, idf, DL, aveDL, DocNum, CL, reader.terms(term).CTF(), a);


 // collect score

        tempscore.put(doc,score);



      if (++pointer >= pointerMax) {
        pointerMax = termDocs.read(docs, freqs);  // refill buffers
        if (pointerMax != 0) {
          pointer = 0;
        } else {
          termDocs.close();                       // close stream
          doc = Integer.MAX_VALUE;                // set to sentinel value
          break;
        }
      }
      doc = docs[pointer];
    }


        /**
         * UCL
         * Proof Scan
         */
         double oldscore = 0;
         double defaultScore = 0;
      for   (Iterator   iterator   =   DocList.iterator();   iterator.hasNext();)
  {
      int docNum = (Integer)iterator.next();
      if(tempscore.containsKey(docNum)){
            if(num == 0)
                queryscore.put(docNum, tempscore.get(docNum));
            else{
                oldscore = (Double)queryscore.get(docNum);
                queryscore.put(docNum, oldscore+(Double)tempscore.get(docNum));
            }
      }
      else{

                    defaultScore = model.defaultScore(tf, df, idf, (normDecoder[norms[docNum] & 0xFF]), aveDL, DocNum, CL, reader.terms(term).CTF(), a);
           if(num == 0)
                queryscore.put(docNum, defaultScore);
            else{
                oldscore = (Double)queryscore.get(docNum);
                queryscore.put(docNum, oldscore+defaultScore);
           }

      }
  }

 return true;

  }

   return  process(term, num, DocList, queryscore);

}

  
  //batch
  
  

public boolean process_var(Term term, int num,Collection DocList, HashMap querymean, HashMap queryvar, double a) throws IOException{
    if(a!=0){

    termDocs = reader.termDocs(term);
        HashMap tempmean = new HashMap();
        HashMap tempvar = new HashMap();
        if (termDocs == null){

            df=idf=tf=DL=0.0d;
            return false;
        }

        norms=reader.norms(term.field());
        next();

        float[] normDecoder = Normalise.getNormDecoder();
    while (doc < Integer.MAX_VALUE) {                           // for docs in window
      int f = freqs[pointer];
      double log2toe = 1.0d / Math.log(2.0d);
      tf=f;
      DL=(normDecoder[norms[doc] & 0xFF]) * 1.0d;
      df=reader.docFreq(term) * 1.0d;
      DocNum = reader.numDocs();
      idf=Math.log(DocNum/df) * log2toe;

      double mean = 0;
      double variance = 0;
      //  System.out.println("Term"+reader.terms(term).CTF());
         mean = model.getmean(tf, df, idf, DL, aveDL, DocNum, CL, reader.terms(term).CTF(), a);
         variance = model.getvar(tf, df, idf, DL, aveDL, DocNum, CL, reader.terms(term).CTF(), a);
         // System.out.println (variance);

 // collect mean
      
        tempmean.put(doc,mean);
        tempvar.put(doc,variance);


      if (++pointer >= pointerMax) {
        pointerMax = termDocs.read(docs, freqs);  // refill buffers
        if (pointerMax != 0) {
          pointer = 0;
        } else {
          termDocs.close();                       // close stream
          doc = Integer.MAX_VALUE;                // set to sentinel value
          break;
        }
      }
      doc = docs[pointer];
    }


        /**
         * UCL
         * Proof Scan
         */
         double oldmean = 0;
         double defaultmean = 0;
         double oldvar = 0;
         double defaultvar = 0;
      for   (Iterator   iterator   =   DocList.iterator();   iterator.hasNext();)
  {
      int docNum = (Integer)iterator.next();
      if(tempmean.containsKey(docNum)){
            if(num == 0){
                querymean.put(docNum, tempmean.get(docNum));
                queryvar.put(docNum, tempvar.get(docNum));
            }
             else{
                oldmean = (Double)querymean.get(docNum);
                querymean.put(docNum, oldmean+(Double)tempmean.get(docNum));
                oldvar = (Double)queryvar.get(docNum);
                queryvar.put(docNum, oldvar+(Double)tempvar.get(docNum));

            }
      }
      else{
     
                    defaultmean = model.defaultmean(tf, df, idf, (normDecoder[norms[docNum] & 0xFF]), aveDL, DocNum, CL, reader.terms(term).CTF(), a);
           defaultvar = model.defaultvar(tf, df, idf, (normDecoder[norms[docNum] & 0xFF]), aveDL, DocNum, CL, reader.terms(term).CTF(), a);

      

           if(num == 0){
                querymean.put(docNum, defaultmean);
                queryvar.put(docNum, defaultvar);
           }
            else{
                oldmean = (Double)querymean.get(docNum);
                querymean.put(docNum, oldmean+defaultmean);
                oldvar = (Double)queryvar.get(docNum);
                queryvar.put(docNum, oldvar+defaultvar);
           }

      }
  }

 return true;

  }

   return  process(term, num, DocList, querymean);

}
  
  
  
  
  
  
  
  
  
  
  
  //normal

public boolean process(Term term, int num,Collection DocList, HashMap queryscore) throws IOException{
        termDocs = reader.termDocs(term);
        HashMap tempscore = new HashMap();
        if (termDocs == null){

            df=idf=tf=DL=0.0d;
            return false;
        }

        norms=reader.norms(term.field());
        next();

        float[] normDecoder = Normalise.getNormDecoder();
    while (doc < Integer.MAX_VALUE) {                           // for docs in window
      int f = freqs[pointer];
      double log2toe = 1.0d / Math.log(2.0d);
      tf=f;
      DL=(normDecoder[norms[doc] & 0xFF]) * 1.0d;
      df=reader.docFreq(term) * 1.0d;
      DocNum = reader.numDocs();
      idf=Math.log(DocNum/df) * log2toe;
      
      double score = 0;
    //    System.out.println("Term"+reader.terms(term).CTF());
         score = model.getscore(tf, df, idf, DL, aveDL, DocNum, CL, reader.terms(term).CTF());
    
  

 // collect score
      
        tempscore.put(doc,score);



      if (++pointer >= pointerMax) {
        pointerMax = termDocs.read(docs, freqs);  // refill buffers
        if (pointerMax != 0) {
          pointer = 0;
        } else {
          termDocs.close();                       // close stream
          doc = Integer.MAX_VALUE;                // set to sentinel value
          break;
        }
      }
      doc = docs[pointer];
    }


        /**
         * UCL
         * Proof Scan
         */
        
        //positions
      TermPositions termPos = reader.termPositions(term);
      termPos.skipTo(doc);
      int pos =0;
      int i=termPos.freq();
      while(termPos.doc()==doc&&i>0){
          System.out.println("docno: " + doc+ " pos: "+termPos.nextPosition());
          i--;
      
      
      }
      
      //////////
        
        
         double oldscore = 0;
         double defaultScore = 0;
	 tf = 0;
  for   (Iterator   iterator   =   DocList.iterator();   iterator.hasNext();)
  {
      int docNum = (Integer)iterator.next();
      if(tempscore.containsKey(docNum)){
            if(num == 0)
                queryscore.put(docNum, tempscore.get(docNum));
            else{
                oldscore = (Double)queryscore.get(docNum);
                queryscore.put(docNum, oldscore+(Double)tempscore.get(docNum));
            }
      }
      else{
     
                 defaultScore = model.defaultScore(tf, df, idf, (normDecoder[norms[docNum] & 0xFF]), aveDL, DocNum, CL, reader.terms(term).CTF());
           
       

           if(num == 0)
                queryscore.put(docNum, defaultScore);
            else{
                oldscore = (Double)queryscore.get(docNum);
                queryscore.put(docNum, oldscore+defaultScore);
           }

      }
  }

 return true;

}


public boolean processVM(Query query, Collection DocList, HashMap queryscore, GetDocTermStats tms) throws IOException,ClassNotFoundException
{
  HashMap tempscore = new HashMap();
  df=idf=tf=DL=0.0d;
  Vector query1 = new Vector();
   for(int j =0 ; j< query.getTerm().toArray().length ; j++){
         String term = ((Term)query.getTerm().get(j)).text();
         query1.add(term);
   }
  for (Iterator   iterator   =   DocList.iterator();   iterator.hasNext();)
  {
      int docNum = (Integer)iterator.next();
      HashMap TermVector = tms.GetDocLevelStats(docNum);
      double score = model.getVSMscore(query1, TermVector);
      queryscore.put(docNum, score);
  }
 return true;

}




}





