package cs.vt.edu.bdd.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This class represents the Information to be held
 * about a specific class, like the name, methods, etc.
 * 
 * @author sunilkamalakar
 *
 */
public class ClassInfoHolder {

	//The name of the class.
	private String name;
	
	//The class we are operating on
	private Class<?> operatingClass;
	
	//Represents the list of constructors that are needed.
	private List<Constructor<?>> constructors;
	
	//Represents only the non-static member variables
	//and functions of the class.
	private List<Field> memberVariables;
	private List<Method> methods;

	//Represents only the static class variables
	//and functions of the class.
	private List<Field> staticVariables;
	private List<Method> staticMethods;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Field> getMemberVariables() {
		return memberVariables;
	}
	public void setMemberVariables(List<Field> regularFields) {
		this.memberVariables = regularFields;
	}
	public List<Method> getMethods() {
		return methods;
	}
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
	public List<Field> getStaticVariables() {
		return staticVariables;
	}
	public void setStaticVariables(List<Field> staticFields) {
		this.staticVariables = staticFields;
	}
	public List<Method> getStaticMethods() {
		return staticMethods;
	}
	public void setStaticMethods(List<Method> staticMethods) {
		this.staticMethods = staticMethods;
	}
	
	public List<Constructor<?>> getConstructors() {
		return constructors;
	}
	public void setConstructors(List<Constructor<?>> constructors) {
		this.constructors = constructors;
	}
	
	public Class<?> getOperatingClass() {
		return operatingClass;
	}
	public void setOperatingClass(Class<?> operatingClass) {
		this.operatingClass = operatingClass;
	}


	@Override
	public String toString() {
		
		StringBuffer retVal = new StringBuffer();
		
		retVal.append(name + "\n");
		
		retVal.append("Constructors\n");
		for (Constructor<?> ctor: constructors)
			retVal.append(ctor.getName() + "\tArguments - " + ctor.getGenericParameterTypes().length + "\n");
			
		retVal.append("Variables:\n");
		for (Field field : staticVariables)
			retVal.append("\t-" + field.getName() + "\n");
		for (Field field : memberVariables)
			retVal.append("\t-" + field.getName() + "\n");
		
		retVal.append("Methods:\n");
		for (Method method : staticMethods)
			retVal.append("\t-" + method.getName() + "\n");
		for (Method method : methods)
			retVal.append("\t-" + method.getName() + "\n");
		
		return retVal.toString();
	}
	
}
