/*******************************************************************************************************
 *
 * msi.gaml.types.GamaFileType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.file.GamaFolderFile;
import msi.gama.util.file.GamaGifFile;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.GamaGetter;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * Written by drogoul Modified on 1st Aug. 2010 Modified on 30 Dec. 2013
 *
 */
@type (
		name = IKeyword.FILE,
		id = IType.FILE,
		wraps = { IGamaFile.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.FILE },
		doc = @doc ("Generic super-type of all file types"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaFileType extends GamaContainerType<IGamaFile> {

	public static Map<String, ParametricFileType> extensionsToFullType = GamaMapFactory.createUnordered();
	static Map<String, ParametricFileType> aliasesToFullType = GamaMapFactory.createUnordered();
	static Multimap<String, String> aliasesToExtensions = HashMultimap.<String, String> create();
	static int currentFileTypeIndex = 1000;

	/**
	 * Adds a new file type definition.
	 *
	 * @param string
	 *            a string representing the type of the file in GAML
	 * @param clazz
	 *            the class that supports this file type
	 * @param s
	 *            an array of allowed extensions for files of this type
	 */
	public static void addFileTypeDefinition(final String alias, final IType<?> bufferType, final IType<?> keyType,
			final IType<?> contentType, final Class clazz, final GamaGetter.Unary<IGamaFile<?, ?>> builder,
			final String[] extensions, final String plugin) {
		// Added to ensure that extensions do not begin with a "." or contain
		// blank characters
		for (final String ext : extensions) {
			String clean = ext.toLowerCase();
			if (clean.startsWith(".")) {
				clean = clean.substring(1);
			}
			aliasesToExtensions.put(alias, clean);
		}

		// classToExtensions.put(clazz, exts);
		final ParametricFileType t =
				new ParametricFileType(alias + "_file", clazz, builder, bufferType, keyType, contentType);
		t.setDefiningPlugin(plugin);
		aliasesToFullType.put(alias, t);
		for (final String s : aliasesToExtensions.get(alias)) {
			extensionsToFullType.put(s, t);
		}
		t.setParent(Types.FILE);
		Types.builtInTypes.initType(alias + "_file", t, IType.AVAILABLE_TYPES + ++currentFileTypeIndex,
				ISymbolKind.Variable.CONTAINER, clazz);

	}

	public static ParametricFileType getTypeFromAlias(final String alias) {
		final ParametricFileType ft = aliasesToFullType.get(alias);
		if (ft == null) { return ParametricFileType.getGenericInstance(); }
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
	 * Verifies if the path has the correct extension with respect to the type of the file.
	 *
	 * @param type
	 *            a string representing the type of the file
	 * @param path
	 *            an absolute or relative file path
	 * @return true if the extension of the path belongs to the extensions of the file type, false if the type is
	 *         unknown or if the extension does not belong to its extensions
	 */

	public static boolean verifyExtension(final String alias, final String path) {
		final ParametricFileType ft = getTypeFromAlias(alias);
		final ParametricFileType ft2 = getTypeFromFileName(path);
		return ft.equals(ft2);
	}

	public static IGamaFile createFile(final IScope scope, final String path, final IModifiableContainer contents) {
		if (new File(path).isDirectory()) { return new GamaFolderFile(scope, path); }
		final ParametricFileType ft = getTypeFromFileName(path);
		return ft.createFile(scope, path, contents);
	}

	public static GamaImageFile createImageFile(final IScope scope, final String path,
			final IModifiableContainer contents) {
		if (new File(path).isDirectory()) { return null; }
		if (path.endsWith(".gif") || path.endsWith(".GIF")) {
			if (contents == null) {
				return new GamaGifFile(scope, path);
			} else if (contents instanceof IMatrix) {
				return new GamaGifFile(scope, path, (IMatrix<Integer>) contents);
			}
		} else if (contents == null) {
			return new GamaImageFile(scope, path);
		} else if (contents instanceof IMatrix) { return new GamaImageFile(scope, path, (IMatrix<Integer>) contents); }
		return null;
	}

	@Override
	public IGamaFile cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentType, final boolean copy) {
		if (obj == null) { return getDefault(); }
		// 04/03/14 Problem of initialization of files. See if it works or not.
		// No copy of the file is done.
		if (obj instanceof IGamaFile) { return (IGamaFile) obj; }
		if (obj instanceof String) {
			if (param == null) { return createFile(scope, (String) obj, null); }
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
		if (exp.isConst() && exp.isContextIndependant()) {
			final String s = Cast.asString(null, exp.getConstValue());
			final ParametricFileType ft = getTypeFromFileName(s);
			return ft;
		}
		return super.typeIfCasting(exp);
	}

}