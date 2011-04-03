package uk.ac.ucl.panda.retrieval.models;

import java.util.HashMap;
import java.util.Vector;

/**
 * The interface that all models must inherit and implement.
 */
public interface Model {

	double defaultScore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF);

	double getscore(double tf, double df, double idf, double DL, double avgDL,
			int DocNum, double CL, int CTF);
       
	double getVSMscore(Vector query, HashMap TermVector);

	double defaultScore(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a);

	double getscore(double tf, double df, double idf, double DL, double avgDL,
			int DocNum, double CL, int CTF, double a);

	double getmean(double tf, double df, double idf, double DL, double avgDL,
			int DocNum, double CL, int CTF, double a);

	double getvar(double tf, double df, double idf, double DL, double avgDL,
			int DocNum, double CL, int CTF, double a);

	double defaultmean(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a);

	double defaultvar(double tf, double df, double idf, double DL,
			double avgDL, int DocNum, double CL, int CTF, double a);
}
