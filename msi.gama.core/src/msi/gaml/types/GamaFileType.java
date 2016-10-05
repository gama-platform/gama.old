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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.file.GamaFolderFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 1st Aug. 2010 Modified on 30 Dec. 2013
 *
 */
@type(name = IKeyword.FILE, id = IType.FILE, wraps = {
		IGamaFile.class }, kind = ISymbolKind.Variable.CONTAINER, concept = { IConcept.TYPE, IConcept.FILE })
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaFileType extends GamaContainerType<IGamaFile> {

	public static Map<String, ParametricFileType> extensionsToFullType = new THashMap<>();
	static Map<String, ParametricFileType> aliasesToFullType = new THashMap<>();
	static Map<String, Set<String>> aliasesToExtensions = new THashMap<>();
	static int currentFileTypeIndex = 1000;

	/**
	 * Adds a new file type definition.
	 *
	 * @param string
	 *            a string representin the type of the file in GAML
	 * @param clazz
	 *            the class that supports this file type
	 * @param s
	 *            an array of allowed extensions for files of this type
	 */
	public static void addFileTypeDefinition(final String alias, final IType bufferType, final IType keyType,
			final IType contentType, final Class clazz, final GamaHelper<IGamaFile> builder,
			final String[] extensions) {
		// GAMA.getGui().debug("GamaFileType registering file type " + alias + "
		// with extensions " +
		// Arrays.toString(extensions) + " with key type " + keyType + " and
		// content type " + contentType);

		final Set<String> exts = new HashSet();
		// Added to ensure that extensions do not begin with a "." or contain
		// blank characters
		for (final String ext : extensions) {
			String clean = ext.toLowerCase();
			if (clean.startsWith(".")) {
				clean = clean.substring(1);
			}
			exts.add(clean);
		}
		aliasesToExtensions.put(alias, exts);
		// classToExtensions.put(clazz, exts);
		final ParametricFileType t = new ParametricFileType(alias + "_file", clazz, builder, bufferType, keyType,
				contentType);
		aliasesToFullType.put(alias, t);
		for (final String s : exts) {
			extensionsToFullType.put(s, t);
		}
		t.setParent(Types.FILE);
		Types.builtInTypes.initType(alias + "_file", t, IType.AVAILABLE_TYPES + ++currentFileTypeIndex,
				ISymbolKind.Variable.CONTAINER, new Class[] { clazz });

	}

	public static ParametricFileType getTypeFromAlias(final String alias) {
		final ParametricFileType ft = aliasesToFullType.get(alias);
		if (ft == null) {
			return ParametricFileType.getGenericInstance();
		}
		return ft;
	}

	public static ParametricFileType getTypeFromFileName(final String fileName) {
		final IPath p = new Path(fileName);
		final String ext = p.getFileExtension();
		ParametricFileType ft = extensionsToFullType.get(ext);
		if (ft == null) {
			ft = ParametricFileType.getGenericInstance();
		}
		return ft;
	}

	/**
	 * Verifies if the path has the correct extension with respect to the type
	 * of the file.
	 *
	 * @param type
	 *            a string representing the type of the file
	 * @param path
	 *            an absolute or relative file path
	 * @return true if the extension of the path belongs to the extensions of
	 *         the file type, false if the type is unknown or if the extension
	 *         does not belong to its extensions
	 */

	public static boolean verifyExtension(final String alias, final String path) {
		final ParametricFileType ft = getTypeFromAlias(alias);
		final ParametricFileType ft2 = getTypeFromFileName(path);
		return ft.equals(ft2);
	}

	public static IGamaFile createFile(final IScope scope, final String path, final IModifiableContainer contents) {
		if (new File(path).isDirectory()) {
			return new GamaFolderFile(scope, path);
		}
		final ParametricFileType ft = getTypeFromFileName(path);
		return ft.createFile(scope, path, contents);
	}

	@Override
	public IGamaFile cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentType, final boolean copy) {
		if (obj == null) {
			return getDefault();
		}
		// 04/03/14 Problem of initialization of files. See if it works or not.
		// No copy of the file is done.
		if (obj instanceof IGamaFile) {
			return (IGamaFile) obj;
		}
		if (obj instanceof String) {
			if (param == null) {
				return createFile(scope, (String) obj, null);
			}
			if (param instanceof IModifiableContainer) {
				return createFile(scope, (String) obj, (IModifiableContainer) param);
			}
		}
		return getDefault();
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

	@Override
	public IContainerType typeIfCasting(final IExpression exp) {
		if (exp.isConst()) {
			final String s = exp.literalValue();
			final ParametricFileType ft = getTypeFromFileName(s);
			return ft;
		}
		return super.typeIfCasting(exp);
	}

}
