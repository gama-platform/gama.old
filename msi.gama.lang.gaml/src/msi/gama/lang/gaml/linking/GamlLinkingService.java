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

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.IScope;

import com.google.inject.Inject;

import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.GamlDefinition;
import msi.gama.lang.gaml.gaml.GamlPackage;

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

	public List<EObject> addSymbol(final String name, final EClass clazz) {
		List<EObject> list = stubbedRefs.get(name);
		if (list == null) {
			// System.out.println("Adding stub reference to " + name + " as a "
			// + clazz.getName());
			// System.out.println("****************************************************");

			final GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(clazz);
			stub.setName(name);
			getResource().getContents().add(stub);
			list = Collections.singletonList((EObject) stub);
			stubbedRefs.put(name, list);
		}
		return list;
	}

	private Resource getResource() {
		if (stubsResource == null) {
			stubsResource = resourceSet.createResource(URI.createURI("gaml:/newSymbols.xmi", false));
		}
		return stubsResource;
	}

	@Override
	protected IScope getScope(final EObject context, final EReference reference) {
		try {
			// AD: Necessary to save memory (cache)
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
		final List<EObject> result = super.getLinkedObjects(context, ref, node);
		// If the default implementation resolved the link, return it
		if (null != result && !result.isEmpty()) {
			return result;
		}
		final String name = getCrossRefNodeAsString(node);
		if (GamlPackage.eINSTANCE.getTypeDefinition().isSuperTypeOf(ref.getEReferenceType())) {
			return addSymbol(name, ref.getEReferenceType());
		}
		return Collections.EMPTY_LIST;
	}
}
