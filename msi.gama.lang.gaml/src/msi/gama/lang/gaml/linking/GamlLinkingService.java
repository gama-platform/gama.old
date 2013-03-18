/**
 * Created by drogoul, 10 mai 2012
 * 
 */
package msi.gama.lang.gaml.linking;

import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.linking.impl.*;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.IScope;
import com.google.inject.Inject;

/**
 * The class GamlLinkingService.
 * 
 * 
 * Provide the linking semantics for GAML. The references to 'imported' table
 * definitions are stubbed out by this class until the Indexer is implemented.
 * 
 * @author John Bito, adapted by Alexis Drogoul
 * @since 10 mai 2012
 */
public class GamlLinkingService extends DefaultLinkingService {

	/**
	 * Keep stubs so that new ones aren't created for each linking pass.
	 */
	private static final Map<String, List<EObject>> stubbedRefs = new Hashtable();
	private static Resource stubsResource;

	@Inject
	private XtextResourceSet resourceSet;

	public GamlLinkingService() {
		super();

	}

	public List<EObject> addSymbol(final String name, EClass clazz) {
		List<EObject> list = stubbedRefs.get(name);
		if ( list == null ) {
			GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(clazz);
			stub.setName(name);
			getResource().getContents().add(stub);
			list = Collections.singletonList((EObject) stub);
			stubbedRefs.put(name, list);
		}
		return list;
	}

	private Resource getResource() {
		if ( stubsResource == null ) {
			stubsResource = resourceSet.createResource(URI.createURI("gaml:/newSymbols.xmi"));
		}
		return stubsResource;
	}

	@Override
	protected IScope getScope(EObject context, EReference reference) {
		try {
			registerImportedNamesAdapter(context);
			return getScopeProvider().getScope(context, reference);
		} finally {
			unRegisterImportedNamesAdapter();
		}
	}

	/**
	 * Override default in order to supply a stub object. If the default
	 * implementation isn't able to resolve the link, assume it to be a local
	 * resource.
	 */
	@Override
	public List<EObject> getLinkedObjects(final EObject context, final EReference ref,
		final INode node) throws IllegalNodeException {
		List<EObject> result = super.getLinkedObjects(context, ref, node);
		// If the default implementation resolved the link, return it
		if ( null != result && !result.isEmpty() ) { return result; }
		if ( GamlPackage.eINSTANCE.getTypeDefinition().isSuperTypeOf(ref.getEReferenceType()) ) {
			String name = getCrossRefNodeAsString(node);
			return addSymbol(name, ref.getEReferenceType());
		}
		return Collections.EMPTY_LIST;
	}
}
