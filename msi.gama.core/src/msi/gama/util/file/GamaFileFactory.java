/**
 * Created by drogoul, 26 déc. 2013
 * 
 */
package msi.gama.util.file;

import java.util.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.GamaHelper;
import org.eclipse.core.runtime.*;

/**
 * Class GamaFileFactory.
 * 
 * @author drogoul
 * @since 26 déc. 2013
 * 
 */
public class GamaFileFactory {

	static Map<String, Set<String>> typesExtensions = new HashMap();
	static Map<Class, Set<String>> classExtensions = new HashMap();
	static Map<String, GamaHelper<IGamaFile>> extensionsToFiles = new HashMap();

	/**
	 * Adds a new file type definition.
	 * 
	 * @param string a string representin the type of the file in GAML
	 * @param clazz the class that supports this file type
	 * @param s an array of allowed extensions for files of this type
	 */
	public static void addFileTypeDefinition(final String string, final Class clazz,
		final GamaHelper<IGamaFile> builder, final String[] extensions) {
		GuiUtils.debug("GamaFileFactory registering file type " + string + " with extensions " +
			Arrays.toString(extensions));
		Set<String> exts = new HashSet(Arrays.asList(extensions));
		typesExtensions.put(string, exts);
		classExtensions.put(clazz, exts);
		for ( String s : extensions ) {
			extensionsToFiles.put(s, builder);
		}
	}

	/**
	 * Verifies if the path has the correct extension with respect to the type of the file.
	 * 
	 * @param type a string representing the type of the file
	 * @param path an absolute or relative file path
	 * @return true if the extension of the path belongs to the extensions of the file type, false if the type is
	 *         unknown or if the extension does not belong to its extensions
	 */

	public static boolean verifyExtension(final String type, final String path) {
		IPath p = new Path(path);
		String ext = p.getFileExtension();
		Set<String> extensions = typesExtensions.get(type);
		if ( extensions == null ) { return false; }
		return extensions.contains(ext);
	}

	public static boolean verifyExtension(final IGamaFile file, final String path) {
		IPath p = new Path(path);
		String ext = p.getFileExtension();
		Class type = file.getClass();
		Set<String> extensions = classExtensions.get(type);
		if ( extensions == null ) { return false; }
		return extensions.contains(ext);
	}

	public static IGamaFile buildFile(final IScope scope, final String path) {
		IPath p = new Path(path);
		String ext = p.getFileExtension();
		GamaHelper<IGamaFile> builder = extensionsToFiles.get(ext);
		if ( builder != null ) { return builder.run(scope, path); }
		return null;
	}

}
