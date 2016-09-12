package msi.gama.lang.gaml.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;

public class GamlResourcesHelper {

	private static final Map<URI, IGamlBuilderListener> resourceListeners = new THashMap();

	private static final Map<URI, ValidationContext> resourceErrors = new THashMap();

	private static final LoadingCache<URI, THashMap<EObject, IGamlDescription>> documentationCache = CacheBuilder
			.newBuilder().build(new CacheLoader<URI, THashMap<EObject, IGamlDescription>>() {

				@Override
				public THashMap load(final URI key) throws Exception {
					return new THashMap();
				}
			});

	public static THashMap<EObject, IGamlDescription> getDocumentationCache(final URI uri) {
		return documentationCache.getUnchecked(properlyEncodedURI(uri));
	}

	public static void removeDocumentation(final URI toRemove) {
		documentationCache.invalidate(properlyEncodedURI(toRemove));
	}

	public static URI properlyEncodedURI(final URI uri) {
		final URI result = URI.createURI(uri.toString(), true);
		return result;
	}

	public static boolean isEdited(final URI uri) {
		return resourceListeners.containsKey(properlyEncodedURI(uri));
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
				toRemove = entry.getKey();
			}
		}
		if (toRemove != null) {
			resourceListeners.remove(toRemove);
			removeDocumentation(toRemove);
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

	public static void discardValidationContext(final GamlResource gamlResource) {
		resourceErrors.remove(properlyEncodedURI(gamlResource.getURI()));
	}

}
