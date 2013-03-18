// EasyXtext
// (c) Vincent Simonet, 2011
package msi.gama.lang.gaml.scoping;

import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gama.precompiler.IUnits;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.types.Types;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.QualifiedName;
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

	public class MapBasedScope extends AbstractScope {

		private final Map<QualifiedName, IEObjectDescription> elements;

		protected MapBasedScope(IScope parent, Map<QualifiedName, IEObjectDescription> elements) {
			super(parent, false);
			this.elements = elements;
		}

		@Override
		protected Iterable<IEObjectDescription> getAllLocalElements() {
			return elements.values();
		}

		@Override
		protected Iterable<IEObjectDescription> getLocalElementsByName(QualifiedName name) {
			IEObjectDescription result = elements.get(name);
			if ( result == null ) { return Collections.emptyList(); }
			return Collections.singleton(result);
		}

		@Override
		protected boolean isShadowed(IEObjectDescription fromParent) {
			return elements.containsKey(fromParent.getName());
		}
	}

	@Inject
	private XtextResourceSet rs;
	@Inject
	private ImportUriGlobalScopeProvider uriScopeProvider;

	private static Map<EClass, Resource> resources;
	private static Map<EClass, Map<QualifiedName, IEObjectDescription>> descriptions = null;
	private EClass eType, eVar, eSkill, eAction, eUnit;

	void initResources() {
		eType = GamlPackage.eINSTANCE.getTypeDefinition();
		eVar = GamlPackage.eINSTANCE.getVarDefinition();
		eSkill = GamlPackage.eINSTANCE.getSkillFakeDefinition();
		eAction = GamlPackage.eINSTANCE.getActionDefinition();
		eUnit = GamlPackage.eINSTANCE.getUnitFakeDefinition();
		resources = new LinkedHashMap();
		resources.put(eType, rs.createResource(URI.createURI("g:/types.xmi")));
		resources.put(eVar, rs.createResource(URI.createURI("g:/vars.xmi")));
		resources.put(eSkill, rs.createResource(URI.createURI("g:/skills.xmi")));
		resources.put(eUnit, rs.createResource(URI.createURI("g:/units.xmi")));
		resources.put(eAction, rs.createResource(URI.createURI("g:/actions.xmi")));
		descriptions = new HashMap();
		descriptions.put(eVar, new LinkedHashMap());
		descriptions.put(eType, new LinkedHashMap());
		descriptions.put(eSkill, new LinkedHashMap());
		descriptions.put(eUnit, new LinkedHashMap());
		descriptions.put(eAction, new LinkedHashMap());
	}

	static void add(EClass eClass, String t) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		// Map<String, String> userData = new HashMap();
		// TODO Put some doc in it at some point
		// userData.put("doc", Types.get(t).toString());
		IEObjectDescription e = EObjectDescription.create(t, stub/* , userData */);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);
		// if ( previous != null ) {
		// descriptions.get(eClass).put(e.getName(), previous);
		// }
	}

	/**
	 * Get the object descriptions for the built-in types.
	 */
	public Map<QualifiedName, IEObjectDescription> getEObjectDescriptions(EClass eClass) {
		if ( descriptions == null ) {
			initResources();
			for ( String t : Types.stringToIType.keySet() ) {
				add(eType, t);
				add(eVar, t);
			}
			for ( String t : AbstractGamlAdditions.BUILT_IN_SPECIES.keySet() ) {
				add(eType, t);
				add(eVar, t);
			}
			for ( String t : AbstractGamlAdditions.CONSTANTS ) {
				add(eType, t);
				add(eVar, t);
			}
			for ( String t : IUnits.UNITS.keySet() ) {
				add(eUnit, t);
			}
			for ( String t : AbstractGamlAdditions.getAllFields() ) {
				add(eVar, t);
			}
			for ( String t : AbstractGamlAdditions.getAllVars() ) {
				add(eVar, t);
			}
			for ( String t : AbstractGamlAdditions.getAllSkills() ) {
				add(eSkill, t);
				add(eVar, t);
			}
			for ( String t : AbstractGamlAdditions.getAllActions() ) {
				add(eAction, t);
			}
		}
		return descriptions.get(eClass);
	}

	/**
	 * Implementation of IGlobalScopeProvider.
	 */
	@Override
	public IScope getScope(final Resource context, final EReference reference,
		final Predicate<IEObjectDescription> filter) {
		return new MapBasedScope(uriScopeProvider.getScope(context, reference, filter),
			getEObjectDescriptions(reference.getEReferenceType()));
	}
}
