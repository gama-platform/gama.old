/*******************************************************************************************************
 *
 * BuiltinGlobalScopeProvider.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
// (c) Vincent Simonet, 2011
package msi.gama.lang.gaml.scoping;

import static msi.gama.lang.gaml.indexer.GamlResourceIndexer.allImportsOf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.SelectableBasedScope;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.GamlDefinition;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IMap;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.IUnits;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

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

/**
 * The Class BuiltinGlobalScopeProvider.
 */
@Singleton
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class BuiltinGlobalScopeProvider extends ImportUriGlobalScopeProvider implements IUnits {

	static {
		DEBUG.ON();
	}

	/** The Constant EMPTY_MAP. */
	static final IMap EMPTY_MAP = GamaMapFactory.createUnordered();

	/** The global scopes. */
	private static IMap<EClass, TerminalMapBasedScope> GLOBAL_SCOPES = GamaMapFactory.createUnordered();

	/** The all names. */
	private static Set<QualifiedName> allNames;

	/** The resources. */
	private static IMap<EClass, Resource> resources;

	/** The descriptions. */
	private static IMap<EClass, IMap<QualifiedName, IEObjectDescription>> descriptions = null;

	/** The e equation. */
	private static EClass eType, eVar, eSkill, eAction, eUnit, eEquation;

	/** The rs. */
	static XtextResourceSet rs = new XtextResourceSet();

	/**
	 * The Class ImmutableMap.
	 */
	public static class ImmutableMap implements Map<String, String> {

		/** The contents. */
		private final String[] contents;

		/**
		 * Instantiates a new immutable map.
		 *
		 * @param strings
		 *            the strings
		 */
		public ImmutableMap(final String... strings) {
			contents = strings == null ? new String[0] : strings;
		}

		/**
		 * Method size()
		 *
		 * @see java.util.Map#size()
		 */
		@Override
		public int size() {
			return contents.length;
		}

		/**
		 * Method isEmpty()
		 *
		 * @see java.util.Map#isEmpty()
		 */
		@Override
		public boolean isEmpty() { return contents.length == 0; }

		/**
		 * Method containsKey()
		 *
		 * @see java.util.Map#containsKey(java.lang.Object)
		 */
		@Override
		public boolean containsKey(final Object key) {
			for (int i = 0; i < contents.length; i += 2) {
				final String k = contents[i];
				if (k.equals(key)) return true;
			}
			return false;
		}

		/**
		 * Method containsValue()
		 *
		 * @see java.util.Map#containsValue(java.lang.Object)
		 */
		@Override
		public boolean containsValue(final Object value) {
			for (int i = 1; i < contents.length; i += 2) {
				final String k = contents[i];
				if (k.equals(value)) return true;
			}
			return false;

		}

		/**
		 * Method get()
		 *
		 * @see java.util.Map#get(java.lang.Object)
		 */
		@Override
		public String get(final Object key) {
			for (int i = 0; i < contents.length; i += 2) {
				final String k = contents[i];
				if (k.equals(key)) return contents[i + 1];
			}
			return null;

		}

		/**
		 * Method put()
		 *
		 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
		 */
		@Override
		public String put(final String key, final String value) {
			// Only replace
			for (int i = 0; i < contents.length; i += 2) {
				final String k = contents[i];
				if (k.equals(key)) {
					final String oldValue = contents[i + 1];
					contents[i + 1] = value;
					return oldValue;
				}
			}

			return null;

		}

		/**
		 * Method remove()
		 *
		 * @see java.util.Map#remove(java.lang.Object)
		 */
		@Override
		public String remove(final Object key) {
			// No remove
			return null;
		}

		/**
		 * Method putAll()
		 *
		 * @see java.util.Map#putAll(java.util.Map)
		 */
		@Override
		public void putAll(final Map<? extends String, ? extends String> m) {
			m.forEach(this::put);
		}

		/**
		 * Method clear()
		 *
		 * @see java.util.Map#clear()
		 */
		@Override
		public void clear() {}

		/**
		 * Method keySet()
		 *
		 * @see java.util.Map#keySet()
		 */
		@Override
		public Set<String> keySet() {
			final HashSet<String> keys = new HashSet<>();
			for (int i = 0; i < contents.length; i += 2) { keys.add(contents[i]); }
			return keys;
		}

		/**
		 * Method values()
		 *
		 * @see java.util.Map#values()
		 */
		@Override
		public Collection<String> values() {
			final HashSet<String> values = new HashSet<>();
			for (int i = 1; i < contents.length; i += 2) { values.add(contents[i]); }
			return values;
		}

		/**
		 * Method entrySet()
		 *
		 * @see java.util.Map#entrySet()
		 */
		@Override
		public Set<java.util.Map.Entry<String, String>> entrySet() {
			final HashSet<Map.Entry<String, String>> keys = new HashSet<>();
			for (int i = 0; i < contents.length; i += 2) {
				final Map.Entry<String, String> entry =
						new GamaPair<>(contents[i], contents[i + 1], Types.STRING, Types.STRING);
				keys.add(entry);
			}
			return keys;
		}

	}

	static {
		// AD 15/01/16: added to make sure that the XText builder can wait
		// until, at least, the main artefacts of GAMA have been built.
		while (!GamaMetaModel.INSTANCE.isInitialized) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		DEBUG.TIMER(DEBUG.PAD("> GAMA: GAML artefacts", 45, ' ') + DEBUG.PAD(" built in", 15, '_'), () -> {
			IUnits.initialize();
			createDescriptions();
		});

	}

	/**
	 * Creates the resource.
	 *
	 * @param uri
	 *            the uri
	 * @return the resource
	 */
	static Resource createResource(final String uri) {
		Resource r = rs.getResource(URI.createURI(uri, false), false);
		if (r == null) { r = rs.createResource(URI.createURI(uri, false)); }
		return r;
	}

	/**
	 * Inits the resources.
	 */
	static void initResources() {
		eType = GamlPackage.eINSTANCE.getTypeDefinition();
		eVar = GamlPackage.eINSTANCE.getVarDefinition();
		eSkill = GamlPackage.eINSTANCE.getSkillFakeDefinition();
		eAction = GamlPackage.eINSTANCE.getActionDefinition();
		eUnit = GamlPackage.eINSTANCE.getUnitFakeDefinition();
		eEquation = GamlPackage.eINSTANCE.getEquationDefinition();
		resources = GamaMapFactory.createUnordered();
		resources.put(eType, createResource("types.xmi"));
		resources.put(eVar, createResource("vars.xmi"));
		resources.put(eSkill, createResource("skills.xmi"));
		resources.put(eUnit, createResource("units.xmi"));
		resources.put(eAction, createResource("actions.xmi"));
		resources.put(eEquation, createResource("equations.xmi"));
		descriptions = GamaMapFactory.createUnordered();
		descriptions.put(eVar, GamaMapFactory.createUnordered());
		descriptions.put(eType, GamaMapFactory.createUnordered());
		descriptions.put(eSkill, GamaMapFactory.createUnordered());
		descriptions.put(eUnit, GamaMapFactory.createUnordered());
		descriptions.put(eAction, GamaMapFactory.createUnordered());
		descriptions.put(eEquation, GamaMapFactory.createUnordered());
		allNames = new HashSet<>();
	}

	/**
	 * Contains.
	 *
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean contains(final QualifiedName name) {
		return allNames.contains(name);
	}

	/**
	 * Adds the.
	 *
	 * @param eClass
	 *            the e class
	 * @param t
	 *            the t
	 * @return the gaml definition
	 */
	static GamlDefinition add(final EClass eClass, final String t) {
		final GamlDefinition stub = (GamlDefinition) EGaml.getInstance().getFactory().create(eClass);
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		final IEObjectDescription e = EObjectDescription.create(t, stub);
		descriptions.get(eClass).put(e.getName(), e);
		allNames.add(e.getName());
		return stub;
	}

	/**
	 * Adds the.
	 *
	 * @param eClass
	 *            the e class
	 * @param t
	 *            the t
	 * @param o
	 *            the o
	 */
	static void add(final EClass eClass, final String t, final OperatorProto o) {
		final GamlDefinition stub = (GamlDefinition) EGaml.getInstance().getFactory().create(eClass);
		stub.setName(t);
		Map<String, String> doc;
		resources.get(eClass).getContents().add(stub);
		final IGamlDescription d =
				GAMA.isInHeadLessMode() ? null : GamlResourceServices.getResourceDocumenter().getGamlDocumentation(o);

		if (d != null) {
			doc = new ImmutableMap("doc", d.getDocumentation(), "title", d.getTitle(), "type", "operator");
		} else {
			doc = new ImmutableMap("type", "operator");
		}
		final IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		descriptions.get(eClass).put(e.getName(), e);
		allNames.add(e.getName());

	}

	/**
	 * Adds the var.
	 *
	 * @param t
	 *            the t
	 * @param o
	 *
	 * @param keyword
	 *            the keyword indicating the type of the stub (variable, action, etc.)
	 * @return the last stub constructed
	 */
	public static GamlDefinition addWithDoc(final String t, final IGamlDescription o, final String keyword,
			final EClass... classes) {

		// DEBUG.OUT("Adding stub for " + keyword + " " + t);

		GamlDefinition stub = null;
		QualifiedName qName = QualifiedName.create(t);
		allNames.add(qName);
		final IGamlDescription d =
				GAMA.isInHeadLessMode() ? null : GamlResourceServices.getResourceDocumenter().getGamlDocumentation(o);
		Map<String, String> doc;
		if (d != null) {
			doc = new ImmutableMap("doc", d.getDocumentation(), "title", d.getTitle(), "type", keyword);
		} else {
			doc = new ImmutableMap("type", keyword);
		}
		for (EClass eClass : classes) {
			stub = (GamlDefinition) EGaml.getInstance().getFactory().create(eClass);
			stub.setName(t);
			resources.get(eClass).getContents().add(stub);
			IMap<QualifiedName, IEObjectDescription> eClassDescriptions = descriptions.get(eClass);

			final IEObjectDescription existing = eClassDescriptions.get(qName);
			// If it already exists, then the previous doc is added to the current and the existing IEObjectDescription
			// is discarded
			if (existing != null) {
				String body = existing.getUserData("doc");
				if (body != null) {
					String title = existing.getUserData("title");
					doc.put("doc", doc.get("doc") + Strings.LN + "<p/><hr><p/>" + Strings.LN + "<b>" + title
							+ "</b><p/>" + body);
				}
			}
			// We create the new IEObjectDescription
			IEObjectDescription e = EObjectDescription.create(qName, stub, doc);
			eClassDescriptions.put(qName, e);
			// GamlResourceServices.getResourceDocumenter().setGamlDocumentation(stub, o, true, true);
		}
		return stub;
	}

	/**
	 * Adds the unit.
	 *
	 * @param eClass
	 *            the e class
	 * @param t
	 *            the t
	 */
	static void addUnit(final EClass eClass, final String t) {
		final GamlDefinition stub = (GamlDefinition) EGaml.getInstance().getFactory().create(eClass);
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		final String d = IUnits.UNITS_EXPR.get(t).getDocumentation();
		final Map<String, String> doc = new ImmutableMap("title", d, "type", "unit");
		final IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		descriptions.get(eClass).put(e.getName(), e);
		allNames.add(e.getName());

	}

	/**
	 * Adds the type.
	 *
	 * @param eClass
	 *            the e class
	 * @param t
	 *            the t
	 * @param type
	 *            the type
	 */
	static void addType(final EClass eClass, final String t, final IType type) {
		final GamlDefinition stub = (GamlDefinition) EGaml.getInstance().getFactory().create(eClass);
		// TODO Add the fields definition here
		stub.setName(t);
		resources.get(eClass).getContents().add(stub);
		final Map<String, String> doc = new ImmutableMap("title", "Type " + type, "type", "type");
		final IEObjectDescription e = EObjectDescription.create(t, stub, doc);
		descriptions.get(eClass).put(e.getName(), e);
		allNames.add(e.getName());

	}

	/**
	 * Get the object descriptions for the built-in types.
	 */
	public IMap<QualifiedName, IEObjectDescription> getEObjectDescriptions(final EClass eClass) {
		createDescriptions();
		return descriptions.get(eClass);
	}

	/**
	 * Gets the global scope.
	 *
	 * @param eClass
	 *            the e class
	 * @return the global scope
	 */
	public TerminalMapBasedScope getGlobalScope(final EClass eClass) {
		if (GLOBAL_SCOPES.containsKey(eClass)) return GLOBAL_SCOPES.get(eClass);
		IMap<QualifiedName, IEObjectDescription> descriptions = getEObjectDescriptions(eClass);
		if (descriptions == null) { descriptions = EMPTY_MAP; }
		final TerminalMapBasedScope result = new TerminalMapBasedScope(descriptions);
		GLOBAL_SCOPES.put(eClass, result);
		return result;
	}

	/**
	 * Creates the descriptions.
	 */
	public static void createDescriptions() {
		if (descriptions == null) {
			initResources();
			add(eAction, IExpressionFactory.TEMPORARY_ACTION_NAME);
			for (final String t : Types.getTypeNames()) {
				addType(eType, t, Types.get(t));
				add(eVar, t);
				add(eAction, t);
			}
			for (final String t : AbstractGamlAdditions.CONSTANTS) {
				add(eType, t);
				add(eVar, t);
			}
			for (final String t : IUnits.UNITS_EXPR.keySet()) { addUnit(eUnit, t); }
			for (final OperatorProto t : AbstractGamlAdditions.getAllFields()) {
				addWithDoc(t.getName(), t, "field", eVar);
			}
			if (!GAMA.isInHeadLessMode()) { addWithDoc(IKeyword.GAMA, GAMA.getPlatformAgent(), "platform", eVar); }
			for (final IDescription t : AbstractGamlAdditions.getAllVars()) {
				addWithDoc(t.getName(), t, "variable", eVar);
			}
			for (final String t : GamaSkillRegistry.INSTANCE.getAllSkillNames()) {
				add(eSkill, t);
				add(eVar, t);
			}
			for (final IDescription t : AbstractGamlAdditions.getAllActions()) {
				addWithDoc(t.getName(), t, "action", eAction, eVar);
				// GamlResourceServices.getResourceDocumenter().setGamlDocumentation(def, t, true, true);
			}
			final OperatorProto[] p = new OperatorProto[1];
			IExpressionCompiler.OPERATORS.forEachPair((a, b) -> {
				p[0] = null;
				b.forEachPair((string, object) -> {
					p[0] = object;
					return false;
				});
				add(eAction, a, p[0]);
				return true;
			});

		}
	}


	@Override
	protected IScope getScope(final Resource resource, final boolean ignoreCase, final EClass type,
			final Predicate<IEObjectDescription> filter) {
		IScope scope = getGlobalScope(type);
		Collection<URI> imports = allImportsOf((GamlResource) resource).keySet();
		int size = imports.size();
		if (size == 0) return scope;
		if (size > 1) {
			imports = Lists.newArrayList(imports);
			Collections.reverse((List<URI>) imports);
		}
		final IResourceDescriptions descriptions = getResourceDescriptions(resource, imports);
		return SelectableBasedScope.createScope(scope, descriptions, filter, type, false);
	}

	/**
	 * Gets the var.
	 *
	 * @param name
	 *            the name
	 * @return the var
	 */
	public static IEObjectDescription getVar(final String name) {
		if (name == null) return null;
		return descriptions.get(eVar).get(QualifiedName.create(name));
	}

	/**
	 * Gets the resource set.
	 *
	 * @return the resource set
	 */
	public static XtextResourceSet getResourceSet() { return rs; }

	/**
	 * Gets the resources.
	 *
	 * @return the resources
	 */
	public static IMap<EClass, Resource> getResources() { return resources; }
}
