package cs.vt.edu.bdd.nlp;

import java.util.List;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class StringHelper {
	
	/**
	 * find aString if it is in strings.
	 * @param aString
	 * @param strings
	 * @return
	 */
	static public boolean Find(String aString, String[] strings){		
		for (int i=0; i<strings.length; i++){
			if (aString.compareTo(strings[i]) == 0){				
				return true;
			}
		}
		return false;
	}
	
	static public String ListToString(List<String> list) {
		if (list == null)
			return null;
		
		String result = "";
		for (int i=0; i< list.size(); i++){
			result += list.get(i) + " ";
		}
		
		return result.trim();
		
	}
	
	static public String TokensListToString(List<Token> list){
		if (list == null)
			return null;
		
		String result = "";
		
		for (int i=0; i< list.size(); i++){
			result += list.get(i).getTokenWord() + " ";
		}
		
		return result.trim();
	}
	
	public static String[] seperateWordsWithWhitespace(String text){
		return WhitespaceTokenizer.INSTANCE.tokenize(text);
	}
	
	// helping method for matching computation	
	/**
	 * apply common practice Java method naming convention to
	 * detect words in a method or class name
	 * @param name
	 * @return
	 */
	public static String[] seperateWords(String name){
		String words = "";		
		for (int i=0; i<name.length(); i++){
			char c = name.charAt(i);
			if (Character.isUpperCase(c)){
				words += " ";				
			} 
			words += c;
		}
		return seperateWordsWithWhitespace(words);
	}

	/**
	 * check if either word is subsequence of the other
	 * with the correction parameter: ratio that is in
	 * [0, 100]
	 * @param word1
	 * @param word2
	 * @return
	 */
	public static boolean isMatched (String word1, String word2, int ratio){

		// if two words match but their lengths' difference is
		// too large then they are not considered matched. For example,
		// "withdraw" and "a"
		// dif = maxLength - minLength;
		//int acceptableDiffValue = 50;
		
		if (ratio < 0){
			ratio = 0;
		} else if (ratio > 100){
			ratio = 100;
		}

		int searchLen = 0;
		String sourceWord, destWord;

		if (word1.length() > word2.length()){			
			sourceWord = word1;
			destWord = word2;
		} else if (word1.length() < word2.length()) {			
			sourceWord = word2;
			destWord = word1;
		} else {
			sourceWord = word2 + " ";
			destWord = word1;
		}

		searchLen = destWord.length();		

		boolean collapsed = false;
		int coveredRate=0;

		for (int i=searchLen; i>=0; i--){
			if (collapsed){
				break;
			}

			for (int j = 0; j < sourceWord.length()-destWord.length(); j++){
				if (sourceWord.regionMatches(true, j, destWord, 0, i)) {
					collapsed = true;
					coveredRate = (i * 100) /destWord.length();              
					break;
				}
			}
		}
		
		int difValue = sourceWord.length() - destWord.length();
		int acceptableValue = 2 * destWord.length(); 
		
		if (collapsed && (coveredRate>=ratio) && (difValue < acceptableValue)){
			return true;
		}
		
		return false;
	}
}
