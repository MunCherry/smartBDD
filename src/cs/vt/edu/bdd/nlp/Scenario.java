package cs.vt.edu.bdd.nlp;

//import java.util.List;

public class Scenario {
	
	//private String author;
	//private List<String> dates;
	//private String status;
	
	private String plainText;
	private String title;
	
	private Paragraph given, when, then;
	
	public Scenario (String text) {
		plainText = text;
	}
	
	public Scenario (){
		
	}
	
	public void setPlainText(String text){
		plainText = text;
	}
	
	public String getPlainText(){
		return plainText;
	}
	
	public String geTitle (){
		return title;
	}
	
	public void setTitle (String _title){
		title = _title;
	}
	
	public Paragraph getGivenContent (){
		return given;
	}
	
	public void setGivenContent (Paragraph _given){
		given = _given;
	}
	
	public Paragraph getWhenContent (){
		return when;
	}
	
	public void setWhenContent (Paragraph _when){
		when = _when;
	}
	
	public Paragraph getThenContent (){
		return then;
	}
	
	public void setThenContent (Paragraph _then){
		then = _then;
	}
	
	public String toPlainTextWithPOSTags(){
		String text = "";
		text += title + "\n\n";
		text += Keyword.GIVEN + " : " + given.toPlainTextWithPOSTags() + "\n\n";
		text += Keyword.WHEN + " : " + when.toPlainTextWithPOSTags() + "\n\n";
		text += Keyword.THEN + " : " + then.toPlainTextWithPOSTags();
		return text;
	}
	
}
