/*******************************************************************************************************
 *
 * SymbolDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.Facets;
import msi.gaml.statements.IStatement;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.COUNTER;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 16 mars 2010
 *
 * @todo Description
 *
 */
public abstract class SymbolDescription implements IDescription {

	/** The type provider facets. */
	protected static Set<String> typeProviderFacets = ImmutableSet
			.copyOf(Arrays.asList(VALUE, TYPE, AS, SPECIES, OF, OVER, FROM, INDEX, FUNCTION, UPDATE, INIT, DEFAULT));

	/** The state. */
	private final EnumSet<Flag> state = EnumSet.noneOf(Flag.class);

	/** The order. */
	private final int order = COUNTER.GET();

	/** The facets. */
	private Facets facets;

	/** The element. */
	protected final EObject element;

	/** The enclosing. */
	protected IDescription enclosing;

	/** The origin name. */
	protected String originName;

	/** The name. */
	protected String name;

	/** The keyword. */
	protected final String keyword;

	/** The type. */
	private IType<?> type;

	/** The validated. */
	// protected boolean validated;

	/** The proto. */
	final SymbolProto proto;

	/**
	 * Instantiates a new symbol description.
	 *
	 * @param keyword
	 *            the keyword
	 * @param superDesc
	 *            the super desc
	 * @param source
	 *            the source
	 * @param facets
	 *            the facets
	 */
	public SymbolDescription(final String keyword, final IDescription superDesc, final EObject source,
			final Facets facets) {
		this.keyword = keyword;
		this.facets = facets;
		element = source;
		setIf(Flag.BuiltIn, element == null);
		if (facets != null && facets.containsKey(ORIGIN)) {
			originName = facets.getLabel(ORIGIN);
			facets.remove(ORIGIN);
		} else if (superDesc != null) { originName = superDesc.getName(); }
		setEnclosingDescription(superDesc);
		proto = DescriptionFactory.getProto(getKeyword(), getSpeciesContext());

	}

	// ---- State management

	/**
	 * Sets the.
	 *
	 * @param flag
	 *            the flag
	 */
	protected void set(final Flag flag) {
		state.add(flag);
	}

	/**
	 * Sets the if.
	 *
	 * @param flag
	 *            the flag
	 * @param condition
	 *            the condition
	 */
	protected void setIf(final Flag flag, final boolean condition) {
		if (condition) {
			set(flag);
		} else {
			unSet(flag);
		}
	}

	/**
	 * Un set.
	 *
	 * @param flag
	 *            the flag
	 */
	protected void unSet(final Flag flag) {
		state.remove(flag);
	}

	/**
	 * Checks if is sets the.
	 *
	 * @param flag
	 *            the flag
	 * @return true, if is sets the
	 */
	protected boolean isSet(final Flag flag) {
		return state.contains(flag);
	}

	@Override
	public int getOrder() { return order; }

	/**
	 * Checks for facets.
	 *
	 * @return true, if successful
	 */
	protected boolean hasFacets() {
		return facets != null;
	}

	/**
	 * Checks for facets not in.
	 *
	 * @param others
	 *            the others
	 * @return true, if successful
	 */
	protected boolean hasFacetsNotIn(final Set<String> others) {
		if (facets == null) return false;
		return !visitFacets((facetName, exp) -> others.contains(facetName));
	}

	@Override
	public final SymbolSerializer<? extends SymbolDescription> getSerializer() {
		final SymbolProto p = getMeta();
		SymbolSerializer<? extends SymbolDescription> d = p.getSerializer();
		if (d == null) {
			d = createSerializer();
			p.setSerializer(d);
		}
		return d;
	}

	@Override
	public IExpressionDescription getFacet(final String string) {
		return !hasFacets() ? null : facets.get(string);
	}

	@Override
	public IExpression getFacetExpr(final String... strings) {
		return !hasFacets() ? null : facets.getExpr(strings);
	}

