package cs.vt.edu.bdd.generator;

import java.util.List;

import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.nlp.Paragraph;
import cs.vt.edu.bdd.nlp.Scenario;

/**
 * This abstract class represents the code generator which 
 * is the higher abstraction for the actual test case code generation.
 * 
 * @author sunilkamalakar
 *
 */
public abstract class AbstractCodeGenerator {

	/**
	 * Generates the template/class structure associated with the feature/scenarios.
	 * This method should be called from the constructor.
	 * 
	 * @return
	 */
	protected abstract void generateTemplate(String className);
	
	
	/**
	 * Generates the methods that needs to be generated.
	 * 
	 * @param methodName
	 * @param content
	 * @return
	 */
	public abstract void generateMethods(List<Scenario> scenarios);
	
	/**
	 * Private method to internally generate the method that needs to be generated.
	 * 
	 * @param methodName
	 * @param content
	 * @return
	 */
	protected abstract void generateMethod(Scenario scenario);
	
	/**
	 * Generates the test-case content for the Given condition.
	 * @param parsedGivenContext
	 * @return
	 * @throws Exception 
	 */
	protected abstract List<Object> generateGiven(Paragraph given, Object methodBody) throws Exception;
	
	/** 
	 * Generates the content for the When condition.
	 * @param parsedWhenContext
	 * @return
	 */
	protected abstract List<Object> generateWhen(Paragraph when, Object methodBody, List<Object> createdObjects) throws Exception;
	
	/**
	 * Generates the content for the Then condition.
	 * @param parsedThenContext
	 * @return
	 */
	protected abstract void generateThen(Paragraph then, Object methodBody, List<Object> createdGivenObjects, List<Object> createdWhenObjs) throws Exception;
	
	/**
	 * Does the final save on the code that is generated.
	 * This method has to be called at the last after all other code has
	 * been generated.
	 * 
	 * @return
	 */
	public abstract boolean saveCodeToFile();
	
	/**
	 * Start the code generation
	 */
	public abstract void generateTestCode(String featureFile);

}
