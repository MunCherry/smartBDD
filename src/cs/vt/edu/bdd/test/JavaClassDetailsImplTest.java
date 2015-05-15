package cs.vt.edu.bdd.test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import cs.vt.edu.bdd.reflection.ClassInfoHolder;
import cs.vt.edu.bdd.reflection.JavaClassDetailsFacade;

public class JavaClassDetailsImplTest {

	@Test
	public void test() {
		
		Map<String, ClassInfoHolder> fileVsClassInfo;
		JavaClassDetailsFacade facade = new JavaClassDetailsFacade();
		fileVsClassInfo = facade.generateClassInfo("/Users/sunilkamalakar/Desktop/MittelmanLab/workspace-se/BDD-test");
		for (String key : fileVsClassInfo.keySet()) {
			System.out.println(fileVsClassInfo.get(key).toString());
		}
		
		Assert.assertNotNull(fileVsClassInfo);		
	}
	
	public static void main(String[] args) {
		
		try {
			File file = new File("/Users/sunilkamalakar/Desktop/MittelmanLab/workspace-se/BDD-test/bin");
			 
	        //convert the file to URL format
			URL url = file.toURI().toURL(); 
			System.out.println(url);
			URL[] urls = new URL[]{url}; 
			
			//load this folder into Class loader
			ClassLoader cl = new URLClassLoader(urls); 
			
			 //load the Address class in 'c:\\other_classes\\'
			Class  cls = cl.loadClass("org.cs.vt.bdd.examples.HelloWorld123");
			
			//System.out.println(urlfrom.getFile());
			
			//Class cls = Class.forName("org.cs.vt.bdd.examples.HelloWorld123", true, cl);
			
			//print the location from where this class was loaded
			ProtectionDomain pDomain = cls.getProtectionDomain();
			CodeSource cSource = pDomain.getCodeSource(); 
			URL urlfrom = cSource.getLocation();
			System.out.println(urlfrom.getFile());
			
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		
	}

}
