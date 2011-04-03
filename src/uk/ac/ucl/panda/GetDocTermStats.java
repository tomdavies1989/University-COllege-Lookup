/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ucl.panda;

import java.io.IOException;
import java.util.HashMap;
import uk.ac.ucl.panda.indexing.io.IndexReader;
import uk.ac.ucl.panda.retrieval.Searcher;
import uk.ac.ucl.panda.utility.io.DocNameExtractor;
import uk.ac.ucl.panda.utility.structure.TermFreqVector;

/**
 *
 * @author administrator
 */
public class GetDocTermStats {
    protected String docDataField1 = "title";
    protected String docDataField2 = "body";
    boolean type = true;
    int totalWords = 0;
     public String cindex;
     public IndexReader rdr;
     public GetDocTermStats(String index) throws IOException,  ClassNotFoundException
     {
         cindex = index;
         rdr = IndexReader.open(cindex);
     }
     public HashMap GetDocLevelStats(String docFileName) throws IOException, ClassNotFoundException {
        IndexReader rdr = IndexReader.open(cindex);
        Searcher search = new Searcher(cindex);
        DocNameExtractor xt = new DocNameExtractor("docname");
        HashMap termstats = new HashMap();
        totalWords = 0;
            for (int j = 0; j < rdr.maxDoc(); j++) {
                String docName = xt.docName(search, j);
             //   if (ent.containsKey(docName1) == true)
                  if (docName.equals(docFileName) == true)
                  {
                        termstats.clear();
                        int docid = j;
                        if (rdr.isDeleted(docid)) {
                            return null;
                        }
                        TermFreqVector tTerms = null;
                        TermFreqVector bTerms = null;
                        tTerms = rdr.getTermFreqVector(docid, docDataField1);
                        bTerms = rdr.getTermFreqVector(docid, docDataField2);
                        if (tTerms != null) {
                            if (type == true) {
                                String Atterms[] = tTerms.getTerms();
                                int AtFreq[] = tTerms.getTermFrequencies();
                                for (int i = 0; i < Atterms.length; i++) {
                                    String id = Atterms[i];
                                    termstats.put(id, AtFreq[i]);
                                    totalWords += AtFreq[i];
                                }
                            }
                        }
                        if (bTerms != null) {
                            if (type == true) {
                                String Abterms[] = bTerms.getTerms();
                                int AbFreq[] = bTerms.getTermFrequencies();
                                for (int i = 0; i < Abterms.length; i++) {
                                     String id =  Abterms[i];
                                        if (termstats.containsKey(id)) {
                                      //  int updateScore = (Integer) ( (Integer) termstats.get(id) + AbFreq[i]);
                                        termstats.put(id, (Integer) ( (Integer) termstats.get(id) + AbFreq[i]));
                                        totalWords += AbFreq[i];
                                    } else {
                                        //eprop.put(Abterms[i], AbFreq[i]);
                                        termstats.put(id, AbFreq[i]);
                                        totalWords += AbFreq[i];
                                    }
                                }
                            }
                        }
              }
       }
      return termstats;
    }



      public HashMap GetDocLevelStats(int docid) throws IOException, ClassNotFoundException {
    //    IndexReader rdr = IndexReader.open(cindex);
         HashMap termstats = new HashMap();
                        if (rdr.isDeleted(docid)) {
                            return null;
                        }
                        TermFreqVector tTerms = null;
                        TermFreqVector bTerms = null;
                        tTerms = rdr.getTermFreqVector(docid, docDataField1);
                        bTerms = rdr.getTermFreqVector(docid, docDataField2);
                        if (tTerms != null) {
                            if (type == true) {
                                String Atterms[] = tTerms.getTerms();
                                int AtFreq[] = tTerms.getTermFrequencies();
                                for (int i = 0; i < Atterms.length; i++) {
                                    String id = Atterms[i];
                                    termstats.put(id, AtFreq[i]);
                                    totalWords += AtFreq[i];
                                }
                            }
                        }
                        if (bTerms != null) {
                            if (type == true) {
                                String Abterms[] = bTerms.getTerms();
                                int AbFreq[] = bTerms.getTermFrequencies();
                                for (int i = 0; i < Abterms.length; i++) {
                                     String id =  Abterms[i];
                                        if (termstats.containsKey(id)) {
                                      //  int updateScore = (Integer) ( (Integer) termstats.get(id) + AbFreq[i]);
                                        termstats.put(id, (Integer) ( (Integer) termstats.get(id) + AbFreq[i]));
                                        totalWords += AbFreq[i];
                                    } else {
                                        //eprop.put(Abterms[i], AbFreq[i]);
                                        termstats.put(id, AbFreq[i]);
                                        totalWords += AbFreq[i];
                                    }
                                }
                            }
                        }
      return termstats;
    }




}
