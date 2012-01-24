/**
 * Created by drogoul, 23 janv. 2012
 * 
 */
package msi.gaml.compilation;

import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.util.*;
import org.osgi.framework.Bundle;

/**
 * The class GamaClassLoader. A custom class loader that can build class loaders for the bundles
 * containing additions to GAML, and keeps a history of them in order to resolve the classes they
 * refer.
 * 
 * @author drogoul
 * @since 23 janv. 2012
 * 
 */
public class GamaClassLoader extends ClassLoader {

	public class BundleClassLoader extends ClassLoader {

		private final Bundle bundle;

		/**
		 * Constructs a new <code>BundleDelegatingClassLoader</code> instance.
		 */
		protected BundleClassLoader(final Bundle bundle) {
			super(null);
			this.bundle = bundle;
		}

		@Override
		protected Class findClass(final String name) throws ClassNotFoundException {
			try {
				return bundle.loadClass(name);
			} catch (ClassNotFoundException cnfe) {
				throw new ClassNotFoundException(name + " not found from [" +
					bundle.getSymbolicName() + "]", cnfe);
			} catch (NoClassDefFoundError ncdfe) {
				throw new ClassNotFoundException(name + " not defined in [" +
					bundle.getSymbolicName() + "]", ncdfe);
			}
		}

		@Override
		protected URL findResource(final String name) {
			return bundle.getResource(name);
		}

		@Override
		protected Enumeration findResources(final String name) throws IOException {
			return bundle.getResources(name);
		}

		@Override
		public URL getResource(final String name) {
			return findResource(name);
		}

		@Override
		protected synchronized Class loadClass(final String name, final boolean resolve)
			throws ClassNotFoundException {
			Class clazz = findClass(name);
			if ( resolve ) {
				resolveClass(clazz);
			}
			return clazz;
		}

	}

	private static GamaClassLoader loader;
	private final List<BundleClassLoader> loaders = new ArrayList();

	public static GamaClassLoader getInstance() {
		if ( loader == null ) {
			loader = new GamaClassLoader();
		}
		return loader;
	}

	private GamaClassLoader() {}

	public BundleClassLoader addBundle(final Bundle bundle) {
		// TODO verify if the bundle is not already known
		final BundleClassLoader loader = createBundleClassLoaderFor(bundle);
		loaders.add(loader);
		return loader;
	}

	@Override
	protected Class findClass(final String name) throws ClassNotFoundException {

		for ( int i = 0, n = loaders.size(); i < n; i++ ) {
			try {
				return loaders.get(i).loadClass(name);
			} catch (ClassNotFoundException cnfe) {}
		}
		throw new ClassNotFoundException(name + " not found in GAMA");

	}

	@Override
	protected URL findResource(final String name) {
		for ( int i = 0, n = loaders.size(); i < n; i++ ) {
			URL url = loaders.get(i).getResource(name);
			if ( url != null ) { return url; }
		}
		return null;
	}

	@Override
	protected Enumeration findResources(final String name) throws IOException {
		for ( int i = 0, n = loaders.size(); i < n; i++ ) {
			try {
				return loaders.get(i).findResources(name);
			} catch (IOException cnfe) {}
		}
		throw new IOException("no resources named " + name + " found in GAMA");
	}

	@Override
	public URL getResource(final String name) {
		return findResource(name);
	}

	@Override
	protected synchronized Class loadClass(final String name, final boolean resolve)
		throws ClassNotFoundException {
		Class clazz = findClass(name);
		if ( resolve ) {
			resolveClass(clazz);
		}
		return clazz;
	}

	private BundleClassLoader createBundleClassLoaderFor(final Bundle bundle) {
		return (BundleClassLoader) AccessController.doPrivileged(new PrivilegedAction() {

			@Override
			public Object run() {
				return new BundleClassLoader(bundle);
			}
		});
	}

}
