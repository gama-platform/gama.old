/*******************************************************************************************************
 *
 * IGamaFile.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import org.eclipse.emf.common.util.URI;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IList;
import msi.gama.util.IModifiableContainer;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 14 nov. 2011
 *
 * @todo Description
 *
 * @param <K>
 * @param <V>
 */
@vars ({ @variable (
		name = IKeyword.NAME,
		type = IType.STRING,
		doc = { @doc ("Returns the name of the receiver file") }),
		@variable (
				name = IKeyword.EXTENSION,
				type = IType.STRING,
				doc = { @doc ("Returns the extension of the receiver file") }),
		@variable (
				name = IKeyword.PATH,
				type = IType.STRING,
				doc = { @doc ("Returns the absolute path of the receiver file") }),
		@variable (
				name = IKeyword.EXISTS,
				type = IType.BOOL,
				doc = { @doc ("Returns whether the receiver file exists or not in the filesystem") }),
		@variable (
				name = IKeyword.ISFOLDER,
				type = IType.BOOL,
				doc = { @doc ("Returns whether the receiver file is a folder or not") }),
		@variable (
				name = IKeyword.READABLE,
				type = IType.BOOL,
				doc = { @doc ("Returns true if the contents of the receiver file can be read") }),
		@variable (
				name = IKeyword.WRITABLE,
				type = IType.BOOL,
				doc = { @doc ("Returns true if the contents of the receiver file can be written") }),
		@variable (
				name = IKeyword.ATTRIBUTES,
				type = IType.LIST,
				of = IType.STRING,
				doc = { @doc ("Retrieves the list of 'attributes' present in the receiver files that support this concept (and an empty list for the others). For instance, in a CSV file, the attributes represent the headers of the columns (if any); in a shape file, the attributes provided to the objects, etc.") }),
		@variable (
				name = IKeyword.CONTENTS,
				type = ITypeProvider.WRAPPED,
				of = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				index = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
				doc = { @doc ("Returns the contents of the receiver file in the form of a container") }) })
@SuppressWarnings ({ "rawtypes" })
public interface IGamaFile<C extends IModifiableContainer, Contents>
		extends IAddressableContainer, IModifiableContainer {

	/**
	 * Sets the writable.
	 *
	 * @param scope
	 *            the scope
	 * @param w
	 *            the w
	 */
	void setWritable(IScope scope, final boolean w);

	/**
	 * Sets the contents.
	 *
	 * @param cont
	 *            the new contents
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	void setContents(final C cont) throws GamaRuntimeException;

	/**
	 * Copy.
	 *
	 * @param scope
	 *            the scope
	 * @return the i gama file
	 */
	@Override
	IGamaFile copy(IScope scope);

	/**
	 * Gets the buffer.
	 *
	 * @return the buffer
	 */
	C getBuffer();

	/**
	 * Exists.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 */
	@getter (
			value = IKeyword.EXISTS,
			initializer = true)
	Boolean exists(IScope scope);

	/**
	 * Gets the extension.
	 *
	 * @param scope
	 *            the scope
	 * @return the extension
	 */
	@getter (
			value = IKeyword.EXTENSION,
			initializer = true)
	String getExtension(IScope scope);

	/**
	 * Gets the name.
	 *
	 * @param scope
	 *            the scope
	 * @return the name
	 */
	@getter (
			value = IKeyword.NAME,
			initializer = true)
	String getName(IScope scope);

	/**
	 * Gets the path.
	 *
	 * @param scope
	 *            the scope
	 * @return the path
	 */
	@getter (
			value = IKeyword.PATH,
			initializer = true)
	String getPath(IScope scope);

	/**
	 * Gets the contents.
	 *
	 * @param scope
	 *            the scope
	 * @return the contents
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@getter (IKeyword.CONTENTS)
	C getContents(IScope scope) throws GamaRuntimeException;

	/**
	 * Gets the attributes.
	 *
	 * @param scope
	 *            the scope
	 * @return the attributes
	 */
	@getter (IKeyword.ATTRIBUTES)
	/**
	 * Retrieves the list of "attributes" present in files that support this concept (and an empty list for the others).
	 * For instance, in a CSV file, attributes represent the headers of the columns (if any); in a shape file, the
	 * attributes provided to the objects, etc.
	 *
	 * @param scope
	 * @return a list of string or an empty list (never null)
	 */
	IList<String> getAttributes(IScope scope);

	/**
	 * Checks if is folder.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 */
	@getter (
			value = IKeyword.ISFOLDER,
			initializer = true)
	Boolean isFolder(IScope scope);

	/**
	 * Checks if is readable.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 */
	@getter (
			value = IKeyword.READABLE,
			initializer = true)
	Boolean isReadable(IScope scope);

	/**
	 * Checks if is writable.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 */
	@getter (
			value = IKeyword.WRITABLE,
			initializer = true)
	Boolean isWritable(IScope scope);

	/**
	 * Compute envelope.
	 *
	 * @param scope
	 *            the scope
	 * @return the envelope 3 D
	 */
	Envelope3D computeEnvelope(final IScope scope);

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param parameters
	 *            the parameters
	 */
	void save(IScope scope, Facets parameters);

	/**
	 * Gets the original path.
	 *
	 * @return the original path
	 */
	String getOriginalPath();

	/**
	 * Contains key.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	@Override
	default boolean containsKey(final IScope scope, final Object o) {
		final C contents = getContents(scope);
		return contents != null && contents.contains(scope, o);
	}

	/**
	 * Gets the URI relative to workspace.
	 *
	 * @return the URI relative to workspace
	 */
	URI getURIRelativeToWorkspace();

}