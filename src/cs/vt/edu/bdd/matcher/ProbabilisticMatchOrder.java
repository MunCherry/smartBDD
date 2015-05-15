package cs.vt.edu.bdd.matcher;

/**
 * This enum represents the order in which we are going
 * to process the matcher, with associate probabilistic values. 
 *
 * @author sunilkamalakar
 *
 */
public enum ProbabilisticMatchOrder {
	
	ALL_WORDS_EXACT_MATCH(100),
	ALL_WORDS_SYNONYMS_MATCH(90),
	SUBSET_WORDS_EXACT_MATCH(75),
	SUBSET_WORDS_SYNONYMS_MATCH(60),
	NO_MATCH(0);
	
	//Represents the value associated with the enum.
	private int value;
	
	private ProbabilisticMatchOrder(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
		
}
