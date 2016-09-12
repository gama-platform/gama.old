package msi.gama.lang.gaml.resource;

import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;

public class GamlResourceServices {

	private static final Map<URI, IGamlBuilderListener> resourceListeners = new THashMap();

	private static final Map<URI, ValidationContext> resourceErrors = new THashMap();

	private static final LoadingCache<URI, THashMap<EObject, IGamlDescription>> documentationCache = CacheBuilder
			.newBuilder().build(new CacheLoader<URI, THashMap<EObject, IGamlDescription>>() {

				@Override
				public THashMap load(final URI key) throws Exception {
					return new THashMap();
				}
			});

	public static THashMap<EObject, IGamlDescription> getDocumentationCache(final Resource r) {
		return documentationCache.getUnchecked(properlyEncodedURI(r.getURI()));
	}

	public static URI properlyEncodedURI(final URI uri) {
		final URI result = URI.createURI(uri.toString(), true);
		return result;
	}

	public static boolean isEdited(final URI uri) {
		return resourceListeners.containsKey(properlyEncodedURI(uri));
	}

	public static boolean isEdited(final Resource r) {
		return isEdited(r.getURI());
	}

	public static void updateState(final URI uri, final ModelDescription model, final boolean newState,
			final ValidationContext status) {
		final URI newURI = properlyEncodedURI(uri);
		final IGamlBuilderListener listener = resourceListeners.get(newURI);
		if (listener == null)
			return;
		final Collection exps = model == null ? newState ? Collections.EMPTY_SET : null : model.getExperiments();
		listener.validationEnded(exps, status);
	}

	public static void addResourceListener(final URI uri, final IGamlBuilderListener listener) {
		final URI newURI = properlyEncodedURI(uri);
		resourceListeners.put(newURI, listener);
	}

	public static void removeResourceListener(final IGamlBuilderListener listener) {
		URI toRemove = null;
		for (final Map.Entry<URI, IGamlBuilderListener> entry : resourceListeners.entrySet()) {
			if (entry.getValue() == listener) {
				toRemove = properlyEncodedURI(entry.getKey());
			}
		}
		if (toRemove != null) {
			resourceListeners.remove(toRemove);
			documentationCache.invalidate(toRemove);
		}

	}

	public static ValidationContext getValidationContext(final GamlResource r) {
		final URI newURI = properlyEncodedURI(r.getURI());
		if (!resourceErrors.containsKey(newURI))
			resourceErrors.put(newURI, new ValidationContext(newURI, r.hasErrors(), r.getDocumentationManager()));
		final ValidationContext result = resourceErrors.get(newURI);
		result.hasInternalSyntaxErrors(r.hasErrors());
		return result;
	}

	public static void discardValidationContext(final Resource r) {
		resourceErrors.remove(properlyEncodedURI(r.getURI()));
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
