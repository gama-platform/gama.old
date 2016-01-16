/*********************************************************************************************
 *
 *
 * 'GamaFileType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.io.File;
import java.util.*;
import org.eclipse.core.runtime.*;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul
 * Modified on 1st Aug. 2010
 * Modified on 30 Dec. 2013
 *
 */
@type(name = IKeyword.FILE, id = IType.FILE, wraps = { IGamaFile.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaFileType extends GamaContainerType<IGamaFile> {

	public static Map<String, IContainerType> extensionsToFullType = new THashMap();
	static Map<String, Set<String>> typesToExtensions = new THashMap();
	static Map<Class, Set<String>> classToExtensions = new THashMap();
	static Map<String, GamaHelper<IGamaFile>> extensionsToFileBuilder = new THashMap();

	/**
	 * Adds a new file type definition.
	 *
	 * @param string a string representin the type of the file in GAML
	 * @param clazz the class that supports this file type
	 * @param s an array of allowed extensions for files of this type
	 */
	public static void addFileTypeDefinition(final String string, final IType keyType, final IType contentType,
		final Class clazz, final GamaHelper<IGamaFile> builder, final String[] extensions) {
		// scope.getGui().debug("GamaFileFactory registering file type " + string + " with extensions " +
		// Arrays.toString(extensions) + " with key type " + keyType + " and content type " + contentType);
		Set<String> exts = new HashSet(Arrays.asList(extensions));
		typesToExtensions.put(string, exts);
		classToExtensions.put(clazz, exts);
		// Added to ensure that extensions do not begin with a "." or contain blank characters
		IContainerType t = (IContainerType) GamaType.from(Types.get(FILE), keyType, contentType);
		for ( String s : extensions ) {
			String ext = s.trim();
			if ( ext.startsWith(".") ) {
				ext = s.substring(1);
			}
			extensionsToFullType.put(s, t);
			extensionsToFileBuilder.put(s, builder);
		}
	}

	/**
	 * Verifies if the path has the correct extension with respect to the type of the file.
	 *
	 * @param type a string representing the type of the file
	 * @param path an absolute or relative file path
	 * @return true if the extension of the path belongs to the extensions of the file type, false if the type is
	 * unknown or if the extension does not belong to its extensions
	 */

	public static boolean verifyExtension(final String type, final String path) {
		IPath p = new Path(path);
		String ext = p.getFileExtension();
		Set<String> extensions = typesToExtensions.get(type);
		if ( extensions == null ) { return false; }
		for ( String s : extensions ) {
			if ( s.equalsIgnoreCase(ext) ) { return true; }
		}
		return false;
	}

	public static boolean verifyExtension(final IGamaFile file, final String path) {
		IPath p = new Path(path);
		String ext = p.getFileExtension();
		Class type = file.getClass();
		Set<String> extensions = classToExtensions.get(type);
		if ( extensions == null ) { return false; }
		for ( String s : extensions ) {
			if ( s.equalsIgnoreCase(ext) ) { return true; }
		}
		return false;
	}

	public static IGamaFile createFile(final IScope scope, final String path) {
		if ( new File(path).isDirectory() ) { return new GamaFolderFile(scope, path); }
		IPath p = new Path(path);
		String ext = p.getFileExtension();
		GamaHelper<IGamaFile> builder = getHelper(ext);
		if ( builder != null ) { return builder.run(scope, path); }
		return new GamaPreferences.GenericFile(path);
	}

	private static GamaHelper<IGamaFile> getHelper(final String extension) {
		for ( String s : extensionsToFileBuilder.keySet() ) {
			if ( s.equalsIgnoreCase(extension) ) { return extensionsToFileBuilder.get(s); }
		}
		return null;
	}

	public static IGamaFile createFile(final IScope scope, final String path, final IModifiableContainer contents) {

		// TODO USE THE BUILDER INSTEAD (NEED TO REGISTER IT TO TAKE TWO PARAMETERS INTO ACCOUNT)

		IGamaFile f = createFile(scope, path);
		if ( f == null ) { return null; }
		f.setWritable(true);
		f.setContents(contents);
		return f;
	}

	@Override
	public IGamaFile cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentType, final boolean copy) {
		if ( obj == null ) { return getDefault(); }
		// 04/03/14 Problem of initialization of files. See if it works or not. No copy of the file is done.
		if ( obj instanceof IGamaFile ) { return (IGamaFile) obj; }
		// if ( obj instanceof IGamaFile ) { return createFile(scope, ((IGamaFile) obj).getPath(), (IGamaFile) obj); }
		if ( obj instanceof String ) {
			if ( param == null ) { return createFile(scope, (String) obj); }
			if ( param instanceof IContainer.Modifiable ) { return createFile(scope, (String) obj,
				(IModifiableContainer) param); }
		}
		return getDefault();
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

	@Override
	public IContainerType typeIfCasting(final IExpression exp) {
		if ( exp.isConst() ) {
			String s = exp.literalValue();
			IPath p = new Path(s);
			String ext = p.getFileExtension();
			IContainerType t = extensionsToFullType.get(ext);
			if ( t != null ) { return t; }
		}
		return super.typeIfCasting(exp);
	}

}
