/*******************************************************************************************************
 *
 * msi.gaml.descriptions.SymbolDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.preferences.GamaPreferences;
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
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 16 mars 2010
 *
 * @todo Description
 *
 */
public abstract class SymbolDescription implements IDescription {

	protected static Set<String> typeProviderFacets = ImmutableSet
			.copyOf(Arrays.asList(VALUE, TYPE, AS, SPECIES, OF, OVER, FROM, INDEX, FUNCTION, UPDATE, INIT, DEFAULT));

	public static int ORDER = 0;
	private final int order = ORDER++;

	private Facets facets;
	protected final EObject element;
	protected IDescription enclosing;
	protected String originName;
	protected String name;
	protected final String keyword;
	private IType<?> type;
	protected boolean validated;
	final SymbolProto proto;

	public SymbolDescription(final String keyword, final IDescription superDesc, final EObject source,
			final Facets facets) {
		this.keyword = keyword;
		this.facets = facets;
		element = source;
		if (superDesc != null) { originName = superDesc.getName(); }
		setEnclosingDescription(superDesc);
		proto = DescriptionFactory.getProto(getKeyword(), getSpeciesContext());
	}

	@Override
	public int getOrder() {
		return order;
	}

	protected boolean hasFacets() {
		return facets != null;
	}

	protected boolean hasFacetsNotIn(final Set<String> others) {
		if (facets == null) return false;
		return !visitFacets((facetName, exp) -> {
			return others.contains(facetName);
		});
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
		for (final String s : strings) {
			facets.remove(s);
		}
		if (facets.isEmpty()) { facets = null; }
	}

	@Override
	public final boolean visitFacets(final Set<String> names, final IFacetVisitor visitor) {
		if (!hasFacets()) return true;
		return facets.forEachFacetIn(names, visitor);
	}

	public IType<?> getTypeDenotedByFacet(final String... s) {
		if (!hasFacets()) return Types.NO_TYPE;
		return getTypeDenotedByFacet(facets.getFirstExistingAmong(s), Types.NO_TYPE);
	}

	@Override
	public String firstFacetFoundAmong(final String... strings) {
		if (!hasFacets()) return null;
		return facets.getFirstExistingAmong(strings);
	}

	public IType<?> getTypeDenotedByFacet(final String s, final IType<?> defaultType) {
		if (!hasFacets()) return defaultType;
		return facets.getTypeDenotedBy(s, this, defaultType);
	}

	public Facets getFacetsCopy() {
		return !hasFacets() ? null : facets.cleanCopy();
	}

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

	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// getSerializer().collectMetaInformation(this, meta);
	// }

	@Override
	public boolean isDocumenting() {
		return enclosing != null && enclosing.isDocumenting();
	}

	@Override
	public int getKind() {
		return getMeta().getKind();
	}

	protected void compileTypeProviderFacets() {
		visitFacets((facetName, exp) -> {
			if (typeProviderFacets.contains(facetName)) { exp.compile(SymbolDescription.this); }
			return true;
		});

	}

	@Override
	public final SymbolProto getMeta() {
		return proto;
	}

