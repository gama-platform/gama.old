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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import static msi.gama.common.interfaces.IKeyword.DO;

import java.awt.Container;
import java.util.*;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbolConstructor;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.factories.*;
import msi.gaml.operators.Containers;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.IType;
import msi.gaml.variables.ContainerVariable;

/**
 * Written by drogoul Modified on 8 f√©vr. 2010
 * 
 * @todo Description
 * 
 */
public class SymbolProto {

	public static Set<String> nonTypeStatements = new HashSet();

	private final ISymbolConstructor constructor;
	private final int kind;
	private final boolean hasSequence;
	private final boolean hasArgs;
	private final boolean hasScope;
	private final boolean isRemoteContext;
	private final boolean isUniqueInContext;
	private final boolean nameUniqueInContext;
	private final Set<String> contextKeywords;
	private final Set<Integer> contextKinds;
	private final Map<String, FacetProto> possibleFacets;
	private final String[][] possibleCombinations;
	private String bestSuitable="";
	private final List<String> mandatoryFacets = new ArrayList();
	private final String omissibleFacet;
	private final SymbolFactory factory;

	static final List<String> ids = Arrays.asList(IType.LABEL, IType.ID,
			IType.NEW_TEMP_ID, IType.NEW_VAR_ID, IType.TYPE_ID);
	static final List<String> definitions = Arrays.asList(IType.ID,
			IType.NEW_TEMP_ID, IType.NEW_VAR_ID);

	static {
		nonTypeStatements.add(IKeyword.EXPERIMENT);
		nonTypeStatements.add(IKeyword.METHOD);
	}

	public SymbolProto(final boolean hasSequence, final boolean hasArgs,
			final int kind, final boolean doesNotHaveScope,
			final Map<String, FacetProto> possibleFacets,
			final String omissible, final String[][] possibleCombinations,
			final Set<String> contextKeywords, final Set<Integer> contextKinds,
			final boolean isRemoteContext, final boolean isUniqueInContext,
			final boolean nameUniqueInContext, final ISymbolConstructor constr) {
		factory = DescriptionFactory.getFactory(kind);
		constructor = constr;
		this.isRemoteContext = isRemoteContext;
		this.hasSequence = hasSequence;
		this.hasArgs = hasArgs;
		this.omissibleFacet = omissible;
		this.isUniqueInContext = isUniqueInContext;
		this.nameUniqueInContext = nameUniqueInContext;
		this.kind = kind;
		this.hasScope = !doesNotHaveScope;
		this.possibleFacets = possibleFacets;
		this.possibleFacets.put(IKeyword.KEYWORD, FacetProto.KEYWORD);
		this.possibleFacets.put(IKeyword.DEPENDS_ON, FacetProto.DEPENDS_ON);
		for (FacetProto f : possibleFacets.values()) {
			if (!f.optional) {
				mandatoryFacets.add(f.name);
			}
		}
		this.possibleCombinations =possibleCombinations;
		this.contextKeywords = contextKeywords;
		this.contextKinds = contextKinds;
	}

	public SymbolFactory getFactory() {
		return factory;
	}

	public boolean isRemoteContext() {
		return isRemoteContext;
	}

	public boolean isFacetDeclaringANewTemp(final String s) {
		FacetProto f = getPossibleFacets().get(s);
		if (f == null) {
			return false;
		}
		return f.types.get(0).equals(IType.NEW_TEMP_ID);
	}

