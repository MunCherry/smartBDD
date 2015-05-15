package cs.vt.edu.bdd.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class ETokenizer {
	/*
	 * 
	 */
	public void Tokenize(String text) {
		try {
			InputStream is = new FileInputStream("Models/en-token.bin");

			TokenizerModel model = new TokenizerModel(is);

			Tokenizer tokenizer = new TokenizerME(model);

			String tokens[] = tokenizer.tokenize(text);

			for (String a : tokens)
				System.out.println(a);

			is.close();
		} catch(InvalidFormatException ie){
			ie.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public String[] TokenizetoString(String text) {
		try {
			InputStream is = new FileInputStream("Models/en-token.bin");

			TokenizerModel model = new TokenizerModel(is);

			Tokenizer tokenizer = new TokenizerME(model);

			String tokens[] = tokenizer.tokenize(text);
			
			for (String a : tokens)
				System.out.println(a);

			is.close();
			
			return tokens;
			
		} catch(InvalidFormatException ie){
			ie.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		return null;
	}
}
