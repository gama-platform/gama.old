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
import java.util.Iterator;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.GamaFileType;

/**
 * Written by drogoul Modified on 7 août 2010
 * 
 * @todo Description
 * 
 */

public abstract class GamaFile<K, V> implements IGamaFile<K, V> {

	private File file;

	protected final String path;

	protected boolean writable = false;

	protected IContainer<K, V> buffer;

	public GamaFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		if ( pathName == null ) { throw new GamaRuntimeException("Attempt to create a null file"); }
		if ( scope != null ) {
			path = scope.getModel().getRelativeFilePath(pathName, false);
			checkValidity();
			setWritable(false);
		} else {
			path = pathName;
		}
	}

	protected void checkValidity() throws GamaRuntimeException {
		if ( getFile().isDirectory() ) { throw new GamaRuntimeException(
			getFile().getAbsolutePath() + " is a folder. Files can not overwrite folders"); }
	}

	@Override
	public void setWritable(final boolean w) {
		writable = w;
		getFile().setWritable(w);
	}

	protected abstract void fillBuffer(IScope scope) throws GamaRuntimeException;

	protected abstract void flushBuffer() throws GamaRuntimeException;

	@Override
	public final void setContents(final IContainer<K, V> cont) throws GamaRuntimeException {
		if ( writable ) {
			buffer = cont;
		}
	}

	protected abstract IGamaFile _copy(IScope scope);

	// protected abstract boolean _isFixedLength();

	protected String _stringValue(IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return buffer.stringValue(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#add(java.lang.Object, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void add(IScope scope, final K index, final Object value, final Object param,
		final boolean all, final boolean add) throws GamaRuntimeException {
		getContents(scope);
		buffer.add(scope, index, value, param, all, add);
		flushBuffer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	public boolean checkBounds(final K index, final boolean forAdding) {
		try {
			getContents(null);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			return false;
		}
		return buffer.checkBounds(index, forAdding);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		getContents(scope);
		return buffer.contains(scope, o);

	}

	@Override
	public IGamaFile copy(IScope scope) {
		return _copy(scope);
	}

	@Override
	public Boolean exists() {
		return getFile().exists();
	}

	/*
	 * @see msi.gama.interfaces.IGamaContainer#first()
	 */
	@Override
	public V first(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return buffer.first(scope);
	}

	/*
	 * @see msi.gama.interfaces.IGamaContainer#get(java.lang.Object)
	 */
	@Override
	public V get(final IScope scope, final K index) throws GamaRuntimeException {
		getContents(scope);
		return buffer.get(scope, index);
	}

	@Override
	public V getFromIndicesList(final IScope scope, final IList indices)
		throws GamaRuntimeException {
		getContents(scope);
		return (V) buffer.getFromIndicesList(scope, indices);
	}

	@Override
	// @getter( IKeyword.EXTENSION)
	public String getExtension() {
		String path = getFile().getPath().toLowerCase();
		int mid = path.lastIndexOf(".");
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
	public IContainer getContents(IScope scope) throws GamaRuntimeException {
		if ( getFile() == null ) { return null; }
		if ( !getFile().exists() ) { throw new GamaRuntimeException("File " +
			getFile().getAbsolutePath() + " does not exist"); }
		fillBuffer(scope);
		return buffer;
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		try {
			getContents(scope);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			return true;
		}
		return buffer.isEmpty(scope);
	}

	@Override
	public Boolean isFolder() {
		return getFile().isDirectory();
	}

	@Override
	public boolean isPgmFile() {
		return path.endsWith(GamaFileType.pgmSuffix);
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
	public Iterator iterator() {
		try {
			getContents(null);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			return GamaList.EMPTY_LIST.iterator();
		}
		return buffer.iterator();
	}

	@Override
	public Iterable<V> iterable(final IScope scope) {
		return getContents(scope).iterable(scope);
	}

	@Override
	public V last(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return buffer.last(scope);
	}

	@Override
	public int length(final IScope scope) {
		try {
			getContents(scope);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			return 0;
		}
		return buffer.length(scope);
	}

	@Override
	public IList listValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return buffer.listValue(scope);
	}

	@Override
	public GamaMap mapValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return buffer.mapValue(scope);
	}

	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		return matrixValue(scope, null);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize)
		throws GamaRuntimeException {
		return _matrixValue(scope, preferredSize);
	}

	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize)
		throws GamaRuntimeException {
		getContents(scope);
		return buffer.matrixValue(scope, preferredSize);
	}

	@Override
	public void remove(IScope scope, Object index, Object value, boolean all) {
		getContents(scope);
		buffer.remove(scope, index, value, all);
		flushBuffer();
	}

	@Override
	public IContainer reverse(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		return buffer.reverse(scope);
		// No side effect
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return _stringValue(scope);
	}

	@Override
	public String toGaml() {
		return (writable ? "write(" : "read(") + getKeyword() + "(" +
			StringUtils.toGamlString(getPath()) + "))";
	}

	@Override
	public abstract String getKeyword();

	@Override
	public V any(final IScope scope) {
		try {
			getContents(scope);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			e.printStackTrace();
		}
		return buffer.any(scope);
	}

	public File getFile() {
		if ( file == null ) {
			file = new File(path);
		}
		return file;
	}
}
