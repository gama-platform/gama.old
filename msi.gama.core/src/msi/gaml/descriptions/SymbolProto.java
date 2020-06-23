/*******************************************************************************************************
 *
 * msi.gaml.descriptions.SymbolProto.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.ISymbolConstructor;
import msi.gaml.compilation.IValidator;
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
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class SymbolProto extends AbstractProto {

	private final ISymbolConstructor constructor;
	private final IValidator validator;
	private SymbolSerializer serializer;
	private final SymbolFactory factory;

	private final int kind;
	private final boolean hasSequence, hasArgs, hasScope, isRemoteContext, isUniqueInContext;
	private final ImmutableSet<String> contextKeywords;
	private final boolean[] contextKinds = new boolean[ISymbolKind.__NUMBER__];
	private final Map<String, FacetProto> possibleFacets;
	private final ImmutableSet<String> mandatoryFacets;
	private final String omissibleFacet;
	private final boolean isPrimitive;
	private final boolean isVar;

	static final List<Integer> ids = Arrays.asList(IType.LABEL, IType.ID, IType.NEW_TEMP_ID, IType.NEW_VAR_ID);

	public SymbolProto(final Class clazz, final boolean hasSequence, final boolean hasArgs, final int kind,
			final boolean doesNotHaveScope, final FacetProto[] possibleFacets, final String omissible,
			final String[] contextKeywords, final int[] parentKinds, final boolean isRemoteContext,
			final boolean isUniqueInContext, final boolean nameUniqueInContext, final ISymbolConstructor constr,
			final IValidator validator, final SymbolSerializer serializer, final String name, final String plugin) {
		super(name, clazz, plugin);
		factory = DescriptionFactory.getFactory(kind);
		this.validator = validator;
		this.serializer = serializer;
		constructor = constr;
		this.isRemoteContext = isRemoteContext;
		this.hasSequence = hasSequence;
		this.isPrimitive = IKeyword.PRIMITIVE.equals(name);
		this.hasArgs = hasArgs;
		this.omissibleFacet = omissible;
		this.isUniqueInContext = isUniqueInContext;
		this.kind = kind;
		this.isVar = ISymbolKind.Variable.KINDS.contains(kind);
		this.hasScope = !doesNotHaveScope;
		if (possibleFacets != null) {
			final Builder<String> builder = ImmutableSet.builder();
			this.possibleFacets = GamaMapFactory.createUnordered();
			for (final FacetProto f : possibleFacets) {
				this.possibleFacets.put(f.name, f);
				f.setOwner(getTitle());
				if (!f.optional) {
					builder.add(f.name);
				}
			}
			mandatoryFacets = builder.build();
		} else {
			this.possibleFacets = null;
			mandatoryFacets = null;
		}
		this.contextKeywords = ImmutableSet.copyOf(contextKeywords);
		Arrays.fill(this.contextKinds, false);
		for (final int i : parentKinds) {
			contextKinds[i] = true;
		}
	}

	public SymbolFactory getFactory() {
		return factory;
	}

	public boolean isRemoteContext() {
		return isRemoteContext;
	}

	public boolean isLabel(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) { return false; }
		return f.isLabel();
	}

	public boolean isId(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) { return false; }
		return f.isId();
	}

	public boolean hasSequence() {
		return hasSequence;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public boolean hasArgs() {
		return hasArgs;
	}

	public boolean hasScope() {
		return hasScope;
	}

	public Map<String, FacetProto> getPossibleFacets() {
		return possibleFacets == null ? Collections.emptyMap() : possibleFacets;
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

	@Override
	public String getTitle() {
		return isVar ? ISymbolKind.Variable.KINDS_AS_STRING.get(kind) + " declaration" : "statement " + getName();
	}

	@Override
	public doc getDocAnnotation() {
		if (support == null) { return null; }
		doc d = super.getDocAnnotation();
		if (d == null) {
			if (support.isAnnotationPresent(action.class)) {
				final doc[] docs = support.getAnnotation(action.class).doc();
				if (docs.length > 0) {
					d = docs[0];
				}
			} else if (support.isAnnotationPresent(symbol.class)) {
				final doc[] docs = support.getAnnotation(symbol.class).doc();
				if (docs.length > 0) {
					d = docs[0];
				}
			}
		}

		return d;
	}

	/**
	 * @return
	 */
	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append(super.getDocumentation());
		sb.append(getFacetsDocumentation());
		return sb.toString();
	}

	public String getFacetsDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append("<b><br/>Possible facets :</b><ul>");
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

	IValidator getValidator() {
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

	/**
	 * @param facet
	 * @return
	 */
	public FacetProto getFacet(final String facet) {
		return possibleFacets == null ? null : possibleFacets.get(facet);
	}

	/**
	 * @param facets
	 * @return
	 */
	public Iterable<String> getMissingMandatoryFacets(final Facets facets) {
		if (facets == null || facets.isEmpty()) {
			if (mandatoryFacets == null || mandatoryFacets.isEmpty()) { return null; }
			return mandatoryFacets;
		}
		return Iterables.filter(mandatoryFacets, each -> !facets.containsKey(each));
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
	//
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// meta.put(GamlProperties.STATEMENTS, name);
	// }

}
