package cs.vt.edu.bdd.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clause {

	TypeOfClause typeOfClause = null;

	private List<Token> tokens = null;
	
	private List<Token> importantTokens = null;

	public Clause(TypeOfClause typeOfClause){
		this.typeOfClause = typeOfClause;		
	}

	public Clause(List<Token> _tokens){
		tokens = _tokens;		
	}

	public void setTokens(List<Token> _tokens) {
		tokens = _tokens;
	}

	public List<Token> getTokens(){
		return tokens;
	}

	public TypeOfClause getTypeOfClause() {
		return typeOfClause;
	}

	public String toPlainText(){
		String text = "";		
		for (int i=0; i<tokens.size(); i++){
			text += tokens.get(i).getTokenWord() + " ";
		}

		return text;
	}
	
	public String importantTokensToString(){
		String text = "";		
		for (int i=0; i<importantTokens.size(); i++){
			text += importantTokens.get(i).getTokenWord() + " ";
		}

		return text;
	}

	public String toTextWithPOSTags(){
		String textwPos = "";
		for(int i=0; i<tokens.size(); i++){
			textwPos += tokens.get(i).getTokenWord() + "[" + 
					tokens.get(i).getTokenPOS() + "]" + " ";
		}
		return textwPos;
	}

	public void addToken(Token token){
		if (tokens != null) {
			tokens.add(token);
		} else {
			tokens = new ArrayList<Token>();
			tokens.add(token);
		}
	}

	public void NumberChunker(){

	}
	/**
	 * List all nouns of the clause
	 * @return
	 */
	public List<Token> GetNouns(){
		List<Token> result = new ArrayList<Token>();		
		if (tokens == null)
			return null;

		for (int i=0; i<tokens.size(); i++){
			if (tokens.get(i).isNoun()){
				result.add(tokens.get(i));
			}
		}

		return result;
	}

	/**
	 * get all verbs in the clause
	 * @return
	 */
	public List<Token> GetVerbs(){
		List<Token> result = new ArrayList<Token>();		
		if (tokens == null)
			return null;

		for (int i=0; i<tokens.size(); i++){
			if (tokens.get(i).isVerb()){
				result.add(tokens.get(i));
			}
		}

		return result;
	}

	public List<Token> getParams(){
		List<Token> params = new ArrayList<Token>();

		if (tokens == null){
			return null;
		}

		for (int i=0; i<tokens.size(); i++){
			if (tokens.get(i).isParam()){
				params.add(tokens.get(i));
			}
		}

		return params;
	}

	public List<String> getParameters() {
		//create a list
		List<String> result = new ArrayList<String>();

		String input = toPlainText();
		//test string
		//String input = "Here \"just\" one \"67\" \"comillas\"";

		//create a pattern to match
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(input);

		//find all matches
		while (m.find()) {
			result.add(m.group(1));
		}

		//return the list of values
		return result;
	}

	public boolean containsNot() {
		return toPlainText().contains("NOT");
	}

	public String toPlainTextWithouQuotes(){
		String text = "";               
		for (int i=0; i<tokens.size(); i++){
			text += tokens.get(i).getTokenWord() + " ";
		}

		return text.replace("\"", "\\\"");
	}
	
	/**
	 * filter out unimportant tokens
	 * @return
	 */
//	public List<Token> filter(){
//		List<Token> importantTokens = new ArrayList<Token>();
//		for (Token token:tokens){
//			if (!token.isUnused()){
//				importantTokens.add(token);
//			}			
//		}		
//		return importantTokens;
//	}
	
	/**
	 * filter out unimportant tokens
	 * @return
	 */
	public void filterOut(){
		importantTokens = new ArrayList<Token>();
		for (Token token : tokens){
			if (!token.isUnimportant()){				
				importantTokens.add(token);
			}		
		}		
	}
	
	public List<Token> getImportantTokens(){
		return importantTokens;
	}

}
