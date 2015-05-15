package cs.vt.edu.bdd.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.vt.edu.bdd.utility.BDDUtility;

/**
 * This class is a facade/wrapper around the Java ClassDetails
 * which is used to get the complete details of a particular
 * class into a class info holder object.
 * 
 * @author sunilkamalakar
 *
 */
public class JavaClassDetailsFacade {
	
	public static String JAVA_FILE_EXTENSION = ".java";

	public Map<String, ClassInfoHolder> generateClassInfo(String baseDirectory) {
	
		Map<String, ClassInfoHolder> fileVsClassInfo = new HashMap<String, ClassInfoHolder>();
		List<String> javaFileNames = 
					BDDUtility.getAllFilesWithExtention(baseDirectory, JAVA_FILE_EXTENSION);
		
		for (String javaFileName : javaFileNames) {
			//Create a new classinfo object
			System.out.println(javaFileName);
			ClassInfoHolder infoHolder = new ClassInfoHolder();
			IClassDetails classDetails = null;
			try {
				classDetails = new JavaClassDetailsImpl(baseDirectory, javaFileName);
				
				List<Field> fields = classDetails.getPublicMembers();
				
				List<Field> staticFields = new ArrayList<Field>();
				List<Field> regularFields = new ArrayList<Field>();
				
				for (Field field : fields) {
					if( Modifier.isStatic(field.getModifiers()) ) {
						staticFields.add(field);
					}
					else {
						regularFields.add(field);
					}
				}
				
				//Get member functions now
				List<Method> methods = classDetails.getPublicMethods();
				
				List<Method> staticMethods = new ArrayList<Method>();
				List<Method> regularMethods = new ArrayList<Method>();
				
				for (Method method : methods) {
					if( Modifier.isStatic(method.getModifiers()) ) {
						staticMethods.add(method);
					}
					else {
						regularMethods.add(method);
					}
				}
				
				//Get the constructors
				List<Constructor<?>> constructors = classDetails.getPublicConstructors();
				
				//Get the operating class
				Class<?> operatingClass = classDetails.getOperatingClass();
				
				//Finally add it to the class object.
				infoHolder.setOperatingClass(operatingClass);
				infoHolder.setName(javaFileName);
				infoHolder.setConstructors(constructors);
				infoHolder.setStaticVariables(staticFields);
				infoHolder.setMemberVariables(regularFields);
				infoHolder.setStaticMethods(staticMethods);
				infoHolder.setMethods(regularMethods);
				
				fileVsClassInfo.put(javaFileName, infoHolder);
				
				
			} catch (ClassNotFoundException e) {
				System.err.println("Class not found: " + e.getMessage());
				continue;
			}
			catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
				continue;
			}
		}
		
		return fileVsClassInfo;
		
	}
	
	
}
