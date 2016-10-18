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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import msi.gama.common.interfaces.IKeyword;
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
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 7 août 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class GamaFile<C extends IModifiableContainer<K, V, K, ValueToAdd> & IAddressableContainer<K, V, K, V>, ValueToAdd, K, V>
		implements IGamaFile<C, ValueToAdd, K, V> {

	private File file;
	private String path;
	protected final String originalPath;
	protected URL url;
	protected boolean writable = false;
	private C buffer;

	public GamaFile(final IScope scope, final String pn) throws GamaRuntimeException {
		originalPath = pn;
		if (originalPath == null) {
			throw GamaRuntimeException.error("Attempt to create a null file", scope);
		} else if (originalPath.startsWith("http")) {
			setPath(fetchFromURL(scope, originalPath));
			if (getPath(scope) == null) {
				// We do not attempt to create the file. It will probably be
				// taken in charge later directly from the URL
				setPath("");
				return;
			}
		}
	}

	/**
	 * Whether or not passing an URL will automatically make GAMA cache its
	 * contents in a temp file. Should be redefined by GamaFiles that can
	 * retrieve from URL directly (like, e.g., GeoTools'datastore-backed files).
	 * If false, the url will be initialized, but the path will be set to the
	 * empty string and no attempt will be made to download data later. In that
	 * case, it is the responsibility of subclasses to use the url -- and NOT
	 * the path -- to download the contents of the file later (for example in
	 * fillBuffer()). The default is true.
	 * 
	 * @return true or false depending on whether the contents should be cached
	 *         in a temp file
	 */
	protected boolean automaticallyFetchFromURL() {
		return true;
	}

	// Might be necessary to redefine it in order to fetch additional resources
	protected String fetchFromURL(final IScope scope, final String urlPath) {
		String pathName = "";
		try {
			url = new URL(urlPath);
		} catch (final MalformedURLException e1) {
			throw GamaRuntimeException.error("Malformed URL " + urlPath, scope);
		}
		if (!automaticallyFetchFromURL()) {
			return null;
		}
		final String status = "Downloading file " + urlPath.substring(urlPath.lastIndexOf('/'));
		try {
			scope.getGui().getStatus().beginSubStatus(status);

			final URLConnection connection = url.openConnection();
			final long size = connection.getContentLengthLong();
			final boolean sizeKnown = size > 0;
			if (url != null) {
				final String suffix = url.getPath().replaceAll("/", "_");
				pathName = FileUtils.constructAbsoluteTempFilePath(scope, suffix);
				try (final InputStream r = url.openStream(); OutputStream fw = new FileOutputStream(pathName)) {
					long doneSoFar = 0;
					final byte[] b = new byte[2048];
					int length;
					while ((length = r.read(b)) != -1) {
						if (sizeKnown) {
							doneSoFar += 2048;
							scope.getGui().getStatus()
									.setSubStatusCompletion((double) size / (double) (size - doneSoFar));
						}
						fw.write(b, 0, length);
					}
				}
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			scope.getGui().getStatus().endSubStatus(status);
		}
		return pathName;

	}

	public GamaFile(final IScope scope, final String pathName, final C container) {
		this(scope, pathName);
		setWritable(scope, true);
		setContents(container);
	}

	protected void checkValidity(final IScope scope) throws GamaRuntimeException {
		if (getFile(scope).exists() && getFile(scope).isDirectory()) {
			throw GamaRuntimeException
					.error(getFile(scope).getAbsolutePath() + " is a folder. Files can not overwrite folders", scope);
		}
	}

	@Override
	public void setWritable(final IScope scope, final boolean w) {
		writable = w;
		getFile(scope).setWritable(w);
	}

	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		throw GamaRuntimeException.error("Loading is not yet impletemented for files of type "
				+ this.getExtension(scope) + ". Please post a request for enhancement to implement "
				+ getClass().getSimpleName() + ".fillBuffer(IScope, Facets)", scope);
	}

	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		throw GamaRuntimeException.error("Saving is not yet impletemented for files of type " + this.getExtension(scope)
				+ ". Please post a request for enhancement to implement " + getClass().getSimpleName()
				+ ".flushBuffer(IScope, Facets)", scope);
	}

	@Override
	public final void setContents(final C cont) throws GamaRuntimeException {
		if (writable) {
			setBuffer(cont);
		}
	}

	protected String _stringValue(final IScope scope) throws GamaRuntimeException {
		return getPath(scope);
	}

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
	public void removeIndexes(final IScope scope, final IContainer<?, ?> indexes) {
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
	public Boolean exists(final IScope scope) {
		return getFile(scope).exists();
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
	public String getExtension(final IScope scope) {
		final String path = getPath(scope).toLowerCase();
		final int mid = path.lastIndexOf(".");
		if (mid == -1) {
			return "";
		}
		return path.substring(mid + 1, path.length());
	}

	@Override
	public String getName(final IScope scope) {
		return getFile(scope).getName();
	}

	@Override
	public String getPath(final IScope scope) {
		if (path == null) {
			if (scope == null || scope.getExperiment() == null)
				path = originalPath;
			else {
				setPath(FileUtils.constructAbsoluteFilePath(scope, originalPath, shouldExist()));
				checkValidity(scope);
			}
		}
		return path;
	}

	@Override
	public C getContents(final IScope scope) throws GamaRuntimeException {
		if (buffer == null && !exists(scope)) {
			throw GamaRuntimeException.error("File " + getFile(scope).getAbsolutePath() + " does not exist", scope);
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
	public Boolean isFolder(final IScope scope) {
		return getFile(scope).isDirectory();
	}

	@Override
	public Boolean isReadable(final IScope scope) {
		return getFile(scope).canRead();
	}

	@Override
	public Boolean isWritable(final IScope scope) {
		return getFile(scope).canWrite();
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
	public IList<V> listValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().listValue(scope, contentsType, copy);
	}

	@Override
	public StreamEx<V> stream(final IScope scope) {
		getContents(scope);
		return getBuffer().stream(scope);
	}

	@Override
	public GamaMap<?, ?> mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().mapValue(scope, keyType, contentsType, copy);
	}

	@Override
	public IMatrix<?> matrixValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		return matrixValue(scope, contentsType, null, copy);
	}

	@Override
	public IMatrix<?> matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		return _matrixValue(scope, contentsType, preferredSize, copy);
	}

	protected IMatrix _matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		getContents(scope);
		return getBuffer().matrixValue(scope, contentsType, preferredSize, copy);
	}

	@Override
	public IContainer<?, ?> reverse(final IScope scope) throws GamaRuntimeException {
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
		return "file('" + /* StringUtils.toGamlString(getPath()) */getPath(null) + "')";
	}

	@Override
	public V anyValue(final IScope scope) {
		getContents(scope);
		return getBuffer().anyValue(scope);
	}

	public File getFile(final IScope scope) {
		if (file == null) {
			file = new File(getPath(scope));
		}
		return file;
	}

	@Override
	public C getBuffer() {
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

	/**
	 * This method is being called from the save statement (see
	 * SaveStatement.java). The scope and all the facets declared in the save
	 * statement are passed as parameters, which allows the programmer to
	 * retrieve them (for instance, to get the crs for shape files, or the
	 * attributes to save from a list of agents, etc.). This method cannot be
	 * redefined. Instead, programmers should redefine flushBuffer(), which
	 * takes the same arguments
	 */

	@Override
	public final void save(final IScope scope, final Facets saveFacets) {
		final IExpression exp = saveFacets.getExpr(IKeyword.REWRITE);
		final boolean overwrite = exp == null || Cast.asBool(scope, exp.value(scope));
		if (overwrite && getFile(scope).exists()) {
			getFile(scope).delete();
		}
		if (!writable)
			throw GamaRuntimeException.error("File " + getName(scope) + " is not writable", scope);
		flushBuffer(scope, saveFacets);

	}

	protected void setPath(final String path) {
		this.path = path;
	}

}
