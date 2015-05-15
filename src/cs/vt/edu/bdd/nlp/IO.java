package cs.vt.edu.bdd.nlp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final public class IO {
	
	static public List<String> readFile (String filePath){
		BufferedReader br = null;
		
		List<String> lines = new ArrayList<String>();
		
		if (filePath == null) return null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(filePath));
 
			while ((sCurrentLine = br.readLine()) != null) {
				lines.add(sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return lines;
	}
	
	public static String FormatText(List<String> lines) {
		String text = "";		
		for (int i=0; i< lines.size(); i++){
			text += lines.get(i) + "\n";
		}
		return text;
	}

}
