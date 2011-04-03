package uk.ac.ucl.panda.retrieval.models;

import java.util.HashMap;
import java.util.Vector;

/**
 * Provides a single point of access to all models.
 */
public class ModelParser implements Model {

	int model;
	Model algorithm;

	/**
	 * Initialises ModelParser as the model specified in the parameters.
	 * 
	 * @param modelType The type of model to be used.
	 */
	public ModelParser(int modelType) {
		
		this.model = modelType;
		// Add your models below , please use 1 for VSM
		if (model == 0) {
			algorithm = new TFIDF();
		}
		else if (model == 2){
			algorithm = new BM25();
		}
	}

        /* tf -> term frequency, df-> document frequency for the term, idf-> inverse document frequency, DL-> document length,
          avgDL-> average document length in the collection
         *,
        */
	@Override
	public double getscore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF) {
		return algorithm.getscore(tf, df, idf, DL, avgDL, DocNum, CL, CTF);
	}

        @Override
	public double getVSMscore(Vector query, HashMap TermVector) {
		return algorithm.getVSMscore(query, TermVector);
	}
	@Override
	public double defaultScore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF) {
		return algorithm.defaultScore(tf, df, idf, DL, avgDL, DocNum, CL, CTF);
	}

	@Override
	public double defaultScore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return algorithm.defaultScore(tf, df, idf, DL, avgDL, DocNum, CL, CTF, a);
	}
	
	@Override
	public double getscore(double tf, double df, double idf, double DL, double avgDL,
			int DocNum, double CL, int CTF, double a) {
		return algorithm.getscore(tf, df, idf, DL, avgDL, DocNum, CL, CTF, a);
	}

	@Override
	public double getvar(double tf, double df, double idf, double DL, double avgDL,
			int DocNum, double CL, int CTF, double a) {
		return algorithm.getvar(tf, df, idf, DL, avgDL, DocNum, CL, CTF, a);
	}

	@Override
	public double getmean(double tf, double df, double idf, double DL, double avgDL,
			int DocNum, double CL, int CTF, double a) {
		return algorithm.getmean(tf, df, idf, DL, avgDL, DocNum, CL, CTF, a);
	}

	@Override
	public double defaultvar(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return algorithm.defaultvar(tf, df, idf, DL, avgDL, DocNum, CL, CTF, a);
	}

	@Override
	public double defaultmean(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a) {
		return algorithm.defaultmean(tf, df, idf, DL, avgDL, DocNum, CL, CTF, a);
	}
}
