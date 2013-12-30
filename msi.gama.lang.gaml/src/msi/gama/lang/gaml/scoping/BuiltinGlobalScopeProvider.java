// EasyXtext
// (c) Vincent Simonet, 2011
package msi.gama.lang.gaml.scoping;

import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.*;
import msi.gaml.factories.DescriptionFactory.Documentation;
import msi.gaml.operators.IUnits;
import msi.gaml.types.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.*;
import org.eclipse.xtext.scoping.impl.*;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * Global GAML scope provider supporting built-in definitions.
 * <p>
 * This global provider generates a global scope which consists in:
 * </p>
 * <ul>
 * <li>Built-in definitions which are defined in the diffents plug-in bundles providing contributions to GAML,</li>
 * <li>A global scope, which is computed by a ImportURI global scope provider.</li>
 * </ul>
 * 
 * @author Vincent Simonet, adapted for GAML by Alexis Drogoul, 2012
 */
public class BuiltinGlobalScopeProvider implements IGlobalScopeProvider {

	public class MapBasedScope extends AbstractScope {

		private final Map<QualifiedName, IEObjectDescription> elements;

		protected MapBasedScope(final IScope parent, final Map<QualifiedName, IEObjectDescription> elements) {
			super(parent, false);
			this.elements = elements;
		}

		@Override
		protected Iterable<IEObjectDescription> getAllLocalElements() {
			return elements.values();
		}

		@Override
		protected Iterable<IEObjectDescription> getLocalElementsByName(final QualifiedName name) {
			if ( elements == null || name == null ) { return Collections.emptyList(); }
			IEObjectDescription result = elements.get(name);
			if ( result == null ) { return Collections.emptyList(); }
			return Collections.singleton(result);
		}

		@Override
		protected boolean isShadowed(final IEObjectDescription fromParent) {
			return elements.containsKey(fromParent.getName());
		}
	}

	static SynchronizedXtextResourceSet rs = new SynchronizedXtextResourceSet();

	// @Inject
	// private XtextResourceSet rs;

	@Inject
	private ImportUriGlobalScopeProvider uriScopeProvider;

	private static Map<EClass, Resource> resources;
	private static Map<EClass, Map<QualifiedName, IEObjectDescription>> descriptions = null;
	private static EClass eType, eVar, eSkill, eAction, eUnit, eEquation;

	static Resource createResource(final String uri) {
		Resource r = rs.getResource(URI.createURI(uri), false);
		if ( r == null ) {
			r = rs.createResource(URI.createURI(uri));
		}
		return r;
	}

	static void initResources() {
		eType = GamlPackage.eINSTANCE.getTypeDefinition();
		eVar = GamlPackage.eINSTANCE.getVarDefinition();
		eSkill = GamlPackage.eINSTANCE.getSkillFakeDefinition();
		eAction = GamlPackage.eINSTANCE.getActionDefinition();
		eUnit = GamlPackage.eINSTANCE.getUnitFakeDefinition();
		eEquation = GamlPackage.eINSTANCE.getEquationDefinition();
		resources = new LinkedHashMap();
		resources.put(eType, createResource("types.xmi"));
		resources.put(eVar, createResource("vars.xmi"));
		resources.put(eSkill, createResource("skills.xmi"));
		resources.put(eUnit, createResource("units.xmi"));
		resources.put(eAction, createResource("actions.xmi"));
		resources.put(eEquation, createResource("equations.xmi"));
		descriptions = new HashMap();
		descriptions.put(eVar, new LinkedHashMap());
		descriptions.put(eType, new LinkedHashMap());
		descriptions.put(eSkill, new LinkedHashMap());
		descriptions.put(eUnit, new LinkedHashMap());
		descriptions.put(eAction, new LinkedHashMap());
		descriptions.put(eEquation, new LinkedHashMap());
	}

	public static boolean contains(final QualifiedName name) {
		for ( Map<QualifiedName, IEObjectDescription> map : descriptions.values() ) {
			if ( map.containsKey(name) ) { return true; }
		}
		return false;
	}

