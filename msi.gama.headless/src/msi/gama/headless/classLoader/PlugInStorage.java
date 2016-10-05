/*********************************************************************************************
 * 
 *
 * 'PlugInStorage.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.classLoader;

/**
 * Class to associate the plug-in to its formated name. Makes seeking easier.
 * 
 * @author VALENTIN Joachim
 */
@SuppressWarnings({ "rawtypes" })
public class PlugInStorage {

	Object PlugInObject;
	String ObjectName;
	boolean OptionInstance;

	public PlugInStorage() {
		PlugInObject = null;
		ObjectName = "";
		OptionInstance = false;
	}

	/**
	 * Defines the name to associate to the object. When you define the name, do
	 * not give the same name for two objects, the name is used to sort and seek
	 * a list of object.
	 */
	public void SetName(final String name) {
		ObjectName = name;
	}

	/** Returns the name associated to the object. */
	public String GetName() {
		return ObjectName;
	}

	/**
	 * Compare the name with the param string. Return <0 if ObjectName
	 * lexicographically < to str, >0 if ObjectName > to str, or =0 if
	 * ObjectName correspond to str.
	 */
	public int Compare(final String str) {
		return ObjectName.compareTo(str);
	}

	/** Defines the stored object as an instance of class. */
	public void Set(final Object obj) {
		OptionInstance = true;
		PlugInObject = obj;

		ObjectName = PlugInObject.getClass().getSimpleName();
		ObjectName = Convert(ObjectName);
	}

	/** Defines the stored object as a class. */
	public void Set(final Class cl) {
		OptionInstance = false;
		PlugInObject = cl;

		ObjectName = ((Class) PlugInObject).getSimpleName();
		ObjectName = Convert(ObjectName);
	}

	/** Returns the stored object as an instance of class. */
	public Object GetInstance() {
		if (OptionInstance) {
			return PlugInObject;
		}
		return null;
	}

	/** Returns the stored object as an instance of class. */
	public Class GetClass() {
		if (!OptionInstance) {
			return (Class) PlugInObject;
		}
		return null;
	}

	/** Convert the string. Replace '_' by '-'. */
	String Convert(final String converted) {
		return converted.replace('_', '-');
	}
}
