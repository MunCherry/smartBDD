package cs.vt.edu.bdd.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.WhitespaceTokenizer;

final public class Analyzer {
	
	private POSTagger tagger;
	
	/**
	 * A Scenario raw text typed by user is parsed to an object of Scenario  
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public Scenario TextToScenario (String text){
		if (tagger == null){
			tagger = new POSTagger();
		}
		List<Token> tokens = tagger.ToTokensWithPOSTag(text);
		return TokensToScenario(tokens);
	}
	
	private Scenario TextToScenario2 (String[] text){
		if (tagger == null){
			tagger = new POSTagger();
		}
		List<Token> tokens = tagger.ToTokensWithPOSTag2(text);
		return TokensToScenario(tokens);
	}	
	
	
	/**
	 * 
	 * @param listOfTexts
	 * @return A list of Scenarios
	 */
	public List<Scenario> TextsToScenarios(String listOfTexts){
		
		List<String[]> spStrings = SeperateScenarios(listOfTexts);
		
		List<Scenario> scenarios = new ArrayList<Scenario>();
		for (int i=0; i<spStrings.size(); i++){
			Scenario sce = TextToScenario2(spStrings.get(i));
			scenarios.add(sce);
		}
		return scenarios;
	}	
	
	/**
	 * If text is an array of strings then this method is
	 * used, so that performance can be monitored
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public Scenario TextToScenario (List<String> text) throws IOException{
		if (tagger == null){
			tagger = new POSTagger();
		}		
		List<Token> tokens = tagger.ToTokensWithPOSTag(text);
		return TokensToScenario(tokens);
	}
	
	
	/**
	 * A helper method that takes an input as a list of token and return a 
	 * Scenario object
	 * @Param: tokens
	 * 
	 */
	private static Scenario TokensToScenario (List<Token> tokens) {
		
		int titlePos = 0, givenPos = 0, whenPos = 0, thenPos = 0;
		boolean scenFound = false, givenFound = false, whenFound = false, thenFound = false;
		
		for (int i=0; i<tokens.size(); i++){
		
			String s = tokens.get(i).getTokenWord();
			if (!scenFound && (s.toUpperCase()).compareTo(Keyword.Scenario) == 0){
				titlePos = i;
				scenFound = true;
				continue;
			}
			
			if (!givenFound && s.toUpperCase().compareTo(Keyword.GIVEN) == 0){
				givenPos = i;
				givenFound = true;
				continue;
			}
			
			if (!whenFound && s.toUpperCase().compareTo(Keyword.WHEN) == 0){
				whenPos = i;
				whenFound = true;
				continue;
			}
			
			if (!thenFound && s.toUpperCase().compareTo(Keyword.THEN) == 0){
				thenPos = i;
				thenFound = true;
				continue;
			}
			
		}
		
		if (scenFound && givenFound && whenFound && thenFound){
			Scenario scenario = new Scenario();
			
			String scenTitle = TokensToText(tokens.subList(titlePos, givenPos));
			scenario.setTitle(scenTitle);
			
			List<Token> givenTokens = tokens.subList(givenPos+1, whenPos);
			Paragraph givenPara = TokensToParagraph(givenTokens, TypeOfClause.GIVEN);
			scenario.setGivenContent(givenPara);
			
			List<Token> whenTokens = tokens.subList(whenPos+1, thenPos);
			Paragraph whenPara = TokensToParagraph(whenTokens, TypeOfClause.WHEN);
			scenario.setWhenContent(whenPara);
			
			List<Token> thenTokens = tokens.subList(thenPos+1, tokens.size());
			Paragraph thenPara = TokensToParagraph(thenTokens, TypeOfClause.THEN);
			scenario.setThenContent(thenPara);
			
			return scenario;
		}	
		
		return null;
		
	}
	
	/**
	 * This helper method transforms a list of token to a Paragraph object
	 * @param tokens
	 * @return
	 */
	private static Paragraph TokensToParagraph(List<Token> tokens, TypeOfClause typeOfClause){
		
		Paragraph para = new Paragraph();
		
		List<Clause> clauses = new ArrayList<Clause>();
		List<String> operators = new ArrayList<String>();
		
		Clause cls = new Clause(typeOfClause);	
		for (Token t : tokens){				
			if ((t.getTokenWord().toUpperCase().compareTo(Keyword.AND) == 0) || 
					t.getTokenWord().toUpperCase().compareTo(Keyword.OR) == 0){
				
				operators.add(t.getTokenWord().toUpperCase());
				
				clauses.add(cls);
				
				cls = new Clause(typeOfClause);
				
				continue;
				}
			else {				
				cls.addToken(t);
			} 

		}
		
		clauses.add(cls);
		operators.add("");
		
		// for filtering out 
		
		for (Clause clause: clauses){
			clause.filterOut();
		}
		
		para.setClauses(clauses);
		para.setLogicOperators(operators);
		
		return para;
	}
	
	/**
	 * This helper method convert list of token back to plain text
	 * @param tokens
	 * @return
	 */
	private static String TokensToText(List<Token> tokens){
		String s = "";
		for (int i=0; i<tokens.size(); i++){
			s += tokens.get(i).getTokenWord() + " ";
		}
		return s;
	}
	
	
	private List<String[]> SeperateScenarios(String inputString){		
		//for storing separated scenario raw text
		List<String[]> separatedText = new ArrayList<String[]>();		
		
		String[] words = WhitespaceTokenizer.INSTANCE.tokenize(inputString);
		
		List<String> list_i = new ArrayList<String>();
		
		for (int i=0; i<words.length; i++){		
			if (words[i].toUpperCase().compareTo(Keyword.Scenario) == 0){
				separatedText.add(ListToString(list_i));				
				list_i = new ArrayList<String>();				
			}			
			list_i.add(words[i]);
		}
		
		separatedText.add(ListToString(list_i));
		
		separatedText.remove(0);
		
		return separatedText;
	}
	
	private String[] ListToString(List<String> strings){
		String[] result = new String[strings.size()];
		for (int i=0; i<strings.size(); i++){
			result[i] = strings.get(i);
		}
		return result;
	}
	
	
}
