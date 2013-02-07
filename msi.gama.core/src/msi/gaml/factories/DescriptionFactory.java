/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.AGENT;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 7 janv. 2011
 * 
 * @todo Description
 * 
 */
public class DescriptionFactory {

	static Map<Integer, SymbolFactory> FACTORIES = new HashMap();

	static Map<String, SymbolProto> KEYWORDS_PROTOS = new HashMap();

	static Map<Integer, SymbolProto> KINDS_PROTOS = new HashMap();

	public static void addFactory(final SymbolFactory factory) {
		for ( int i : factory.getHandles() ) {
			FACTORIES.put(i, factory);
		}
	}

	public final static SymbolProto getProto(final IDescription context,
		final ISyntacticElement source, final String keyword) {
		SymbolProto md = getProto(keyword);
		if ( md == null ) {
			if ( context != null ) {
				context.flagError("Unknown symbol " + keyword, IGamlIssue.UNKNOWN_KEYWORD, source,
					keyword);
			}
			return null;
		}
		return md;
	}

	public final static SymbolProto getProto(final String keyword) {
		return KEYWORDS_PROTOS.get(keyword);
	}

	public static String getOmissibleFacetForSymbol(final String keyword) {
		SymbolProto md = getProto(keyword);
		if ( md == null ) { return IKeyword.NAME; }
		return md.getOmissible();
	}

	public static SymbolFactory getFactory(final int kind) {
		return FACTORIES.get(kind);
	}

	public static void addProto(final SymbolProto md, final List<String> names) {
		int kind = md.getKind();
		if ( !ISymbolKind.Variable.KINDS.contains(kind) ) {
			SymbolProto.nonVariableStatements.addAll(names);
		}
		for ( String s : names ) {
			if ( KEYWORDS_PROTOS.containsKey(s) ) { return; }
			KEYWORDS_PROTOS.put(s, md);
		}
		KINDS_PROTOS.put(kind, md);
	}

	public static void addNewTypeName(final String s, final int kind) {
		if ( KEYWORDS_PROTOS.containsKey(s) ) { return; }
		SymbolProto p = KINDS_PROTOS.get(kind);
		if ( p != null ) {
			KEYWORDS_PROTOS.put(s, p);
		}
	}

	public static SymbolFactory getFactory(final String keyword) {
		SymbolProto p = KEYWORDS_PROTOS.get(keyword);
		if ( p != null ) { return p.getFactory(); }
		return null;
	}

	public static void addSpeciesNameAsType(final String name) {
		if ( !name.equals(AGENT) ) {
			KEYWORDS_PROTOS.put(name, KEYWORDS_PROTOS.get(AGENT));
		}
	}

	// -----

	public synchronized static IDescription create(final SymbolFactory factory,
		final String keyword, final IDescription superDesc, final IChildrenProvider children,
		final Facets facets) {
		return factory.create(new SyntheticStatement(keyword, facets), superDesc, children);
	}

	public synchronized static IDescription create(final String keyword,
		final IDescription superDesc, final IChildrenProvider children, final Facets facets) {
		return create(getFactory(keyword), keyword, superDesc, children, facets);
	}

	public synchronized static IDescription create(final String keyword,
		final IDescription superDesc, final IChildrenProvider children, final String ... facets) {
		return create(getFactory(keyword), keyword, superDesc, children, new Facets(facets));
	}

	public synchronized static IDescription create(final String keyword,
		final IDescription superDescription, final String ... facets) {
		return create(keyword, superDescription, IChildrenProvider.NONE, facets);
	}

	public synchronized static IDescription create(final String keyword, final String ... facets) {
		return create(keyword, null, facets);
	}

	public synchronized static ISymbol compile(final IDescription desc) {
		return getFactory(desc.getKeyword()).compile(desc);
	}

	public static ModelFactory getModelFactory() {
		return (ModelFactory) getFactory(ISymbolKind.MODEL);
	}

	public static Set<String> getAllowedFacetsFor(final String key) {
		if ( key == null ) { return Collections.EMPTY_SET; }
		SymbolProto md = getProto(key);
		return md == null ? Collections.EMPTY_SET : md.getPossibleFacets().keySet();
	}

	public static SpeciesDescription createSpeciesDescription(final String name, final Class clazz,
		final IDescription superDesc, final IAgentConstructor helper, final Set<String> skills,
		final Facets facets) {
		return ((SpeciesFactory) getFactory(ISymbolKind.SPECIES)).createSpeciesDescription(name,
			clazz, superDesc, helper, skills, facets == null ? new Facets() : facets);
	}

}
