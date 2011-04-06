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
public class TFIDF implements  Model {

        /* tf -> term frequency, df-> document frequency, idf-> inverse document frequency
    DL-> document length, avgDL-> average document length, DocNum-> number of documents in the collection,
         * CL-> collection length, CTF->  term frequency in the collection
    */
  	@Override
        // if tf > 0 getscore will be called to compute document score for the given term.
	public double getscore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF) {
                tf = tf/DL;
		double score = tf * idf; // basic tf*idf function
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
	public double getVSMscore(Vector query, HashMap TermVector)
  {
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
