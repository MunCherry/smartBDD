package cs.vt.edu.bdd.nlp;

public class Token {	
	private String word;
	private String pos;
	//private String type;
	
	public Token(){
		
	}
	
	public Token(String _word){
		word = _word;
	}
	
	public Token(String _word, String _pos){
		word = _word;
		pos = _pos;
	}
	
	public void setTokenWord(String _word){
		word = _word;
	}
	
	public String getTokenWord(){
		return word;
	}
	
	public void setTokenPOS(String _pos){
		pos = _pos;
	}
	
	public String getTokenPOS(){
		return pos;
	}
	
	public boolean isNoun(){
		if (StringHelper.Find(pos, POSDict.nounTags))
			return true;
		else
			return false;
	}
	
	public boolean isVerb(){
		if (StringHelper.Find(pos, POSDict.verbTags))
			return true;
		else
			return false;
	}
	
	public boolean isParam(){
		if (word.startsWith("\""))
			return true;
		
		return false;
	}
	
	public boolean isUnimportant(){
		if (StringHelper.Find(pos, POSDict.unusedTags)){
			return true;
		} else{
			return false;
		}
			
	}

}
