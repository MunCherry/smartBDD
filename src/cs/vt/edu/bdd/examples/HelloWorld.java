package cs.vt.edu.bdd.examples;

public class HelloWorld {
	
	//TODO: The scenario needs more than one word. Fix it.
	
	public HelloWorld() {
		value = "";
	}
	
	public HelloWorld(String val1, String val2) {
		value = val1;
	}
	
	private String value = null;
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean compare(String strForComparison) {
		
		return value.equals(strForComparison);
	
	}
	
}
