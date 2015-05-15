package cs.vt.edu.bdd.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import cs.vt.edu.bdd.matcher.IProbabilisticMatcher;
import cs.vt.edu.bdd.matcher.ProbabilisticMatcherImpl;
import cs.vt.edu.bdd.nlp.Analyzer;
import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.nlp.Paragraph;
import cs.vt.edu.bdd.nlp.Scenario;
import cs.vt.edu.bdd.reflection.ClassInfoHolder;
import cs.vt.edu.bdd.utility.BDDUtility;

/**
 * This class is used to generate the template .java file
 * which is a representation of the natural language features as 
 * a mapped test class represented using JUnit.
 * 
 * @author sunilkamalakar
 *
 */
public final class JunitTestCodeGenerator extends AbstractCodeGenerator {
	
	//The NLP analyzer
	private Analyzer analyser = null;
	
	//The probabilistic matcher
	private IProbabilisticMatcher probabilisticMatcher = null;
	
	//The current class name we are dealing with
	private String className = null;
	
	//The top level code model
	private JCodeModel jCodeModel = null;
	
	//The top level package for the class
	private JPackage jPackage = null;
	
	//The class which is going to be generated
	private JDefinedClass jDefinedClass = null;
	
	//The path to the project that we are dealing with
	private String projectPath = null;
	
	public JunitTestCodeGenerator(String projectPath) {	
		this.projectPath = projectPath;
		analyser = new Analyzer();
		probabilisticMatcher = new ProbabilisticMatcherImpl(projectPath);
	}
	
