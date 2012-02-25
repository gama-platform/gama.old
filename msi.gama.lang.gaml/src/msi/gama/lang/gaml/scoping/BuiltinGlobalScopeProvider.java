// EasyXtext
// (c) Vincent Simonet, 2011
package msi.gama.lang.gaml.scoping;

import java.io.IOException;
import java.util.*;
import msi.gaml.compilation.GamaBundleLoader;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.*;
import org.eclipse.xtext.scoping.impl.*;
import org.osgi.framework.Bundle;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * Global GAML scope provider supporting built-in definitions.
 * <p>
 * This global provider generates a global scope which consists in:
 * </p>
 * <ul>
 * <li>Built-in definitions which are defined in the diffents plug-in bundles providing
 * contributions to GAML,</li>
 * <li>A global scope, which is computed by a ImportURI global scope provider.</li>
 * </ul>
 * 
 * @author Vincent Simonet, adapted for GAML by Alexis Drogoul, 2012
 */
public class BuiltinGlobalScopeProvider implements IGlobalScopeProvider {

	@Inject
	private IResourceDescription.Manager descriptionManager;

	@Inject
	private IResourceFactory resourceFactory;

	@Inject
	private ImportUriGlobalScopeProvider uriScopeProvider;

	Iterable<IEObjectDescription> objectDescriptions = null;

	private Resource getResource(final Bundle bundle, final String filename) {
		Path path = new Path(filename);
		Resource resource =
			resourceFactory.createResource(URI.createURI("platform:/plugin/" +
				bundle.getSymbolicName() + "/" + path.toString()));

		// InputStream inputStream;
		try {
			// inputStream = FileLocator.openStream(bundle, path, false);
			resource.load(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return resource;
	}

	public final IResourceDescription getResourceDescription(final Bundle bundle,
		final String filename) {
		Resource r = getResource(bundle, filename);
		if ( r == null ) { return null; }
		return descriptionManager.getResourceDescription(r);
	}

	/**
	 * Get the object descriptions for the built-in scope.
	 */
	private Iterable<IEObjectDescription> getEObjectDescriptions() {
		if ( objectDescriptions == null && GamaBundleLoader.contributionsLoaded ) {
			List<IEObjectDescription> temp = new ArrayList();
			for ( Map.Entry<Bundle, String> entry : GamaBundleLoader.gamlAdditionsBundleAndFiles
				.entrySet() ) {
				IResourceDescription rd = getResourceDescription(entry.getKey(), entry.getValue());
				if ( rd != null ) {
					Iterable<IEObjectDescription> desc = rd.getExportedObjects();
					Iterables.addAll(temp, desc);
				}
			}
			objectDescriptions = Scopes.filterDuplicates(temp);
		}
		return objectDescriptions == null ? Collections.EMPTY_LIST : objectDescriptions;
	}

	/**
	 * Implementation of IGlobalScopeProvider.
	 */
	@Override
	public IScope getScope(final Resource context, final EReference reference,
		final Predicate<IEObjectDescription> filter) {
		return MapBasedScope.createScope(uriScopeProvider.getScope(context, reference, filter),
			getEObjectDescriptions());
	}
}