	private void flagError(final String s, final String code, final boolean warning, final boolean info,
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
		flagError(s, code, false, false, this.getUnderlyingElement(facet, code.equals(IGamlIssue.UNKNOWN_FACET)), data);
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
		flagError(s, code, true, false, this.getUnderlyingElement(facet, code.equals(IGamlIssue.UNKNOWN_FACET)), data);
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

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
	// @Override
	public final void addChildren(final Iterable<? extends IDescription> originalChildren) {
		if (originalChildren == null /* || !getMeta().hasSequence() */) return;
		for (final IDescription c : originalChildren) {
			addChild(c);
		}
	}

	// @Override
	public IDescription addChild(final IDescription child) {
		if (child == null) return null;
		child.setEnclosingDescription(this);
		return child;
	}

	@Override
	public void setEnclosingDescription(final IDescription desc) {
		enclosing = desc;
	}

	@Override
	public EObject getUnderlyingElement(final Object facet, final boolean returnFacet) {
		if (facet == null) return element;
		if (facet instanceof EObject) return (EObject) facet;
		if (facet instanceof IExpressionDescription) {
			final IExpressionDescription f = (IExpressionDescription) facet;
			final EObject result = f.getTarget();
			if (result != null) return result;
		}
		if (facet instanceof String) {
			if (getMeta() != null && !returnFacet) {
				if (facet.equals(getMeta().getOmissible())) {
					final EObject o = GAML.getEcoreUtils().getExprOf(element);
					if (o != null) return o;
				}
			}
			if (returnFacet) {
				final EObject facetObject = GAML.getEcoreUtils().getFacetsMapOf(element).get(facet);
				if (facetObject != null) return facetObject;
			}
			final IExpressionDescription f = getFacet((String) facet);
			if (f != null) {
				final EObject result = f.getTarget();
				if (result != null)
					return result;
				else {
					final EObject facetObject = GAML.getEcoreUtils().getFacetsMapOf(element).get(facet);
					if (facetObject != null) return facetObject;
				}
			}
			// Last chance if the expression is a constant (no information on EObjects), see Issue #2760

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
	public IDescription getEnclosingDescription() {
		return enclosing;
	}

	@Override
	public boolean hasAttribute(final String aName) {
		return false;
	}

	@Override
	public boolean manipulatesVar(final String aName) {
		return false;
	}

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

	protected IType<?> computeType() {

		// Adapter ca pour prendre ne ocmpte les ITypeProvider
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
	public String getTitle() {
		return "statement " + getKeyword();
	}

	@Override
	public String getDocumentation() {
		return getMeta().getDocumentation();
	}

	@Override
	public String getDefiningPlugin() {
		return getMeta().getDefiningPlugin();
	}

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
	public boolean isBuiltIn() {
		return element == null;
	}

	protected boolean isSynthetic() {
		return false;
	}

	@Override
	public String getOriginName() {
		return originName;
	}

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

		if (validated) return this;
		validated = true;

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
		if (!validateFacets()) return null;
		if (!validateChildren()) return null;
		if (proto.getDeprecated() != null) {
			warning("'" + getKeyword() + "' is deprecated. " + proto.getDeprecated(), IGamlIssue.DEPRECATED);
		}

		// If a custom validator has been defined, run it
		if (proto.getValidator() != null) {
			final boolean semantic = proto.getValidator().validate(this, element);
			if (!semantic) return null;
		}

		return this;
	}

	protected boolean canBeDefinedIn(final IDescription sd) {
		return getMeta().canBeDefinedIn(sd);
	}

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

			if (fp == null) {
				if (facet.contains(IGamlIssue.DOUBLED_CODE)) {
					final String correct = facet.replace(IGamlIssue.DOUBLED_CODE, "");
					final String error = "Facet " + correct + " is declared twice. Please correct.";
					error(error, IGamlIssue.DUPLICATE_DEFINITION, facet, "1");
					error(error, IGamlIssue.DUPLICATE_DEFINITION, correct, "2");
					return false;
				} else if (!isDo) {
					error("Unknown facet " + facet, IGamlIssue.UNKNOWN_FACET, facet);
					return false;
				}
				return true;
			} else if (fp.deprecated != null) {
				warning("Facet '" + facet + "' is deprecated: " + fp.deprecated, IGamlIssue.DEPRECATED, facet);
			}
			if (fp.values != null) {
				final String val = expr.getExpression().literalValue();
				// We have a multi-valued facet
				if (!fp.values.contains(val)) {
					error("Facet '" + facet + "' is expecting a value among " + fp.values + " instead of " + val,
							facet);
					return false;
				}
			} else {
				IExpression exp;
				if (fp.isNewTemp) {
					exp = createVarWithTypes(facet);
					expr.setExpression(exp);
				} else if (!fp.isLabel()) {
					final boolean isRemote = fp.isRemote && this instanceof StatementRemoteWithChildrenDescription;
					IDescription previousEnclosingDescription = null;
					if (isRemote) {
						previousEnclosingDescription =
								((StatementRemoteWithChildrenDescription) this).pushRemoteContext();
					}
					exp = expr.compile(SymbolDescription.this);
					if (isRemote) {
						((StatementRemoteWithChildrenDescription) this).popRemoteContext(previousEnclosingDescription);
					}
				} else {
					exp = expr.getExpression();
				}

				if (exp != null && !isBuiltIn) {
					// Some expresssions might not be compiled
					boolean compatible = false;
					final IType<?> actualType = exp.getGamlType();
					final IType<?> contentType = fp.contentType;
					final IType<?> keyType = fp.keyType;
					for (final IType<?> type : fp.types) {
						IType<?> requestedType1 = type;
						if (requestedType1.isContainer()) {
							requestedType1 = GamaType.from(requestedType1, keyType, contentType);
						}
						compatible = compatible || actualType.equals(requestedType1)
								|| requestedType1.id() == IType.NONE
								|| actualType.id() != IType.NONE && actualType.isTranslatableInto(requestedType1)
								|| Types.isEmptyContainerCase(requestedType1, exp);
						if (compatible) { break; }
					}
					if (!compatible) {
						final String[] strings = new String[fp.types.length];
						for (int i = 0; i < fp.types.length; i++) {
							IType<?> requestedType2 = fp.types[i];
							if (requestedType2.isContainer()) {
								requestedType2 = GamaType.from(requestedType2, keyType, contentType);
							}
							strings[i] = requestedType2.toString();
						}

						warning("Facet '" + facet + "' is expecting " + Arrays.toString(strings) + " instead of "
								+ actualType, IGamlIssue.SHOULD_CAST, facet, fp.types[0].toString());
					}
				}
			}
			return true;
		});

	}

	// Nothing to do here
	protected IExpression createVarWithTypes(final String tag) {
		return null;
	}

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
	public Facets getFacets() {
		return facets == null ? Facets.NULL : facets;
	}

	@Override
	public void attachAlternateVarDescriptionProvider(final IVarDescriptionProvider vp) {

	}

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