	@Override
	public IExpressionDescription getFacet(final String... strings) {
		return !hasFacets() ? null : facets.getDescr(strings);
	}

	@Override
	public boolean hasFacet(final String string) {
		return hasFacets() && facets.containsKey(string);
	}

	@Override
	public String getLitteral(final String string) {
		return !hasFacets() ? null : facets.getLabel(string);
	}

	@Override
	public void setFacet(final String name, final IExpressionDescription desc) {
		if (!hasFacets()) { facets = new Facets(); }
		facets.put(name, desc);
	}

	@Override
	public void setFacet(final String string, final IExpression exp) {
		if (!hasFacets()) { facets = new Facets(); }
		facets.put(string, exp);
	}

	@Override
	public void removeFacets(final String... strings) {
		if (!hasFacets()) return;
		for (final String s : strings) { facets.remove(s); }
		if (facets.isEmpty()) { facets = null; }
	}

	@Override
	public final boolean visitFacets(final Set<String> names, final IFacetVisitor visitor) {
		if (!hasFacets()) return true;
		return facets.forEachFacetIn(names, visitor);
	}

	/**
	 * Gets the type denoted by facet.
	 *
	 * @param s
	 *            the s
	 * @return the type denoted by facet
	 */
	public IType<?> getTypeDenotedByFacet(final String... s) {
		if (!hasFacets()) return Types.NO_TYPE;
		return getTypeDenotedByFacet(facets.getFirstExistingAmong(s), Types.NO_TYPE);
	}

	@Override
	public String firstFacetFoundAmong(final String... strings) {
		if (!hasFacets()) return null;
		return facets.getFirstExistingAmong(strings);
	}

	/**
	 * Gets the type denoted by facet.
	 *
	 * @param s
	 *            the s
	 * @param defaultType
	 *            the default type
	 * @return the type denoted by facet
	 */
	public IType<?> getTypeDenotedByFacet(final String s, final IType<?> defaultType) {
		if (!hasFacets()) return defaultType;
		return facets.getTypeDenotedBy(s, this, defaultType);
	}

	/**
	 * Gets the facets copy.
	 *
	 * @return the facets copy
	 */
	public Facets getFacetsCopy() { return !hasFacets() ? null : facets.cleanCopy(); }

	/**
	 * @return
	 */
	protected SymbolSerializer<? extends SymbolDescription> createSerializer() {
		return SYMBOL_SERIALIZER;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getSerializer().serialize(this, includingBuiltIn);
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		getSerializer().collectMetaInformation(this, meta);
	}

	@Override
	public boolean isDocumenting() { return enclosing != null && enclosing.isDocumenting(); }

	@Override
	public int getKind() { return getMeta().getKind(); }

	/**
	 * Compile type provider facets.
	 */
	protected void compileTypeProviderFacets() {
		visitFacets((facetName, exp) -> {
			if (typeProviderFacets.contains(facetName)) { exp.compile(SymbolDescription.this); }
			return true;
		});

	}

	@Override
	public final SymbolProto getMeta() { return proto; }

