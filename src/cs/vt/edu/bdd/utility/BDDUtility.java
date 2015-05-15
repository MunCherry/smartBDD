package cs.vt.edu.bdd.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.text.WordUtils;

import cs.vt.edu.bdd.generator.JunitTestCodeConstants;
import cs.vt.edu.bdd.generator.JunitTestCodeGenerator;
import cs.vt.edu.bdd.nlp.Scenario;
import cs.vt.edu.bdd.reflection.ClassInfoHolder;

public class BDDUtility {
	
	public static List<String> getAllFilesWithExtention(
			String directoryPath, final String extension) {
		return getAllFilesWithExtention(directoryPath, extension, true);
	}
	
	public static List<String> getAllFilesWithExtention(
								String directoryPath, final String extension, boolean skipTestFolder) {
		
		List<String> fileNames = new ArrayList<String>();
		
		File directory = new File(directoryPath);
		Collection<File> files = null;
		
		if(directory.exists() && directory.isDirectory()) {
			files = FileUtils.listFiles(directory, 
						new IOFileFilter() {
							
							@Override
							public boolean accept(File file, String extension) {
								return file.exists() && file.getName().endsWith(extension);
							}
							
							@Override
							public boolean accept(File file) {
								return file.exists() && file.getName().endsWith(extension);
							}
						}, 
						DirectoryFileFilter.DIRECTORY);			
		}
		
		for (File file : files) {
			
			//TODO: only capture those files in the package.
			String fileInPackage = file.getAbsolutePath();
			fileInPackage = fileInPackage.replace(directory.getAbsolutePath(), "");
			
			//Remove /src/ from the path.
			fileInPackage = fileInPackage.substring(5);
			
			fileInPackage = fileInPackage.replace(System.getProperty("file.separator"), ".");
			
			if(skipTestFolder && fileInPackage.contains(JunitTestCodeConstants.JUNIT_TEST_CLASS_PACKAGE)) {
				//fileNames.add(fileInPackage.substring(0, fileInPackage.length() - new String(extension).length()));
			}
			else {
				//TODO: Fix this, not sure why the reflection fails for this particular class.
				if( !fileInPackage.contains("ScenarioEditor") ) {	
					fileNames.add(fileInPackage.substring(0, fileInPackage.length() - new String(extension).length()));
				}
			}
		}
		return fileNames;
	}
	
	public static String getFileName(String fullPath) {
		
		File file = new File(fullPath);
		if(file.exists()) {
			return file.getName();
		}
		
		return null;
	}
	
	public static String extractMethodName(Scenario scenario) {
		
		String scenarioTitle = scenario.geTitle().substring(scenario.geTitle().indexOf(':') + 1);
		String methodNameFromScenario = WordUtils.capitalize(scenarioTitle);
		methodNameFromScenario = methodNameFromScenario.replaceAll("\\s", "");
		
		//TODO: To make this non-random by getting a meaningful name
		return "test" + methodNameFromScenario;
	}
	
	public static boolean writeContentToFile(String path, String content) {
		
		try{
		  // Create file 
		  FileWriter fstream = new FileWriter(path);
		  BufferedWriter out = new BufferedWriter(fstream);
		  out.write(content);
		  //Close the output stream
		  out.close();
		}
		catch (Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		return new File(path).exists();
		
	}

	public static String extractClassName(String file) {
		
		return FilenameUtils.getBaseName(file);
		
	}
	
	public static String extractExtention(String file) {
		
		return FilenameUtils.getExtension(file);
		
	}
	
	public static Constructor<?> getConstructorWithMaxVal(Map<Constructor<?>, Integer> ctorVsVal) {
		
		Constructor<?> keyWithMaxVal = null;
		
		for(Constructor<?> ctor: ctorVsVal.keySet()) {
			
			if(keyWithMaxVal == null || ctorVsVal.get(ctor) > ctorVsVal.get(keyWithMaxVal)) {
				keyWithMaxVal = ctor;
			}
		}
		
		if(ctorVsVal.get(keyWithMaxVal) != null && ctorVsVal.get(keyWithMaxVal) == 0) {
			return null;
		}
		
		return keyWithMaxVal;
		
	}

	//TODO: We can have only a single method for this with generic keys
	public static Method getMethodWithMaxVal(Map<Method, Integer> methodVsMatch) {
		
		Method keyWithMaxVal = null;
		
		for(Method ctor: methodVsMatch.keySet()) {
			
			if(keyWithMaxVal == null || methodVsMatch.get(ctor) > methodVsMatch.get(keyWithMaxVal)) {
				keyWithMaxVal = ctor;
			}
		}
		
		if(methodVsMatch.get(keyWithMaxVal) != null && methodVsMatch.get(keyWithMaxVal) == 0) {
			return null;
		}
		
		return keyWithMaxVal;
		
	}
	
	public static String convertCamelCaseString(String camelCaseString, String optionalSeparator) {
		  int n = camelCaseString.trim().length();
		  StringBuilder sb = new StringBuilder(n * 2);
		  for (int i = 0; i < n; i++) {
		    char c = camelCaseString.charAt(i);
		    int x = (int) c;
		    // A=65, N=78, Z=90, a=97
		    // See http://blossomassociates.net/ascii.html
		    if(optionalSeparator==null) optionalSeparator="";
		    if (x >= 65 && x <= 90) { // Converting to lower case
		      c = (char) (x + 32);
		      sb.append(optionalSeparator).append(c);
		    } else
		      sb.append(c);
		  }
		 
		  // Final string converted from camelCaseString
		  String convertedString = sb.toString();
		  sb = null;
		  return convertedString;
		}

	public static ClassInfoHolder getClassWithMaxValue(
			Map<ClassInfoHolder, Integer> classVsVal) {
		
		ClassInfoHolder keyWithMaxVal = null;
		
		for(ClassInfoHolder infoHolder: classVsVal.keySet()) {
			
			if(keyWithMaxVal == null || classVsVal.get(infoHolder) > classVsVal.get(keyWithMaxVal)) {
				keyWithMaxVal = infoHolder;
			}
		}
		
		if(classVsVal.get(keyWithMaxVal) != null && classVsVal.get(keyWithMaxVal) == 0) {
			return null;
		}
		
		return keyWithMaxVal;	
	}
	
	//TODO: We can have only a single method for this with generic keys
	public static Integer getValueWithHighestProbability(Map<Method, Integer> methodVsMatch) {
		
		Integer maxVal = 0;
		
		for(Method method: methodVsMatch.keySet()) {
			
			if(maxVal == 0 || methodVsMatch.get(method) > maxVal) {
				maxVal = methodVsMatch.get(method);
			}
		}
		
		if(maxVal <= 0) {
			return 0;
		}
		
		return maxVal;	
	}
	
	public static String extractClassNameFromPackageName(String fullyQualifiedName) {
		return fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.') + 1);
	}
	
}