package cs.vt.edu.bdd.matcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.reflection.ClassInfoHolder;


/**
 * This Interface acts as the probabilistic matcher engine,
 * which maps the natural language processed using the NLP library
 * with the code implementation.
 * 
 * @author Tung Dao
 */
public interface IProbabilisticMatcher {
	
	/**
	 * This method returns the class of interest for a particular 
	 * clause.
	 * 
	 * @param clause
	 * @return
	 */
	public ClassInfoHolder obtainClassOfInterest(Clause clause);

	/**
	 * This method is used to map a particular clause in an given condition
	 * onto the actual class in the code implementation.
	 * 
	 * TODO: It may accept a different parameter than a String.
	 * 
	 * @return
	 */
	public Map<ClassInfoHolder, Integer> buildClassMatcherStats(List<ClassInfoHolder> classes, Clause clause);
	
	/**
	 * This method is used to build a probabilistic match of the particular
	 * clause with it's actual method implementation. It builds this statistics
	 * based on comparative match with all the 
	 * 
	 * @param clause
	 * @return
	 */
	public Map<Method, Integer> buildMethodMatcherStats(ClassInfoHolder classObj, Clause clause);
	
	/**
	 * This method is used to build the probabilistic match of a particular 
	 * clause in the Given clause to the actual constructor.
	 *  
	 * @param classObj
	 * @param clause
	 * @return
	 */
	public Map<Constructor<?>, Integer> buildConstructorMatcherStats(ClassInfoHolder classObj, Clause clause);
	
}
