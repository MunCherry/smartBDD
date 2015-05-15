package cs.vt.edu.bdd.matcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.reflection.ClassInfoHolder;
import cs.vt.edu.bdd.reflection.JavaClassDetailsFacade;

public class DummyPM extends ProbabilisticMatcherImpl {
	
	private Map<String, ClassInfoHolder> fileVsClassInfo;
	
	public DummyPM(String projectPath) {
		super(projectPath);
		JavaClassDetailsFacade facade = new JavaClassDetailsFacade();
		fileVsClassInfo = facade.generateClassInfo(projectPath);
	}

	@Override
	public Map<ClassInfoHolder, Integer> buildClassMatcherStats(
			List<ClassInfoHolder> classes, Clause clause) {
		
		Map<ClassInfoHolder, Integer> retVal = new HashMap<ClassInfoHolder, Integer>();
		
		/*
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
		*/
		
		return retVal;
	}

	@Override
	public Map<Method, Integer> buildMethodMatcherStats(
			ClassInfoHolder classObj, Clause clause) {
		
		Map<Method, Integer> retVal = new HashMap<Method, Integer>();
		
		/*
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
			else if(clause.toPlainText().contains("pushed") && method.getName().contains("push")) {
				retVal.put(method, 100);
				continue;
			}
			else if(clause.toPlainText().contains("pop") && method.getName().contains("pop")) {
				retVal.put(method, 100);
				continue;
			}
			else if(clause.toPlainText().contains("size") && method.getName().contains("size")) {
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
		
		*/
		
		return retVal;
		
	}

	@Override
	public Map<Constructor<?>, Integer> buildConstructorMatcherStats(
			ClassInfoHolder classObj, Clause clause) {
		
		Map<Constructor<?>, Integer> retVal = new HashMap<Constructor<?>, Integer>();

		for(Constructor<?> ctor:classObj.getConstructors()) {
			if(clause.getParameters().size() == ctor.getGenericParameterTypes().length)
				retVal.put(ctor, 100);
		}
		
		return retVal;
		
	}

	@Override
	public ClassInfoHolder obtainClassOfInterest(Clause clause) {
		// TODO: How do we find the class of interest???
		for(String className: fileVsClassInfo.keySet()) {
			
			if(className.endsWith("Stack") && clause.toPlainText().contains("stack")) {
				return fileVsClassInfo.get(className);
			}
			if(className.endsWith("AtmAccount") && clause.toPlainText().contains("Atm")) {
				return fileVsClassInfo.get(className);
			}
		}
		
		return null;
	}
}
