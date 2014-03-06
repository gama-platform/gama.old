/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import java.io.File;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 7 août 2010
 * 
 * @todo Description
 * 
 */

public abstract class GamaFile<C extends IModifiableContainer<K, V, K, ValueToAdd> & IAddressableContainer<K, V, K, V>, ValueToAdd, K, V>
	implements IGamaFile<C, ValueToAdd, K, V> {

	@Override
	public IContainer<?, K> buildIndexes(final IScope scope, final IContainer value, final IContainerType containerType) {
		fillBuffer(scope);
		return getBuffer().buildIndexes(scope, value, containerType);
	}

	@Override
	public ValueToAdd buildValue(final IScope scope, final Object object, final IContainerType containerType) {
		fillBuffer(scope);
		return getBuffer().buildValue(scope, object, containerType);
	}

	@Override
	public IContainer<?, ValueToAdd> buildValues(final IScope scope, final IContainer objects,
		final IContainerType containerType) {
		fillBuffer(scope);
		return getBuffer().buildValues(scope, objects, containerType);

	}

	@Override
	public K buildIndex(final IScope scope, final Object object, final IContainerType containerType) {
		fillBuffer(scope);
		return getBuffer().buildIndex(scope, object, containerType);
	}

	private File file;

	protected final String path;

	protected boolean writable = false;

	private C buffer;

	public GamaFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		if ( pathName == null ) { throw GamaRuntimeException.error("Attempt to create a null file"); }
		if ( scope != null ) {
			path = scope.getModel().getRelativeFilePath(pathName, false);
			checkValidity();
			// AD 27/04/13 Let the flags of the file remain the same. Can be turned off and on using the "read" and
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

	protected void checkValidity() throws GamaRuntimeException {
		if ( getFile().isDirectory() ) { throw GamaRuntimeException.error(getFile().getAbsolutePath() +
			" is a folder. Files can not overwrite folders"); }
		// For the moment, the verification is disabled, so as to allow "forcing" the loading of a file in a different
		// way (for instance, a .asc file into a text file).
		// if ( !GamaFileType.verifyExtension(this, getPath()) ) { throw GamaRuntimeException.warning("The extension " +
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
		if ( writable ) {
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
	 * @see msi.gama.interfaces.IGamaContainer#add(java.lang.Object, java.lang.Object,
	 * java.lang.Object)
	 */
	// @Override
	// public void add(final IScope scope, final K index, final Object value, final Object param, final boolean all,
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

	// The same but with an index (this index represents the old notion of parameter where it is needed.
	@Override
	public void addValueAtIndex(final IScope scope, final K index, final ValueToAdd value) {
		fillBuffer(scope);
		getBuffer().addValueAtIndex(scope, index, value);
	}

	// Put, that takes a mandatory index (also replaces the parameter)
	@Override
	public void setValueAtIndex(final IScope scope, final K index, final ValueToAdd value) {
		fillBuffer(scope);
		getBuffer().setValueAtIndex(scope, index, value);
	}

	// Then, methods for "all" operations
	// Adds the values if possible, without replacing existing ones
	@Override
	public void addVallues(final IScope scope, final IContainer values) {
		fillBuffer(scope);
		getBuffer().addVallues(scope, values);
	}

	// Adds this value to all slots (if this operation is available), otherwise replaces the values with this one
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
	public void removeAllOccurencesOfValue(final IScope scope, final Object value) {
		fillBuffer(scope);
		getBuffer().removeAllOccurencesOfValue(scope, value);
	}

	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, Object> indexes) {
		fillBuffer(scope);
		getBuffer().removeIndexes(scope, indexes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object, boolean)
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
		return getBuffer().getFromIndicesList(scope, indices);
	}

	@Override
	// @getter( IKeyword.EXTENSION)
	public String getExtension() {
		final String path = getFile().getPath().toLowerCase();
		final int mid = path.lastIndexOf(".");
		if ( mid == -1 ) { return ""; }
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
		if ( getFile() == null ) { return null; }
		if ( !getFile().exists() ) { throw GamaRuntimeException.error("File " + getFile().getAbsolutePath() +
			" does not exist"); }
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
	public IList listValue(final IScope scope, final IType contentsType) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().listValue(scope, contentsType);
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType)
		throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().mapValue(scope, keyType, contentsType);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType) throws GamaRuntimeException {
		return matrixValue(scope, contentsType, null);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize)
		throws GamaRuntimeException {
		return _matrixValue(scope, contentsType, preferredSize);
	}

	protected IMatrix _matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize)
		throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().matrixValue(scope, contentsType, preferredSize);
	}

	//
	// @Override
	// public void remove(final IScope scope, final Object index, final Object value, final boolean all) {
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
	public String toGaml() {
		return "file(" + StringUtils.toGamlString(getPath()) + ")";
	}

	@Override
	public V anyValue(final IScope scope) {
		getContents(scope);
		return getBuffer().anyValue(scope);
	}

	public File getFile() {
		if ( file == null ) {
			file = new File(path);
		}
		return file;
	}

	protected C getBuffer() {
		return buffer;
	}

	protected void setBuffer(C buffer) {
		this.buffer = buffer;
	}
}
