/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ucl.panda;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import uk.ac.ucl.panda.indexing.TrecIndex;
import uk.ac.ucl.panda.retrieval.TrecRetrieval;
import uk.ac.ucl.panda.utility.io.*;
import uk.ac.ucl.panda.crawling.*;

/**
 * 
 * @author xxms
 */
public class Panda {

	// Paths
	private final String INDEX = "../Index/";
	private final String DATA  = "../Data/Documents/";
	
	//Adjust with full server paths if changing server
	
	//Tom's URLS
	private final static String PANDA_ETC  = System.getProperty("panda.etc", "C:/wamp/www/UCLWebSearch/etc/");
	private final static String PANDA_VAR  = System.getProperty("panda.var", "C:/wamp/www/UCLWebSearch/var/");
	private final static String PANDA_HOME = System.getProperty("panda.home", "C:/wamp/www/UCLWebSearch/");
	
	//Change to your URLS and comment mine out
	//private final static String PANDA_ETC  = System.getProperty("panda.etc", "C:/wamp/www/UCLWebSearch/etc/");
	//private final static String PANDA_VAR  = System.getProperty("panda.var", "C:/wamp/www/UCLWebSearch/var/");
	//private final static String PANDA_HOME = System.getProperty("panda.home", "C:/wamp/www/UCLWebSearch/");
	
	protected String newline = System.getProperty("line.separator");

	protected String fileseparator = System.getProperty("file.separator");

	protected BufferedReader buf = null;
	/** The unkown option */
	protected String unknownOption;

	/** Specifies whether a help message is printed */
	protected boolean printHelp;

	/** Specified whether a version message is printed */
	protected boolean printVersion;

	/** Specifies whether to index a collection */
	protected boolean indexing;
	
	/** Specifies whether to crawl*/
	protected boolean crawling;
	/**
	 * Specifies whether to perform trec_eval like evaluation.
	 */
	protected boolean evaluation;

	/**
	 * Specifies batch.
	 */
	protected boolean batch;

	/**
	 * Specifies var and mean.
	 */
	protected boolean variance;

	protected boolean plot;
	
	

	protected void version() {
		System.out.println("LuceneTrec version: 1.0 alpha");
		// System.out.println("Built on ");
	}

	/**
	 * Prints a help message that explains the possible options.
	 */
	protected void usage() {
		System.out.println("LuceneTrec version: 1.0 alpha");
		System.out.println("usage: java LuceneTrec [flags in any order]");
		System.out.println("");
		System.out.println("  -h --help		print this message");
		System.out.println("  -V --version	 print version information");
		System.out.println("  -i --index	   index a collection");
		System.out.println("  -b --batch	retrieve for batch");
		System.out.println("  -c --crawl -[depth] -[spanHosts] -[numberOfThreads] -[baseURL] -[outputDirectory]	crawl the given webspace");
		System.out.println("  -e --evaluate	evaluates the results");
		System.out.println("  -v --var   get var and mean for each query");
		System.out.println("				   var/results with the specified qrels file");
		System.out.println("				   in the file etc/trec.qrels");
		System.out.println("");
		System.out.println("If invoked with \'-i\', then both the direct and");
		System.out
		.println("inverted files are build, unless it is specified which");
		System.out.println("of the structures to build.");
		/*
		 * System.out.println("  -d --direct	  creates the direct file");
		 * System.out.println(
		 * "  -v --inverted	creates the inverted file, from an already existing direct"
		 * );System.out.println(
		 * "  -j --ifile	   creates the inverted file, from scratch, single pass"
		 * );System.out.println(
		 * "  -l --langmodel   creates additional structures for language modelling"
		 * ); System.out.println("");System.out.println(
		 * "If invoked with \'-r\', there are the following options.");
		 * System.out
		 * .println("  -c value		 parameter value for term frequency normalisation."
		 * );System.out.println(
		 * "				   If it is not specified, then the default value for each");
		 * System.out.println(
		 * "				   weighting model is used, eg PL2 => c=1, BM25 b=> 0.75");
		 * System.out.println("  -q --queryexpand applies query expansion");
		 * System.out.println("  -l --langmodel   applies language modelling");
		 * System.out.println("");System.out.println(
		 * "If invoked with \'-e\', there is the following options.");
		 * System.out.println(
		 * "  -p --perquery	reports the average precision for each query separately."
		 * );System.out.println(
		 * "  -n --named		evaluates for the named-page finding task.");
		 * System.out
		 * .println("  filename.res	 restrict evaluation to filename.res only."
		 * ); System.out.println("");System.out.println(
		 * "If invoked with one of the following options, then the contents of the "
		 * );System.out.println(
		 * "corresponding data structure are shown in the standard output.");
		 * System
		 * .out.println("  --printdocid	 prints the contents of the document index"
		 * );System.out.println(
		 * "  --printlexicon   prints the contents of the lexicon");
		 * System.out.println
		 * ("  --printinverted  prints the contents of the inverted file");
		 * System
		 * .out.println("  --printdirect	prints the contents of the direct file"
		 * );System.out.println(
		 * "  --printstats	 prints statistics about the indexed collection");
		 */
	}

