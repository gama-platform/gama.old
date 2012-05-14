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
	private static final Map<String, GamlVarRef> stubbedRefs = new Hashtable<String, GamlVarRef>();
	private static Resource stubsResource;

	@Inject
	private XtextResourceSet resourceSet;

	public GamlLinkingService() {
		super();

	}

	public static void addSymbol(final String name) {
		GamlVarRef stub = stubbedRefs.get(name);
		if ( stub == null ) {
			// GuiUtils.debug("Creating stub reference to " + name);
			stub = EGaml.getFactory().createGamlVarRef();
			stub.setName(name);
			// getResource().getContents().add(stub);
			stubbedRefs.put(name, stub);
		}

	}

	private Resource getResource() {
		if ( stubsResource == null ) {
			stubsResource = resourceSet.createResource(URI.createURI("gaml:/keywords.xmi"));
			if ( !stubbedRefs.isEmpty() ) {
				for ( EObject obj : stubbedRefs.values() ) {
					stubsResource.getContents().add(obj);
				}
			}
		}
		return stubsResource;
	}

	/**
	 * Override default in order to supply a stub object. If the default
	 * implementation isn't able to resolve the link, assume it to be a local
	 * resource.
	 * 
	 * @param context
	 *            the model element containing the reference
	 * @param ref
	 *            the reference defining the type that must be resolved
	 * @param node
	 *            the parse tree node containing the text of the reference (ID)
	 * @return the default implementation's return if non-empty or else an
	 *         internally-generated PhysicalFileName
	 * @throws IllegalNodeException
	 *             if detected by the default implementation
	 * @see org.eclipse.xtext.linking.impl.DefaultLinkingService#getLinkedObjects(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.ecore.EReference, org.eclipse.xtext.parsetree.AbstractNode)
	 */

	@Override
	public List<EObject> getLinkedObjects(final EObject context, final EReference ref,
		final INode node) throws IllegalNodeException {
		List<EObject> result = super.getLinkedObjects(context, ref, node);
		// If the default implementation resolved the link, return it
		if ( null != result && !result.isEmpty() ) { return result; }

		if ( GamlPackage.Literals.GAML_VAR_REF.isSuperTypeOf(ref.getEReferenceType()) ) {
			String name = getCrossRefNodeAsString(node);
			// GuiUtils.debug("Missing reference to " + name);
			GamlVarRef stub = stubbedRefs.get(name);
			if ( stub == null ) {
				// GuiUtils.debug("Creating stub reference to " + name);
				stub = EGaml.getFactory().createGamlVarRef();
				stub.setName(name);
				getResource().getContents().add(stub);
				stubbedRefs.put(name, stub);
			}
			result = Collections.singletonList((EObject) stub);
		}
		return result;
	}
}
