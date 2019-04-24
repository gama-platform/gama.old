/*********************************************************************************************
 *
 * 'GamlResourceServices.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.documentation.GamlResourceDocumenter;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.parsing.GamlSyntacticConverter;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlResourceServices {

	private static int resourceCount = 0;
	private static IDocManager documenter = new GamlResourceDocumenter();
	private static GamlSyntacticConverter converter = new GamlSyntacticConverter();
	private static final Map<URI, IGamlBuilderListener> resourceListeners = new THashMap<>();
	private static final Map<URI, ValidationContext> resourceErrors = new THashMap<>();
	private static final XtextResourceSet poolSet = new XtextResourceSet() {
		{
			setClasspathURIContext(GamlResourceServices.class);
		}

	};
	private static final LoadingCache<URI, THashMap<EObject, IGamlDescription>> documentationCache =
			CacheBuilder.newBuilder().build(new CacheLoader<URI, THashMap<EObject, IGamlDescription>>() {

				@Override
				public THashMap load(final URI key) throws Exception {
					return new THashMap<>();
				}
			});

	public static THashMap<EObject, IGamlDescription> getDocumentationCache(final Resource r) {
		return documentationCache.getUnchecked(properlyEncodedURI(r.getURI()));
	}

	public static URI properlyEncodedURI(final URI uri) {
		URI pre_properlyEncodedURI = uri;
		if(!uri.isPlatformResource()) {			
			File file = new File(uri.toFileString());
			try {
				pre_properlyEncodedURI = URI.createFileURI(file.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final URI result = URI.createURI(pre_properlyEncodedURI.toString(), true);
		
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
		// DEBUG.LOG("Beginning updating the state of editor in
		// ResourceServices for " + uri.lastSegment());
		final URI newURI = properlyEncodedURI(uri);

		final IGamlBuilderListener listener = resourceListeners.get(newURI);
		if (listener == null) { return; }
		// DEBUG.LOG("Finishing updating the state of editor for " +
		// uri.lastSegment());
		final Iterable exps = model == null ? newState ? Collections.EMPTY_SET : null
				: Iterables.filter(model.getExperiments(), each -> !each.isAbstract());
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
		if (!resourceErrors.containsKey(newURI)) {
			resourceErrors.put(newURI, new ValidationContext(newURI, r.hasErrors(), getResourceDocumenter()));
		}
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
		try {
			path = new Path(URLDecoder.decode(path.toOSString(), "UTF-8"));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return path;

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
		// final String modelPath, projectPath;
		if (r.getURI().isFile()) {
			return path.toOSString();
		} else {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			final IPath fullPath = file.getProject().getLocation();
			return fullPath == null ? "" : fullPath.toOSString();
		}
	}

	public synchronized static GamlResource getTemporaryResource(final IDescription existing) {
		ResourceSet rs = null;
		GamlResource r = null;
		if (existing != null) {
			final ModelDescription desc = existing.getModelDescription();
			if (desc != null) {
				final EObject e = desc.getUnderlyingElement(null);
				if (e != null) {
					r = (GamlResource) e.eResource();
					if (r != null) {
						rs = r.getResourceSet();
					}
				}
			}
		}
		if (rs == null) {
			rs = poolSet;
		}
		final URI uri = URI.createURI(IKeyword.SYNTHETIC_RESOURCES_PREFIX + resourceCount++ + ".gaml", false);
		// TODO Modifier le cache de la resource ici ?
		final GamlResource result = (GamlResource) rs.createResource(uri);
		final TOrderedHashMap<URI, String> imports = new TOrderedHashMap();
		imports.put(uri, null);
		if (r != null) {
			imports.put(r.getURI(), null);
			final Map<URI, String> uris = GamlResourceIndexer.allLabeledImportsOf(r);
			imports.putAll(uris);
		}
		result.getCache().getOrCreate(result).set(GamlResourceIndexer.IMPORTED_URIS, imports);
		return result;
	}

	public static void discardTemporaryResource(final GamlResource temp) {
		try {
			temp.delete(null);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static IDocManager getResourceDocumenter() {
		return documenter;
	}

	public static ISyntacticElement buildSyntacticContents(final GamlResource r) {
		return converter.buildSyntacticContents(r.getParseResult().getRootASTElement(), null);
	}

}
