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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import gnu.trove.set.hash.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 8 févr. 2010
 * 
 * @todo Description
 * 
 */
public class SymbolProto {

	public static Set<String> nonTypeStatements = new HashSet();
	final ISymbolConstructor constructor;
	final IDescriptionValidator validator;
	final int kind;
	final boolean hasSequence;
	final boolean hasArgs;
	final boolean hasScope;
	final boolean isRemoteContext;
	final boolean isUniqueInContext;
	// private final boolean nameUniqueInContext;
	final Set<String> contextKeywords;
	final boolean[] contextKinds = new boolean[ISymbolKind.__NUMBER__];
	final Map<String, FacetProto> possibleFacets;
	// private final String[][] possibleCombinations;
	// private final String bestSuitable = "";
	final Set<String> mandatoryFacets = new THashSet<String>();
	final String omissibleFacet;
	final SymbolFactory factory;

	static final TIntHashSet ids = new TIntHashSet(new int[] { IType.LABEL, IType.ID, IType.NEW_TEMP_ID,
		IType.NEW_VAR_ID, IType.TYPE_ID });

	static {
		nonTypeStatements.add(IKeyword.EXPERIMENT);
		nonTypeStatements.add(IKeyword.METHOD);
	}

	public SymbolProto(final boolean hasSequence, final boolean hasArgs, final int kind,
		final boolean doesNotHaveScope, final Map<String, FacetProto> possibleFacets, final String omissible,
		final String[][] possibleCombinations, final Set<String> contextKeywords, final Set<Integer> contextKinds,
		final boolean isRemoteContext, final boolean isUniqueInContext, final boolean nameUniqueInContext,
		final ISymbolConstructor constr, final IDescriptionValidator validator) {
		factory = DescriptionFactory.getFactory(kind);
		this.validator = validator;
		constructor = constr;
		this.isRemoteContext = isRemoteContext;
		this.hasSequence = hasSequence;
		this.hasArgs = hasArgs;
		this.omissibleFacet = omissible;
		this.isUniqueInContext = isUniqueInContext;
		// this.nameUniqueInContext = nameUniqueInContext;
		this.kind = kind;
		this.hasScope = !doesNotHaveScope;
		this.possibleFacets = possibleFacets;
		this.possibleFacets.put(IKeyword.KEYWORD, FacetProto.KEYWORD);
		this.possibleFacets.put(IKeyword.DEPENDS_ON, FacetProto.DEPENDS_ON);
		if ( !possibleFacets.containsKey(IKeyword.NAME) ) {
			this.possibleFacets.put(IKeyword.NAME, FacetProto.NAME);
		}
		for ( FacetProto f : possibleFacets.values() ) {
			if ( !f.optional ) {
				mandatoryFacets.add(f.name);
			}
		}
		// this.possibleCombinations = possibleCombinations;
		this.contextKeywords = contextKeywords;
		Arrays.fill(this.contextKinds, false);
		for ( Integer i : contextKinds ) {
			this.contextKinds[i] = true;
		}
	}

	public SymbolFactory getFactory() {
		return factory;
	}

	public boolean isRemoteContext() {
		return isRemoteContext;
	}

	public boolean isLabel(final String s) {
		FacetProto f = getPossibleFacets().get(s);
		if ( f == null ) { return false; }
		return f.isLabel;
	}

	public boolean hasSequence() {
		return hasSequence;
	}

	public boolean hasArgs() {
		return hasArgs;
	}

	public boolean hasScope() {
		return hasScope;
	}

	public Map<String, FacetProto> getPossibleFacets() {
		return possibleFacets;
	}

	public boolean isTopLevel() {
		return kind == ISymbolKind.BEHAVIOR;
	}

	public int getKind() {
		return kind;
	}

	public ISymbolConstructor getConstructor() {
		return constructor;
	}

	/**
	 * @return
	 */
	public String getOmissible() {
		return omissibleFacet;
	}

	/**
	 * @return
	 */
	public String getDocumentation() {
		// TODO Insert here the possibility to grab a @doc annotation in the
		// symbol.
		StringBuilder sb = new StringBuilder(200);
		sb.append("<b>Facets allowed:</b><br><ul>");
		for ( FacetProto f : this.getPossibleFacets().values() ) {
			sb.append("<li><b>").append(f.name).append("</b> type: ").append(Types.get(f.types[0])).append(" <i>[")
				.append(f.optional ? "optional" : "required").append("]</i>");
			if ( f.values.size() > 0 ) {
				sb.append(" among: ").append(f.values);
			}
			if ( f.doc != null && f.doc.length() > 0 ) {
				sb.append(" - ").append(f.doc);
			}
			if ( f.deprecated != null ) {
				sb.append(" [<b>Deprecated</b>: ");
				sb.append("<i>");
				sb.append(f.deprecated);
				sb.append("</i>]");
			}
			sb.append("</li>");
		}
		return sb.toString();
	}
}