	@Override
	protected void generateTemplate(String className) {
		
		try {

	        
			jCodeModel = new JCodeModel();
			jPackage = jCodeModel._package(JunitTestCodeConstants.JUNIT_TEST_CLASS_PACKAGE);
	        jDefinedClass = jPackage._class(className);
	        
	        //JPackage superClassPackage = jCodeModel._package(JunitTestCodeConstants.JUNIT_TESTCASE_PACKAGE);
	        //Class<?> superClass = Class.forName(JunitTestCodeConstants.JUNIT_TESTCASE_PACKAGE);
	        //jDefinedClass._extends(superClass);
	
	        JDocComment jDocComment = jDefinedClass.javadoc();
	        jDocComment.add(JunitTestCodeConstants.DEFAULT_CLASS_LEVEL_COMMENT);
	        	
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

	@Override
	public void generateMethods(List<Scenario> scenarios) {
		
		for(Scenario scenario: scenarios) {
			this.generateMethod(scenario);			
		}
		
	}

	@Override
	protected void generateMethod(Scenario scenario) {
		
		JBlock jBlock = null;
		
		try {
		
			((ProbabilisticMatcherImpl)probabilisticMatcher).setCurrentScenario(scenario);
	       
			String methodName = BDDUtility.extractMethodName(scenario);
	        JMethod jmCreate = jDefinedClass.method(JMod.PUBLIC , void.class, methodName);
	        jmCreate._throws(Exception.class);
	        jmCreate.javadoc().add(JunitTestCodeConstants.DEFAULT_METHOD_LEVEL_COMMENT);
	        
	        jmCreate.annotate(jCodeModel.ref(JunitTestCodeConstants.JUNIT_METHOD_TEST_ANNOTATION));
	        
	        jBlock = jmCreate.body();
	
	        List<Object> createdGivenObjects = generateGiven(scenario.getGivenContent(), jBlock);
	        
	        if(createdGivenObjects != null) {
	        	List<Object> createdVariables = generateWhen(scenario.getWhenContent(), jBlock, createdGivenObjects);
	        	
	        	if(createdVariables != null) {
	        		generateThen(scenario.getThenContent(), jBlock, createdGivenObjects, createdVariables);        	        		
	        	}
	        }
	       
	        ((ProbabilisticMatcherImpl)probabilisticMatcher).setCurrentScenario(null);
		}
		catch(Exception ex) {
			if(jBlock != null) {
				String failStatement = "org.junit.Assert.fail" + "(" + "\"" + 
										"An exception prevented SmartBDD from generation the code. " +
										"Please revise test case or contact the author." + 
										 "Message: " + ex.getMessage() + "\");";
				jBlock.directStatement(failStatement);
			}
		}
	}

	@Override
	protected List<Object> generateGiven(Paragraph given, Object methodBody) throws Exception {
		
		List<Object> retVal = new ArrayList<Object>();
		
		JBlock jBlock = (JBlock)methodBody;
		
		//Each clause in the given should correspond to a constructor call.
		for(Clause clause: given.getClauses()) {
			
			//TODO: Tung refer this.
			//Get the class we are interested in.
			Map<ClassInfoHolder, Integer> classVsVal = probabilisticMatcher.buildClassMatcherStats(
															((ProbabilisticMatcherImpl)probabilisticMatcher).retrieveAllClasses(),
															clause);
			
			ClassInfoHolder clazz = BDDUtility.getClassWithMaxValue(classVsVal);
			
			//ClassInfoHolder clazz = probabilisticMatcher.obtainClassOfInterest(clause);
			
			if(clazz == null) {
				//No matching class found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching Class found for clause - " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be because the implementation class does not exist"+ "\");";
				jBlock.directStatement(failStatement);
				return null;
			}
			
			Map<Constructor<?>, Integer> ctorVsMatch = probabilisticMatcher.buildConstructorMatcherStats(clazz, clause);
			
			Constructor<?> ctor = BDDUtility.getConstructorWithMaxVal(ctorVsMatch);
			
			if(ctor == null || ( ctor.getParameterTypes().length != clause.getParameters().size()) ) {
				//No matching class found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching constructor found for clause - " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be because the implementation class does not have a matching constructor"+ "\");";
				jBlock.directStatement(failStatement);
				return null;
	        }
			
			List<String> parameterValues = clause.getParameters();
			
	        JClass jClassavpImpl;
			try {
				jClassavpImpl = jCodeModel.ref(clazz.getOperatingClass());
				String varName = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
				varName = Character.toLowerCase(varName.charAt(0)) + varName.substring(1);
				JVar jvarAvpImpl = jBlock.decl(jClassavpImpl, varName);
		        JExpression jExpr = JExpr._new(jClassavpImpl);
		        
		        int iteratorCount = 0;
		        for(Type type : ctor.getParameterTypes()) { 	
		        	if(parameterValues.get(iteratorCount) != null) {
		        		addArgumentToExpression(type, parameterValues.get(iteratorCount++), (JInvocation)jExpr);		
		        	}
		        }
		        
		        jvarAvpImpl.init(jExpr);
		        retVal.add(jvarAvpImpl);
	 
			} catch (Exception e) {
				throw e;
			}
			
			((ProbabilisticMatcherImpl)probabilisticMatcher).addCreatedClassToGivenList(clazz);
		}
		
		return retVal;
	}

	@Override
	protected List<Object> generateWhen(Paragraph when, Object methodBody, List<Object> createdGivenObjects) throws Exception{
		
		List<Object> retVal = new ArrayList<Object>();
		
		JBlock jBlock = (JBlock)methodBody;
		
		//Each clause in the given should correspond to a constructor call.
		for(Clause clause: when.getClauses()) {
			
			//TODO: Call the probabilistic matcher
			//and have to hash map with values ready.
		
			//TODO: How to know the variable of interest
			
			//Get the class we are interested in.
			ClassInfoHolder clazz = probabilisticMatcher.obtainClassOfInterest(clause);
			
			
			if(clazz == null) {
				//No matching class found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching Class found for clause - " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be a problem with SmartBDD probabilistic matcher" + "\");";
				jBlock.directStatement(failStatement);
				return null;
			}
			
			Map<Method, Integer> methodVsMatch = probabilisticMatcher.buildMethodMatcherStats(clazz, clause);
			Method method = BDDUtility.getMethodWithMaxVal(methodVsMatch);
			
			if(method == null || ( method.getParameterTypes().length != clause.getParameters().size() ) ) {
				//No matching method call found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching method found for clause " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be because the matching method is not present." + "\");";
				jBlock.directStatement(failStatement);
				return null;
			}
			
			System.out.println(BDDUtility.convertCamelCaseString(method.getName(), null));
			
			List<String> parameterValues = clause.getParameters();
			
	        JClass jClassavpImpl; 
			try {
				jClassavpImpl = jCodeModel.ref(clazz.getOperatingClass());
				String varName = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
				varName = Character.toLowerCase(varName.charAt(0)) + varName.substring(1);
				
				JVar jvarAvpImpl = null;
				
				
				for(Object obj: createdGivenObjects) {
					JVar jVar = (JVar)obj;
					if(jVar.name().equals(varName)) {
						jvarAvpImpl = jVar;
						break;
					}
				}
				
				JInvocation jExpr = jvarAvpImpl.invoke(method.getName());
				
		        int iteratorCount = 0;
		        for(Type type : method.getParameterTypes()) {
		        	if(parameterValues.get(iteratorCount) != null) {
		        		addArgumentToExpression(type, parameterValues.get(iteratorCount++), (JInvocation)jExpr);		
		        	}
		        }
		        
		        if(method.getReturnType().equals(Void.TYPE)) {
		        	jBlock.add(jExpr);
		        }
		        else {
		        	System.out.println(method.getReturnType());
		        	System.out.println(method.getReturnType().getName());
		        	
		        	JType jType = null;
		        	try {
		        		jType = JType.parse(jCodeModel, method.getReturnType().getName());
		        	}
		        	catch(Exception e) {
		        		System.out.print("Not a primitive type");
		        	}
		        	
		        	JVar jVar = null;
		        	if(jType != null && jType.isPrimitive()) {
		        		jVar = jBlock.decl(jType, "retVal" + retVal.size());
		        	}
		        	else {
		        		jClassavpImpl = jCodeModel.ref(clazz.getOperatingClass());
		        		jVar = jBlock.decl(jClassavpImpl, "retVal" + retVal.size());
		        	}
		        	
		        	System.out.println("When: " + jExpr);
		        	jVar.init(jExpr);
		        	retVal.add(jVar);
		        }
	 
			} catch (Exception e) {
				throw e;
			}
		}
		
		return retVal;
		
	}

	@Override
	protected void generateThen(Paragraph then, Object methodBody, List<Object> createdGivenObjs, List<Object> createdWhenObjs) throws Exception{
		
		JBlock jBlock = (JBlock)methodBody;
		
		//Each clause in the given should correspond to a constructor call.
		for(Clause clause: then.getClauses()) {
			
			//TODO: Call the probabilistic matcher
			//and have to hash map with values ready.
		
			//TODO: How to know the variable of interest
			//Get the class we are interested in.
			ClassInfoHolder clazz = probabilisticMatcher.obtainClassOfInterest(clause);
			
			if(clazz == null) {
				//No matching class found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching Class found for clause - " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be a problem with SmartBDD probabilistic matcher" + "\");";
				jBlock.directStatement(failStatement);
				return;
			}
			
			Map<Method, Integer> methodVsMatch = probabilisticMatcher.buildMethodMatcherStats(clazz, clause);
			Method method = BDDUtility.getMethodWithMaxVal(methodVsMatch);
			
			if(method == null) {
				//No matching method call found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching method found for clause " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be because the implementation method is not present." + "\");";
				jBlock.directStatement(failStatement);
				return;
			}
			
			System.out.println(BDDUtility.convertCamelCaseString(method.getName(), " "));
			
			//System.out.println("Method params" + method.getParameterTypes().length + "\tClause Params" + (clause.getParameters().size() - 1) );
			
			if( method.getReturnType().equals(Boolean.TYPE) && 
					method.getParameterTypes().length != clause.getParameters().size()) {
				
				//No matching method call found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching method found for clause " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be because an exact match method is not present." + "\");";
				jBlock.directStatement(failStatement);
				return;
				
			}
			else if( !method.getReturnType().equals(Boolean.TYPE) && (method.getParameterTypes().length != (clause.getParameters().size() - 1)) ){
				
				//No matching method call found for the clause
				String failStatement = "org.junit.Assert.fail" + "(" + "\"No matching method found for clause " +
										"[" + clause.toPlainTextWithouQuotes() + "]" + 
										". This could be because an exact match method is not present." + "\");";
				jBlock.directStatement(failStatement);
				return;
			}
			
			List<String> parameterValues = clause.getParameters();
			
	       
			
			try {
				
				JClass jClassavpImpl = jCodeModel.ref(clazz.getOperatingClass());
				String varName = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
				varName = Character.toLowerCase(varName.charAt(0)) + varName.substring(1);
				
				JVar jvarAvpImpl = null;
				
				for(Object obj: createdGivenObjs) {
					JVar jVar = (JVar)obj;
					if(jVar.name().equals(varName)) {
						jvarAvpImpl = jVar;
						break;
					}
				}

				JClass assertClass = jCodeModel.ref(org.junit.Assert.class);
				JInvocation jExpr = null;
				
				//Based on the method make a decision to apply a particular assert condition.
				//TODO: This is very simple for now that we check if we have a boolean return condition
				//and make an assumption to say if not it is assertEquals.
				if(method.getReturnType().equals(Boolean.TYPE)) {
					
					if(!clause.containsNot())
						jExpr = assertClass.staticInvoke("assertTrue");
					else
						jExpr = assertClass.staticInvoke("assertFalse");
					
					JInvocation assertMethodCall = jvarAvpImpl.invoke(method.getName());
					
					int iteratorCount = 0;
			        for(Type type : method.getParameterTypes()) {
			        	if(parameterValues.get(iteratorCount) != null) {
			        		addArgumentToExpression(type, parameterValues.get(iteratorCount++), (JInvocation)assertMethodCall);	
			        	}
			        }
			        jExpr.arg(assertMethodCall);
					
				}
				else {
					if(!clause.containsNot())
						jExpr = assertClass.staticInvoke("assertEquals");
					else  {
						assertClass = jCodeModel.ref(junitx.framework.Assert.class);
						jBlock.directStatement("//To use assertNotEquals you need to install junit addon v1.4");
						jExpr = assertClass.staticInvoke("assertNotEquals");
					}
					
					JInvocation assertMethodCall = jvarAvpImpl.invoke(method.getName());
					
					int iteratorCount = 0;
			        for(Type type : method.getParameterTypes()) {
			        	if(parameterValues.get(iteratorCount) != null) {
			        		addArgumentToExpression(type, parameterValues.get(iteratorCount++), (JInvocation)assertMethodCall);	
			        	}
			        }
			        jExpr.arg(assertMethodCall);
			        JExpression assertParamValue = addArgumentToExpression(method.getReturnType(), parameterValues.get(parameterValues.size() - 1));
			        
			        jExpr.arg(assertParamValue);
			        
				    if(method.getReturnType().equals(Float.TYPE)) {
				        	jExpr.arg(JExpr.lit(0.0f));
				    }
				    if(method.getReturnType().equals(Double.TYPE)) {
				        	jExpr.arg(JExpr.lit(0.0D));
				    }
				}
				
				//if(clause.containsNot()) {
				//	jBlock.directStatement("//To use assertNotEquals you need to install junit addon v1.4");
				//}
				jBlock.add(jExpr);

			} catch (Exception e) {
				throw e;
			}
			
		}
		
		
		
	}

	@Override
	public boolean saveCodeToFile() {
		
		//The path to the file created
		String pathToFile = projectPath + System.getProperty("file.separator") + JunitTestCodeConstants.PROJECT_BASE_SOURCE;
		
        //Finally build the class
        try {
			jCodeModel.build( new File(pathToFile) );
			
			addImportsToFile(pathToFile);
			
		} catch (IOException e) {
			System.err.println("Not able auto-generate the file\n" + e.toString());
		}
		
		return new File(  JunitTestCodeConstants.PROJECT_BASE_SOURCE + System.getProperty("file.separator") + 
						  JunitTestCodeConstants.JUNIT_TEST_CLASS_PACKAGE.replace(".", System.getProperty("file.separator"))
					   ).exists();
	}
	
	private void addImportsToFile(String projectPath) throws IOException {
		
		//Add the imports to the file.
		String pathToFile = projectPath + System.getProperty("file.separator") +
				  			JunitTestCodeConstants.JUNIT_TEST_CLASS_PACKAGE.replace(".", System.getProperty("file.separator"))
				  			+ System.getProperty("file.separator") + className + ".java";
		System.out.println("Created file at: " + pathToFile);
		
		File generatedFile = new File(pathToFile);
		StringBuffer generatedFileContents = new StringBuffer(FileUtils.readFileToString(generatedFile));
		
		generatedFileContents = generatedFileContents.insert(generatedFileContents.indexOf("\n", 2), 
									"\n\n" + 
									"import org.junit.Test;" + "\n" +
									//"import junitx.framework.Assert;" +
									"\n"
								);
		
		BDDUtility.writeContentToFile(pathToFile, generatedFileContents.toString());
		
	}

	@Override
	synchronized public void generateTestCode(String featureFile) {
		
		try {
			
//			if(!featureFile.startsWith(System.getProperty("file.separator"))) {
//				featureFile = projectPath + System.getProperty("file.separator") + featureFile;
//			}
			
			String featureFileInString = FileUtils.readFileToString(new File(featureFile));
			
			String className = BDDUtility.extractClassName(featureFile)
					+ JunitTestCodeConstants.TEST_EXTENTION;

			this.className = className;
			generateTemplate(className);
			
			List<Scenario> scenarios = analyser.TextsToScenarios(featureFileInString);
			System.out.println("Number of scenario's: " + scenarios.size());
			generateMethods(scenarios);
			
			saveCodeToFile();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void addArgumentToExpression(Type type, String param, JInvocation jInvocation) {
		
		try {
			
			String typeParam = type.toString();
			if(typeParam.contains("boolean")) {
				boolean val = Boolean.getBoolean(param);
				jInvocation = jInvocation.arg(JExpr.lit(val));
			}
			else if(typeParam.contains("int")) {
				int val = Integer.parseInt(param);
				jInvocation = jInvocation.arg(JExpr.lit(val));
			}
			else if(typeParam.contains("long")) {
				long val = Long.parseLong(param);
				jInvocation = jInvocation.arg(JExpr.lit(val));
			}
			else if(typeParam.contains("float")) {
				float val = Float.parseFloat(param);
				jInvocation = jInvocation.arg(JExpr.lit(val));
			}
			else if(typeParam.contains("double")) {
				double val = Double.parseDouble(param);
				jInvocation = jInvocation.arg(JExpr.lit(val));
			}
			else if(typeParam.contains("char")) {
				char val = param.charAt(0);
				jInvocation = jInvocation.arg(JExpr.lit(val));
			}
			else if(typeParam.contains("String")) {
				jInvocation = jInvocation.arg(JExpr.lit(param));
			}
			else {
				//TODO: Figure out if we have a complex type.
				//And how can this be represented in natural language.
			}		
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		
	}
	
	private JExpression addArgumentToExpression(Type type, String param) {
		
		JExpression retVal = null;
		
		try {
			
			String typeParam = type.toString();
			if(typeParam.contains("boolean")) {
				boolean val = Boolean.getBoolean(param);
				retVal = JExpr.lit(val);
			}
			else if(typeParam.contains("int")) {
				int val = Integer.parseInt(param);
				retVal = JExpr.lit(val);
			}
			else if(typeParam.contains("long")) {
				long val = Long.parseLong(param);
				retVal = JExpr.lit(val);
			}
			else if(typeParam.contains("float")) {
				float val = Float.parseFloat(param);
				retVal = JExpr.lit(val);
			}
			else if(typeParam.contains("double")) {
				double val = Double.parseDouble(param);
				retVal = JExpr.lit(val);
			}
			else if(typeParam.contains("char")) {
				char val = param.charAt(0);
				retVal = JExpr.lit(val);
			}
			else if(typeParam.contains("String")) {
				retVal = JExpr.lit(param);
			}
			else {
				//TODO: Figure out if we have a complex type.
				//And how can this be represented in natural language.
			}		
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return retVal;
		
	}
	
	public List<Scenario> obtainScenarios(String pathToScenario) throws IOException {
		
		String featureFileInString = FileUtils.readFileToString(new File(pathToScenario));	
		
		System.out.println(featureFileInString);
		
		List<Scenario> scenarioList = analyser.TextsToScenarios(featureFileInString);
		return new ArrayList<Scenario>(scenarioList);
	}
	
	public static void main(String[] args) throws IOException {
		
		AbstractCodeGenerator generator = new JunitTestCodeGenerator("/Users/sunilkamalakar/Desktop/MittelmanLab/workspace-se/BDD-test");
		//generator.generateTestCode("/Users/sunilkamalakar/Desktop/MittelmanLab/workspace-se/BDD-test", "/Users/sunilkamalakar/Desktop/MittelmanLab/workspace-se/BDD-test/bdd-test/Stack.feature");
		generator.generateTestCode("BDD Scenarios/WebRequester.feature");
		generator.generateTestCode("BDD Scenarios/AtmAccount.feature");
		generator.generateTestCode("BDD Scenarios/StackQueue.feature");
		generator.generateTestCode("BDD Scenarios/Stack.feature");
	}
	
}

