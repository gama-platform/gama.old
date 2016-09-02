package msi.gama.lang.gaml.resource;

import java.net.URLDecoder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class GamlResourceFileHelper {

	public GamlResourceFileHelper() {
	}

	/**
	 * Returns the path from the root of the workspace
	 * 
	 * @return an IPath. Never null.
	 */
	public static IPath getPathOf(final Resource r) {
		IPath path;
		final URI uri = r.getURI();
		if (uri.isPlatform()) {
			path = new Path(uri.toPlatformString(false));
		} else if (uri.isFile()) {
			path = new Path(uri.toFileString());
		} else {
			path = new Path(uri.path());
		}
		path = new Path(URLDecoder.decode(path.toOSString()));
		return path;

	}

	public static IPath getAbsoluteContainerFolderPathOf(final Resource r) {
		URI uri = r.getURI();
		if (uri.isFile()) {
			uri = uri.trimSegments(1);
			return Path.fromOSString(uri.path());
		}
		IPath path = getPathOf(r);
		if (!r.getURI().isFile()) {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			final IPath fullPath = file.getLocation();
			path = fullPath; // toOSString ?
		}
		if (path == null)
			return null;
		return path.uptoSegment(path.segmentCount() - 1);
	}

	public static String getModelPathOf(final Resource r) {
		if (r.getURI().isFile()) {
			return new Path(r.getURI().toFileString()).toOSString();
		} else {
			final IPath path = getPathOf(r);
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			final IPath fullPath = file.getLocation();
			return fullPath == null ? "" : fullPath.toOSString();
		}
	}

	public static String getProjectPathOf(final Resource r) {
		final IPath path = getPathOf(r);
		final String modelPath, projectPath;
		if (r.getURI().isFile()) {
			return path.toOSString();
		} else {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			final IPath fullPath = file.getProject().getLocation();
			return fullPath == null ? "" : fullPath.toOSString();
		}
	}

}
