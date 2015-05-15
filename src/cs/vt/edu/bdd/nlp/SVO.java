package cs.vt.edu.bdd.nlp;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.WhitespaceTokenizer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;


public class SVO {
		 
	private LexicalizedParser lp;
	private String pathToModel = "resources/models/englishPCFG.ser.gz";
	private String[] options = new String[] {"-maxLength", "80", "-retainTmpSubcategories"};
	
	public SVO(){
		initializeParser();
	}
	
	public SVO(String _pathToModel, String[] _options){
		pathToModel = _pathToModel;
		options = _options;
		initializeParser();
	}
		
	private void initializeParser(){
		if (lp != null)
			return;
		lp = LexicalizedParser.loadModel(pathToModel);
		lp.setOptionFlags(options);
	}
	
	/**
	 * analyze a sentence and return its typed dependency list/tree
	 * @param sent
	 * @return
	 */
	public List<TypedDependency> getTDList(String[] sent){
		
		if (lp == null)
			return null;
		
		List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
	    Tree parse = lp.apply(rawWords);
	    
	    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	    
	    return tdl;
	}
	
	public String test(String text){	
		String[] sent = WhitespaceTokenizer.INSTANCE.tokenize(text);
//		List<TypedDependency> list = getTDList(sent);
//		String result = "";
//		
//		for (int i=0; i< list.size(); i++){
//			result += list.get(i).reln().getShortName() + "   ";			
//			result += list.get(i).gov().toString() + "   ";			
//			//result += list.get(i).dep().toString() + "--";
//			result += list.get(i).dep().nodeString() + "--";
//			result += list.get(i).dep().index();
//			//result += list.get(i).dep().pennString();
//			result += "\n";
//		}
//		
//		return result;
		
		return getMainVerbs(sent).toString();
		
	}
	
	public List<String> getSubjects(String[] sent){
		List<TypedDependency> tdList = getTDList(sent);
		
		List<String> subjects = new ArrayList<String>();
		for (int i=0; i<tdList.size(); i++ ){
			if (tdList.get(i).reln().getShortName().compareTo("nsubj")==0){
				subjects.add(tdList.get(i).dep().nodeString());
			}
		}
		
		return subjects;
	}
	
	/**
	 * This method return the main verbs of a sentence.
	 * @param sent
	 * @return
	 */
	public List<String> getMainVerbs(String[] sent){
		List<TypedDependency> tdList = getTDList(sent);
		List<String> verbs = new ArrayList<String>();
		for (TypedDependency type: tdList){
			if (type.reln().getShortName().compareTo("dobj") == 0 ||
					type.reln().getShortName().compareTo("nsubjpass") == 0 ||
					type.reln().getShortName().compareTo("auxpass") == 0 ||
					type.reln().getShortName().compareTo("aux") == 0 ||
					type.reln().getShortName().compareTo("expl") == 0){
				verbs.add(type.gov().nodeString());
			} else if (type.reln().getShortName().compareTo("rcmod") == 0 ||
					type.reln().getShortName().compareTo("root") == 0){
				verbs.add(type.dep().nodeString());
			}
		}
		
		return verbs;
	}

}

