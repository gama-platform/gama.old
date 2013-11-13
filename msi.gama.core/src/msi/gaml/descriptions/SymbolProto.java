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
	// static final TIntHashSet definitions = new TIntHashSet(new int[] { IType.ID, IType.NEW_TEMP_ID, IType.NEW_VAR_ID
	// });

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

	// public final ISymbol compile(final IDescription desc) {
	// validateFacets(desc);
	// ISymbol cs = getConstructor().create(desc);
	// if ( cs == null ) { return null; }
	// if ( hasArgs() ) {
	// ((IStatement.WithArgs) cs).setFormalArgs(((StatementDescription) desc).validateArgs());
	// }
	// if ( hasSequence && !desc.getKeyword().equals(PRIMITIVE) ) {
	// if ( isRemoteContext ) {
	// desc.copyTempsAbove();
	// }
	// cs.setChildren(desc.compileChildren());
	// }
	// return cs;
	//
	// }
	//
	// public final void validate(final IDescription desc) {
	// if ( desc == null || desc.isBuiltIn() ) { return; }
	// final IDescription sd = desc.getEnclosingDescription();
	// if ( sd != null ) {
	// // We first verify that the description is at the right place
	// if ( !contextKinds[sd.getKind()] && !contextKeywords.contains(sd.getKeyword()) ) {
	// desc.error(desc.getKeyword() + " cannot be defined in " + sd.getKeyword(), IGamlIssue.WRONG_CONTEXT,
	// desc.getName());
	// return;
	// }
	// // If it is supposed to be unique, we verify this
	// if ( isUniqueInContext ) {
	// final String keyword = desc.getKeyword();
	// for ( final IDescription child : sd.getChildren() ) {
	// if ( child.getKeyword().equals(keyword) && child != desc ) {
	// final String error =
	// keyword + " is defined twice. Only one definition is allowed in " + sd.getKeyword();
	// child.error(error, IGamlIssue.DUPLICATE_KEYWORD, child.getUnderlyingElement(null), keyword);
	// desc.error(error, IGamlIssue.DUPLICATE_KEYWORD, desc.getUnderlyingElement(null), keyword);
	// return;
	// }
	// }
	// }
	// }
	// // We then validate its facets
	// validateFacets(desc);
	//
	// if ( hasSequence && !PRIMITIVE.equals(desc.getKeyword()) ) {
	// if ( isRemoteContext ) {
	// desc.copyTempsAbove();
	// }
	// desc.validateChildren();
	// }
	//
	// // If a custom validator has been defined, run it
	// if ( validator != null ) {
	// validator.validate(desc);
	// }
	// }

	public SymbolFactory getFactory() {
		return factory;
	}

	public boolean isRemoteContext() {
		return isRemoteContext;
	}

	// public boolean isFacetDeclaringANewTemp(final String s) {
	// FacetProto f = getPossibleFacets().get(s);
	// if ( f == null ) { return false; }
	// return f.types[0] == IType.NEW_TEMP_ID;
	// }

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

	// static List<String> typeProviderFacets = Arrays.asList(VALUE, TYPE, AS, SPECIES, OF, OVER, FROM, INDEX);
	//
	// public IExpression createVarWithTypes(final String tag, final IDescription sd) {
	// final Facets ff = sd.getFacets();
	// final TypesManager types = sd.getModelDescription().getTypesManager();
	// for ( String s : typeProviderFacets ) {
	// IExpressionDescription expr = ff.get(s);
	// if ( expr != null ) {
	// expr.compile(sd);
	// }
	// }
	// IExpression value = ff.getExpr(VALUE);
	// IType t = Types.NO_TYPE;
	// IType ct = Types.NO_TYPE;
	// IType kt = Types.NO_TYPE;
	//
	// // Definition of the type
	//
	// if ( !ff.contains(TYPE) ) {
	// final String kw = sd.getKeyword();
	// if ( kw.equals(CREATE) || kw.equals(CAPTURE) || kw.equals(RELEASE) ) {
	// t = Types.get(IType.LIST);
	// } else if ( value != null ) {
	// t = value.getType();
	// } else if ( ff.contains(OVER) ) {
	// t = ff.getExpr(OVER).getContentType();
	// } else if ( ff.contains(FROM) ) {
	// t = ff.getExpr(FROM).getType();
	// }
	// } else {
	// t = types.get(ff.getLabel(TYPE));
	// }
	//
	// // Definition of the content type and key type
	// if ( t.hasContents() ) {
	// ct = t.defaultContentType();
	// kt = t.defaultKeyType();
	// if ( ff.contains(AS) ) {
	// ct = types.get(ff.getLabel(AS));
	// } else if ( ff.contains(SPECIES) ) {
	// ct = ff.getExpr(SPECIES).getContentType();
	// } else if ( ff.contains(OF) ) {
	// ct = types.get(ff.getLabel(OF));
	// } else if ( value != null ) {
	// ct = value.getContentType();
	// kt = value.getKeyType();
	// }
	// }
	//
	// return ((StatementDescription) sd).addNewTempIfNecessary(tag, t, ct, kt);
	//
	// }

	// public void validateFacets(final IDescription context) {
	// final Facets facets = context.getFacets();
	// // Special case for "do", which can accept (at parsing time) any facet
	// final boolean isDo = context.getKeyword().equals(DO);
	// final boolean isBuiltIn = context.isBuiltIn();
	// final Set<String> mandatories = new THashSet(mandatoryFacets);
	// boolean ok = !isDo && facets.forEachEntry(new TObjectObjectProcedure<String, IExpressionDescription>() {
	//
	// @Override
	// public boolean execute(final String facet, final IExpressionDescription expr) {
	// mandatories.remove(facet);
	// FacetProto fp = possibleFacets.get(facet);
	// if ( fp == null ) {
	// context.error("Unknown facet " + facet, IGamlIssue.UNKNOWN_FACET, facet);
	// return false;
	// }
	//
	// if ( fp.values.size() > 0 ) {
	// final String val = expr.getExpression().literalValue();
	// // We have a multi-valued facet
	// if ( !fp.values.contains(val) ) {
	// context.error("Facet '" + facet + "' is expecting a value among " + fp.values + " instead of " +
	// val, facet);
	// return false;
	// }
	// } else if ( fp.isType ) {
	// final String val = expr.getExpression().literalValue();
	// // The facet is supposed to be a type (IType.TYPE_ID)
	// final IType type = context.getTypeNamed(val);
	// if ( type == Types.NO_TYPE && !UNKNOWN.equals(val) && !IKeyword.SIGNAL.equals(val) ) {
	// context.error("Facet '" + facet + "' is expecting a type name. " + val + " is not a type name",
	// IGamlIssue.NOT_A_TYPE, facet, val);
	// return false;
	// }
	// } else {
	// IExpression exp;
	// if ( fp.types[0] == IType.NEW_TEMP_ID ) {
	// exp = createVarWithTypes(facet, context);
	// expr.setExpression(exp);
	// } else if ( !fp.isLabel && !facet.equals(WITH) ) {
	// exp = expr.compile(context);
	// } else {
	// exp = expr.getExpression();
	// }
	//
	// if ( exp != null && !isBuiltIn ) {
	// // Some expresssions might not be compiled (like "depends_on", for instance)
	// boolean compatible = false;
	// final IType actualType = exp.getType();
	// TypesManager tm = context.getModelDescription().getTypesManager();
	// for ( final int type : fp.types ) {
	// compatible = compatible || actualType.isTranslatableInto(tm.get(type));
	// if ( compatible ) {
	// break;
	// }
	// }
	// if ( !compatible ) {
	// final String[] strings = new String[fp.types.length];
	// for ( int i = 0; i < fp.types.length; i++ ) {
	// strings[i] = tm.get(fp.types[i]).toString();
	// }
	// context.warning("Facet '" + facet + "' is expecting " + Arrays.toString(strings) +
	// " instead of " + actualType, IGamlIssue.SHOULD_CAST, facet, tm.get(fp.types[0])
	// .toString());
	// return false;
	// }
	// }
	// }
	// return true;
	// }
	// });
	// if ( ok && !mandatories.isEmpty() ) {
	// context.error("Missing facets " + mandatories, IGamlIssue.MISSING_FACET);
	// return;
	// }
	// }

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
			sb.append("</li>");
		}
		return sb.toString();
	}
}
