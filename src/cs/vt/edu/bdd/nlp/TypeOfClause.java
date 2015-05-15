package cs.vt.edu.bdd.nlp;

/**
 * This enum represents the order in which we are going
 * to process the matcher, with associate probabilistic values. 
 *
 * @author sunilkamalakar
 *
 */
public enum TypeOfClause {
	
	GIVEN(1),
	WHEN(2),
	THEN(3),
	OTHER(4);
	
	//Represents the value associated with the enum.
	private int value;
	
	private TypeOfClause(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
		
}
