package cs.vt.edu.bdd.matcher;

import java.util.ArrayList;
import java.util.List;

import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.nlp.StringHelper;
import cs.vt.edu.bdd.nlp.Token;
import cs.vt.edu.bdd.reflection.ClassInfoHolder;
import cs.vt.edu.bdd.utility.BDDUtility;

public class ClassMatchingHelper {
	
	public int computeMatching (ClassInfoHolder clazz, Clause clause, Integer ratio){
		if (ratio == null)
			ratio = new Integer(100); // default value of ratio is 100% single word match
		String className = BDDUtility.extractClassNameFromPackageName(clazz.getName());
		String[] classNameWords = StringHelper.seperateWords(className);
		
		List<Integer> mapList= mapClassNameToClause(classNameWords, clause, ratio);
		
		int xName = calculateClassNameXVal(mapList.size(), classNameWords.length);
		int yPos = calculateClassPosYValue(mapList, clause);
		
		int rawVal = metricFunction(xName, yPos);
		
		//TODO: Scaling might not be a good solution.
		/*
		// convert raw value to scaled value
		int matchingVal;
		if (rawVal >= 90) {
			matchingVal = ProbabilisticMatchOrder.ALL_WORDS_EXACT_MATCH.getValue();
		} else if (rawVal>50) {
			matchingVal = ProbabilisticMatchOrder.SUBSET_WORDS_EXACT_MATCH.getValue();
		} else {
			matchingVal = ProbabilisticMatchOrder.NO_MATCH.getValue();
		}

		return matchingVal;
		*/
		return rawVal;
	}
	
	/**
	 * match class name to clause and return 
	 * the list of index of matched tokens in the clause 
	 * @param methodWords
	 * @param clause
	 * @param ratio
	 * @return
	 */
	private List<Integer> mapClassNameToClause(String[] classWords, Clause clause, int ratio){			
		//List<Token> tokens = clause.getTokens();
		List<Token> tokens = clause.getImportantTokens();
		List<Integer> tokenMatchedList = new ArrayList<Integer>();			
		for (int i=0; i<classWords.length; i++){
			for (int j=0; j<tokens.size(); j++){
				if (StringHelper.isMatched(classWords[i], tokens.get(j).getTokenWord(), ratio)){
					tokenMatchedList.add(j);
					break;
				}
			}
		}	
		return tokenMatchedList;
	}
	
	/**
	 * compute x value: 0<=x<=100
	 * x = 100 means method name completely match the clause 
	 * @param map
	 * @param methodWords
	 * @return
	 */
	private int calculateClassNameXVal(int matchedCount, int classWordCount){
		int xVal = (matchedCount * 100) / classWordCount;
		return xVal;
	}
	
	/**
	 * compute y-value based on if the class name
	 * matchs any Noun
	 * @param tokenMatchedIndices
	 * @param clause
	 * @return
	 */
	private int calculateClassPosYValue(List<Integer> tokenMatchedIndices, Clause clause){		
		int nounCount = 0;
		
		for (int i=0; i<tokenMatchedIndices.size(); i++){
			int index = tokenMatchedIndices.get(i);
			if (clause.getImportantTokens().get(index).isNoun()){
				nounCount ++;
			}
		}	

		switch (nounCount){			
			case 1: return 100;
			case 2: return 75;
			case 3: return 50;
			default: return 0;		
		}
	}
	
	
	/**
	 * compute the matching value based on 2 information
	 * variables, name matching: x; POS matching: y; 
	 * @param xName
	 * @param yPOS
	 * @return
	 */
	private int metricFunction(int xName, int yPOS){			
		if (xName > 90) {
			return 100;
		} else if (xName >= 70){
			if (yPOS > 80)
				return 100;
			else
				return 75;
		} else if (xName >= 50){
			return 50;
		} else
			return 0;
	}
}
