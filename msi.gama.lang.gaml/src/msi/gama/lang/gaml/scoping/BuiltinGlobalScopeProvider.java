/*********************************************************************************************
 *
 *
 * 'BuiltinGlobalScopeProvider.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
// (c) Vincent Simonet, 2011
package msi.gama.lang.gaml.scoping;

import java.util.*;
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
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.util.GuiUtils;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.utils.EGaml;
import msi.gama.util.GamaPair;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.*;

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

	@Inject
	private ImportUriGlobalScopeProvider uriScopeProvider;
	// @Inject
	// private ResourceSetGlobalScopeProvider resourceSetScopeProvider;

	static final THashMap EMPTY_MAP = new THashMap();
	private static THashMap<EClass, Resource> resources;
	private static THashMap<EClass, THashMap<QualifiedName, IEObjectDescription>> descriptions = null;
	private static EClass eType, eVar, eSkill, eAction, eUnit, eEquation;

	static XtextResourceSet rs = new XtextResourceSet();

	public static class AllImportUriGlobalScopeProvider extends ImportUriGlobalScopeProvider {

		@Override
		protected LinkedHashSet<URI> getImportedUris(final Resource resource) {
			return new LinkedHashSet(
				((GamlResource) resource).computeAllImportedURIs(resource.getResourceSet()).keySet());
		}
	}

	public static class ImmutableMap implements Map<String, String> {

		private final String[] contents;

		public ImmutableMap(final String ... strings) {
			contents = strings == null ? new String[0] : strings;
		}

		/**
		 * Method size()
		 * @see java.util.Map#size()
		 */
		@Override
		public int size() {
			return contents.length;
		}

		/**
		 * Method isEmpty()
		 * @see java.util.Map#isEmpty()
		 */
		@Override
		public boolean isEmpty() {
			return contents.length == 0;
		}

		/**
		 * Method containsKey()
		 * @see java.util.Map#containsKey(java.lang.Object)
		 */
		@Override
		public boolean containsKey(final Object key) {
			for ( int i = 0; i < contents.length; i += 2 ) {
				String k = contents[i];
				if ( k.equals(key) ) { return true; }
			}
			return false;
		}

		/**
		 * Method containsValue()
		 * @see java.util.Map#containsValue(java.lang.Object)
		 */
		@Override
		public boolean containsValue(final Object value) {
			for ( int i = 1; i < contents.length; i += 2 ) {
				String k = contents[i];
				if ( k.equals(value) ) { return true; }
			}
			return false;

		}

		/**
		 * Method get()
		 * @see java.util.Map#get(java.lang.Object)
		 */
		@Override
		public String get(final Object key) {
			for ( int i = 0; i < contents.length; i += 2 ) {
				String k = contents[i];
				if ( k.equals(key) ) { return contents[i + 1]; }
			}
			return null;

		}

		/**
		 * Method put()
		 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
		 */
		@Override
		public String put(final String key, final String value) {
			// Only replace
			for ( int i = 0; i < contents.length; i += 2 ) {
				String k = contents[i];
				if ( k.equals(key) ) {
					String oldValue = contents[i + 1];
					contents[i + 1] = value;
					return oldValue;
				}
			}

			return null;

		}

		/**
		 * Method remove()
		 * @see java.util.Map#remove(java.lang.Object)
		 */
		@Override
		public String remove(final Object key) {
			// No remove
			return null;
		}

		/**
		 * Method putAll()
		 * @see java.util.Map#putAll(java.util.Map)
		 */
		@Override
		public void putAll(final Map<? extends String, ? extends String> m) {
			for ( Map.Entry<? extends String, ? extends String> entry : m.entrySet() ) {
				put(entry.getKey(), entry.getValue());
			}
		}

		/**
		 * Method clear()
		 * @see java.util.Map#clear()
		 */
		@Override
		public void clear() {}

		/**
		 * Method keySet()
		 * @see java.util.Map#keySet()
		 */
		@Override
		public Set<String> keySet() {
			THashSet<String> keys = new THashSet();
			for ( int i = 0; i < contents.length; i += 2 ) {
				keys.add(contents[i]);
			}
			return keys;
		}

		/**
		 * Method values()
		 * @see java.util.Map#values()
		 */
		@Override
		public Collection<String> values() {
			THashSet<String> values = new THashSet();
			for ( int i = 1; i < contents.length; i += 2 ) {
				values.add(contents[i]);
			}
			return values;
		}

		/**
		 * Method entrySet()
		 * @see java.util.Map#entrySet()
		 */
		@Override
		public Set<java.util.Map.Entry<String, String>> entrySet() {
			THashSet<Map.Entry<String, String>> keys = new THashSet();
			for ( int i = 0; i < contents.length; i += 2 ) {
				Map.Entry<String, String> entry =
					new GamaPair<String, String>(contents[i], contents[i + 1], Types.STRING, Types.STRING);
				keys.add(entry);
			}
			return keys;
		}

	}

	public static class MapBasedScope extends AbstractScope {

		private final THashMap<QualifiedName, IEObjectDescription> elements;

		protected MapBasedScope(final IScope parent, final THashMap<QualifiedName, IEObjectDescription> elements) {
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

	static Resource createResource(final String uri) {
		Resource r = rs.getResource(URI.createURI(uri, false), false);
		if ( r == null ) {
			r = rs.createResource(URI.createURI(uri, false));
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
		resources = new THashMap();
		resources.put(eType, createResource("types.xmi"));
		resources.put(eVar, createResource("vars.xmi"));
		resources.put(eSkill, createResource("skills.xmi"));
		resources.put(eUnit, createResource("units.xmi"));
		resources.put(eAction, createResource("actions.xmi"));
		resources.put(eEquation, createResource("equations.xmi"));
		descriptions = new THashMap();
		descriptions.put(eVar, new THashMap());
		descriptions.put(eType, new THashMap());
		descriptions.put(eSkill, new THashMap());
		descriptions.put(eUnit, new THashMap());
		descriptions.put(eAction, new THashMap());
		descriptions.put(eEquation, new THashMap());
	}

	public static boolean contains(final QualifiedName name) {
		for ( Map<QualifiedName, IEObjectDescription> map : descriptions.values() ) {
			if ( map.containsKey(name) ) { return true; }
		}
		return false;
	}

	static void add(final EClass eClass, final String t) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		IEObjectDescription e = EObjectDescription.create(t, stub/* , userData */);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);
	}

	static void add(final EClass eClass, final String t, final OperatorProto o) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		stub.setName(t);
		Map<String, String> doc;
		resources.get(eClass).getContents().add(stub);
		IGamlDescription d = GuiUtils.isInHeadLessMode() ? null : DescriptionFactory.getGamlDocumentation(o);

		if ( d != null ) {
			doc = new ImmutableMap("doc", d.getDocumentation(), "title", d.getTitle(), "type", "operator");
		} else {
			doc = new ImmutableMap("type", "operator");
		}
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addVar(final EClass eClass, final String t, final IGamlDescription o, final String keyword) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		IGamlDescription d = GuiUtils.isInHeadLessMode() ? null : DescriptionFactory.getGamlDocumentation(o);
		// IGamlDescription d = null;
		Map<String, String> doc;
		if ( d != null ) {
			doc = new ImmutableMap("doc", d.getDocumentation(), "title", d.getTitle(), "type", keyword);
		} else {
			doc = new ImmutableMap("type", keyword);
		}
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addAction(final EClass eClass, final String t, final IGamlDescription o) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		IGamlDescription d = GuiUtils.isInHeadLessMode() ? null : DescriptionFactory.getGamlDocumentation(o);
		Map<String, String> doc;
		if ( d != null ) {
			doc = new ImmutableMap("doc", d.getDocumentation(), "title", d.getTitle(), "type", "action");
		} else {
			doc = new ImmutableMap("type", "action");
		}
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addUnit(final EClass eClass, final String t) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		String d = IExpressionFactory.UNITS_EXPR.get(t).getDocumentation();
		Map<String, String> doc = new ImmutableMap("title", d, "type", "unit");
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	static void addType(final EClass eClass, final String t, final IType type) {
		GamlDefinition stub = (GamlDefinition) EGaml.getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		Map<String, String> doc = new ImmutableMap("title", "Type " + type, "type", "type");
		IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		IEObjectDescription previous = descriptions.get(eClass).put(e.getName(), e);

	}

	/**
	 * Get the object descriptions for the built-in types.
	 */
	public THashMap<QualifiedName, IEObjectDescription> getEObjectDescriptions(final EClass eClass) {
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
			for ( String t : IExpressionFactory.UNITS_EXPR.keySet() ) {
				addUnit(eUnit, t);
			}
			for ( OperatorProto t : AbstractGamlAdditions.getAllFields() ) {
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
			for ( Map.Entry<String, Map<Signature, OperatorProto>> t : IExpressionCompiler.OPERATORS.entrySet() ) {
				List<OperatorProto> ccc = new ArrayList<OperatorProto>(t.getValue().values());
				OperatorProto p;
				if ( ccc.isEmpty() ) {
					p = null;
				} else {
					p = ccc.get(0);
				}
				add(eAction, t.getKey(), p);
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
		THashMap<QualifiedName, IEObjectDescription> descriptions = getEObjectDescriptions(eclass);
		if ( descriptions == null ) {
			descriptions = EMPTY_MAP;
		}
		IScope parent;
		try {
			parent = uriScopeProvider.getScope(context, reference, filter);
			// parent = resourceSetScopeProvider.getScope(context, reference, filter);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Diagnostic d = new EObjectDiagnosticImpl(Severity.ERROR, "", "The imports of this model are not valid",
				context.getContents().get(0), null, 0, null);
			context.getErrors().add(d);
			return IScope.NULLSCOPE;
		}

		return new MapBasedScope(parent, descriptions);
	}
}
