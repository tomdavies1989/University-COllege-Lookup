/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ucl.panda.retrieval.models;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author administrator
 */
public class BM25 implements  Model {

        /* tf -> term frequency, df-> document frequency, idf-> inverse document frequency
    DL-> document length, avgDL-> average document length, DocNum-> number of documents in the collection,
         * CL-> collection length, CTF->  term frequency in the collection
    */
  	@Override
        // if tf > 0 getscore will be called to compute document score for the given term.
	public double getscore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF) {
  		//wikipedia
  		//double top = tf * 3;
  		//double bottom = tf + (2 * (1 - 0.75 + 0.75*(DL/avgDL)));
  		
  		//double score = idf * (top/bottom);

  		//jun's notes
  		int k3 = 2;
  		int k1 = 2;
  		double b = 0.75;
  		
  		double k = k1* ((1-b) + b*(DL/avgDL));
  		
  		double first = ((k3+1)*tf)/(k3+tf);
  		double second = (k1 + 1)* df/(k+df);
  		
  		double score = idf * first * second;
  		
  		return score;
	}

        /* if tf =0, defaultScore function is used to compute the document score.
        i.e default score for the document if the term is not present in the document.*/
	@Override
	public double defaultScore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF) {
		return 0.0d;
	}

        //Implement getVSMscore for vector space model
        //query --> is a query term vector, TermVector --> is a hash with term as key and frequency of the term in the document as value

        @Override
	public double getVSMscore(Vector query, HashMap TermVector) {
		return 0.0;
	}

//Following functions are not needed for Text Retrieval assignment

	@Override
	public double defaultScore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return 0.0d;

	}

	@Override
	public double getscore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return 0.0d;

	}

	@Override
	public double getmean(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {

		return 0.0d;
	}

	@Override
	public double getvar(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return 0.0;
	}

	@Override
	public double defaultmean(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return 0.0d;
	}

	@Override
	public double defaultvar(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return 0.0;
	}


}
