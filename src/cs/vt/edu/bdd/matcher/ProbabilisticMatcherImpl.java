
package cs.vt.edu.bdd.matcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.nlp.Scenario;
import cs.vt.edu.bdd.nlp.TypeOfClause;
import cs.vt.edu.bdd.reflection.ClassInfoHolder;
import cs.vt.edu.bdd.reflection.JavaClassDetailsFacade;
import cs.vt.edu.bdd.utility.BDDUtility;

public class ProbabilisticMatcherImpl  implements IProbabilisticMatcher{
	
	//The reflection objects
	private Map<String, ClassInfoHolder> fileVsClassInfo;
	
	//The current scenario 
	private Scenario currentScenario = null;
	
	private List<ClassInfoHolder> createdClassesInGiven = null;

	public ProbabilisticMatcherImpl(String projectPath) {
		JavaClassDetailsFacade facade = new JavaClassDetailsFacade();
		fileVsClassInfo = facade.generateClassInfo(projectPath);
	}

	public void setCurrentScenario(Scenario currentScenario) {
		createdClassesInGiven = new ArrayList<ClassInfoHolder>();
		this.currentScenario = currentScenario;
	}
	
	public void addCreatedClassToGivenList(ClassInfoHolder infoHolder) {
		createdClassesInGiven.add(infoHolder);
	}
	
	public List<ClassInfoHolder> retrieveAllClasses() {
		Collection<ClassInfoHolder> classInfoHolderCollection = fileVsClassInfo.values();
		List<ClassInfoHolder> classInfoHolderList = new ArrayList<ClassInfoHolder>(classInfoHolderCollection);
		return classInfoHolderList;
	}

	/*
	@Override
	public Map<ClassInfoHolder, Integer> buildClassMatcherStats(
			List<ClassInfoHolder> classes, Clause clause) {
		
		Map<ClassInfoHolder, Integer> retVal = new HashMap<ClassInfoHolder, Integer>();
		
		//TODO: Pick the important information from the clause.
		
		//Then iterate through the classes to find the necessary information.
		for( ClassInfoHolder clazz: classes ) {
			
			//Start with the highest probability and continue upon insertion.
			switch(ProbabilisticMatchOrder.ALL_WORDS_EXACT_MATCH) {
			
				case ALL_WORDS_EXACT_MATCH: {
					
				}
				case ALL_WORDS_SYNONYMS_MATCH: {
					
				}
				case SUBSET_WORDS_EXACT_MATCH: {
					
				}
				case SUBSET_WORDS_SYNONYMS_MATCH: {
					
				}
				default: {
					retVal.put(clazz, ProbabilisticMatchOrder.NO_MATCH.getValue());
					continue;
				}
				
			}
			
		}
		
		return retVal;
	}

	@Override
	public Map<Method, Integer> buildMethodMatcherStats(
			ClassInfoHolder classObj, Clause clause) {
		
		Map<Method, Integer> retVal = new HashMap<Method, Integer>();
		
		//TODO: Pick the important information from the clause.
		
		//Then iterate through all the methods of the class.
		//TODO: Also consider the static methods
		for( Method method: classObj.getMethods()) {
			
			if(clause.toPlainText().contains("deposits") && method.getName().contains("deposit")) {
				retVal.put(method, 100);
				continue;
			}
			else if(clause.toPlainText().contains("withdraw") && method.getName().contains("withdraw")) {
				retVal.put(method, 100);
				continue;
			}
			else if(clause.toPlainText().contains("balance") && method.getName().contains("Balance")) {
				retVal.put(method, 100);
				continue;
			}
			
			//Start with the highest probability and continue upon insertion.
			switch(ProbabilisticMatchOrder.ALL_WORDS_EXACT_MATCH) {
			
				case ALL_WORDS_EXACT_MATCH: {
  					
				}
				
				case ALL_WORDS_SYNONYMS_MATCH: {
					
				}
				
				case SUBSET_WORDS_EXACT_MATCH: {
					
				}
				
				case SUBSET_WORDS_SYNONYMS_MATCH: {
					
				}
				
				default: {
					retVal.put(method, ProbabilisticMatchOrder.NO_MATCH.getValue());
					continue;
				}
			}
			
		}
		
		
		return retVal;
		
	}	
	*/
	
	
	
