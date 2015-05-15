package cs.vt.edu.bdd.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This interface represents the all the details that we
 * want to pull out of a given Java file.
 * 
 * @author Tung Dao
 *
 */
public interface IClassDetails {
	
	/**
	 * Returns the all the public constructors of the class.
	 * @return A List of Constructors
	 */
	public List<Constructor<?>> getPublicConstructors();
	
	/**
	 * Returns the all the public methods of the class,
	 * excluding the methods of the Object class.
	 * @return A List of Methods
	 */
	public List<Method> getPublicMethods();
	
	/**
	 * This method returns all the public member variable's
	 * associated with the class. 
	 * @return List of string representation of the member variables.
	 */
	public List<Field> getPublicMembers();
	
	/**
	 * This represents the operation where we provide the
	 * class as a string path and the class object is created.
	 * Any class implementing this interface should call this
	 * in it's constructor. 
	 * @param pathOfClass
	 * @return
	 * @throws ClassNotFoundException 
	 */	
	public void setOperatingClass(String baseDirectory, String pathOfClass) throws ClassNotFoundException;
	
	/**
	 * Obtain the class object that we are dealing with.
	 * This is required for the code generator to instantiate a new
	 * object of that type.
	 */
	public Class<?> getOperatingClass();
	
	/**
	 * This method returns the class name associated with the class.
	 * @return - The string name of the class
	 */
	public String getClassName();
	

}