	public boolean isLabel(final String s) {
		FacetProto f = getPossibleFacets().get(s);
		if (f == null) {
			return false;
		}
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

	public boolean isUnique() {
		return isUniqueInContext;
	}

	public boolean nameIsUnique() {
		return nameUniqueInContext;
	}

	public Map<String, FacetProto> getPossibleFacets() {
		return possibleFacets;
	}

	public String[][] getPossibleCombinations() {
		return possibleCombinations;
	}

	public List<String> getMandatoryFacets() {
		return mandatoryFacets;
	}

	public void verifyMandatoryFacets(final ISyntacticElement e,
			final Facets facets, final IDescription context) {
		for (String s : mandatoryFacets) {
			if (!facets.containsKey(s)) {
				context.flagError("Missing facet " + s,
						IGamlIssue.MISSING_FACET, e, s);
			}
		}
	}

	public void verifyFacetsValidity(final ISyntacticElement e,
			final Facets facets, final IDescription context) {
		// Special case for "do", which can accept (at parsing time) any facet
		if (e.getKeyword().equals(DO)) {
			return;
		}
		for (Facet s : facets.entrySet()) {

			if (s != null && !possibleFacets.containsKey(s.getKey())) {
				context.flagError("Unknown facet " + s.getKey(),
						IGamlIssue.UNKNOWN_FACET, e, s.getKey());
			}
		}
	}


	public String getSuitable(){
		return bestSuitable;
	}
	public void verifyFacetsCombinations(final ISyntacticElement e,
			final Facets facets, final IDescription context) {
		 if ( getPossibleCombinations().length>0) { 
//			 System.out.println("before\n");
//			 String needFacets="";
			 int maxMatch=0;
			 for ( String[] c : getPossibleCombinations() ) {
				 boolean allPresent = true;
				 if(c==null){ return; }
//				 String comb="\n\n"+"{ ";
				 String missing="";
				 int nbMatch=0;
				 for ( String s : c ) {
					 allPresent = allPresent && facets.containsKey(s);
					 if(facets.containsKey(s)){nbMatch++;}
					 else{missing+=s+": ";}
//					 comb+="["+s+"] ";
				 }
//				 comb+="}";
			 	if ( allPresent ) { 
//					bestSuitable="";
			 		return; 
			 	}
			 	if(nbMatch>maxMatch){
			 		bestSuitable=missing; 
			 		maxMatch=nbMatch;
			 	}
//			 	needFacets+=comb;
			 }

//			 context.flagWarning("Missing some facets:"
//			 +bestSuitable,
//			 IGamlIssue.GENERAL, e);

		}
	}

	public boolean verifyContext(final IDescription upper) {
		if (upper == null) {
			return true;
		}
		String upperKeyword = upper.getKeyword();
		Integer upperKind = upper.getKind();
		boolean result = contextKeywords.contains(upperKeyword)
				|| contextKinds.contains(upperKind);
		return result;
	}

	public void verifyFacetsIds(final ISyntacticElement e, final Facets facets,
			final IDescription context) {
		for (Facet facet : facets.entrySet()) {
			if (facet == null) {
				continue;
			}
			String facetName = facet.getKey();
			FacetProto f = possibleFacets.get(facetName);
			if (f == null) {
				continue;
			}
			if (f.isLabel && facets.containsKey(facetName)
					&& facets.get(facetName) != null) {
				facets.put(facetName, facets.get(facetName).compileAsLabel());
				if (f.types.get(0).equals(IType.LABEL)) {
					if (f.values != null && f.values.length != 0) {
						boolean found = false;
						for (String possibleValue : f.values) {
							if (facets.equals(facetName, possibleValue)) {
								found = true;
								break;
							}
						}
						if (!found) {
							context.flagError(
									"The value of facet " + facet.getKey()
											+ " must be one of "
											+ Arrays.toString(f.values),
									IGamlIssue.NOT_AMONG, e);

						} else {
							String facetValue = facets.getLabel(facetName)
									.trim();
							if (IExpressionCompiler.RESERVED
									.contains(facetValue)) {
								context.flagError(
										facetValue
												+ " is a reserved keyword. It cannot be used as an identifier",
										IGamlIssue.IS_RESERVED, e, facetValue);
							}
						}
					}

				}
			}
		}
	}

	public void verifyFacets(final ISyntacticElement e, final Facets facets,
			final IDescription context) {
		verifyMandatoryFacets(e, facets, context);
		verifyFacetsValidity(e, facets, context);
		verifyFacetsCombinations(e, facets, context);
		verifyFacetsIds(e, facets, context);
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
		for (FacetProto f : this.getPossibleFacets().values()) {
			sb.append("<li><b>").append(f.name).append("</b> type: ")
					.append(f.types.get(0)).append(" <i>[")
					.append(f.optional ? "optional" : "required")
					.append("]</i>");
			if (f.values != null && f.values.length != 0) {
				sb.append(" among: ").append(Arrays.toString(f.values));
			}
			if (f.doc != null && f.doc.length() > 0)
				sb.append(" doc: ").append(f.doc.split("§")[0]);
			sb.append("</li>");
		}
		return sb.toString();
	}
}