	/**
	 * Flag error.
	 *
	 * @param s
	 *            the s
	 * @param code
	 *            the code
	 * @param warning
	 *            the warning
	 * @param info
	 *            the info
	 * @param source
	 *            the source
	 * @param data
	 *            the data
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected void flagError(final String s, final String code, final boolean warning, final boolean info,
			final EObject source, final String... data) throws GamaRuntimeException {

		if (warning && !info && !GamaPreferences.Modeling.WARNINGS_ENABLED.getValue()) return;
		if (info && !GamaPreferences.Modeling.INFO_ENABLED.getValue()) return;

		IDescription desc = this;
		EObject e = source;
		if (e == null) { e = getUnderlyingElement(); }
		while (e == null && desc != null) {
			desc = desc.getEnclosingDescription();
			if (desc != null) { e = desc.getUnderlyingElement(); }
		}
		// throws a runtime exception if there is no way to signal the error in
		// the source
		// (i.e. we are probably in a runtime scenario)
		if (e == null || e.eResource() == null || e.eResource().getURI().path().contains(SYNTHETIC_RESOURCES_PREFIX)) {
			if (!warning && !info) throw GamaRuntimeException.error(s, msi.gama.runtime.GAMA.getRuntimeScope());
			return;

		}
		final ValidationContext c = getValidationContext();
		if (c == null) {
			DEBUG.ERR((warning ? "Warning" : "Error") + ": " + s);
			return;
		}
		c.add(new GamlCompilationError(s, code, e, warning, info, data));
	}

	@Override
	public void document(final EObject e, final IGamlDescription desc) {
		if (!isDocumenting()) return;
		final ValidationContext c = getValidationContext();
		if (c == null) return;
		c.setGamlDocumentation(e, desc, true);
	}

	@Override
	public void error(final String message) {
		error(message, IGamlIssue.GENERAL);
	}

	@Override
	public void error(final String message, final String code) {
		flagError(message, code, false, false, getUnderlyingElement(), (String[]) null);
	}

	@Override
	public void error(final String s, final String code, final EObject facet, final String... data) {
		flagError(s, code, false, false, facet, data);
	}

	@Override
	public void error(final String s, final String code, final String facet, final String... data) {
		flagError(s, code, false, false, this.getUnderlyingElement(facet, IGamlIssue.UNKNOWN_FACET.equals(code)), data);
	}

	@Override
	public void info(final String message, final String code) {
		flagError(message, code, false, true, getUnderlyingElement(), (String[]) null);
	}

	@Override
	public void info(final String s, final String code, final EObject facet, final String... data) {
		flagError(s, code, false, true, facet, data);
	}

	@Override
	public void info(final String s, final String code, final String facet, final String... data) {
		flagError(s, code, false, true, this.getUnderlyingElement(facet, false), data);
	}

	@Override
	public void warning(final String message, final String code) {
		flagError(message, code, true, false, null, (String[]) null);
	}

	@Override
	public void warning(final String s, final String code, final EObject object, final String... data) {
		flagError(s, code, true, false, object, data);
	}

	@Override
	public void warning(final String s, final String code, final String facet, final String... data) {
		flagError(s, code, true, false, this.getUnderlyingElement(facet, IGamlIssue.UNKNOWN_FACET.equals(code)), data);
	}

	@Override
	public String getKeyword() { return keyword; }

	@Override
	public String getName() {
		if (name == null) { name = getLitteral(NAME); }
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
		if (getMeta().getPossibleFacets().containsKey(NAME)) {
			setFacet(NAME, LabelExpressionDescription.create(name));
		}
	}

	@Override
	public void dispose() {
		// DEBUG.LOG("Disposing " + getKeyword() + " " + getName());
		if (isBuiltIn()) return;
		visitOwnChildren(DISPOSING_VISITOR);
		if (hasFacets()) { facets.dispose(); }
		facets = null;
		enclosing = null;
		type = null;
	}

	@Override
	public ModelDescription getModelDescription() {
		if (enclosing == null) return null;
		final ModelDescription result = enclosing.getModelDescription();
		if (result != null) {
			if (this.isSynthetic()) return result;
			if (result.isBuiltIn() && !this.isBuiltIn()) return null;
		}
		return result;
	}

	// To add children from outside
	/**
	 * Adds the children.
	 *
	 * @param originalChildren
	 *            the original children
	 */
	// @Override
	public final void addChildren(final Iterable<? extends IDescription> originalChildren) {
		if (originalChildren == null /* || !getMeta().hasSequence() */) return;
		for (final IDescription c : originalChildren) { addChild(c); }
	}

