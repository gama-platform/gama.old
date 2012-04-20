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
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.ISymbolKind;
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
// @facet(name = IKeyword.KEYWORD, type = IType.ID, optional = true)
public class SymbolMetaDescription {

	public static Set<String> nonVariableStatements = new HashSet();

	public static class FacetMetaDescription {

		public String name;
		public String[] types;
		public boolean optional;
		public boolean isLabel;
		public String[] values;

		private FacetMetaDescription() {}

		FacetMetaDescription(final facet f) {
			name = f.name();
			types = f.type();
			optional = f.optional();
			isLabel = ids.contains(types[0]);
			values = f.values();
		}

		static FacetMetaDescription DEPENDS_ON() {
			FacetMetaDescription f = new FacetMetaDescription();
			f.name = IKeyword.DEPENDS_ON;
			f.types = new String[] { IType.NONE_STR };
			f.optional = true;
			f.isLabel = true;
			f.values = new String[0];
			return f;
		}

		static FacetMetaDescription KEYWORD() {
			FacetMetaDescription f = new FacetMetaDescription();
			f.name = IKeyword.KEYWORD;
			f.types = new String[] { IType.ID };
			f.optional = true;
			f.isLabel = true;
			f.values = new String[0];
			return f;
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

	public SymbolMetaDescription(final Class instantiationClass, final Class baseClass,
		final String keyword, final boolean hasSequence, final boolean hasArgs, final int kind,
		final boolean doesNotHaveScope, final List<facet> possibleFacets, final String omissible,
		final List<combination> possibleCombinations, final List<String> contexts,
		final boolean isRemoteContext) {
		setInstantiationClass(instantiationClass);
		setBaseClass(baseClass);
		setRemoteContext(isRemoteContext);
		setHasSequence(hasSequence);
		setHasArgs(hasArgs);
		this.omissibleFacet = omissible;
		this.isTopLevel = kind == ISymbolKind.BEHAVIOR;
		this.hasScope = !doesNotHaveScope;
		getPossibleFacets().put(IKeyword.KEYWORD, FacetMetaDescription.KEYWORD());
		getPossibleFacets().put(IKeyword.DEPENDS_ON, FacetMetaDescription.DEPENDS_ON());
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

	public void verifyMandatoryFacets(final ISyntacticElement e, final Set<String> facets,
		final IDescription context) {
		for ( String s : mandatoryFacets ) {
			if ( !facets.contains(s) ) {
				new GamlCompilationError("Missing facet " + s, e);
			}
		}
	}

	public void verifyFacetsValidity(final ISyntacticElement e, final Set<String> facets,
		final IDescription context) {
		// Special case for "do", which can accept (at parsing time) any facet
		if ( e.getKeyword().equals(DO) ) { return; }
		for ( String s : facets ) {
			if ( !possibleFacets.containsKey(s) ) {
				GamlCompilationError error = new GamlCompilationError("Unknown facet " + s, e);
				error.setObjectOfInterest(s);
			}
		}
	}

	public void verifyFacetsCombinations(final ISyntacticElement e, final Set<String> facets,
		final IDescription context) {
		if ( getPossibleCombinations().isEmpty() ) { return; }
		for ( String[] c : getPossibleCombinations() ) {
			boolean allPresent = true;
			for ( String s : c ) {
				allPresent = allPresent && facets.contains(s);
			}
			if ( allPresent ) { return; }
		}
		new GamlCompilationError("Wrong combination of facets " + facets, e);
	}

	public boolean verifyContext(final String context) {
		return possibleContexts.contains(context);
	}

	public void verifyFacetsIds(final ISyntacticElement e, final Facets facets,
		final IDescription context) {
		for ( String s : facets.keySet() ) {
			FacetMetaDescription f = possibleFacets.get(s);
			if ( f == null ) {
				continue;
			}
			if ( f.types[0].equals(IType.LABEL) ) {
				facets.put(f.name, facets.get(f.name).compileAsLabel());
				// facets.compileAsLabel(f.name);
				boolean found = false;
				if ( f.values != null && f.values.length != 0 ) {
					for ( String v : f.values ) {
						if ( facets.equals(s, v) ) {
							found = true;
						}
					}
					if ( !found ) {
						GamlCompilationError error =
							new GamlCompilationError("The value of facet " + s +
								" must be one of " + Arrays.toString(f.values), e);
						error.setObjectOfInterest(s);
					}
				}

			} else if ( IType.ID.equals(f.types[0]) || IType.NEW_TEMP_ID.equals(f.types[0]) ||
				IType.NEW_VAR_ID.equals(f.types[0]) || IType.TYPE_ID.equals(f.types[0]) ) {
				facets.put(f.name, facets.get(f.name).compileAsLabel());
				String id = facets.getLabel(s).trim();

				if ( IExpressionParser.RESERVED.contains(id) ) {
					GamlCompilationError error =
						new GamlCompilationError(id +
							" is a reserved keyword. It cannot be used as an identifiant", e);
					error.setObjectOfInterest(id);
				}
				// if ( !id.isEmpty() && !Character.isJavaIdentifierStart(id.charAt(0)) ) { throw
				// new GamlException(
				// "Character " + id.charAt(0) + " not allowed at the beginning of identifiant" +
				// id, e); }
				// for ( char ch : id.toCharArray() ) {
				// if ( !Character.isJavaIdentifierPart(ch) ) { throw new GamlException(
				// "Character " + ch + " not allowed in identifiant " + id, e); }
				// }

			}

		}
	}

	public void verifyFacets(final ISyntacticElement e, final Facets facets,
		final IDescription context) {
		Set<String> tags = facets.keySet();
		verifyMandatoryFacets(e, tags, context);
		verifyFacetsValidity(e, tags, context);
		verifyFacetsCombinations(e, tags, context);
		verifyFacetsIds(e, facets, context);
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
