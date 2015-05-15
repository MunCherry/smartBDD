package cs.vt.edu.bdd.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class ESentenceDetector {
	
	/*
	 * Output detected sentences of a input paragraph
	 * @param: paragraph
	 */	
	public void SentenceDetect(String paragraph) {
		
		try {
			// always start with a model, a model is learned from training data
			InputStream is = new FileInputStream("C:\\Users\\Tung\\Desktop\\FALL2012\\6604\\models\\en-sent.bin");			
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
			
			String sentences[] = sdetector.sentDetect(paragraph);
			int lent = sentences.length;
			
			for (int i=0; i < lent; i++) {
				System.out.println(sentences[i]);				
			}
			
			is.close();
			
		} catch (IOException e){
			e.printStackTrace();
		}		
		
	}	
	
	public String SentenceDetectToString(String paragraph) {

		try {
			// always start with a model, a model is learned from training data
			InputStream is = new FileInputStream("Models/en-sent.bin");
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);

			String sentences[] = sdetector.sentDetect(paragraph);
			int lent = sentences.length;

			String result = "";

			for (int i=0; i < lent; i++) {
				result += sentences[i].toString() + "\n";				
			}

			is.close();

			return result;

		} catch (IOException e){
			e.printStackTrace();
		}		

		return null;

	}
}
