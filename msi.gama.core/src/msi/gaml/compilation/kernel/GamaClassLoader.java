/*******************************************************************************************************
 *
 * msi.gaml.compilation.kernel.GamaClassLoader.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation.kernel;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;

/**
 * The class GamaClassLoader. A custom class loader that can build class loaders
 * for the bundles containing additions to GAML, and keeps a history of them in
 * order to resolve the classes they refer.
 * 
 * @author drogoul
 * @since 23 janv. 2012
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaClassLoader extends ClassLoader {

	public static class ListBasedLoader extends ClassLoader {

		ListBasedLoader() {
		}

		Set<Class> classes = new LinkedHashSet();

		public boolean addNewClass(final Class c) {
			return classes.add(c);
		}

		@Override
		protected synchronized Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			final Class clazz = findClass(name);
			if (resolve) {
				resolveClass(clazz);
			}
			return clazz;
		}

		@Override
		protected Class findClass(final String name) throws ClassNotFoundException {
			for (final Class c : classes) {
				if (c.getCanonicalName().equals(name)) {
					return c;
				}
			}
			return null;
		}

	}

	public static class BundleClassLoader extends ClassLoader {

		private final Bundle bundle;

		/**
		 * Constructs a new <code>BundleDelegatingClassLoader</code> instance.
		 */
		protected BundleClassLoader(final Bundle bundle) {
			super(null);
			this.bundle = bundle;
		}

		public Bundle getBundle() {
			return bundle;
		}

		@Override
		protected Class findClass(final String name) throws ClassNotFoundException {
			try {
				return bundle.loadClass(name);
			} catch (final ClassNotFoundException cnfe) {
				throw new ClassNotFoundException(name + " not found in [" + bundle.getSymbolicName() + "]", cnfe);
			} catch (final NoClassDefFoundError ncdfe) {
				throw new ClassNotFoundException(name + " not defined in [" + bundle.getSymbolicName() + "]", ncdfe);
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
		protected synchronized Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			final Class clazz = findClass(name);
			if (resolve) {
				resolveClass(clazz);
			}
			return clazz;
		}

	}

	private volatile static GamaClassLoader loader;
	private final List<ClassLoader> loaders = new ArrayList<>();

	public static GamaClassLoader getInstance() {
		if (loader == null) {
			loader = new GamaClassLoader();
		}
		return loader;
	}

	private final static ListBasedLoader customLoader = new ListBasedLoader();

	private GamaClassLoader() {
		super();
	}

	//
	// public boolean addNewClass(final Class c) {
	// return customLoader.addNewClass(c);
	// }

	public ClassLoader addBundle(final Bundle bundle) {
		// TODO verify if the bundle is not already known
		final BundleClassLoader loader = createBundleClassLoaderFor(bundle);
		return addLoader(loader);
	}

	public ClassLoader addLoader(final ClassLoader loader) {
		loaders.add(loader);
		return loader;
	}

	@Override
	protected Class findClass(final String name) throws ClassNotFoundException {

		for (int i = 0, n = loaders.size(); i < n; i++) {
			try {
				return loaders.get(i).loadClass(name);
			} catch (final ClassNotFoundException cnfe) {
			}
		}
		final Class c = customLoader.findClass(name);
		if (c == null) {
			throw new ClassNotFoundException(name + " not found in GAMA");
		}
		return c;

	}

	@Override
	protected URL findResource(final String name) {
		for (int i = 0, n = loaders.size(); i < n; i++) {
			final URL url = loaders.get(i).getResource(name);
			if (url != null) {
				return url;
			}
		}
		return null;
	}

	//
	// @Override
	// protected Enumeration findResources(final String name) throws IOException
	// {
	// for ( int i = 0, n = loaders.size(); i < n; i++ ) {
	// try {
	// return loaders.get(i).findResources(name);
	// } catch (IOException cnfe) {}
	// }
	// throw new IOException("no resources named " + name + " found in GAMA");
	// }

	@Override
	public URL getResource(final String name) {
		return findResource(name);
	}

	@Override
	protected synchronized Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		final Class clazz = findClass(name);
		if (resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	private BundleClassLoader createBundleClassLoaderFor(final Bundle bundle) {
		return (BundleClassLoader) AccessController
				.doPrivileged((PrivilegedAction) () -> new BundleClassLoader(bundle));
	}

}
