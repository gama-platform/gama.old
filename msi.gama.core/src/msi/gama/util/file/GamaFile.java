/*********************************************************************************************
 *
 *
 * 'GamaFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.File;

import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 7 août 2010
 *
 * @todo Description
 *
 */

public abstract class GamaFile<C extends IModifiableContainer<K, V, K, ValueToAdd> & IAddressableContainer<K, V, K, V>, ValueToAdd, K, V>
		implements IGamaFile<C, ValueToAdd, K, V> {

	private File file;

	protected final String path;

	protected boolean writable = false;

	private C buffer;

	public GamaFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		if (pathName == null) {
			throw GamaRuntimeException.error("Attempt to create a null file", scope);
		}
		if (scope != null) {
			path = FileUtils.constructAbsoluteFilePath(scope, pathName, true);
			checkValidity(scope);
			// AD 27/04/13 Let the flags of the file remain the same. Can be
			// turned off and on using the "read" and
			// "write" operators, so no need to decide for a default here
			// setWritable(false);
		} else {
			path = pathName;
		}
	}

	public GamaFile(final IScope scope, final String pathName, final C container) {
		this(scope, pathName);
		setWritable(true);
		setContents(container);
	}

	protected void checkValidity(final IScope scope) throws GamaRuntimeException {
		if (getFile().isDirectory()) {
			throw GamaRuntimeException
					.error(getFile().getAbsolutePath() + " is a folder. Files can not overwrite folders", scope);
		}
		// For the moment, the verification is disabled, so as to allow
		// "forcing" the loading of a file in a different
		// way (for instance, a .asc file into a text file).
		// if ( !GamaFileType.verifyExtension(this, getPath()) ) { throw
		// GamaRuntimeException.warning("The extension " +
		// this.getExtension() + " is not recognized for this type of file"); }
	}

	@Override
	public void setWritable(final boolean w) {
		writable = w;
		getFile().setWritable(w);
	}

	protected abstract void fillBuffer(IScope scope) throws GamaRuntimeException;

	protected abstract void flushBuffer() throws GamaRuntimeException;

	@Override
	public final void setContents(final C cont) throws GamaRuntimeException {
		if (writable) {
			setBuffer(cont);
		}
	}

	protected String _stringValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().stringValue(scope);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#add(java.lang.Object,
	 * java.lang.Object, java.lang.Object)
	 */
	// @Override
	// public void add(final IScope scope, final K index, final Object value,
	// final Object param, final boolean all,
	// final boolean add) throws GamaRuntimeException {
	// getContents(scope);
	// buffer.add(scope, index, value, param, all, add);
	// flushBuffer();
	// }

	// 09/01/14:Trying to keep the interface simple.
	// Three methods for add and put operations:
	// The simple method, that simply contains the object to add
	@Override
	public void addValue(final IScope scope, final ValueToAdd value) {
		fillBuffer(scope);
		getBuffer().addValue(scope, value);
	}

	// The same but with an index (this index represents the old notion of
	// parameter where it is needed.
	@Override
	public void addValueAtIndex(final IScope scope, final Object index, final ValueToAdd value) {
		fillBuffer(scope);
		getBuffer().addValueAtIndex(scope, index, value);
	}

	// Put, that takes a mandatory index (also replaces the parameter)
	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final ValueToAdd value) {
		fillBuffer(scope);
		getBuffer().setValueAtIndex(scope, index, value);
	}

	// Then, methods for "all" operations
	// Adds the values if possible, without replacing existing ones
	@Override
	public void addValues(final IScope scope, final IContainer values) {
		fillBuffer(scope);
		getBuffer().addValues(scope, values);
	}

	// Adds this value to all slots (if this operation is available), otherwise
	// replaces the values with this one
	@Override
	public void setAllValues(final IScope scope, final ValueToAdd value) {
		fillBuffer(scope);
		getBuffer().setAllValues(scope, value);
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		fillBuffer(scope);
		getBuffer().removeValue(scope, value);
	}

	@Override
	public void removeIndex(final IScope scope, final Object index) {
		fillBuffer(scope);
		getBuffer().removeIndex(scope, index);
	}

	@Override
	public void removeValues(final IScope scope, final IContainer values) {
		fillBuffer(scope);
		getBuffer().removeValues(scope, values);
	}

	@Override
	public void removeAllOccurrencesOfValue(final IScope scope, final Object value) {
		fillBuffer(scope);
		getBuffer().removeAllOccurrencesOfValue(scope, value);
	}

	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, Object> indexes) {
		fillBuffer(scope);
		getBuffer().removeIndexes(scope, indexes);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object,
	 * boolean)
	 */
	@Override
	public boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
		getContents(null);
		return getBuffer().checkBounds(scope, index, forAdding);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().contains(scope, o);

	}

	@Override
	public IGamaFile copy(final IScope scope) {
		// files are supposed to be immutable
		return this;
	}

	@Override
	public Boolean exists() {
		return getFile().exists();
	}

	/*
	 * @see msi.gama.interfaces.IGamaContainer#first()
	 */
	@Override
	public V firstValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().firstValue(scope);
	}

	/*
	 * @see msi.gama.interfaces.IGamaContainer#get(java.lang.Object)
	 */
	@Override
	public V get(final IScope scope, final K index) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().get(scope, index);
	}

	@Override
	public V getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		getContents(scope);
		return (V) getBuffer().getFromIndicesList(scope, indices);
	}

	@Override
	// @getter( IKeyword.EXTENSION)
	public String getExtension() {
		final String path = getFile().getPath().toLowerCase();
		final int mid = path.lastIndexOf(".");
		if (mid == -1) {
			return "";
		}
		return path.substring(mid + 1, path.length());
	}

	@Override
	public String getName() {
		return getFile().getName();
	}

	@Override
	// @getter( IKeyword.PATH)
	public String getPath() {
		return getFile().getPath();
	}

	@Override
	public C getContents(final IScope scope) throws GamaRuntimeException {
		// if ( getFile() == null ) { return null; }
		if (!getFile().exists()) {
			throw GamaRuntimeException.error("File " + getFile().getAbsolutePath() + " does not exist", scope);
		}
		fillBuffer(scope);
		return getBuffer();
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		getContents(scope);
		return getBuffer().isEmpty(scope);
	}

	@Override
	public Boolean isFolder() {
		return getFile().isDirectory();
	}

	@Override
	public Boolean isReadable() {
		return getFile().canRead();
	}

	@Override
	public Boolean isWritable() {
		return getFile().canWrite();
	}

	@Override
	public java.lang.Iterable<? extends V> iterable(final IScope scope) {
		return getContents(scope).iterable(scope);
	}

	@Override
	public V lastValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().lastValue(scope);
	}

	@Override
	public int length(final IScope scope) {
		getContents(scope);
		return getBuffer().length(scope);
	}

	@Override
	public IList listValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().listValue(scope, contentsType, copy);
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().mapValue(scope, keyType, contentsType, copy);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		return matrixValue(scope, contentsType, null, copy);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		return _matrixValue(scope, contentsType, preferredSize, copy);
	}

	protected IMatrix _matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().matrixValue(scope, contentsType, preferredSize, copy);
	}

	//
	// @Override
	// public void remove(final IScope scope, final Object index, final Object
	// value, final boolean all) {
	// getContents(scope);
	// buffer.remove(scope, index, value, all);
	// flushBuffer();
	// }

	@Override
	public IContainer reverse(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().reverse(scope);
		// No side effect
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return _stringValue(scope);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "file('" + /* StringUtils.toGamlString(getPath()) */getPath() + "')";
	}

	@Override
	public V anyValue(final IScope scope) {
		getContents(scope);
		return getBuffer().anyValue(scope);
	}

	public File getFile() {
		if (file == null) {
			file = new File(path);
		}
		return file;
	}

	protected C getBuffer() {
		return buffer;
	}

	protected void setBuffer(final C buffer) {
		this.buffer = buffer;
	}

	public void invalidateContents() {
		buffer = null;
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO what to return ?
		return GamaListFactory.create();
	}

}
