// EasyXtext
// (c) Vincent Simonet, 2011
package msi.gama.lang.gaml.scoping;

import java.io.*;
import java.util.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.lang.gaml.gaml.GamlVarRef;
import msi.gaml.compilation.GamaBundleLoader;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.*;
import org.eclipse.xtext.scoping.impl.*;
import org.osgi.framework.Bundle;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
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
	public volatile static boolean scopeBuilt;

	private Resource getResource(final Bundle bundle, final String filename) {
		Path path = new Path(filename);
		Resource resource =
			resourceFactory.createResource(URI.createURI("platform:/plugin/" +
				bundle.getSymbolicName() + "/" + path.toString()));
		InputStream inputStream;
		try {
			GuiUtils.debug("===> Loading " + resource.getURI() + " to populate the global scope.");
			inputStream = FileLocator.openStream(bundle, path, false);
			resource.load(inputStream, Collections.EMPTY_MAP);
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

			LinkedHashMap map = Maps.newLinkedHashMap();
			for ( IEObjectDescription e : temp ) {
				EObject o = e.getEObjectOrProxy();
				if ( o instanceof GamlVarRef ) {
					map.put(((GamlVarRef) o).getName(), e);
				}
			}
			objectDescriptions = new ArrayList(map.values());
			// objectDescriptions = Scopes.filterDuplicates(temp);
			scopeBuilt = true;
		}
		return objectDescriptions == null ? Collections.EMPTY_LIST : objectDescriptions;
	}

	public static final Runnable postConstributions = new Runnable() {

		@Override
		public void run() {
			while (!scopeBuilt) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			int i = 0;
			for ( Runnable run : toRunAfterLoad ) {
				GuiUtils.debug("RUNNING POST CONTRIBUTION " + i++);
				run.run();
			}
			toRunAfterLoad.clear();

		}
	};

	private final static List<Runnable> toRunAfterLoad = new ArrayList();

	/**
	 * @param run
	 */
	public static void registerRunnableAfterLoad(final Runnable run) {
		toRunAfterLoad.add(run);
	}

	/**
	 * Implementation of IGlobalScopeProvider.
	 */
	@Override
	public IScope getScope(final Resource context, final EReference reference,
		final Predicate<IEObjectDescription> filter) {
		// if ( context.getURI().isPlatform() ) { return IScope.NULLSCOPE; }
		return MapBasedScope.createScope(uriScopeProvider.getScope(context, reference, filter),
			getEObjectDescriptions());
	}
}
