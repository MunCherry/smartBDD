package cs.vt.edu.bdd.nlp;

import java.util.List;

public class Paragraph {
	
	private String plainText;	
	private List<String> logicOperators;
	private List<Clause> clauses;
	
	public Paragraph(){
		
	}
	
	public Paragraph(String text){
		plainText = text;
	}
	
	public void setPlainText(String text){
		plainText = text;
	}
	
	public String getPlainText(){
		return plainText;
	}
	
	public int getNumberOfClauses(){
		if (clauses != null)
			return clauses.size();
		
		return -1;
	} 
	
	public int getNumberOfOperators(){
		if (logicOperators != null)
			return logicOperators.size()-1;
		
		return -1;
	}
	
	public List<Clause> getClauses(){
		return clauses;
	}
	
	public void setClauses(List<Clause> _clauses){
		clauses = _clauses;		
	}
	
	public List<String> getLogicOperators(){		
		return logicOperators;
	}
	
	public void setLogicOperators(List<String> operators){
		logicOperators = operators;
		logicOperators.add(null);
	}
	
	public String toPlainText(){
		String text = "";
		if (this != null) {
			for (int i=0; i<clauses.size(); i++){
				text += clauses.get(i).toPlainText() + "\n" + 
						logicOperators.get(i) + " ";
			}
			
		}
		
		return text;
	}
	
	public String toPlainTextWithPOSTags(){
		String text = "";
		if (this != null) {
			for (int i=0; i<clauses.size(); i++){
				text += clauses.get(i).toTextWithPOSTags() + "\n" +
						logicOperators.get(i) + " ";
			}
		}
		return text;
	}

}
