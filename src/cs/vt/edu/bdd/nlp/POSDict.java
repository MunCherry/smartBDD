package cs.vt.edu.bdd.nlp;

import java.util.Dictionary;
import java.util.Hashtable;

final public class POSDict {
	
	private Dictionary<String, String> PosDict = new Hashtable<String, String>();
	
	public POSDict (){
		
	}
	
	public static String[] keys = {"CC", "CD", "DT", "EX", "FW", "IN", "JJ" , "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP", 
			"NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD",
			"VBG", "VBN", "VBP", "VBZ", "WDT", "WP", "WP$", "WRB"};
	
	public static String[] values = {"Coordinating conjunction", "Cardinal number", "Determiner", "Existential there", "Foreign word",
			"Preposition or subordinating conjunction", "Adjective", "Adjective, comparative", "Adjective, superlative",
			"List item marker", "Modal", "Noun, singular or mass", "Noun, plural", "Proper noun, singular",
			"Proper noun, plural", "Predeterminer", "Possessive ending", "Personal pronoun", "Possessive pronoun",
			"Adverb", "Adverb, comparative", "Adverb, superlative", "Particle", "Symbol", "to", "Interjection",
			"Verb, base form", "Verb, past tense", "Verb, gerund or present participle", "Verb, past participle",
			"Verb, non�3rd person singular present", "Verb, 3rd person singular present", "Wh�determiner", "Wh�pronoun",
			"Possessive wh�pronoun", "Wh�adverb"};
	
	/**
	 * Unimportant POS
	 */
	
	public static String[] unusedValues = {"Determiner"};
	public static String[] unusedTags = {"DT", "VBZ"};
	
	/**
	 * Nouns	
	 */
	public static String[] nounValues = {"Noun, singular or mass", "Noun, plural", "Proper noun, singular",
		"Proper noun, plural", "Personal pronoun", "Possessive pronoun"};
	
	public static String[] nounTags = {"NN", "NNS", "NNP",
		"NNPS", "PRP", "PRP$"};
	
	/**
	 * Verbs
	 */
	public static String[] verbValues = {"Verb, base form", "Verb, past tense", "Verb, gerund or present participle", 
		"Verb, past participle", "Verb, non-3rd person singular present", "Verb, 3rd person singular present"};
	
	public static String[] verbTags = {"VB", "VBD", "VBG", "VBN", "VBP", "VBZ"};
	
	public Dictionary<String, String> getPosTagDic(){
		PosDict = new Hashtable<String, String>();
		for (int i=0; i<keys.length; i++){
			PosDict.put(keys[i], values[i]);
		}
		return PosDict;
	}

}