/*********************************************************************************************
 * 
 * 
 * 'GamlLinkingService.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.linking;

import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.linking.impl.*;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
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

	@Inject
	IQualifiedNameConverter qualifiedNameConverter;

	public GamlLinkingService() {
		super();

	}

	public List<EObject> addSymbol(final String name, final EClass clazz) {
		List<EObject> list = stubbedRefs.get(name);
		if ( list == null ) {
			System.out.println("Adding stub reference to " + name + " as a " + clazz.getName());
			System.out.println("****************************************************");

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
			stubsResource = resourceSet.createResource(URI.createURI("gaml:/newSymbols.xmi", false));
		}
		return stubsResource;
	}

	@Override
	protected IScope getScope(final EObject context, final EReference reference) {
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
	public List<EObject> getLinkedObjects(final EObject context, final EReference ref, final INode node)
		throws IllegalNodeException {
		List<EObject> result = super.getLinkedObjects(context, ref, node);
		// If the default implementation resolved the link, return it
		if ( null != result && !result.isEmpty() ) { return result; }
		String name = getCrossRefNodeAsString(node);
		if ( GamlPackage.eINSTANCE.getTypeDefinition().isSuperTypeOf(ref.getEReferenceType()) ) { return addSymbol(
			name, ref.getEReferenceType()); }
		// else if ( GamlPackage.eINSTANCE.getVarDefinition().isSuperTypeOf(ref.getEReferenceType()) &&
		// name.contains("_model") ) {
		// String newVar = name;// .replace("_model", "");
		// QualifiedName qualifiedLinkName = qualifiedNameConverter.toQualifiedName(newVar);
		// Iterator<Resource> r = context.eResource().getResourceSet().getResources().iterator();
		// while (r.hasNext()) {
		// EObject context1 = r.next().getContents().get(0);
		// final IScope scope = getScope(context1, ref);
		// IEObjectDescription eObjectDescription = scope.getSingleElement(qualifiedLinkName);
		// if ( eObjectDescription != null ) { return addSymbol(name, ref.getEReferenceType()); }
		// }
		// }
		return Collections.EMPTY_LIST;
	}
}