	static void add(final EClass eClass, final String t) {
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

	static void add(final EClass eClass, final String t, final IOperator o) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		Documentation d = DescriptionFactory.getGamlDocumentation(o);
		Map<String, String> doc = new HashMap();
		if ( d != null ) {
			doc.put("doc", d.getDocumentation());
			doc.put("title", d.getTitle());
		}
		doc.put("type", "operator");
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addVar(final EClass eClass, final String t, final IGamlDescription o, final String keyword) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		Documentation d = DescriptionFactory.getGamlDocumentation(o);
		Map<String, String> doc = new HashMap();
		if ( d != null ) {
			doc.put("doc", d.getDocumentation());
			doc.put("title", d.getTitle());
		}
		doc.put("type", keyword);
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addAction(final EClass eClass, final String t, final IGamlDescription o) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		Documentation d = DescriptionFactory.getGamlDocumentation(o);
		Map<String, String> doc = new HashMap();
		if ( d != null ) {
			doc.put("doc", d.getDocumentation());
			doc.put("title", d.getTitle());
		}
		doc.put("type", "action");
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addUnit(final EClass eClass, final String t, final Object value) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		Map<String, String> doc = new HashMap();
		doc.put("title", "Unit " + t + " of value " + value);
		doc.put("type", "unit");
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addType(final EClass eClass, final String t, final IType type) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		Map<String, String> doc = new HashMap();
		doc.put("title", "Type " + type);
		doc.put("type", "type");
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	/**
	 * Get the object descriptions for the built-in types.
	 */
	public Map<QualifiedName, IEObjectDescription> getEObjectDescriptions(final EClass eClass) {
		createDescriptions();
		return descriptions.get(eClass);
	}

	public static void createDescriptions() {
		if ( descriptions == null ) {
			initResources();
			for ( String t : Types.getTypeNames() ) {
				addType(eType, t, Types.get(t));
				add(eVar, t);
				add(eAction, t);
			}
			for ( String t : AbstractGamlAdditions.CONSTANTS ) {
				add(eType, t);
				add(eVar, t);
			}
			for ( String t : IUnits.UNITS.keySet() ) {
				addUnit(eUnit, t, IUnits.UNITS.get(t));
			}
			for ( TypeFieldExpression t : AbstractGamlAdditions.getAllFields() ) {
				addVar(eVar, t.getName(), t, "field");
			}
			for ( IDescription t : AbstractGamlAdditions.getAllVars() ) {
				addVar(eVar, t.getName(), t, "variable");
			}
			for ( String t : AbstractGamlAdditions.getAllSkills() ) {
				add(eSkill, t);
				add(eVar, t);
			}
			for ( IDescription t : AbstractGamlAdditions.getAllActions() ) {
				addAction(eAction, t.getName(), t);
				add(eVar, t.getName());
			}
			for ( Map.Entry<String, Map<Signature, IOperator>> t : IExpressionCompiler.OPERATORS.entrySet() ) {
				add(eAction, t.getKey(), new ArrayList<IOperator>(t.getValue().values()).get(0));
			}
		}
	}

	static {
		createDescriptions();
	}

	/**
	 * Implementation of IGlobalScopeProvider.
	 */
	@Override
	public IScope getScope(final Resource context, final EReference reference,
		final Predicate<IEObjectDescription> filter) {
		EClass eclass = reference.getEReferenceType();
		Map<QualifiedName, IEObjectDescription> descriptions = getEObjectDescriptions(eclass);
		if ( descriptions == null ) {
			descriptions = Collections.EMPTY_MAP;
		}
		IScope parent;
		try {
			parent = uriScopeProvider.getScope(context, reference, filter);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Diagnostic d =
				new EObjectDiagnosticImpl(Severity.ERROR, "", "The imports of this model are not valid", context
					.getContents().get(0), null, 0, null);
			context.getErrors().add(d);
			return IScope.NULLSCOPE;
		}
		return new MapBasedScope(parent, descriptions);
	}
}
