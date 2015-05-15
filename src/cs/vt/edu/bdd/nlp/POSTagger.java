package cs.vt.edu.bdd.nlp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;


public class POSTagger {	
	static private String relativePathToModel="/resources/models/en-pos-maxent.bin";
	private POSTaggerME tagger; 
	
	/**
	 * Load the trained POS model for later tagging
	 */
	private void LoadModel(){
		try{
			
			IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
			String root= workspace.getRoot().getLocation().toFile().getPath().toString();			
			
			
			//String root = "/Users/sunilkamalakar/Desktop/MittelmanLab/workspace/BDD-Project";

			
			String pathToModel = root + relativePathToModel;
			File file = new File(pathToModel);		
			POSModel model = new POSModelLoader().load(file);				
			tagger = new POSTaggerME(model);
		} catch(Exception e){
			System.err.println(e.getMessage());
		}
		

	}
	
	public POSTagger(){		
		LoadModel();
	}
	
	/**
	 * 
	 * @param text
	 * @return List of tokens each of which is a pair of a work (token) and its POS
	 * 
	 */
	public List<Token> ToTokensWithPOSTag(String text){
		String[] words = WhitespaceTokenizer.INSTANCE.tokenize(text);
		String[] tags = tagger.tag(words);

		List<Token> tokens = new ArrayList<Token>();

		for (int i=0; i<words.length; i++){
			Token tk = new Token(words[i], tags[i]);
			tokens.add(tk);
		}
		return tokens;
	}
	
	public List<Token> ToTokensWithPOSTag2(String[] words){		
		String[] tags = tagger.tag(words);

		List<Token> tokens = new ArrayList<Token>();

		for (int i=0; i<words.length; i++){
			Token tk = new Token(words[i], tags[i]);
			tokens.add(tk);
		}
		return tokens;
	}
	

	/**
	 * POS tag many lines of text and use performance monitor
	 * to how fast the tagging.
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public List<Token> ToTokensWithPOSTag(List<String> text) throws IOException{		
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");		
		List<Token> tokens = new ArrayList<Token>();
		perfMon.start();
		
		for (String line : text){
			String[] words = WhitespaceTokenizer.INSTANCE.tokenize(line);
			String[] tags = tagger.tag(words);
			for (int i=0; i<words.length; i++){
				Token tk = new Token(words[i], tags[i]);
				tokens.add(tk);
			}
			perfMon.incrementCounter();
		}		
		perfMon.stopAndPrintFinalResult();
		
		return tokens;		
	}		
	/**
	 * Tag a sentence that is an array of strings (words)
	 * @param sent
	 * @return
	 */
	public String[] TagsForSentence(String[] sent){				
		String[] tags = tagger.tag(sent);
		return tags;
	}
}
