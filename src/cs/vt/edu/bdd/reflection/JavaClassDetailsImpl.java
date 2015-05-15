package cs.vt.edu.bdd.reflection;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the IClassDetails which is used to pull 
 * out the details of any given Java class using Java reflection technology.
 * 
 * @author Tung Dao
 *
 */
public class JavaClassDetailsImpl implements IClassDetails {
	
	//The folder containing the class files in a java project
	public static String CLASS_FILES_DIRECTORY = "bin";
	
	//The all important class object
	private Class<?> operatingClass = null;

	public JavaClassDetailsImpl(String baseDirectory, String pathOfClass) throws ClassNotFoundException {
		super();
		setOperatingClass(baseDirectory, pathOfClass);
	}
	
	@Override
	public Class<?> getOperatingClass() {
		return operatingClass;
	}

	@Override
	public List<Method> getPublicMethods() {
		
		List<Method> methodsList = new ArrayList<Method>();
		Method[] methods = null;
		
		if(operatingClass != null) {
			methods = operatingClass.getMethods();
		
			for(Method method: methods) {
				if(method.getDeclaringClass() != Object.class &&
						Modifier.isPublic(method.getModifiers()) ) {
					
					methodsList.add(method);
				}
			}
		}
		
		return methodsList;
	}

	@Override
	public void setOperatingClass(String baseDirectory, String pathOfClass) throws ClassNotFoundException {
		
		try {
			File file = new File(baseDirectory + 
								System.getProperty("file.separator") + CLASS_FILES_DIRECTORY);
			
			URL url = file.toURI().toURL();
			URL[] urlList = new URL[]{url};
			ClassLoader loader = new URLClassLoader(urlList);			
			operatingClass = Class.forName(pathOfClass, true, loader);
		}
		catch(ClassNotFoundException ex) {
			throw ex;
		}
		catch(Exception ex) {
			System.err.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public String getClassName() {
		return operatingClass.getName();
	}

	@Override
	public List<Field> getPublicMembers() {
		
		List<Field> members = new ArrayList<Field>();
		Field[] fieldArr = null;
		
		if(operatingClass != null) {
			
			fieldArr = operatingClass.getDeclaredFields();
			for (Field field : fieldArr) {
				if(Modifier.isPublic(field.getModifiers())) {
					members.add(field);
				}
			}
		}
		
		return members;
	}

	@Override
	public List<Constructor<?>> getPublicConstructors() {
		
		List<Constructor<?>> ctors = new ArrayList<Constructor<?>>();
		Constructor<?>[] ctorArr = null;
		
		if(operatingClass != null) {
			
			ctorArr = operatingClass.getConstructors();
			for (Constructor<?> ctor : ctorArr) {
				if(Modifier.isPublic(ctor.getModifiers())) {
					ctors.add(ctor);
				}
			}
		}
		
		return ctors;
	}

}
