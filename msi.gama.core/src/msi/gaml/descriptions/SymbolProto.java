/*********************************************************************************************
 *
 *
 * 'SymbolProto.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.ISymbolConstructor;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.SymbolFactory;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 8 févr. 2010
 *
 * @todo Description
 *
 */
public class SymbolProto extends AbstractProto {

	private final ISymbolConstructor constructor;
	private final IDescriptionValidator validator;
	private SymbolSerializer serializer;
	private final SymbolFactory factory;

	private final int kind;
	private final boolean hasSequence, hasArgs, hasScope, isRemoteContext, isUniqueInContext;
	private final Set<String> contextKeywords;
	private final boolean[] contextKinds = new boolean[ISymbolKind.__NUMBER__];
	private final Map<String, FacetProto> possibleFacets;
	private final Set<String> mandatoryFacets = new THashSet<String>();
	private final String omissibleFacet;

	static final TIntHashSet ids = new TIntHashSet(
			new int[] { IType.LABEL, IType.ID, IType.NEW_TEMP_ID, IType.NEW_VAR_ID });

	public SymbolProto(final Class clazz, final boolean hasSequence, final boolean hasArgs, final int kind,
			final boolean doesNotHaveScope, final Map<String, FacetProto> possibleFacets, final String omissible,
			/* final String[][] possibleCombinations, */final Set<String> contextKeywords,
			final TIntHashSet contextKinds, final boolean isRemoteContext, final boolean isUniqueInContext,
			final boolean nameUniqueInContext, final ISymbolConstructor constr, final IDescriptionValidator validator,
			final SymbolSerializer serializer, final String name, final String plugin) {
		super(name, clazz, plugin);
		factory = DescriptionFactory.getFactory(kind);
		this.validator = validator;
		this.serializer = serializer;
		constructor = constr;
		this.isRemoteContext = isRemoteContext;
		this.hasSequence = hasSequence;
		this.hasArgs = hasArgs;
		this.omissibleFacet = omissible;
		this.isUniqueInContext = isUniqueInContext;
		this.kind = kind;
		this.hasScope = !doesNotHaveScope;
		this.possibleFacets = possibleFacets;
		// this.possibleFacets.put(IKeyword.KEYWORD, FacetProto.KEYWORD);
		// this.possibleFacets.put(IKeyword.DEPENDS_ON, FacetProto.DEPENDS_ON);
		// if (!possibleFacets.containsKey(IKeyword.NAME)) {
		// this.possibleFacets.put(IKeyword.NAME, FacetProto.NAME);
		// }
		for (final FacetProto f : possibleFacets.values()) {
			if (!f.optional) {
				mandatoryFacets.add(f.name);
			}
		}
		this.contextKeywords = contextKeywords;
		Arrays.fill(this.contextKinds, false);
		contextKinds.forEach(new TIntProcedure() {

			@Override
			public boolean execute(final int i) {
				SymbolProto.this.contextKinds[i] = true;
				return true;
			}
		});

	}

	public SymbolFactory getFactory() {
		return factory;
	}

	public boolean isRemoteContext() {
		return isRemoteContext;
	}

	public boolean isLabel(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) {
			return false;
		}
		return f.isLabel();
	}

	public boolean isId(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) {
			return false;
		}
		return f.isId();
	}

	public boolean hasSequence() {
		return hasSequence;
	}

	public boolean hasArgs() {
		return isHasArgs();
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

	@Override
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
	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append(super.getDocumentation());
		sb.append("<b><br/>Facets :</b><ul>");
		final List<FacetProto> protos = new ArrayList(getPossibleFacets().values());
		Collections.sort(protos);
		for (final FacetProto f : protos) {
			if (!f.internal) {
				sb.append("<li>").append(f.getDocumentation());
			}
			sb.append("</li>");
		}
		return sb.toString();
	}

	/**
	 * @return
	 */
	public boolean isBreakable() {
		final String name = getName();
		return IKeyword.ASK.equals(name) || IKeyword.LOOP.equals(name) || IKeyword.SWITCH.equals(name);
	}

	IDescriptionValidator getValidator() {
		return validator;
	}

	public SymbolSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(final SymbolSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * @param symbolDescription
	 * @return
	 */
	public ISymbol create(final SymbolDescription description) {
		return constructor.create(description);
	}

	/**
	 * @param sd
	 * @return
	 */
	public boolean canBeDefinedIn(final IDescription sd) {
		return contextKinds[sd.getKind()] || contextKeywords.contains(sd.getKeyword());
	}

	public boolean shouldBeDefinedIn(final String context) {
		return contextKeywords.contains(context);
	}

	public boolean isUniqueInContext() {
		return isUniqueInContext;
	}

	public boolean isHasArgs() {
		return hasArgs;
	}

	/**
	 * @param facet
	 * @return
	 */
	public FacetProto getFacet(final String facet) {
		return possibleFacets.get(facet);
	}

	/**
	 * @param facets
	 * @return
	 */
	public Set<String> getMissingMandatoryFacets(final Facets facets) {
		if (facets == null)
			return null;
		Set<String> missing = null;
		for (final String s : mandatoryFacets) {
			if (!facets.containsKey(s)) {
				if (missing == null) {
					missing = new THashSet<String>();
				}
				missing.add(s);
			}
		}
		return missing;
	}

	/**
	 * Method serialize()
	 * 
	 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		for (final FacetProto f : possibleFacets.values()) {
			final String s = f.serialize(includingBuiltIn);
			if (!s.isEmpty()) {
				sb.append(s).append(" ");
			}
		}
		return getName() + " " + sb.toString();
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.STATEMENTS, name);
	}

}