	/**
	 * Adds the child.
	 *
	 * @param child
	 *            the child
	 * @return the i description
	 */
	// @Override
	public IDescription addChild(final IDescription child) {
		if (child == null) return null;
		child.setEnclosingDescription(this);
		return child;
	}

	@Override
	public void setEnclosingDescription(final IDescription desc) { enclosing = desc; }

	@Override
	public EObject getUnderlyingElement(final Object facet, final boolean returnFacet) {
		if (facet == null) return element;
		if (facet instanceof EObject) return (EObject) facet;
		if (facet instanceof IExpressionDescription f) {
			final EObject result = f.getTarget();
			if (result != null) return result;
		}
		if (facet instanceof String) {
			if (getMeta() != null && !returnFacet && facet.equals(getMeta().getOmissible())) {
				final EObject o = GAML.getEcoreUtils().getExprOf(element);
				if (o != null) return o;
			}
			if (returnFacet) {
				final EObject facetObject = GAML.getEcoreUtils().getFacetsMapOf(element).get(facet);
				if (facetObject != null) return facetObject;
			}
			final IExpressionDescription f = getFacet((String) facet);
			if (f != null) {
				final EObject result = f.getTarget();
				if (result != null) return result;
				final EObject facetObject = GAML.getEcoreUtils().getFacetsMapOf(element).get(facet);
				if (facetObject != null) return facetObject;
			}
			// Last chance if the expression is a constant (no information on EObjects), see Issue #2760)

			final EObject facetExpr = GAML.getEcoreUtils().getExpressionAtKey(element, (String) facet);
			if (facetExpr != null) return facetExpr;

		}
		return null;
	}

	@Override
	public IDescription copy(final IDescription into) {
		return this;
	}

	@Override
	public boolean visitOwnChildrenRecursively(final DescriptionVisitor<IDescription> visitor) {
		return true;
	}

	@Override
	public IDescription getEnclosingDescription() { return enclosing; }

	@Override
	public boolean hasAttribute(final String aName) {
		return false;
	}

	@Override
	public boolean manipulatesVar(final String aName) {
		return false;
	}

	/**
	 * Checks for action.
	 *
	 * @param aName
	 *            the a name
	 * @param superInvocation
	 *            the super invocation
	 * @return true, if successful
	 */
	protected boolean hasAction(final String aName, final boolean superInvocation) {
		return false;
	}

	@Override
	public IVarDescriptionProvider getDescriptionDeclaringVar(final String aName) {
		return hasAttribute(aName) ? this : enclosing == null ? null : enclosing.getDescriptionDeclaringVar(aName);
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String aName, final boolean superInvocation) {
		return enclosing == null ? null : enclosing.getDescriptionDeclaringAction(aName, superInvocation);
	}

	@Override
	public IExpression getVarExpr(final String aName, final boolean asField) {
		return null;
	}

	@Override
	public IType<?> getTypeNamed(final String s) {
		final ModelDescription m = getModelDescription();
		if (m == null) return Types.get(s);
		return m.getTypeNamed(s);
	}

	@Override
	public IType<?> getGamlType() {
		if (type == null) { type = computeType(); }
		return type;
	}

