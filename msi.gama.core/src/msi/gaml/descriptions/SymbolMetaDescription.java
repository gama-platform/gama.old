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

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.*;
import msi.gaml.expressions.IExpressionParser;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 8 f√©vr. 2010
 * 
 * @todo Description
 * 
 */
@facet(name = IKeyword.KEYWORD, type = IType.ID, optional = true)
public class SymbolMetaDescription {

	private class FacetMetaDescription {

		String name;
		String[] types;
		boolean optional;
		boolean isLabel;
		String[] values;

		FacetMetaDescription(final facet f) {
			name = f.name();
			types = f.type();
			optional = f.optional();
			isLabel = ids.contains(types[0]);
			values = f.values();
		}
	}

	private Class instantiationClass = null;
	private ISymbolConstructor constructor;
	private Class baseClass = null;
	private boolean hasSequence = false;
	private boolean hasArgs = false;
	private boolean hasScope = true;
	private boolean isTopLevel = false;
	private boolean isRemoteContext = false;
	private final List<String> possibleContexts;
	private final Map<String, FacetMetaDescription> possibleFacets = new HashMap();
	private final List<String[]> combinations = new ArrayList();
	private final List<String> mandatoryFacets = new ArrayList();
	private final String omissibleFacet;

	private static final List<String> ids = Arrays.asList(IType.LABEL, IType.ID, IType.NEW_TEMP_ID,
		IType.NEW_VAR_ID, IType.TYPE_ID);

	// private final facet keywordFacet;

	public SymbolMetaDescription(final Class instantiationClass, final Class baseClass,
		final String keyword, final boolean hasSequence, final boolean hasArgs,
		final boolean isTopLevel, final boolean doesNotHaveScope, final List<facet> possibleFacets,
		final String omissible, final List<combination> possibleCombinations,
		final List<String> contexts, final boolean isRemoteContext) {
		facet k = getClass().getAnnotation(facet.class);
		setInstantiationClass(instantiationClass);
		setBaseClass(baseClass);
		setRemoteContext(isRemoteContext);
		// this.keyword = keyword;
		setHasSequence(hasSequence);
		setHasArgs(hasArgs);
		this.omissibleFacet = omissible;
		this.isTopLevel = isTopLevel;
		this.hasScope = !doesNotHaveScope;
		getPossibleFacets().put(k.name(), new FacetMetaDescription(k));
		for ( facet f : possibleFacets ) {
			getPossibleFacets().put(f.name(), new FacetMetaDescription(f));
		}
		for ( FacetMetaDescription f : getPossibleFacets().values() ) {
			if ( !f.optional ) {
				getMandatoryFacets().add(f.name);
			}
		}
		possibleContexts = contexts;
	}

	public boolean isRemoteContext() {
		return isRemoteContext;
	}

	public void setRemoteContext(final boolean isRemoteContext) {
		this.isRemoteContext = isRemoteContext;
	}

	public boolean isFacetDeclaringANewTemp(final String s) {
		// optimiser boucle
		FacetMetaDescription f = getPossibleFacets().get(s);
		if ( f == null ) { return false; }
		return f.types[0].equals(IType.NEW_TEMP_ID);
	}

	public boolean isLabel(final String s) {
		FacetMetaDescription f = getPossibleFacets().get(s);
		if ( f == null ) { return false; }
		return f.isLabel;
	}

	public boolean isDefinition() {
		return isLabel(omissibleFacet);
	}

	public boolean isControl() {
		return !isDefinition();
	}

	public void setInstantiationClass(final Class instantiationClass) {
		this.instantiationClass = instantiationClass;
		constructor = GamlCompiler.getSymbolConstructor(instantiationClass);
	}

	public void setBaseClass(final Class baseClass) {
		this.baseClass = baseClass;
	}

	public Class getInstantiationClass() {
		return instantiationClass;
	}

	public Class getBaseClass() {
		return baseClass;
	}

	public void setHasSequence(final boolean hasSequence) {
		this.hasSequence = hasSequence;
	}

	public boolean hasSequence() {
		return hasSequence;
	}

	public void setHasArgs(final boolean hasArgs) {
		this.hasArgs = hasArgs;
	}

	public boolean hasArgs() {
		return hasArgs;
	}

	public boolean hasScope() {
		return hasScope;
	}

	public Map<String, FacetMetaDescription> getPossibleFacets() {
		return possibleFacets;
	}

	public List<String[]> getPossibleCombinations() {
		return combinations;
	}

	public List<String> getMandatoryFacets() {
		return mandatoryFacets;
	}

	public void verifyMandatoryFacets(final Set<String> facets) throws GamlException {
		for ( String s : mandatoryFacets ) {
			if ( !facets.contains(s) ) { throw new GamlException("Missing facet " + s); }
		}
	}

	public void verifyFacetsValidity(final Set<String> facets) throws GamlException {
		for ( String s : facets ) {
			if ( !possibleFacets.containsKey(s) ) { throw new GamlException("Unknown facet " + s); }
		}
	}

	public void verifyFacetsCombinations(final Set<String> facets) throws GamlException {
		if ( getPossibleCombinations().isEmpty() ) { return; }
		for ( String[] c : getPossibleCombinations() ) {
			boolean allPresent = true;
			for ( String s : c ) {
				allPresent = allPresent && facets.contains(s);
			}
			if ( allPresent ) { return; }
		}
		throw new GamlException("Wrong combination of facets " + facets);
	}

	public boolean verifyContext(final String context) {
		return possibleContexts.contains(context);
	}

	public void verifyFacetsIds(final Facets facets) throws GamlException {
		for ( String s : facets.keySet() ) {
			FacetMetaDescription f = possibleFacets.get(s);
			if ( f.types[0].equals(IType.LABEL) ) {
				facets.compileAsLabel(f.name);
				boolean found = false;
				if ( f.values != null && f.values.length != 0 ) {
					for ( String v : f.values ) {
						if ( facets.equals(s, v) ) {
							found = true;
						}
					}
					if ( !found ) { throw new GamlException("The value of facet " + s +
						"must be one of " + String.valueOf(f.values)); }
				}

			} else if ( IType.ID.equals(f.types[0]) || IType.NEW_TEMP_ID.equals(f.types[0]) ||
				IType.NEW_VAR_ID.equals(f.types[0]) || IType.TYPE_ID.equals(f.types[0]) ) {
				facets.compileAsLabel(f.name);
				String id = facets.getString(s).trim();

				if ( IExpressionParser.RESERVED.contains(id) ) { throw new GamlException(id +
					" is a reserved keyword. It cannot be used as an identifiant"); }
				if ( !Character.isJavaIdentifierStart(id.charAt(0)) ) { throw new GamlException(
					"Character " + id.charAt(0) + " not allowed at the beginning of identifiant" +
						id); }
				for ( char ch : id.toCharArray() ) {
					if ( !Character.isJavaIdentifierPart(ch) ) { throw new GamlException(
						"Character " + ch + " not allowed in identifiant " + id); }
				}

			}

		}
	}

	public void verifyFacets(final Facets facets) throws GamlException {
		Set<String> tags = facets.keySet();
		verifyMandatoryFacets(tags);
		verifyFacetsValidity(tags);
		verifyFacetsCombinations(tags);
		verifyFacetsIds(facets);
	}

	public boolean isTopLevel() {
		return isTopLevel;
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
}
