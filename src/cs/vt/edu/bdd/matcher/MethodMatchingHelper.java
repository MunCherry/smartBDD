package cs.vt.edu.bdd.matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.nlp.StringHelper;
import cs.vt.edu.bdd.nlp.Token;
import cs.vt.edu.bdd.nlp.TypeOfClause;

public class MethodMatchingHelper {

	/**
	 * compute the probability that if a given method is matched with
	 * a given clause
	 * @param method
	 * @param clause
	 * @param ratio: correction ratio of matching two single words, its
	 * defaul value is 100
	 * @return
	 */
	public int computeMatching(Method method, Clause clause, Integer ratio){
		if (ratio == null)
			ratio = new Integer(100);
		
		String methodName = method.getName();
		String[] methodWords = StringHelper.seperateWords(methodName);
		List<Integer> mapList= mapMethodNameToClause(methodWords, clause, ratio);

		int xName = calculateMethodNameXVal(mapList.size(), methodWords.length);
		int yPos = calculateMethodPosYValue(mapList, clause);

		List<Token> possibleParams = clause.getParams();
		
		//We make the number of arguments check based on the type of clause
		int zParam = calculateMethodParamZVal(method, possibleParams, clause.getTypeOfClause());


		int rawVal = metricFunction(xName, yPos, zParam);

		//TODO: Scaling might not be a good solution.
		/*
		// convert raw value to scaled value
		int matchingVal;
		if (rawVal>90) {
			matchingVal = ProbabilisticMatchOrder.ALL_WORDS_EXACT_MATCH.getValue();
		} else if (rawVal>25) {
			matchingVal = ProbabilisticMatchOrder.SUBSET_WORDS_EXACT_MATCH.getValue();
		} else {
			matchingVal = ProbabilisticMatchOrder.NO_MATCH.getValue();
		}

		return matchingVal;
		*/
		
		return rawVal;
	}

	/**
	 * compute the matching value based on 3 information
	 * variables, name matching: x; POS matching: y; 
	 * number of parameters: z
	 * @param xName
	 * @param yPOS
	 * @param zParam
	 * @return
	 */
	private int metricFunction(int xName, int yPOS, int zParam){			
		if (xName >= 90 && zParam >= 90) {
			return 100;
		} else if (xName >= 90){
			return 90; 
		}
		else if (xName >= 70){
			if (yPOS > 80 || zParam > 80)
				return 70;
			else
				return 50;
		} else if (xName >= 50){
			return 25;
		} else
			return 0;
	}

	/**
	 * match method name to clause and keep 
	 * the list of index of matched tokens in the 
	 * clause 
	 * @param methodWords
	 * @param clause
	 * @param ratio
	 * @return
	 */
	private List<Integer> mapMethodNameToClause(String[] methodWords, Clause clause, int ratio){			
		//List<Token> tokens = clause.getTokens();
		List<Token> tokens = clause.getImportantTokens();
		List<Integer> tokenMatchedList = new ArrayList<Integer>();			
		for (int i=0; i<methodWords.length; i++){
			for (int j=0; j<tokens.size(); j++){
				if (StringHelper.isMatched(methodWords[i], tokens.get(j).getTokenWord(), ratio)){
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
	private int calculateMethodNameXVal(int matchedCount, int methodWordCount){
		
		int xVal = 0;
		if(matchedCount > 0)
			//xVal = (matchedCount * 100) / methodWordCount;
			xVal = 100;
		return xVal;
	}

	/**
	 * compute y value based on POS information, i.e.,
	 * if method name matches some tokens of the clause,
	 * then those matched tokens play critical roles in
	 * terms of grammatical relationship (usually verbs) in the clause.
	 * y value is in [0,100], verbs valued more than nouns
	 * and others POS because we are matching methods that imply
	 * action (verb)
	 * @param tokenMatchedIndices
	 * @param clause
	 * @return
	 */
	private int calculateMethodPosYValue(List<Integer> tokenMatchedIndices, Clause clause){
		int verbCount = 0;
		int nounCount = 0;			
		int verbVal = 100;
		int nounVal = 50;

		for (int i=0; i<tokenMatchedIndices.size(); i++){
			int index = tokenMatchedIndices.get(i);
			if (clause.getImportantTokens().get(index).isVerb()){
				verbCount ++;
			} else if (clause.getImportantTokens().get(index).isNoun()){
				nounCount ++;
			}
		}

		if (verbCount == 1)
			return 100;

		int yVal = 0;
		if((verbCount + nounCount) > 0)
			yVal = (verbCount*verbVal + nounCount*nounVal) / (verbCount + nounCount);

		return yVal;
	}

	/**
	 * compute z value based on the parameters information
	 * if the number of params of the method equalize to
	 * the number of detected params of the clause, then
	 * y is set to 100, otherwise y is set to 0
	 * @param actualParams
	 * @param possibleParams
	 * @return
	 */
	private int calculateMethodParamZVal(Method method, List<Token> possibleParams, TypeOfClause typeOfClause){
		
		int zVal = 0;
		Class<?>[] actualParams = method.getParameterTypes();
		
		if(typeOfClause.getValue() == TypeOfClause.THEN.getValue()) {
			
			if(method.getReturnType().equals(Boolean.TYPE)) {
				//If boolean one parameter is okay if user passes "true" or "false"
				if (actualParams.length == possibleParams.size() ||
						actualParams.length == (possibleParams.size() - 1) ){
					zVal = 100;
				} else {
					zVal = 0;
				}
			}
			else {
				if (actualParams.length == (possibleParams.size() - 1) ){
					zVal = 100;
				} else {
					zVal = 0;
				}
			}
			
			
		}
		else {
			if (actualParams.length == possibleParams.size()){
				zVal = 100;
			} else {
				zVal = 0;
			}
		}
		return zVal;
	}
}
