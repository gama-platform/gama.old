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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.descript;

import java.io.File;
import java.net.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.ecore.resource.Resource;

public class GamlDescriptIO {

	private IUpdateOnChange callback;
	private volatile boolean canRun;
	private volatile boolean isRunning;

	/**
	 * The solution of Bill Pugh
	 * see http://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh
	 * 
	 * @return GamlDescriptIO unique instance of singleton
	 */
	public final synchronized static GamlDescriptIO getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {

		private static final GamlDescriptIO INSTANCE = new GamlDescriptIO();
	}

	// Private constructor prevents instantiation from other classes
	private GamlDescriptIO() {
		canRun = true;
		isRunning = false;
	}

	/**
	 * @see IUpdateOnChange#update()
	 * @param c the callback that implements IUpdateOnChange
	 */
	public void setCallback(final IUpdateOnChange c) {
		this.callback = c;
	}

	public void process(final Resource r) throws Exception {
		if ( canRun ) {
			isRunning = true;
			try {
				String filePath = getPath(r);
				if ( callback != null ) {
					callback.update(filePath, r);
				}
			} finally {
				isRunning = false;
			}
		}
	}

	public void canRun(final boolean b) {
		canRun = b;
	}

	public boolean isBuilding() {
		return isRunning;
	}

	public IUpdateOnChange getCallback() {
		return callback;
	}

	/**
	 * @see org.eclipse.core.resources.IResource#getLocation()
	 * @see org.eclipse.core.runtime.IPath#toOSString()
	 * @param f File from a Project of the Workspace
	 * @return a platform-dependent string representation of this path
	 */
	public static String getPath(final IResource f) {
		return f.getLocation().toOSString();
	}

	/**
	 * @see org.eclipse.emf.ecore.resource.Resource#getURI()
	 * @see org.eclipse.emf.common.util.URI#toString()
	 * @see #getPath(URL)
	 * @param r
	 * @return
	 */
	public static String getPath(final Resource r) {
		try {
			return getPath(new URL(r.getURI().toString()));
		} catch (MalformedURLException e) {}
		return null;
	}

	/**
	 * @see org.eclipse.core.runtime.FileLocator#resolve(URL)
	 * @see java.io.File#getAbsolutePath();
	 * @param u URL to resolve
	 * @return The absolute pathname string denoting the same file or directory as this URL
	 */
	public static String getPath(final URL u) {
		try {
			URL url = FileLocator.resolve(u);
			return new File(url.getFile()).getAbsolutePath();
		} catch (Exception e) {}
		return null;
	}
}
