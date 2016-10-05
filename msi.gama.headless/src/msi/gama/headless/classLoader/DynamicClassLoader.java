/*********************************************************************************************
 * 
 *
 * 'DynamicClassLoader.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.classLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarFile;

// import java.lang.* //Systematically loaded by java.
/**
 * <p>
 * Class to load dynamic plug-ins using introspection and reflexion. Plug-ins
 * are java class files (compiled files) and they must be stored in the same
 * directory. After loading, classes are associated to an unique name and stored
 * onto an array. You can either store the class definition or objects created
 * from a loaded class. If you use instance option (that build object from the
 * class), your class must have a default constructor (with no parameters).
 * </p>
 * 
 * <p>
 * Initially created for the software Patty, where it's used to add aircraft or
 * entity specifications to the program.
 * </p>
 * 
 * @author VALENTIN Joachim
 */
public class DynamicClassLoader {

	String PlugInDirectory; // Source directory for plug-ins.
	boolean OptionInstance; // If true, the loader create and store an instance
							// of the class, rather
							// than the class itself.

	public DynamicClassLoader() {
		PlugInDirectory = "";
		OptionInstance = false;
	}

	/**
	 * Constructor that initialize the plug-in directory and the loading option.
	 */
	public DynamicClassLoader(final String dir, final String pack, final boolean option) {
		PlugInDirectory = new String(dir);
		OptionInstance = option;
	}

	/** Defines the plug-in directory. */
	public void SetDirectory(final String dir) {
		PlugInDirectory = new String(dir);
	}

	/** Returns the plug-in directory. */
	public String GetDirectory() {
		return PlugInDirectory;
	}

	/** Defines class instance storage option. */
	public void SetInstanceOption() {
		OptionInstance = true;
	}

	/** Defines class storage option. */
	public void UnsetInstanceOption() {
		OptionInstance = false;
	}

	/** Returns storage option (class or instance). */
	public boolean GetInstanceOption() {
		return OptionInstance;
	}

	/** Defines the plug-in directory and load them. */
	public int LoadPlugIn(final String dir) {
		PlugInDirectory = new String(dir);
		return LoadPlugIn();
	}

	/** Load plug-ins from the previously defined directory. */

	@SuppressWarnings("rawtypes")
	public int loadClasses() {
		File directory;
		FilenameFilter classFilter;
		String[] classFiles;
		// Class tmp_class;
		// PlugInStorage stored;

		// Create the file system object for the plug in directory.
		directory = new File(PlugInDirectory);

		// Create a filter to get only class files from the directory.
		classFilter = (dir, name) -> name.endsWith(".jar");

		// Get the list of all filtered filename of the directory.
		// In fact, all *.class files.
		if ((classFiles = directory.list(classFilter)) == null) {
			System.err.println("Nothing to load.");
			return -1;
		}

		// Get all classes associated with *.class files and put them into the
		// array.
		for (int index = 0; index < classFiles.length; ++index) {
			// stored = new PlugInStorage();
			try {
				// Remove the extension of filename.
				// classFiles[index] = classFiles[index].substring(0,
				// classFiles[index].indexOf(".class"));

				// Load the class from the filename. Send ClassNotFoundException
				// or
				// NoClassDefFoundError on error.

				// tmp_class = Class.forName(PackagePath + classFiles[index]);
				final URL url = new URL("jar:file:" + PlugInDirectory + "/" + classFiles[index] + "!/");
				final URLClassLoader ucl = new URLClassLoader(new URL[] { url });

				// On charge le jar en m�moire
				final JarFile jar = new JarFile(PlugInDirectory + "/" + classFiles[index]);
				Enumeration enumeration;
				String tmp = "";
				// On r�cup�re le contenu du jar
				enumeration = jar.entries();

				while (enumeration.hasMoreElements()) {

					tmp = enumeration.nextElement().toString();

					// On v�rifie que le fichier courant est un .class (et pas
					// un fichier
					// d'informations du jar )
					if (tmp.length() > 6 && tmp.substring(tmp.length() - 6).compareTo(".class") == 0) {

						tmp = tmp.substring(0, tmp.length() - 6);
						tmp = tmp.replaceAll("/", ".");
						System.out.println("class " + tmp);
						ucl.loadClass(tmp);

						// Class tmpClass = Class.forName(tmp ,true,ucl);

					}

				}

			} catch (final Exception e) {
				System.err.println("Unable to create instance of the class \"" + classFiles[index] + "\"");
				System.err.println("Unable to create instance of the class \"" + e);
				return -1;
			}
		}

		return 0;
	}

	public int LoadPlugIn() {
		File directory;
		FilenameFilter classFilter;
		String[] classFiles;
		// Class tmp_class;
		// PlugInStorage stored;

		// Create the file system object for the plug in directory.
		directory = new File(PlugInDirectory);

		// Create a filter to get only class files from the directory.
		classFilter = (dir, name) -> name.endsWith(".jar");

		// Get the list of all filtered filename of the directory.
		// In fact, all *.class files.
		if ((classFiles = directory.list(classFilter)) == null) {
			System.err.println("Nothing to load.");
			return -1;
		}

		// Get all classes associated with *.class files and put them into the
		// array.
		for (int index = 0; index < classFiles.length; ++index) {
			// stored = new PlugInStorage();
			try {
				// Remove the extension of filename.
				// classFiles[index] = classFiles[index].substring(0,
				// classFiles[index].indexOf(".class"));

				// Load the class from the filename. Send ClassNotFoundException
				// or
				// NoClassDefFoundError on error.

				// tmp_class = Class.forName(PackagePath + classFiles[index]);
				// final URL url = new URL("jar:file:" + PlugInDirectory + "/" +
				// classFiles[index] + "!/");
				// final URLClassLoader ucl = new URLClassLoader(new URL[] { url
				// });

				System.out.println("loading : " + classFiles[index]);

			} catch (final Exception e) {
				System.err.println("Unable to create instance of the class \"" + classFiles[index] + "\"");
				System.err.println("Unable to create instance of the class \"" + e);
				return -1;
			}
		}
		loadClasses();
		return 0;
	}

}