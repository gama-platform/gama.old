/*******************************************************************************************************
 *
 * SymbolProto.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.ISymbolConstructor;
import msi.gaml.compilation.IValidator;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.SymbolFactory;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 8 févr. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class SymbolProto extends AbstractProto {

	/** The null validator. */
	static IValidator NULL_VALIDATOR = (d, e, i) -> true;

	/** The constructor. */
	private final ISymbolConstructor constructor;

	/** The validator. */
	private IValidator validator;

	/** The serializer. */
	private SymbolSerializer serializer;

	/** The factory. */
	private final SymbolFactory factory;

	/** The kind. */
	private final int kind;

	/** The is unique in context. */
	private final boolean hasSequence, hasArgs, hasScope, isRemoteContext, isUniqueInContext;

	/** The context keywords. */
	private final ImmutableSet<String> contextKeywords;

	/** The context kinds. */
	private final boolean[] contextKinds = new boolean[ISymbolKind.__NUMBER__];

	/** The possible facets. */
	private final Map<String, FacetProto> possibleFacets;

	/** The mandatory facets. */
	private final ImmutableList<String> mandatoryFacets;

	/** The omissible facet. */
	private final String omissibleFacet;

	/** The is primitive. */
	private final boolean isPrimitive;

	/** The is breakable. */
	private final boolean isBreakable;

	/** The is continuable. */
	private final boolean isContinuable;

	/** The is var. */
	private final boolean isVar;

	/** The Constant ids. */
	static final List<Integer> ids = Arrays.asList(IType.LABEL, IType.ID, IType.NEW_TEMP_ID, IType.NEW_VAR_ID);

	/** The Constant BREAKABLE_STATEMENTS. */
	public static final Set<String> BREAKABLE_STATEMENTS = new HashSet();

	/** The Constant CONTINUABLE_STATEMENTS. */
	public static final Set<String> CONTINUABLE_STATEMENTS = new HashSet();

	/**
	 * Instantiates a new symbol proto.
	 *
	 * @param clazz
	 *            the clazz
	 * @param hasSequence
	 *            the has sequence
	 * @param hasArgs
	 *            the has args
	 * @param kind
	 *            the kind
	 * @param doesNotHaveScope
	 *            the does not have scope
	 * @param possibleFacets
	 *            the possible facets
	 * @param omissible
	 *            the omissible
	 * @param contextKeywords
	 *            the context keywords
	 * @param parentKinds
	 *            the parent kinds
	 * @param isRemoteContext
	 *            the is remote context
	 * @param isUniqueInContext
	 *            the is unique in context
	 * @param nameUniqueInContext
	 *            the name unique in context
	 * @param constr
	 *            the constr
	 * @param name
	 *            the name
	 * @param plugin
	 *            the plugin
	 */
	public SymbolProto(final Class clazz, final boolean isBreakable, final boolean isContinuable,
			final boolean hasSequence, final boolean hasArgs, final int kind, final boolean doesNotHaveScope,
			final FacetProto[] possibleFacets, final String omissible, final String[] contextKeywords,
			final int[] parentKinds, final boolean isRemoteContext, final boolean isUniqueInContext,
			final boolean nameUniqueInContext, final ISymbolConstructor constr, final String name,
			final String plugin) {
		super(name, clazz, plugin);
		factory = DescriptionFactory.getFactory(kind);
		constructor = constr;
		this.isBreakable = isBreakable;
		this.isContinuable = isContinuable;
		if (isContinuable) { CONTINUABLE_STATEMENTS.add(name); }
		if (isBreakable) { BREAKABLE_STATEMENTS.add(name); }
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
			final ImmutableList.Builder<String> builder = ImmutableList.builder();
			this.possibleFacets = GamaMapFactory.createUnordered();
			for (final FacetProto f : possibleFacets) {
				this.possibleFacets.put(f.name, f);
				f.setOwner(getTitle());
				f.setClass(clazz);
				if (!f.optional) { builder.add(f.name); }
			}
			mandatoryFacets = builder.build();
		} else {
			this.possibleFacets = null;
			mandatoryFacets = null;
		}
		this.contextKeywords = ImmutableSet.copyOf(contextKeywords);
		Arrays.fill(this.contextKinds, false);
		for (final int i : parentKinds) { contextKinds[i] = true; }
	}

	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public SymbolFactory getFactory() { return factory; }

	/**
	 * Checks if is remote context.
	 *
	 * @return true, if is remote context
	 */
	public boolean isRemoteContext() { return isRemoteContext; }

	/**
	 * Checks if is label.
	 *
	 * @param s
	 *            the s
	 * @return true, if is label
	 */
	public boolean isLabel(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) return false;
		return f.isLabel();
	}

	/**
	 * Checks if is id.
	 *
	 * @param s
	 *            the s
	 * @return true, if is id
	 */
	public boolean isId(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) return false;
		return f.isId();
	}

	/**
	 * Checks for sequence.
	 *
	 * @return true, if successful
	 */
	public boolean hasSequence() {
		return hasSequence;
	}

	/**
	 * Checks if is primitive.
	 *
	 * @return true, if is primitive
	 */
	public boolean isPrimitive() { return isPrimitive; }

	/**
	 * Checks for args.
	 *
	 * @return true, if successful
	 */
	public boolean hasArgs() {
		return hasArgs;
	}

	/**
	 * Checks for scope.
	 *
	 * @return true, if successful
	 */
	public boolean hasScope() {
		return hasScope;
	}

	/**
	 * Gets the possible facets.
	 *
	 * @return the possible facets
	 */
	public Map<String, FacetProto> getPossibleFacets() {
		return possibleFacets == null ? Collections.emptyMap() : possibleFacets;
	}

	/**
	 * Checks if is top level.
	 *
	 * @return true, if is top level
	 */
	public boolean isTopLevel() { return kind == ISymbolKind.BEHAVIOR; }

	@Override
	public int getKind() { return kind; }

	/**
	 * Gets the constructor.
	 *
	 * @return the constructor
	 */
	public ISymbolConstructor getConstructor() { return constructor; }

	/**
	 * @return
	 */
	public String getOmissible() { return omissibleFacet; }

	@Override
	public String getTitle() {
		return isVar ? ISymbolKind.Variable.KINDS_AS_STRING.get(kind) + " declaration" : "Statement " + getName();
	}

	@Override
	public doc getDocAnnotation() {
		if (support == null) return null;
		doc d = super.getDocAnnotation();
		if (d == null) {
			if (support.isAnnotationPresent(action.class)) {
				final doc[] docs = support.getAnnotation(action.class).doc();
				if (docs.length > 0) { d = docs[0]; }
			} else if (support.isAnnotationPresent(symbol.class)) {
				final doc[] docs = support.getAnnotation(symbol.class).doc();
				if (docs.length > 0) { d = docs[0]; }
			}
		}

		return d;
	}

	/**
	 * @return
	 */
	@Override
	public Doc getDocumentation() {
		if (documentation == null) {
			documentation = new RegularDoc(super.getDocumentation().toString());
			final List<FacetProto> protos = new ArrayList(getPossibleFacets().values());
			Collections.sort(protos);
			for (final FacetProto f : protos) {
				if (!f.internal) { documentation.set("Possible facets: ", f.name, f.getDocumentation()); }
			}
		}
		return documentation;
	}

	/**
	 * @return
	 */
	public boolean isBreakable() { return isBreakable; }

	/**
	 * Checks if is continuable.
	 *
	 * @return true, if is continuable
	 */
	public boolean isContinuable() { return isContinuable; }

	/**
	 * Gets the validator.
	 *
	 * @return the validator
	 */
	IValidator getValidator() {
		if (validator == null) {
			final validator v = support.getAnnotation(validator.class);
			try {
				validator = v != null ? v.value().getConstructor().newInstance() : NULL_VALIDATOR;
			} catch (Exception e) {}
		}

		return validator;
	}

	/**
	 * Gets the serializer.
	 *
	 * @return the serializer
	 */
	public SymbolSerializer getSerializer() {
		if (serializer == null) {
			final serializer s = support.getAnnotation(serializer.class);
			try {
				if (s != null) { serializer = s.value().getConstructor().newInstance(); }
			} catch (Exception e) {}
		}

		return serializer;
	}

	/**
	 * Sets the serializer.
	 *
	 * @param serializer
	 *            the new serializer
	 */
	public void setSerializer(final SymbolSerializer serializer) { this.serializer = serializer; }

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

	/**
	 * Should be defined in.
	 *
	 * @param context
	 *            the context
	 * @return true, if successful
	 */
	public boolean shouldBeDefinedIn(final String context) {
		return contextKeywords.contains(context);
	}

	/**
	 * Checks if is unique in context.
	 *
	 * @return true, if is unique in context
	 */
	public boolean isUniqueInContext() { return isUniqueInContext; }

	/**
	 * @param facet
	 * @return
	 */
	public FacetProto getFacet(final String facet) {
		return possibleFacets == null ? null : possibleFacets.get(facet);
	}

	/**
	 * Method serialize()
	 *
	 * @see msi.gaml.interfaces.IGamlable#serializeToGaml(boolean)
	 */
	@Override
	public String serializeToGaml(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		for (final FacetProto f : possibleFacets.values()) {
			final String s = f.serializeToGaml(includingBuiltIn);
			if (!s.isEmpty()) { sb.append(s).append(" "); }
		}
		return getName() + " " + sb.toString();
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.STATEMENTS, name);
	}

	/**
	 * Gets the mandatory facets.
	 *
	 * @return the mandatory facets
	 */
	public ImmutableList<String> getMandatoryFacets() { return mandatoryFacets; }

}