	@Override
	public Map<ClassInfoHolder, Integer> buildClassMatcherStats(
			List<ClassInfoHolder> classes, Clause clause) {
		
		Map<ClassInfoHolder, Integer> retVal = new HashMap<ClassInfoHolder, Integer>();
		
		
		//if (clause.getTypeOfClause().getValue() == TypeOfClause.GIVEN.getValue()){
			
			ClassMatchingHelper helper = new ClassMatchingHelper();
			
			//TODO: Pick the important information from the clause.
			
			System.out.println("Clause: " + clause.toPlainText());
			System.out.println("Class Matcher");
			//Then iterate through the classes to find the necessary information.
			for( ClassInfoHolder clazz: classes ) {
				int probabilisticVal = helper.computeMatching(clazz, clause, null);
				retVal.put(clazz, probabilisticVal);
				System.out.println(BDDUtility.extractClassNameFromPackageName(clazz.getName())
									+ " - " + probabilisticVal);
			}
			System.out.println("END");
		/*	
		} else {
			if (createdClassesInGiven.size() == 1){
				retVal.put(createdClassesInGiven.get(0), 100);
				System.out.println("Clause: " + clause.toPlainText());
				System.out.println("Class Matcher");
				System.out.println(BDDUtility.extractClassNameFromPackageName(createdClassesInGiven.get(0).getName())
						+ " - " + 100);
			} else if (createdClassesInGiven.size() > 1) { //there are more than one class created
				for(ClassInfoHolder claz : createdClassesInGiven){					
					Map<Method, Integer> methodVsMatch = buildMethodMatcherStats(claz, clause);
					Method method = BDDUtility.getMethodWithMaxVal(methodVsMatch);
					if (method != null){
						for (Method mtd : claz.getMethods()){
							if (method.getName().equals(mtd.getName())){
								retVal.put(claz, 100);							
							} else{
								retVal.put(claz, 0);
							}
//							System.out.println("Clause: " + clause.toPlainText());
//							System.out.println("Class Matcher");
//							System.out.println(BDDUtility.extractClassNameFromPackageName(claz.getName()));
						}
					} else{
						retVal.put(claz, 0);
					}
				}
			}
		}
		*/
		return retVal;
	}

	@Override
	public Map<Method, Integer> buildMethodMatcherStats(
			ClassInfoHolder classObj, Clause clause) {
		
		Map<Method, Integer> retVal = new HashMap<Method, Integer>();
		MethodMatchingHelper helper = new MethodMatchingHelper();
		
		System.out.println("Clause: " + clause.toPlainText());
		System.out.println("Method Matcher");
		for( Method method: classObj.getMethods()) {
			int probabilisticVal = helper.computeMatching(method, clause, null);
			retVal.put(method, probabilisticVal);
			System.out.println(method.getName() + " - " + probabilisticVal);
		}
		System.out.println("END");
		
		return retVal;
		
	}

	@Override
	public Map<Constructor<?>, Integer> buildConstructorMatcherStats(
			ClassInfoHolder classObj, Clause clause) {
		
		Map<Constructor<?>, Integer> retVal = new HashMap<Constructor<?>, Integer>();

		System.out.println("Clause: " + clause.toPlainText());
		System.out.println("Constructor Matcher");
		for(Constructor<?> ctor:classObj.getConstructors()) {
			if(clause.getParameters().size() == ctor.getParameterTypes().length) {
				retVal.put(ctor, 100);
				System.out.println(ctor.getName() + " - " + 100);
			}
			System.out.println(ctor.getName() + " - " + 0);
		}
		
		return retVal;
	}

	@Override
	public ClassInfoHolder obtainClassOfInterest(Clause clause) {
		/*
		// TODO: How do we find the class of interest???
		for(String className: fileVsClassInfo.keySet()) {
			
			if(className.endsWith("Stack") && clause.toPlainText().contains("stack")) {
				return fileVsClassInfo.get(className);
			}
			else if(className.endsWith("AtmAccount") && clause.toPlainText().contains("Atm")) {
				return fileVsClassInfo.get(className);
			}
			else if(className.endsWith("WebRequester")) {
				return fileVsClassInfo.get(className);
			}
		}
		return null;
		*/
		//TODO: Tung refer this.
		ClassInfoHolder clazz = null;
		
		//First build the class matcher statistics.
		System.out.println("Starting class mathcer stats" + createdClassesInGiven);
		Map<ClassInfoHolder, Integer> classVsVal = buildClassMatcherStats(createdClassesInGiven, clause);	
		clazz = BDDUtility.getClassWithMaxValue(classVsVal);
		System.out.println("Ending class matcher stats");
		
		if(clazz == null) {
			
			int highestProbability = 0;
			for(ClassInfoHolder infoHolder: createdClassesInGiven) {
				Map<Method, Integer> methodVsVal = buildMethodMatcherStats(infoHolder, clause);
				
				int currHighestProbability = BDDUtility.getValueWithHighestProbability(methodVsVal);
				
				//TODO: Threshold required.
				if( currHighestProbability > highestProbability ) {
					highestProbability = currHighestProbability;
					clazz = infoHolder;
				}
			}
		}
		
		return clazz;
		
	}
}
