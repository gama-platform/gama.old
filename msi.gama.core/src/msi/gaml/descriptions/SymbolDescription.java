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
import java.util.*;
import org.eclipse.emf.ecore.EObject;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 16 mars 2010
 *
 * @todo Description
 *
 */
public abstract class SymbolDescription implements IDescription {

	protected static List<String> typeProviderFacets =
		Arrays.asList(VALUE, TYPE, AS, SPECIES, OF, OVER, FROM, INDEX, FUNCTION, UPDATE, INIT, DEFAULT);

	protected final Facets facets;
	protected final EObject element;
	protected IDescription enclosing;
	protected String originName;
	protected final String keyword;
	protected boolean validated = false;

	// protected boolean isDisposed = false;

	public SymbolDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
		final EObject source, final Facets facets) {
		this.facets = facets;
		facets.putAsLabel(KEYWORD, keyword);
		this.keyword = keyword;
		element = source;
		if ( superDesc != null ) {
			// setOriginName(superDesc.getName());
			originName = superDesc.getName();
		}
		setEnclosingDescription(superDesc);
		if ( getMeta().hasSequence() ) {
			// this.children = new ArrayList();
			addChildren(cp.getChildren());
		} else {
			// this.children = null;
		}
	}

	public final SymbolSerializer getSerializer() {
		SymbolProto p = getMeta();
		SymbolSerializer d = p.getSerializer();
		if ( d == null ) {
			d = createSerializer();
			p.setSerializer(d);
		}
		return d;
	}

	/**
	 * @return
	 */
	protected SymbolSerializer createSerializer() {
		return new SymbolSerializer();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getSerializer().serialize(this, includingBuiltIn);
	}

	@Override
	public void collectPlugins(final Set<String> plugins) {
		getSerializer().collectPlugins(this, plugins);
	}

	@Override
	public boolean isDocumenting() {
		if ( enclosing == null ) { return false; }
		return ((SymbolDescription) enclosing).isDocumenting();
	}

	@Override
	public int getKind() {
		return getMeta().getKind();
	}

	protected void compileTypeProviderFacets() {
		for ( String s : typeProviderFacets ) {
			IExpressionDescription expr = facets.get(s);
			if ( expr != null ) {
				expr.compile(this);
			}
		}
	}

	@Override
	public SymbolProto getMeta() {
		return DescriptionFactory.getProto(keyword, getModelDescription());
	}

	private void flagError(final String s, final String code, final boolean warning, final boolean info,
		final EObject source, final String ... data) throws GamaRuntimeException {

		if ( warning && !info && !GamaPreferences.WARNINGS_ENABLED.getValue() ) { return; }
		if ( info && !GamaPreferences.INFO_ENABLED.getValue() ) { return; }

		IDescription desc = this;
		EObject e = source;
		if ( e == null ) {
			e = getUnderlyingElement(null);
		}
		while (e == null && desc != null) {
			desc = desc.getEnclosingDescription();
			if ( desc != null ) {
				e = desc.getUnderlyingElement(null);
			}
		}
		if ( !warning && !info ) {
			String resource = e == null ? "(no file)" : e.eResource().getURI().lastSegment();
			System.err.println("COMPILATION ERROR in " + this.toString() + ": " + s + "; source: " + resource);
		}
		// throws a runtime exception if there is no way to signal the error in the source
		// (i.e. we are probably in a runtime scenario)
		if ( e == null ||
			e.eResource().getURI().path().contains(IExpressionCompiler.SYNTHETIC_RESOURCES_PREFIX) ) { throw warning
				? GamaRuntimeException.warning(s) : GamaRuntimeException.error(s); }
		ErrorCollector c = getErrorCollector();
		if ( c == null ) {
			System.out.println((warning ? "Warning" : "Error") + ": " + s);
			return;
		}
		c.add(new GamlCompilationError(s, code, e, warning, info, data));
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
	public void error(final String s, final String code, final EObject facet, final String ... data) {
		flagError(s, code, false, false, facet, data);
	}

	@Override
	public void error(final String s, final String code, final String facet, final String ... data) {
		flagError(s, code, false, false, this.getUnderlyingElement(facet), data);
	}

	@Override
	public void info(final String message, final String code) {
		flagError(message, code, false, true, getUnderlyingElement(null), (String[]) null);
	}

	@Override
	public void info(final String s, final String code, final EObject facet, final String ... data) {
		flagError(s, code, false, true, facet, data);
	}

	@Override
	public void info(final String s, final String code, final String facet, final String ... data) {
		flagError(s, code, false, true, this.getUnderlyingElement(facet), data);
	}

	@Override
	public void warning(final String message, final String code) {
		flagError(message, code, true, false, null, (String[]) null);
	}

	@Override
	public void warning(final String s, final String code, final EObject object, final String ... data) {
		flagError(s, code, true, false, object, data);
	}

	@Override
	public void warning(final String s, final String code, final String facet, final String ... data) {
		flagError(s, code, true, false, this.getUnderlyingElement(facet), data);
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

	@Override
	public String getName() {
		return facets.getLabel(NAME);
	}

	@Override
	public void setName(final String name) {
		// / Nothing
	}

	@Override
	public void dispose() {
		if ( isBuiltIn() ) { return; }
		facets.dispose();
		// if ( children != null ) {
		// for ( IDescription c : children ) {
		// c.dispose();
		// }
		// children.clear();
		// }
		enclosing = null;
	}

	@Override
	public ModelDescription getModelDescription() {
		if ( enclosing == null ) { return null; }
		ModelDescription result = enclosing.getModelDescription();
		if ( result != null && result.isBuiltIn() && !this.isBuiltIn() ) { return null; }
		return result;
	}

	// To add children from outside
	@Override
	public final void addChildren(final List<IDescription> originalChildren) {
		for ( IDescription c : originalChildren ) {
			addChild(c);
		}
	}

	@Override
	public IDescription addChild(final IDescription child) {
		if ( child == null ) { return null; }
		child.setEnclosingDescription(this);
		// children.add(child);
		return child;
	}

	@Override
	public void setEnclosingDescription(final IDescription desc) {
		enclosing = desc;
	}

	@Override
	public EObject getUnderlyingElement(final Object facet) {
		if ( facet == null ) { return element; }
		if ( facet instanceof EObject ) { return (EObject) facet; }

		IExpressionDescription f =
			facet instanceof IExpressionDescription ? (IExpressionDescription) facet : facets.get(facet);
		if ( f == null ) { return element; }
		EObject target = f.getTarget();
		if ( target == null ) { return element; }
		return getExpressionFactory().getFacetExpression(this, target);
		// if ( target.eContainer() == null ) { return target; }
		// return target.eContainer(); // Should be a Facet

		// if ( f != null && f.getTarget() != null && f.getTarget().eContainer() != null ) { return f.getTarget(); }
		// return element;

	}

	@Override
	public IDescription copy(final IDescription into) {
		return this;
	}

	@Override
	public Facets getFacets() {
		return facets;
	}

	@Override
	public abstract List<IDescription> getChildren();

	// {
	// return children == null ? Collections.EMPTY_LIST : children;
	// }

	@Override
	public IDescription getEnclosingDescription() {
		return enclosing;
	}

	protected boolean hasVar(final String name) {
		return false;
	}

	protected boolean hasAction(final String name) {
		return false;
	}

	protected boolean hasAspect(final String name) {
		return false;
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		return hasVar(name) ? this : enclosing == null ? null : enclosing.getDescriptionDeclaringVar(name);
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name) {
		return hasAction(name) ? this : enclosing == null ? null : enclosing.getDescriptionDeclaringAction(name);
	}

	@Override
	public IExpression getVarExpr(final String name) {
		return null;
	}

	@Override
	public IExpression addTemp(final IDescription declaration, final String name, final IType type) {
		return null;
	}

	@Override
	public void copyTempsAbove() {
		// Nothing to do
	}

	@Override
	public IType getTypeNamed(final String s) {
		ModelDescription m = getModelDescription();
		if ( m == null ) { return Types.get(s); }
		return m.getTypeNamed(s);
	}

	@Override
	public IType getType() {
		IType tt = facets.getTypeDenotedBy(TYPE, this);
		IType kt = facets.getTypeDenotedBy(INDEX, this, tt.getKeyType());
		IType ct = facets.getTypeDenotedBy(OF, this, tt.getContentType());
		boolean isContainerWithNoContentsType = tt.isContainer() && ct == Types.NO_TYPE;
		boolean isContainerWithNoKeyType = tt.isContainer() && kt == Types.NO_TYPE;
		boolean isSpeciesWithAgentType = tt.id() == IType.SPECIES && ct.id() == IType.AGENT;
		if ( isContainerWithNoContentsType || isContainerWithNoKeyType || isSpeciesWithAgentType ) {
			compileTypeProviderFacets();
			IExpression expr = facets.getExpr(INIT, VALUE, UPDATE, FUNCTION, DEFAULT);
			if ( expr != null ) {
				IType exprType = expr.getType();
				if ( isContainerWithNoKeyType ) {
					kt = exprType.getKeyType();
				}
				if ( isContainerWithNoContentsType || isSpeciesWithAgentType ) {
					ct = exprType.getContentType();
				}
			}
		}
		return GamaType.from(tt, kt, ct);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		if ( enclosing == null ) { return null; }
		return enclosing.getSpeciesContext();
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getSpeciesDescription(java.lang.String)
	 */
	@Override
	public SpeciesDescription getSpeciesDescription(final String actualSpecies) {
		ModelDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getSpeciesDescription(actualSpecies);
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getAction(java.lang.String)
	 */
	@Override
	public StatementDescription getAction(final String name) {
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
	public ErrorCollector getErrorCollector() {
		ModelDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getErrorCollector();
	}

	@Override
	public boolean isBuiltIn() {
		return element == null;
	}

	@Override
	public String getOriginName() {
		return originName;
	}

	@Override
	public void setOriginName(final String name) {
		if ( originName == null ) {
			originName = name;
		}
	}

	@Override
	public void resetOriginName() {
		originName = null;
	}

	@Override
	public IDescription validate() {
		if ( validated ) { return this; }
		validated = true;
		if ( isBuiltIn() ) {
			// We simply make sure that the facets are correctly compiled
			validateFacets(false);
			return this;
		}
		final IDescription sd = getEnclosingDescription();
		final SymbolProto proto = getMeta();
		if ( sd != null ) {
			// We first verify that the description is at the right place
			if ( !canBeDefinedIn(sd) ) {
				error(keyword + " cannot be defined in " + sd.getKeyword(), IGamlIssue.WRONG_CONTEXT);
				return this;
			}
			// If it is supposed to be unique, we verify this
			if ( proto.isUniqueInContext() ) {
				for ( final IDescription child : sd.getChildren() ) {
					if ( child.getKeyword().equals(keyword) && child != this ) {
						final String error =
							keyword + " is defined twice. Only one definition is allowed in " + sd.getKeyword();
						child.error(error, IGamlIssue.DUPLICATE_KEYWORD, child.getUnderlyingElement(null), keyword);
						error(error, IGamlIssue.DUPLICATE_KEYWORD, getUnderlyingElement(null), keyword);
						return this;
					}
				}
			}
		}
		// We then validate its facets
		validateFacets(true);

		if ( proto.hasSequence() && !PRIMITIVE.equals(keyword) ) {
			if ( proto.isRemoteContext() ) {
				copyTempsAbove();
			}
			validateChildren();
		}

		if ( proto.getDeprecated() != null ) {
			warning("'" + getKeyword() + "' is deprecated. " + proto.getDeprecated(), IGamlIssue.DEPRECATED);
		}

		// If a custom validator has been defined, run it
		if ( proto.getValidator() != null ) {
			proto.getValidator().validate(this);
		}

		// getMeta().validate(this);
		return this;
	}

	protected boolean canBeDefinedIn(final IDescription sd) {
		return getMeta().canBeDefinedIn(sd);
	}

	private final boolean validateFacets(final boolean document) {

		// final Facets facets = getFacets();
		// Special case for "do", which can accept (at parsing time) any facet
		final boolean isDo = keyword.equals(DO);
		final boolean isBuiltIn = isBuiltIn();
		final SymbolProto proto = getMeta();
		Set<String> missingFacets = proto.getMissingMandatoryFacets(facets);
		if ( missingFacets != null ) {
			error("Missing facets " + missingFacets, IGamlIssue.MISSING_FACET);
			return false;
		}
		// final Set<String> mandatories = new THashSet(proto.mandatoryFacets);
		boolean ok = facets.forEachEntry(new TObjectObjectProcedure<String, IExpressionDescription>() {

			@Override
			public boolean execute(final String facet, final IExpressionDescription expr) {
				// mandatories.remove(facet);
				FacetProto fp = proto.getFacet(facet);
				if ( fp == null ) {
					if ( !isDo ) {
						error("Unknown facet " + facet, IGamlIssue.UNKNOWN_FACET, facet);
						return false;
					}
					return true;
				} else if ( fp.deprecated != null ) {
					warning("Facet '" + facet + "' is deprecated: " + fp.deprecated, IGamlIssue.DEPRECATED, facet);
				}
				if ( fp.values.size() > 0 ) {
					final String val = expr.getExpression().literalValue();
					// We have a multi-valued facet
					if ( !fp.values.contains(val) ) {
						error("Facet '" + facet + "' is expecting a value among " + fp.values + " instead of " + val,
							facet);
						return false;
					}
				} else {
					IExpression exp;
					if ( fp.types[0] == IType.NEW_TEMP_ID ) {
						exp = createVarWithTypes(facet);
						expr.setExpression(exp);
					} else if ( !fp.isLabel() && !facet.equals(WITH) && !facet.equals(DEPENDS_ON) ) {
						exp = expr.compile(SymbolDescription.this);
					} else {
						exp = expr.getExpression();
					}

					if ( exp != null && !isBuiltIn ) {

						// Some expresssions might not be compiled (like "depends_on", for instance)
						boolean compatible = false;
						final IType actualType = exp.getType();
						TypesManager tm = getModelDescription().getTypesManager();
						for ( final int type : fp.types ) {
							compatible = compatible || actualType.isTranslatableInto(tm.get(type));
							if ( compatible ) {
								break;
							}
						}
						if ( !compatible ) {
							final String[] strings = new String[fp.types.length];
							for ( int i = 0; i < fp.types.length; i++ ) {
								strings[i] = tm.get(fp.types[i]).toString();
							}

							warning("Facet '" + facet + "' is expecting " + Arrays.toString(strings) + " instead of " +
								actualType, IGamlIssue.SHOULD_CAST, facet, tm.get(fp.types[0]).toString());
						}
					}
				}
				return true;
			}
		});
		// if ( ok && !mandatories.isEmpty() ) {
		// error("Missing facets " + mandatories, IGamlIssue.MISSING_FACET);
		// return false;
		// }
		return ok;

	}

	// Nothing to do here
	protected IExpression createVarWithTypes(final String tag) {
		return null;
	}

	protected void validateChildren() {
		for ( final IDescription child : getChildren() ) {
			child.validate();
		}
	}

	@Override
	public final ISymbol compile() {
		final SymbolProto proto = getMeta();
		validate();
		ISymbol cs = proto.create(this);
		if ( cs == null ) { return null; }
		if ( proto.isHasArgs() ) {
			((IStatement.WithArgs) cs).setFormalArgs(((StatementDescription) this).validateArgs());
		}
		if ( proto.hasSequence() && !keyword.equals(PRIMITIVE) ) {
			if ( proto.isRemoteContext() ) {
				copyTempsAbove();
			}
			cs.setChildren(compileChildren());
		}
		return cs;

	}

	/**
	 * Method compileChildren()
	 * @see msi.gaml.descriptions.IDescription#compileChildren()
	 */
	protected List<? extends ISymbol> compileChildren() {
		List<IDescription> children = getChildren();
		if ( children.isEmpty() ) { return Collections.EMPTY_LIST; }
		final List<ISymbol> lce = new ArrayList();
		for ( final IDescription sd : children ) {
			final ISymbol s = sd.compile();
			if ( s != null ) {
				lce.add(s);
			}
		}
		return lce;

	}

	@Override
	public Iterable<IDescription> getChildrenWithKeyword(final String keyword) {
		return Iterables.filter(getChildren(), new Predicate<IDescription>() {

			@Override
			public boolean apply(final IDescription input) {
				return input.getKeyword().equals(keyword);
			}
		});
	}

	@Override
	public IDescription getChildWithKeyword(final String keyword) {
		return Iterables.find(getChildren(), new Predicate<IDescription>() {

			@Override
			public boolean apply(final IDescription input) {
				return input.getKeyword().equals(keyword);
			}
		});
	}

}
