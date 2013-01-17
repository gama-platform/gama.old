// EasyXtext
// (c) Vincent Simonet, 2011
package msi.gama.lang.gaml.scoping;

import java.util.ArrayList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.*;
import org.eclipse.xtext.scoping.impl.*;
import com.google.common.base.Predicate;
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

	// @Inject
	// private IResourceDescription.Manager descriptionManager;

	@Inject
	private IResourceFactory resourceFactory;

	@Inject
	private ImportUriGlobalScopeProvider uriScopeProvider;

	static ArrayList<IEObjectDescription> objectDescriptions = null;
	static Resource globalResource = null;

	/**
	 * Get the object descriptions for the built-in scope.
	 */
	public Iterable<IEObjectDescription> getEObjectDescriptions() {
		// if ( globalResource == null ) {
		// globalResource = resourceFactory.createResource(URI.createURI("global.gaml"));
		// }
		// if ( objectDescriptions == null ) {
		// Collections.sort(AbstractGamlAdditions.ALL_STATEMENTS);
		// for ( String s : AbstractGamlAdditions.ALL_STATEMENTS ) {
		// System.out.println(s);
		// }
		// objectDescriptions = new ArrayList();
		// for ( String name : AbstractGamlAdditions.DEFINITION_STATEMENTS ) {
		// BuiltInDefinitionStatementKey o =
		// EGaml.getFactory().createBuiltInDefinitionStatementKey();
		// o.setName(name);
		// globalResource.getContents().add(o);
		// objectDescriptions.add(EObjectDescription.create(name, o));
		// System.out.println("Definition:" + name);
		// }
		// for ( String name : AbstractGamlAdditions.DECLARATION_STATEMENTS ) {
		// BuiltInType o = EGaml.getFactory().createBuiltInType();
		// o.setName(name);
		// globalResource.getContents().add(o);
		// objectDescriptions.add(EObjectDescription.create(name, o));
		// System.out.println("Type:" + name);
		// }
		// }
		return objectDescriptions;
	}

	/**
	 * Implementation of IGlobalScopeProvider.
	 */
	@Override
	public IScope getScope(final Resource context, final EReference reference,
		final Predicate<IEObjectDescription> filter) {
		// if ( context.getURI().isPlatform() ) { return IScope.NULLSCOPE; }
		// if ( scope == null ) {
		// scope =
		return MapBasedScope.createScope(uriScopeProvider.getScope(context, reference, filter),
			getEObjectDescriptions());
		// }
		// return scope;
	}
}
