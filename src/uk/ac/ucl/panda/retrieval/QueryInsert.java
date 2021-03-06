package uk.ac.ucl.panda.retrieval;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class QueryInsert {
	
	private String searchterm;
	
	public QueryInsert(String searchterm){
		this.searchterm = searchterm;
	}
	
	public  void insertQuery(){
		try
		{
			FileWriter fstream = new FileWriter("Data/topics.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			String output = "<top>\n<num> Number: 401\n<title> " 
				+ searchterm +
				"\n<desc> Description: \n<narr> Narrative: \n</top>";
			
			out.write(output);
			out.close();
			System.out.println(searchterm + " was correctly added");
		}
		catch(Exception e) {e.printStackTrace();}
	}

}
