/*********************************************************************************************
 *
 * 'DescriptionFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.AGENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntProcedure;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.GAML;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.compilation.ast.ISyntacticElement.SyntacticVisitor;
import msi.gaml.compilation.ast.SyntacticFactory;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 7 janv. 2011
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DescriptionFactory {

	static TIntObjectHashMap<SymbolFactory> FACTORIES = new TIntObjectHashMap(10, 0.5f, Integer.MAX_VALUE);
	static Map<String, SymbolProto> STATEMENT_KEYWORDS_PROTOS = new HashMap();
	static Map<String, SymbolProto> VAR_KEYWORDS_PROTOS = new HashMap();
	static TIntObjectHashMap<SymbolProto> KINDS_PROTOS = new TIntObjectHashMap(10, 0.5f, Integer.MAX_VALUE);

	public static void addFactory(final SymbolFactory factory) {
		factory.getHandles().forEach((TIntProcedure) i -> {
			FACTORIES.put(i, factory);
			return true;
		});

	}

	public final static void visitStatementProtos(final BiConsumer<String, SymbolProto> consumer) {
		STATEMENT_KEYWORDS_PROTOS.forEach(consumer);
	}

	public final static void visitVarProtos(final BiConsumer<String, SymbolProto> consumer) {
		VAR_KEYWORDS_PROTOS.forEach(consumer);
	}

	public final static SymbolProto getProto(final String keyword, final IDescription superDesc) {
		final SymbolProto p = getStatementProto(keyword);
		// If not a statement, we try to find a var declaration prototype
		if (p == null) { return getVarProto(keyword, superDesc); }
		return p;
	}

	public final static SymbolProto getStatementProto(final String keyword) {
		return STATEMENT_KEYWORDS_PROTOS.get(keyword);
	}

	public final static SymbolProto getVarProto(final String keyword, final IDescription superDesc) {
		final SymbolProto p = VAR_KEYWORDS_PROTOS.get(keyword);
		if (p == null) {
			// If not a var declaration, we try to find if it is not a species
			// name (in which case, it is an "agent"
			// declaration prototype)
			if (superDesc == null) { return null; }
			final ModelDescription md = superDesc.getModelDescription();
			if (md == null) { return null; }
			final IType t = md.getTypesManager().get(keyword);
			if (t.isAgentType()) { return getVarProto(AGENT, null); }
		}
		return p;
	}

	public final static Iterable<String> getProtoNames() {
		return Iterables.concat(getStatementProtoNames(), getVarProtoNames());
	}

	public final static Iterable<String> getStatementProtoNames() {
		return STATEMENT_KEYWORDS_PROTOS.keySet();
	}

	public final static Iterable<String> getVarProtoNames() {
		return VAR_KEYWORDS_PROTOS.keySet();
	}

	public final static boolean isStatementProto(final String s) {
		// WARNING METHOD is treated here as a special keyword, but it should be
		// leveraged in the future
		return STATEMENT_KEYWORDS_PROTOS.containsKey(s) || IKeyword.METHOD.equals(s);
	}

	public static SymbolFactory getFactory(final int kind) {
		return FACTORIES.get(kind);
	}

	public static String getOmissibleFacetForSymbol(final String keyword) {
		final SymbolProto md = getProto(keyword, null);
		if (md == null) { return IKeyword.NAME; }
		return md.getOmissible();
	}

	public static void addProto(final SymbolProto md, final Iterable<String> names) {
		final int kind = md.getKind();
		if (ISymbolKind.Variable.KINDS.contains(kind)) {
			for (final String s : names) {
				VAR_KEYWORDS_PROTOS.putIfAbsent(s, md);
			}
		} else {
			// if ( !ISymbolKind.Variable.KINDS.contains(kind) ) {
			// SymbolProto.nonTypeStatements.addAll(names);
			// }
			for (final String s : names) {
				// scope.getGui().debug("DescriptionFactory.addProto " + s);
				STATEMENT_KEYWORDS_PROTOS.putIfAbsent(s, md);
			}
		}
		KINDS_PROTOS.put(kind, md);
	}

	public static void addNewTypeName(final String s, final int kind) {
		if (VAR_KEYWORDS_PROTOS.containsKey(s)) { return; }
		final SymbolProto p = KINDS_PROTOS.get(kind);
		if (p != null) {
			if (s.equals("species")) {
				VAR_KEYWORDS_PROTOS.put(SyntacticFactory.SPECIES_VAR, p);
			} else {
				VAR_KEYWORDS_PROTOS.put(s, p);
			}
		}
	}

	public static SymbolFactory getFactory(final String keyword) {
		final SymbolProto p = getProto(keyword, null);
		if (p != null) { return p.getFactory(); }
		return null;
	}

	public static void addSpeciesNameAsType(final String name) {
		if (!name.equals(AGENT) && !name.equals(IKeyword.EXPERIMENT)) {
			VAR_KEYWORDS_PROTOS.putIfAbsent(name, VAR_KEYWORDS_PROTOS.get(AGENT));
		}
	}

	public synchronized static IDescription create(final SymbolFactory factory, final String keyword,
			final IDescription superDesc, final Iterable<IDescription> children, final Facets facets) {
		final IDescription result =
				create(SyntacticFactory.create(keyword, facets, children != null), superDesc, children);
		return result;
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
			final Iterable<IDescription> children, final Facets facets) {
		return create(getFactory(keyword), keyword, superDesc, children, facets);
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
			final Iterable<IDescription> children) {
		return create(getFactory(keyword), keyword, superDesc, children, null);
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
			final Iterable<IDescription> children, final String... facets) {
		return create(getFactory(keyword), keyword, superDesc, children, new Facets(facets));
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDescription,
			final String... facets) {
		return create(keyword, superDescription, null, facets);
	}

	public synchronized static IDescription create(final String keyword, final String... facets) {
		return create(keyword, GAML.getModelContext(), facets);
	}

	public static ModelFactory getModelFactory() {
		return (ModelFactory) getFactory(ISymbolKind.MODEL);
	}

	public static Set<String> getAllowedFacetsFor(final String key) {
		if (key == null) { return Collections.EMPTY_SET; }
		final SymbolProto md = getProto(key, null);
		return md == null ? Collections.EMPTY_SET : md.getPossibleFacets().keySet();
	}

	public static SpeciesDescription createBuiltInSpeciesDescription(final String name, final Class clazz,
			final SpeciesDescription superDesc, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> skills, final String plugin) {
		return ((SpeciesFactory) getFactory(ISymbolKind.SPECIES)).createBuiltInSpeciesDescription(name, clazz,
				superDesc, parent, helper, skills, null, plugin);
	}

	public static SpeciesDescription createPlatformSpeciesDescription(final String name, final Class clazz,
			final SpeciesDescription macro, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> allSkills, final String plugin) {
		return ((SpeciesFactory) getFactory(ISymbolKind.PLATFORM)).createBuiltInSpeciesDescription(name, clazz, macro,
				parent, helper, allSkills, null, plugin);
	}

	public static SpeciesDescription createBuiltInExperimentDescription(final String name, final Class clazz,
			final SpeciesDescription superDesc, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> skills, final String plugin) {
		return ((ExperimentFactory) getFactory(ISymbolKind.EXPERIMENT)).createBuiltInSpeciesDescription(name, clazz,
				superDesc, parent, helper, skills, null, plugin);
	}

	public static SpeciesDescription createBuiltInPlatformSpeciesDescriotion(final String name, final Class clazz,
			final SpeciesDescription superDesc, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> skills, final String plugin) {
		return ((SpeciesFactory) getFactory(ISymbolKind.SPECIES)).createBuiltInSpeciesDescription(name, clazz,
				superDesc, parent, helper, skills, null, plugin);
	}

	public static ModelDescription createRootModelDescription(final String name, final Class clazz,
			final SpeciesDescription macro, final SpeciesDescription parent) {
		return ModelFactory.createRootModel(name, clazz, macro, parent);
	}

	public static final IDescription create(final ISyntacticElement source, final IDescription superDesc,
			final Iterable<IDescription> cp) {
		if (source == null) { return null; }
		final String keyword = source.getKeyword();
		final SymbolProto md = DescriptionFactory.getProto(keyword, superDesc);
		if (md == null) {
			superDesc.error("Unknown statement " + keyword, IGamlIssue.UNKNOWN_KEYWORD, source.getElement(), keyword);
			return null;
		}
		Iterable<IDescription> children = cp;
		if (children == null) {
			final List<IDescription> childrenList = new ArrayList<>();
			final SyntacticVisitor visitor = element -> {
				final IDescription desc = create(element, superDesc, null);
				if (desc != null) {
					childrenList.add(desc);
				}

			};
			source.visitChildren(visitor);
			source.visitSpecies(visitor);
			source.visitExperiments(visitor);
			children = childrenList;
		}
		final Facets facets = source.copyFacets(md);
		final EObject element = source.getElement();
		final IDescription desc = md.getFactory().buildDescription(keyword, facets, element, children, superDesc, md);
		return desc;

	}

	public static Iterable<SymbolProto> getStatementProtos() {
		return Iterables.concat(STATEMENT_KEYWORDS_PROTOS.values(), VAR_KEYWORDS_PROTOS.values());
	}

	public static Iterable<? extends FacetProto> getFacetsProtos() {
		return Iterables.concat(Iterables.transform(getStatementProtos(), (each) -> each.getPossibleFacets().values()));
	}

}