	/**
	 * Compute type.
	 *
	 * @return the i type
	 */
	protected IType<?> computeType() {

		// Adapter ca pour prendre en compte les ITypeProvider
		// 13/02/20: Addition of VALUE (see #2932)
		IType<?> tt = getTypeDenotedByFacet(DATA, TYPE, SPECIES, AS, TARGET, ON, VALUE);
		IType<?> kt = getTypeDenotedByFacet(INDEX, tt.getKeyType());
		IType<?> ct = getTypeDenotedByFacet(OF, tt.getContentType());
		final boolean isContainerWithNoContentsType = tt.isContainer() && ct == Types.NO_TYPE;
		final boolean isContainerWithNoKeyType = tt.isContainer() && kt == Types.NO_TYPE;
		// final boolean isSpeciesWithAgentType = tt.id() == IType.SPECIES && ct.id() == IType.AGENT;
		if (isContainerWithNoContentsType || isContainerWithNoKeyType /* || isSpeciesWithAgentType */) {
			compileTypeProviderFacets();
			final IExpression expr = getFacetExpr(INIT, VALUE, UPDATE, FUNCTION, DEFAULT);
			if (expr != null) {
				final IType<?> exprType = expr.getGamlType();
				if (tt.isAssignableFrom(exprType)) {
					tt = exprType;
				} else {
					if (isContainerWithNoKeyType) { kt = exprType.getKeyType(); }
					if (isContainerWithNoContentsType /* || isSpeciesWithAgentType */) {
						ct = exprType.getContentType();
					}
				}
			}
		}

		return GamaType.from(tt, kt, ct);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		if (enclosing == null) return null;
		return enclosing.getSpeciesContext();
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getSpeciesDescription(java.lang.String)
	 */
	@Override
	public SpeciesDescription getSpeciesDescription(final String actualSpecies) {
		final ModelDescription model = getModelDescription();
		if (model == null) return null;
		return model.getSpeciesDescription(actualSpecies);
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getAction(java.lang.String)
	 */
	@Override
	public ActionDescription getAction(final String aName) {
		return null;
	}

	@Override
	public String getTitle() { return "statement " + getKeyword(); }

	@Override
	public Doc getDocumentation() { return getMeta().getDocumentation(); }

	@Override
	public String getDefiningPlugin() { return getMeta().getDefiningPlugin(); }

	@Override
	public void setDefiningPlugin(final String plugin) {
		// Nothing to do here
	}

	@Override
	public ValidationContext getValidationContext() {
		final ModelDescription model = getModelDescription();
		if (model == null) return null;
		return model.getValidationContext();
	}

	@Override
	public boolean isBuiltIn() { return state.contains(Flag.BuiltIn); }

	/**
	 * Checks if is synthetic.
	 *
	 * @return true, if is synthetic
	 */
	protected boolean isSynthetic() { return state.contains(Flag.Synthetic); }

	@Override
	public String getOriginName() { return originName; }

	@Override
	public void setOriginName(final String name) {
		if (originName == null) { originName = name; }
	}

	@Override
	public void resetOriginName() {
		originName = null;
	}

	@Override
	public IDescription validate() {

		if (state.contains(Flag.Validated)) return this;
		set(Flag.Validated);

		if (isBuiltIn()) {
			// We simply make sure that the facets are correctly compiled
			validateFacets();
			return this;
		}
		final IDescription sd = getEnclosingDescription();
		if (sd != null) {
			// We first verify that the description is at the right place
			if (!canBeDefinedIn(sd)) {
				error(getKeyword() + " cannot be defined in " + sd.getKeyword(), IGamlIssue.WRONG_CONTEXT);
				return null;
			}
			// If it is supposed to be unique, we verify this
			if (proto.isUniqueInContext()) {
				final boolean hasError = !sd.visitOwnChildren(child -> {
					if (child.getKeyword().equals(getKeyword()) && child != SymbolDescription.this) {
						final String error = getKeyword() + " is defined twice. Only one definition is allowed in "
								+ sd.getKeyword();
						child.error(error, IGamlIssue.DUPLICATE_KEYWORD, child.getUnderlyingElement(), getKeyword());
						error(error, IGamlIssue.DUPLICATE_KEYWORD, getUnderlyingElement(), getKeyword());
						return false;
					}
					return true;

				});
				if (hasError) return null;
			}
		}

		// We then validate its facets
		if (!validateFacets() || !validateChildren()) return null;
		if (proto.getDeprecated() != null) {
			warning("'" + getKeyword() + "' is deprecated. " + proto.getDeprecated(), IGamlIssue.DEPRECATED);
		}

		// If a custom validator has been defined, run it
		if (!proto.getValidator().validate(this, element)) return null;
		return this;
	}

	/**
	 * Can be defined in.
	 *
	 * @param sd
	 *            the sd
	 * @return true, if successful
	 */
	protected boolean canBeDefinedIn(final IDescription sd) {
		return getMeta().canBeDefinedIn(sd);
	}

	/**
	 * Validate facets.
	 *
	 * @return true, if successful
	 */
	private final boolean validateFacets() {
		// Special case for "do", which can accept (at parsing time) any facet
		final boolean isDo = DO.equals(getKeyword()) || INVOKE.equals(getKeyword());
		final boolean isBuiltIn = isBuiltIn();
		final Iterable<String> missingFacets = proto.getMissingMandatoryFacets(facets);
		if (missingFacets != null && !Iterables.isEmpty(missingFacets)) {
			error("Missing facets " + ImmutableSet.copyOf(missingFacets), IGamlIssue.MISSING_FACET,
					getUnderlyingElement(), Iterables.getFirst(missingFacets, ""), "nil");
			return false;
		}

		return visitFacets((facet, expr) -> {
			final FacetProto fp = proto.getFacet(facet);
			if (fp == null) return processUnknowFacet(isDo, facet);
			if (fp.getDeprecated() != null) {
				warning("Facet '" + facet + "' is deprecated: " + fp.getDeprecated(), IGamlIssue.DEPRECATED, facet);
			}
			if (fp.values != null) {
				if (!processMultiValuedFacet(facet, expr, fp)) return false;
			} else {
				IExpression exp = compileExpression(facet, expr, fp);
				if (exp != null && !isBuiltIn) {
					// Some expresssions might not be compiled
					final IType<?> actualType = exp.getGamlType();
					if (specialCaseForPointAndDate(fp, actualType)) return true;
					final IType<?> contentType = fp.contentType;
					final IType<?> keyType = fp.keyType;
					boolean compatible = verifyFacetTypesCompatibility(fp, exp, actualType, contentType, keyType);
					if (!compatible) {
						emitFacetTypesIncompatibilityWarning(facet, fp, actualType, contentType, keyType);
					}
					// verifyFacetExprEnclosingContext(facet, fp, exp);
				}
			}
			return true;
		});

	}

	/**
	 * Verify facet expr enclosing context.
	 *
	 * @param facet
	 *            the facet
	 * @param fp
	 *            the fp
	 * @param exp
	 *            the exp
	 */
	// private void verifyFacetExprEnclosingContext(final String facet, final FacetProto fp, final IExpression exp) {
	// if (!exp.isValidInContext(enclosing)) {
	// warning("Facet '" + facet + "' cannot be compiled in the context of " + enclosing, IGamlIssue.WRONG_CONTEXT,
	// facet);
	// }
	// }

	/**
	 * Special case for point and date.
	 *
	 * @param fp
	 *            the fp
	 * @param actualType
	 *            the actual type
	 */
	private boolean specialCaseForPointAndDate(final FacetProto fp, final IType<?> actualType) {
		// Special case for init. Temporary solution before we can pass ITypeProvider.OWNER_TYPE to the init
		// facet. Concerned types are point and date, which belong to "NumberVariable" and can accept nil,
		// while int and float cannot
		if (INIT.equals(fp.name)) {
			IType<?> requestedType = SymbolDescription.this.getGamlType();
			if ((Types.POINT == requestedType || Types.DATE == requestedType) && actualType == Types.NO_TYPE)
				return true;
		}
		return false;
	}

	/**
	 * Compile expression.
	 *
	 * @param facet
	 *            the facet
	 * @param expr
	 *            the expr
	 * @param fp
	 *            the fp
	 * @return the i expression
	 */
	private IExpression compileExpression(final String facet, final IExpressionDescription expr, final FacetProto fp) {
		IExpression exp;
		if (fp.isNewTemp) {
			exp = createVarWithTypes(facet);
			expr.setExpression(exp);
		} else if (!fp.isLabel()) {
			final boolean isRemote = fp.isRemote && this instanceof StatementRemoteWithChildrenDescription;
			IDescription previousEnclosingDescription = null;
			if (isRemote) {
				previousEnclosingDescription = ((StatementRemoteWithChildrenDescription) this).pushRemoteContext();
			}
			exp = expr.compile(SymbolDescription.this);
			if (isRemote) {
				((StatementRemoteWithChildrenDescription) this).popRemoteContext(previousEnclosingDescription);
			}
		} else {
			exp = expr.getExpression();
		}
		return exp;
	}

	/**
	 * Emit facet types incompatibility warning.
	 *
	 * @param facet
	 *            the facet
	 * @param fp
	 *            the fp
	 * @param actualType
	 *            the actual type
	 * @param contentType
	 *            the content type
	 * @param keyType
	 *            the key type
	 */
	private void emitFacetTypesIncompatibilityWarning(final String facet, final FacetProto fp,
			final IType<?> actualType, final IType<?> contentType, final IType<?> keyType) {
		final String[] strings = new String[fp.types.length];
		for (int i = 0; i < fp.types.length; i++) {
			IType<?> requestedType2 = fp.types[i];
			if (requestedType2.isContainer()) { requestedType2 = GamaType.from(requestedType2, keyType, contentType); }
			strings[i] = requestedType2.toString();
		}

		warning("Facet '" + facet + "' is expecting " + Arrays.toString(strings) + " instead of " + actualType,
				IGamlIssue.SHOULD_CAST, facet, fp.types[0].toString());
	}

	/**
	 * Verify facet types compatibility.
	 *
	 * @param fp
	 *            the fp
	 * @param exp
	 *            the exp
	 * @param actualType
	 *            the actual type
	 * @param contentType
	 *            the content type
	 * @param keyType
	 *            the key type
	 * @return true, if successful
	 */
	private boolean verifyFacetTypesCompatibility(final FacetProto fp, final IExpression exp, final IType<?> actualType,
			final IType<?> contentType, final IType<?> keyType) {
		boolean compatible = false;
		for (final IType<?> type : fp.types) {
			IType<?> requestedType1 = type;
			if (requestedType1.isContainer()) { requestedType1 = GamaType.from(requestedType1, keyType, contentType); }
			compatible = compatible || actualType.equals(requestedType1) || requestedType1.id() == IType.NONE
					|| actualType.id() != IType.NONE && actualType.isTranslatableInto(requestedType1)
					|| Types.isEmptyContainerCase(requestedType1, exp);
			if (compatible) { break; }
		}
		return compatible;
	}

	/**
	 * Process multi valued facet.
	 *
	 * @param facet
	 *            the facet
	 * @param expr
	 *            the expr
	 * @param fp
	 *            the fp
	 * @return true, if successful
	 */
	private boolean processMultiValuedFacet(final String facet, final IExpressionDescription expr,
			final FacetProto fp) {
		final String val = expr.getExpression().literalValue();
		// We have a multi-valued facet
		if (!fp.values.contains(val)) {
			error("Facet '" + facet + "' is expecting a value among " + fp.values + " instead of " + val, facet);
			return false;
		}
		return true;
	}

	/**
	 * Process unknow facet.
	 *
	 * @param isDo
	 *            the is do
	 * @param facet
	 *            the facet
	 * @return true, if successful
	 */
	private boolean processUnknowFacet(final boolean isDo, final String facet) {
		if (facet.contains(IGamlIssue.DOUBLED_CODE)) {
			final String correct = facet.replace(IGamlIssue.DOUBLED_CODE, "");
			final String error = "Facet " + correct + " is declared twice. Please correct.";
			error(error, IGamlIssue.DUPLICATE_DEFINITION, facet, "1");
			error(error, IGamlIssue.DUPLICATE_DEFINITION, correct, "2");
			return false;
		}
		if (!isDo) {
			error("Unknown facet " + facet, IGamlIssue.UNKNOWN_FACET, facet);
			return false;
		}
		return true;
	}

	/**
	 * Creates the var with types.
	 *
	 * @param tag
	 *            the tag
	 * @return the i expression
	 */
	// Nothing to do here
	protected IExpression createVarWithTypes(final String tag) {
		return null;
	}

	/**
	 * Validate children.
	 *
	 * @return true, if successful
	 */
	protected boolean validateChildren() {
		return visitOwnChildren(VALIDATING_VISITOR);
	}

	// protected boolean validateChildrenInParallel() {
	// final ConcurrentHashMap map = new ConcurrentHashMap<>();
	// for (final IDescription d : getOwnChildren()) {
	// map.putIfAbsent(d, d);
	// }
	// map.forEach(1, (BiConsumer) VALIDATING_VISITOR);
	// return true;
	// }

	@Override
	public final ISymbol compile() {
		validate();
		final ISymbol cs = proto.create(this);
		if (cs == null) return null;
		if (proto.hasArgs()) {
			((IStatement.WithArgs) cs).setFormalArgs(((StatementDescription) this).createCompiledArgs());
		}
		if (proto.hasSequence() && !proto.isPrimitive()) { cs.setChildren(compileChildren()); }
		return cs;

	}

	/**
	 * Method compileChildren()
	 *
	 * @see msi.gaml.descriptions.IDescription#compileChildren()
	 */
	protected Iterable<? extends ISymbol> compileChildren() {

		final List<ISymbol> lce = new ArrayList<>();
		visitChildren(desc -> {
			final ISymbol s = desc.compile();
			if (s != null) { lce.add(s); }
			return true;
		});
		return lce;
	}

	@Override
	public Iterable<IDescription> getChildrenWithKeyword(final String aKeyword) {
		return Iterables.filter(getOwnChildren(), each -> each.getKeyword().equals(aKeyword));
	}

	@Override
	public IDescription getChildWithKeyword(final String aKeyword) {
		return Iterables.find(getOwnChildren(), each -> each.getKeyword().equals(aKeyword), null);
	}
	//
	// @Override
	// public void computeStats(final FacetVisitor proc, final int[] facetNumber, final int[] descWithNoFacets,
	// final int[] descNumber) {
	// visitFacets(proc);
	// final int facetSize = facets == null ? 0 : facets.size();
	// facetNumber[0] += facetSize;
	// descNumber[0]++;
	// if (facetSize == 1)
	// descWithNoFacets[0]++;
	//
	// visitChildren(new DescriptionVisitor<IDescription>() {
	//
	// @Override
	// public boolean visit(final IDescription desc) {
	// desc.computeStats(proc, facetNumber, descWithNoFacets, descNumber);
	// return true;
	// }
	// });
	//
	// }

	/**
	 * Convenience method to access facets from other structures. However, this method should be (when possible)
	 * replaced by the usage of the visitor pattern through visitFacets()
	 */
	@Override
	public Facets getFacets() { return facets == null ? Facets.NULL : facets; }

	@Override
	public void attachAlternateVarDescriptionProvider(final IVarDescriptionProvider vp) {}

	/**
	 * Gets the similar child.
	 *
	 * @param container
	 *            the container
	 * @param desc
	 *            the desc
	 * @return the similar child
	 */
	public static IDescription getSimilarChild(final IDescription container, final IDescription desc) {
		final IDescription[] found = new IDescription[1];
		container.visitChildren(d -> {
			if (d != null && d.getKeyword().equals(desc.getKeyword()) && d.getName().equals(desc.getName())) {
				found[0] = d;
				return false;
			}
			return true;
		});
		return found[0];
	}

	@Override
	public void replaceChildrenWith(final Iterable<IDescription> array) {}

}