	/**
	 * set config files
	 */

	private static void setproperties(Properties appProp) {
		appProp.setProperty("panda.home", PANDA_HOME);
		appProp.setProperty("panda.var", PANDA_VAR);
		appProp.setProperty("panda.etc", PANDA_ETC);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, Exception {
		Properties appProp = new Properties();
		setproperties(appProp);
		try {
			Panda panda = new Panda();
			int status = panda.processOptions(args);
			panda.applyOptions(status, appProp);
		} catch (java.lang.OutOfMemoryError oome) {
			System.err.println(oome);
			oome.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Processes the command line arguments and sets the corresponding
	 * properties accordingly.
	 * 
	 * @param args
	 *            the command line arguments.
	 * @return int zero if the command line arguments are processed
	 *         successfully, otherwise it returns an error code.
	 */
	protected int processOptions(String[] args) {
		if (args.length == 0)
			return ERROR_NO_ARGUMENTS;

		if(args[0].equals("-c") || args[0].equals("--crawl") ){
			try{
			new Crawler(args[1],args[2],args[3],args[4], args[5]);}
			catch(Exception e){e.printStackTrace();}
		}
		
		int pos = 0;
		while (pos < args.length) {
			if (args[pos].equals("-h") || args[pos].equals("--help"))
				printHelp = true;

			else if (args[pos].equals("-i") || args[pos].equals("--index"))
				indexing = true;

			else if (args[pos].equals("-v") || args[pos].equals("--var"))
				variance = true;

			else if (args[pos].equals("-e") || args[pos].equals("--evaluate")) {
				evaluation = true;
			} else if (args[pos].equals("-b") || args[pos].equals("--batch")) {
				batch = true;
			} else if (args[pos].equals("-p") || args[pos].equals("--plot")) {
				plot = true;
			} else if (args[pos].equals("-c")) {
				crawling = true;
			} 

			else {
				unknownOption = args[pos];
				return ERROR_UNKNOWN_OPTION;
			}
			pos++;
		}

		return ARGUMENTS_OK;
	}

	/**
	 * Calls the required classes from Terrier.
	 */
	public void run(Properties appProp) throws IOException, Exception {
		if (printVersion) {
			version();
			return;
		}
		if (printHelp) {
			usage();
			return;
		}
		long startTime = System.currentTimeMillis();
		if (batch) {
			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "IndexDir.config");
			String index = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Topics.config");
			String topics = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Qrels.config");
			String qrels = buf.readLine();
			// System.out.println(index);
			// System.out.println(topics);
			// System.out.println(qrels);
			TrecRetrieval trecsearch = new TrecRetrieval();
			trecsearch.batch(index, topics, qrels, appProp
					.getProperty("panda.var"));
		}
		else if (evaluation) {
			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "IndexDir.config");
			String index = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Topics.config");
			String topics = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Qrels.config");
			String qrels = buf.readLine();

			// System.out.println(index);
			// System.out.println(topics);
			// System.out.println(qrels);

			TrecRetrieval trecsearch = new TrecRetrieval();
			trecsearch.process(index, topics, qrels, appProp
					.getProperty("panda.var"));
		} else if (variance) {
			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "IndexDir.config");
			String index = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Topics.config");
			String topics = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Qrels.config");
			String qrels = buf.readLine();

			// System.out.println(index);
			// System.out.println(topics);:q!
			// System.out.println(qrels);

			TrecRetrieval trecsearch = new TrecRetrieval();
			trecsearch.process_var(index, topics, qrels, appProp
					.getProperty("panda.var"));


		} else if (variance) {
			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "IndexDir.config");
			String index = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Topics.config");
			String topics = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Qrels.config");
			String qrels = buf.readLine();

			// System.out.println(index);
			// System.out.println(topics);
			// System.out.println(qrels);

			TrecRetrieval trecsearch = new TrecRetrieval();
			trecsearch.process_var(index, topics, qrels, appProp
					.getProperty("panda.var"));

		} else if (plot) {
			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "IndexDir.config");
			String index = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Topics.config");
			String topics = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "Qrels.config");
			String qrels = buf.readLine();

			// System.out.println(index);
			// System.out.println(topics);
			// System.out.println(qrels);

			TrecRetrieval trecsearch = new TrecRetrieval();
			trecsearch.process_plot(index, topics, qrels, appProp
					.getProperty("panda.var"));

		} else if (indexing) {

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "IndexDir.config");
                        String index = buf.readLine();

			buf = FileReader.openFileReader(appProp.getProperty("panda.etc")
					+ fileseparator + "DataDir.config");
                        String data = buf.readLine();
			TrecIndex trecindex = new TrecIndex();
			// System.out.println(appProp);
			// System.s(0);
			//trecindex.pocess(INDEX, DATA, appProp);
                        trecindex.pocess(index, data, appProp);
		}

		long endTime = System.currentTimeMillis();
		System.err.println("Time elapsed: " + (endTime - startTime) / 1000.0d
				+ " seconds.");
	}

	public void applyOptions(int status, Properties appProp)
	throws IOException, Exception {
		switch (status) {
		case ERROR_NO_ARGUMENTS:
			usage();
			break;
		case ERROR_NO_C_VALUE:
			System.err
			.println("A value for the term frequency normalisation parameter");
			System.err
			.println("is required. Please specify it with the option '-c value'");
			break;
		case ERROR_CONFLICTING_ARGUMENTS:
			System.err
			.println("There is a conclict between the specified options. For example,");
			System.err
			.println("option '-c' is used only in conjuction with option '-r'.");
			System.err
			.println("In addition, options '-v' or '-d' are used only in conjuction");
			System.err.println("with option '-i'");
			break;
		case ERROR_DIRECT_FILE_EXISTS:
			System.err
			.println("Trying to build a new direct file, while there exists a previous");

			break;
		case ERROR_DIRECT_FILE_NOT_EXISTS:
			System.err
			.println("Trying to build an inverted file, while there is no direct file.");
			break;
		case ERROR_INVERTED_FILE_EXISTS:
			System.err
			.println("Trying to build a new inverted file, while there exists a previous. Please delete the .if file in the index directory.");
			System.err.println("inverted file.");
			break;
		case ERROR_PRINT_DOCINDEX_FILE_NOT_EXISTS:
			System.err
			.println("The specified document index file does not exist.");
			break;
		case ERROR_PRINT_INVERTED_FILE_NOT_EXISTS:
			System.err.println("The specified inverted index does not exist.");
			break;
		case ERROR_PRINT_DIRECT_FILE_NOT_EXISTS:
			System.err.println("The specified direct index does not exist.");
			break;
		case ERROR_UNKNOWN_OPTION:
			System.err.println("The option '" + unknownOption
					+ "' is not recognised");
			break;
		case ERROR_DIRECT_NOT_INDEXING:
			System.err
			.println("The option '-d' or '--direct' can be used only while indexing with option '-i'.");
			break;
		case ERROR_INVERTED_NOT_INDEXING:
			System.err
			.println("The option '-i' or '--inverted' can be used only while indexing with option '-i'.");
			break;
		case ERROR_EXPAND_NOT_RETRIEVE:
			System.err
			.println("The option '-q' or '--queryexpand' can be used only while retrieving with option '-r'.");
			break;
		case ERROR_LANGUAGEMODEL_NOT_RETRIEVE:
			System.err
			.println("The option '-l' or '--langmodel' can be used only while retrieving with option '-r'.");
			break;
		case ERROR_GIVEN_C_NOT_RETRIEVING:
			System.err
			.println("A value for the parameter c can be specified only while retrieving with option '-r'.");
			break;
		case ARGUMENTS_OK:
		default:
			run(appProp);
		}
	}

	protected static final int ARGUMENTS_OK = 0;
	protected static final int ERROR_NO_ARGUMENTS = 1;
	protected static final int ERROR_NO_C_VALUE = 2;
	protected static final int ERROR_CONFLICTING_ARGUMENTS = 3;
	protected static final int ERROR_DIRECT_FILE_EXISTS = 4;
	protected static final int ERROR_INVERTED_FILE_EXISTS = 5;
	protected static final int ERROR_DIRECT_FILE_NOT_EXISTS = 6;
	protected static final int ERROR_PRINT_DOCINDEX_FILE_NOT_EXISTS = 7;
	protected static final int ERROR_PRINT_LEXICON_FILE_NOT_EXISTS = 8;
	protected static final int ERROR_PRINT_INVERTED_FILE_NOT_EXISTS = 9;
	protected static final int ERROR_PRINT_STATS_FILE_NOT_EXISTS = 10;
	protected static final int ERROR_PRINT_DIRECT_FILE_NOT_EXISTS = 11;
	protected static final int ERROR_UNKNOWN_OPTION = 12;
	protected static final int ERROR_DIRECT_NOT_INDEXING = 13;
	protected static final int ERROR_INVERTED_NOT_INDEXING = 14;
	protected static final int ERROR_EXPAND_NOT_RETRIEVE = 15;
	protected static final int ERROR_GIVEN_C_NOT_RETRIEVING = 16;
	protected static final int ERROR_LANGUAGEMODEL_NOT_RETRIEVE = 17;

}
