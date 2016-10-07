/*********************************************************************************************
 *
 *
 * 'SymbolDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import static msi.gama.util.GAML.getExpressionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.Facets;
import msi.gaml.statements.IStatement;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 16 mars 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class SymbolDescription implements IDescription {

	protected static Set<String> typeProviderFacets = new HashSet(
			Arrays.asList(VALUE, TYPE, AS, SPECIES, OF, OVER, FROM, INDEX, FUNCTION, UPDATE, INIT, DEFAULT));

	private Facets facets;
	protected final EObject element;
	protected IDescription enclosing;
	protected String originName;
	protected String name;
	protected final String keyword;
	// protected boolean validated;
	private IType type;

	public SymbolDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
			final EObject source, final Facets facets) {
		this.keyword = keyword;
		this.facets = facets;
		element = source;
		if (superDesc != null) {
			originName = superDesc.getName();
		}
		setEnclosingDescription(superDesc);
		if (getMeta().hasSequence()) {
			addChildren(cp.getChildren());
		}
	}

	protected boolean hasFacets() {
		return facets != null;
	}

	protected boolean hasFacetsNotIn(final Set<String> others) {
		if (facets == null)
			return false;
		return !visitFacets(new FacetVisitor() {

			@Override
			public boolean visit(final String name, final IExpressionDescription exp) {
				if (others.contains(name))
					return true;
				return false;
			}
		});
	}

	public final SymbolSerializer getSerializer() {
		final SymbolProto p = getMeta();
		SymbolSerializer d = p.getSerializer();
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
		return hasFacets() && facets.contains(string);
	}

	@Override
	public String getLitteral(final String string) {
		return !hasFacets() ? null : facets.getLabel(string);
	}

	@Override
	public void setFacet(final String name, final IExpressionDescription desc) {
		if (!hasFacets())
			facets = new Facets();
		facets.put(name, desc);
	}

	@Override
	public void setFacet(final String string, final IExpression exp) {
		if (!hasFacets()) {
			facets = new Facets();
		}
		facets.put(string, exp);
	}

	@Override
	public void removeFacets(final String... strings) {
		if (!hasFacets())
			return;
		for (final String s : strings) {
			facets.remove(s);
		}
		if (facets.isEmpty())
			facets = null;
	}

	@Override
	public boolean visitFacets(final Set<String> names, final FacetVisitor visitor) {
		if (!hasFacets())
			return true;
		if (names == null)
			return facets.forEachEntry(visitor);
		for (final String s : names) {
			final IExpressionDescription exp = facets.get(s);
			if (exp != null) {
				if (!visitor.visit(s, exp))
					return false;
			}
		}
		return true;
	}

	public IType getTypeDenotedByFacet(final String... s) {
		if (!hasFacets())
			return Types.NO_TYPE;
		return getTypeDenotedByFacet(facets.getFirstExistingAmong(s), Types.NO_TYPE);
	}

	public IType getTypeDenotedByFacet(final String s, final IType defaultType) {
		if (!hasFacets())
			return defaultType;
		return facets.getTypeDenotedBy(s, this, defaultType);
	}

	public Facets getFacetsCopy() {
		return !hasFacets() ? null : facets.cleanCopy();
	}

	/**
	 * @return
	 */
	protected SymbolSerializer createSerializer() {
		return SymbolSerializer.getInstance();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getSerializer().serialize(this, includingBuiltIn);
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		getSerializer().collectMetaInformation(this, meta);
	}

	protected boolean isDocumenting() {
		final ModelDescription md = getModelDescription();
		if (md == null)
			return false;
		return md.isDocumenting();
	}

	@Override
	public int getKind() {
		return getMeta().getKind();
	}

	protected void compileTypeProviderFacets() {
		visitFacets(new FacetVisitor() {

			@Override
			public boolean visit(final String name, final IExpressionDescription exp) {
				if (typeProviderFacets.contains(name))
					exp.compile(SymbolDescription.this);
				return true;
			}
		});

	}

	@Override
	public SymbolProto getMeta() {
		return DescriptionFactory.getProto(getKeyword(), getModelDescription());
	}

	private void flagError(final String s, final String code, final boolean warning, final boolean info,
			final EObject source, final String... data) throws GamaRuntimeException {

		if (warning && !info && !GamaPreferences.WARNINGS_ENABLED.getValue()) {
			return;
		}
		if (info && !GamaPreferences.INFO_ENABLED.getValue()) {
			return;
		}

		IDescription desc = this;
		EObject e = source;
		if (e == null) {
			e = getUnderlyingElement(null);
		}
		while (e == null && desc != null) {
			desc = desc.getEnclosingDescription();
			if (desc != null) {
				e = desc.getUnderlyingElement(null);
			}
		}
		if (!warning && !info) {
			// final String resource = e == null || e.eResource() == null ? "(no
			// file)"
			// : e.eResource().getURI().lastSegment();
			// System.err.println("COMPILATION ERROR in " + this.toString() + ":
			// " + s + "; source: " + resource);
		}
		// throws a runtime exception if there is no way to signal the error in
		// the source
		// (i.e. we are probably in a runtime scenario)
		if (e == null || e.eResource() == null || e.eResource().getURI().path().contains(SYNTHETIC_RESOURCES_PREFIX)) {
			throw warning ? GamaRuntimeException.warning(s) : GamaRuntimeException.error(s);
		}
		final ValidationContext c = getValidationContext();
		if (c == null) {
			System.out.println((warning ? "Warning" : "Error") + ": " + s);
			return;
		}
		c.add(new GamlCompilationError(s, code, e, warning, info, data));
	}

	@Override
	public void document(final EObject e, final IGamlDescription desc) {
		if (!isDocumenting())
			return;
		final ValidationContext c = getValidationContext();
		if (c == null) {
			return;
		}
		c.setGamlDocumentation(e, desc, true);
	}

	@Override
	public void error(final String message) {
		error(message, IGamlIssue.GENERAL);
	}

	@Override
	public void error(final String message, final String code) {
		flagError(message, code, false, false, getUnderlyingElement(null), (String[]) null);
	}

	@Override
	public void error(final String s, final String code, final EObject facet, final String... data) {
		flagError(s, code, false, false, facet, data);
	}

	@Override
	public void error(final String s, final String code, final String facet, final String... data) {
		flagError(s, code, false, false, this.getUnderlyingElement(facet), data);
	}

	@Override
	public void info(final String message, final String code) {
		flagError(message, code, false, true, getUnderlyingElement(null), (String[]) null);
	}

	@Override
	public void info(final String s, final String code, final EObject facet, final String... data) {
		flagError(s, code, false, true, facet, data);
	}

	@Override
	public void info(final String s, final String code, final String facet, final String... data) {
		flagError(s, code, false, true, this.getUnderlyingElement(facet), data);
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
		flagError(s, code, true, false, this.getUnderlyingElement(facet), data);
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

	@Override
	public String getName() {
		if (name == null)
			name = getLitteral(NAME);
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
		if (getMeta().getPossibleFacets().containsKey(NAME))
			setFacet(NAME, LabelExpressionDescription.create(name));
	}

	@Override
	public void dispose() {
		if (isBuiltIn()) {
			return;
		}
		visitOwnChildren(DISPOSING_VISITOR);
		if (hasFacets())
			facets.dispose();
		facets = null;
		enclosing = null;
		type = null;
	}

	@Override
	public ModelDescription getModelDescription() {
		if (enclosing == null) {
			return null;
		}
		final ModelDescription result = enclosing.getModelDescription();
		if (result != null && result.isBuiltIn() && !this.isBuiltIn()) {
			return null;
		}
		return result;
	}

	// To add children from outside
	@Override
	public final void addChildren(final Iterable<IDescription> originalChildren) {
		for (final IDescription c : originalChildren) {
			addChild(c);
		}
	}

	@Override
	public IDescription addChild(final IDescription child) {
		if (child == null) {
			return null;
		}
		child.setEnclosingDescription(this);
		return child;
	}

	@Override
	public void setEnclosingDescription(final IDescription desc) {
		enclosing = desc;
	}

	@Override
	public EObject getUnderlyingElement(final Object facet) {
		if (facet == null) {
			return element;
		}
		if (facet instanceof EObject) {
			return (EObject) facet;
		}

		final IExpressionDescription f = facet instanceof IExpressionDescription ? (IExpressionDescription) facet
				: facet instanceof String ? getFacet((String) facet) : null;
		if (f == null) {
			return element;
		}
		final EObject target = f.getTarget();
		if (target == null) {
			return element;
		}
		return getExpressionFactory().getFacetExpression(this, target);
	}

	@Override
	public IDescription copy(final IDescription into) {
		return this;
	}

	@Override
	public abstract boolean visitChildren(DescriptionVisitor visitor);

	@Override
	public abstract boolean visitOwnChildren(DescriptionVisitor visitor);

	@Override
	public IDescription getEnclosingDescription() {
		return enclosing;
	}

	@Override
	public boolean hasAttribute(final String name) {
		return false;
	}

	@Override
	public boolean manipulatesVar(final String name) {
		return false;
	}

	protected boolean hasAction(final String name) {
		return false;
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		return hasAttribute(name) ? this : enclosing == null ? null : enclosing.getDescriptionDeclaringVar(name);
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name) {
		return hasAction(name) ? this : enclosing == null ? null : enclosing.getDescriptionDeclaringAction(name);
	}

	@Override
	public IExpression getVarExpr(final String name, final boolean asField) {
		return null;
	}

	@Override
	public IType getTypeNamed(final String s) {
		final ModelDescription m = getModelDescription();
		if (m == null) {
			return Types.get(s);
		}
		return m.getTypeNamed(s);
	}

	@Override
	public IType getType() {
		if (type == null) {
			type = computeType();
		}
		return type;
	}

	protected IType computeType() {

		// Adapter ca pour prendre ne ocmpte les ITypeProvider
		IType tt = getTypeDenotedByFacet(TYPE, SPECIES, AS, TARGET, ON);
		IType kt = getTypeDenotedByFacet(INDEX, tt.getKeyType());
		IType ct = getTypeDenotedByFacet(OF, tt.getContentType());
		final boolean isContainerWithNoContentsType = tt.isContainer() && ct == Types.NO_TYPE;
		final boolean isContainerWithNoKeyType = tt.isContainer() && kt == Types.NO_TYPE;
		final boolean isSpeciesWithAgentType = tt.id() == IType.SPECIES && ct.id() == IType.AGENT;
		if (isContainerWithNoContentsType || isContainerWithNoKeyType || isSpeciesWithAgentType) {
			compileTypeProviderFacets();
			final IExpression expr = getFacetExpr(INIT, VALUE, UPDATE, FUNCTION, DEFAULT);
			if (expr != null) {
				final IType exprType = expr.getType();
				if (tt.isAssignableFrom(exprType)) {
					tt = exprType;
				} else {
					if (isContainerWithNoKeyType) {
						kt = exprType.getKeyType();
					}
					if (isContainerWithNoContentsType || isSpeciesWithAgentType) {
						ct = exprType.getContentType();
					}
				}
			}
		}

		return GamaType.from(tt, kt, ct);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		if (enclosing == null) {
			return null;
		}
		return enclosing.getSpeciesContext();
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getSpeciesDescription(java.lang.String)
	 */
	@Override
	public SpeciesDescription getSpeciesDescription(final String actualSpecies) {
		final ModelDescription model = getModelDescription();
		if (model == null) {
			return null;
		}
		return model.getSpeciesDescription(actualSpecies);
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getAction(java.lang.String)
	 */
	@Override
	public ActionDescription getAction(final String name) {
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
		if (model == null) {
			return null;
		}
		return model.getValidationContext();
	}

	@Override
	public boolean isBuiltIn() {
		return element == null && !isSynthetic();
	}

	public boolean isSynthetic() {
		return getName() != null && getName().startsWith(SYNTHETIC);
	}

	@Override
	public String getOriginName() {
		return originName;
	}

	@Override
	public void setOriginName(final String name) {
		if (originName == null) {
			originName = name;
		}
	}

	@Override
	public void resetOriginName() {
		originName = null;
	}

	@Override
	public IDescription validate() {
		// if (validated && "ball_in_group".equals(getName())) {
		// System.out.println("Trying to revalidate " + this);
		// return this;
		// }
		// validated = true;
		if (isBuiltIn()) {
			// We simply make sure that the facets are correctly compiled
			validateFacets();
			return this;
		}
		final IDescription sd = getEnclosingDescription();
		final SymbolProto proto = getMeta();
		if (sd != null) {
			// We first verify that the description is at the right place
			if (!canBeDefinedIn(sd)) {
				error(getKeyword() + " cannot be defined in " + sd.getKeyword(), IGamlIssue.WRONG_CONTEXT);
				// return this;
				return null;
			}
			// If it is supposed to be unique, we verify this
			if (proto.isUniqueInContext()) {
				final boolean hasError = !sd.visitOwnChildren(new DescriptionVisitor<IDescription>() {

					@Override
					public boolean visit(final IDescription child) {
						if (child.getKeyword().equals(getKeyword()) && child != SymbolDescription.this) {
							final String error = getKeyword() + " is defined twice. Only one definition is allowed in "
									+ sd.getKeyword();
							child.error(error, IGamlIssue.DUPLICATE_KEYWORD, child.getUnderlyingElement(null),
									getKeyword());
							error(error, IGamlIssue.DUPLICATE_KEYWORD, getUnderlyingElement(null), getKeyword());
							return false;
						}
						return true;

					}

				});

				if (hasError)
					// return this;
					return null;

			}
		}
		// We then validate its facets
		if (!validateFacets())
			return null;

		// if (proto.isRemoteContext()) {
		// copyTempsAbove();
		// }
		if (!validateChildren())
			return null;

		if (proto.getDeprecated() != null) {
			warning("'" + getKeyword() + "' is deprecated. " + proto.getDeprecated(), IGamlIssue.DEPRECATED);
		}

		// If a custom validator has been defined, run it
		if (proto.getValidator() != null) {
			proto.getValidator().validate(this);
		}

		return this;
	}

	protected boolean canBeDefinedIn(final IDescription sd) {
		return getMeta().canBeDefinedIn(sd);
	}

	private final boolean validateFacets() {
		// Special case for "do", which can accept (at parsing time) any facet
		final boolean isDo = DO.equals(getKeyword());
		final boolean isBuiltIn = isBuiltIn();
		final SymbolProto proto = getMeta();
		final Set<String> missingFacets = proto.getMissingMandatoryFacets(facets);
		if (missingFacets != null) {
			error("Missing facets " + missingFacets, IGamlIssue.MISSING_FACET);
			return false;
		}
		final boolean ok = visitFacets(new FacetVisitor() {

			@Override
			public boolean visit(final String facet, final IExpressionDescription expr) {
				final FacetProto fp = proto.getFacet(facet);
				if (fp == null) {
					if (!isDo) {
						error("Unknown facet " + facet, IGamlIssue.UNKNOWN_FACET, facet);
						return false;
					}
					return true;
				} else if (fp.deprecated != null) {
					warning("Facet '" + facet + "' is deprecated: " + fp.deprecated, IGamlIssue.DEPRECATED, facet);
				}
				if (fp.values.size() > 0) {
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
						exp = expr.compile(SymbolDescription.this);
					} else {
						exp = expr.getExpression();
					}

					if (exp != null && !isBuiltIn) {
						// Some expresssions might not be compiled
						boolean compatible = false;
						final IType actualType = exp.getType();
						// final ITypesManager tm =
						// getModelDescription().getTypesManager();
						final IType contentType = fp.contentType;
						final IType keyType = fp.keyType;
						for (final IType type : fp.types) {
							IType requestedType = type;
							if (requestedType.isContainer()) {
								requestedType = GamaType.from(requestedType, keyType, contentType);
							}
							compatible = compatible || actualType.isTranslatableInto(requestedType);
							if (compatible) {
								break;
							}
						}
						if (!compatible) {
							final String[] strings = new String[fp.types.length];
							for (int i = 0; i < fp.types.length; i++) {
								IType requestedType = fp.types[i];
								if (requestedType.isContainer()) {
									requestedType = GamaType.from(requestedType, keyType, contentType);
								}
								strings[i] = requestedType.toString();
							}

							warning("Facet '" + facet + "' is expecting " + Arrays.toString(strings) + " instead of "
									+ actualType, IGamlIssue.SHOULD_CAST, facet, fp.types[0].toString());
						}
					}
					// else if (exp == null) // VERIFY this
					// return false;
				}
				return true;
			}
		});
		return ok;

	}

	// Nothing to do here
	protected IExpression createVarWithTypes(final String tag) {
		return null;
	}

	protected boolean validateChildren() {
		return visitOwnChildren(VALIDATING_VISITOR);
	}

	@Override
	public final ISymbol compile() {
		final SymbolProto proto = getMeta();
		validate();
		final ISymbol cs = proto.create(this);
		if (cs == null) {
			return null;
		}
		if (proto.hasArgs()) {
			((IStatement.WithArgs) cs).setFormalArgs(((StatementDescription) this).createCompiledArgs());
		}
		if (proto.hasSequence() && !proto.isPrimitive()) {
			// if (proto.isRemoteContext()) {
			// copyTempsAbove();
			// }
			cs.setChildren(compileChildren());
		}
		return cs;

	}

	/**
	 * Method compileChildren()
	 * 
	 * @see msi.gaml.descriptions.IDescription#compileChildren()
	 */
	protected List<? extends ISymbol> compileChildren() {
		final List<ISymbol> lce = new ArrayList<>();
		visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				final ISymbol s = desc.compile();
				if (s != null) {
					lce.add(s);
				}
				return true;
			}

		});

		return lce;

	}

	@Override
	public Iterable<IDescription> getChildrenWithKeyword(final String keyword) {

		final List<IDescription> result = new ArrayList<>();

		visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				if (desc.getKeyword().equals(keyword))
					result.add(desc);
				return true;
			}

		});

		return result;
	}

	@Override
	public IDescription getChildWithKeyword(final String keyword) {
		final IDescription[] result = new IDescription[1];
		visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				if (desc.getKeyword().equals(keyword)) {
					result[0] = desc;
					return false;
				}
				return true;
			}
		});
		return result[0];

	}

	@Override
	public void computeStats(final FacetVisitor proc, final int[] facetNumber, final int[] descWithNoFacets,
			final int[] descNumber) {
		visitFacets(proc);
		final int facetSize = facets == null ? 0 : facets.size();
		facetNumber[0] += facetSize;
		descNumber[0]++;
		if (facetSize == 1)
			descWithNoFacets[0]++;

		visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				desc.computeStats(proc, facetNumber, descWithNoFacets, descNumber);
				return true;
			}
		});

	}

	/**
	 * Convenience method to access facets from other structures. However, this
	 * method should be (when possible) replaced by the usage of the visitor
	 * pattern through visitFacets()
	 */
	@Override
	public Facets getFacets() {
		return facets == null ? Facets.NULL : facets;
	}

}
