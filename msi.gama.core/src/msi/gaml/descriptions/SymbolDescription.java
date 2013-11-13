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

import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.set.hash.THashSet;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 16 mars 2010
 * 
 * @todo Description
 * 
 */
public class SymbolDescription implements IDescription {

	protected final Facets facets;
	protected final EObject element;
	protected IDescription enclosing;
	protected String originName;
	protected final List<IDescription> children;
	protected final String keyword;

	// protected boolean isDisposed = false;

	public SymbolDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
		final EObject source, final Facets facets) {
		this.facets = facets;
		facets.putAsLabel(KEYWORD, keyword);
		this.keyword = keyword;
		element = source;
		if ( superDesc != null ) {
			originName = superDesc.getName();
		}
		setEnclosingDescription(superDesc);
		if ( getMeta().hasSequence() ) {
			this.children = new ArrayList();
			addChildren(cp.getChildren());
		} else {
			this.children = null;
		}
	}

	protected String typeToString() {
		String t = getType().toString();
		if ( getType().hasContents() ) {
			t += "&lt;" + getKeyType().toString() + ", " + getContentType().toString() + "&gt;";
		}
		return t;
	}

	@Override
	public int getKind() {
		return getMeta().getKind();
	}

	@Override
	public SymbolProto getMeta() {
		return DescriptionFactory.getProto(keyword);
	}

	private void flagError(final String s, final String code, final boolean warning, final boolean info,
		final EObject source, final String ... data) throws GamaRuntimeException {

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
			GuiUtils.debug("COMPILATION ERROR in " + this.toString() + ": " + s + "; source: " + resource);
		}
		// throws a runtime exception if there is no way to signal the error in the source
		// (i.e. we are probably in a runtime scenario)
		if ( e == null ) { throw warning ? GamaRuntimeException.warning(s) : GamaRuntimeException.error(s); }
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
		flagError(message, code, true, true, getUnderlyingElement(null), (String[]) null);
	}

	@Override
	public void info(final String s, final String code, final EObject facet, final String ... data) {
		flagError(s, code, true, true, facet, data);
	}

	@Override
	public void info(final String s, final String code, final String facet, final String ... data) {
		flagError(s, code, true, true, this.getUnderlyingElement(facet), data);
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
	public void dispose() {
		if ( isBuiltIn() ) { return; }
		facets.dispose();
		if ( children != null ) {
			for ( IDescription c : children ) {
				c.dispose();
			}
			children.clear();
		}
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
		children.add(child);
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
		if ( f != null && f.getTarget() != null && f.getTarget().eContainer() != null ) { return f.getTarget(); }
		return element;

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
	public List<IDescription> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

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
	public IExpression addTemp(final String name, final IType type, final IType contentType, final IType keyType) {
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
		return Types.get(IType.NONE);
	}

	@Override
	public IType getContentType() {
		return Types.get(IType.NONE);
	}

	@Override
	public IType getKeyType() {
		return Types.get(IType.NONE);
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
	public boolean hasErrors() {
		ErrorCollector c = getErrorCollector();
		if ( c == null ) { return false; }
		return c.hasErrors();
	}

	@Override
	public List<GamlCompilationError> getErrors() {
		ErrorCollector c = getErrorCollector();
		if ( c == null ) { return Collections.EMPTY_LIST; }
		return c.get();
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
	public final IDescription validate() {
		if ( isBuiltIn() ) {
			// We simply make sure that the facets are correctly compiled
			validateFacets();
			return this;
		}
		final IDescription sd = getEnclosingDescription();
		final SymbolProto proto = getMeta();
		if ( sd != null ) {
			// We first verify that the description is at the right place
			if ( !proto.contextKinds[sd.getKind()] && !proto.contextKeywords.contains(sd.getKeyword()) ) {
				error(keyword + " cannot be defined in " + sd.getKeyword(), IGamlIssue.WRONG_CONTEXT, getName());
				return this;
			}
			// If it is supposed to be unique, we verify this
			if ( proto.isUniqueInContext ) {
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
		validateFacets();

		if ( proto.hasSequence && !PRIMITIVE.equals(keyword) ) {
			if ( proto.isRemoteContext ) {
				copyTempsAbove();
			}
			validateChildren();
		}

		// If a custom validator has been defined, run it
		if ( proto.validator != null ) {
			proto.validator.validate(this);
		}

		// getMeta().validate(this);
		return this;
	}

	private final boolean validateFacets() {

		// final Facets facets = getFacets();
		// Special case for "do", which can accept (at parsing time) any facet
		final boolean isDo = keyword.equals(DO);
		final boolean isBuiltIn = isBuiltIn();
		final SymbolProto proto = getMeta();
		final Set<String> mandatories = new THashSet(proto.mandatoryFacets);
		boolean ok = facets.forEachEntry(new TObjectObjectProcedure<String, IExpressionDescription>() {

			@Override
			public boolean execute(final String facet, final IExpressionDescription expr) {
				mandatories.remove(facet);
				FacetProto fp = proto.possibleFacets.get(facet);
				if ( fp == null ) {
					if ( !isDo ) {
						error("Unknown facet " + facet, IGamlIssue.UNKNOWN_FACET, facet);
						return false;
					}
				} else if ( fp.values.size() > 0 ) {
					final String val = expr.getExpression().literalValue();
					// We have a multi-valued facet
					if ( !fp.values.contains(val) ) {
						error("Facet '" + facet + "' is expecting a value among " + fp.values + " instead of " + val,
							facet);
						return false;
					}
				} else if ( fp.isType ) {
					final String val = expr.getExpression().literalValue();
					// The facet is supposed to be a type (IType.TYPE_ID)
					final IType type = getTypeNamed(val);
					if ( type == Types.NO_TYPE && !UNKNOWN.equals(val) && !IKeyword.SIGNAL.equals(val) ) {
						error("Facet '" + facet + "' is expecting a type name. " + val + " is not a type name",
							IGamlIssue.NOT_A_TYPE, facet, val);
						return false;
					}
				} else {
					IExpression exp;
					if ( fp.types[0] == IType.NEW_TEMP_ID ) {
						exp = createVarWithTypes(facet);
						expr.setExpression(exp);
					} else if ( !fp.isLabel && !facet.equals(WITH) ) {
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
							// return false;
						}
					}
				}
				return true;
			}
		});
		if ( ok && !mandatories.isEmpty() ) {
			error("Missing facets " + mandatories, IGamlIssue.MISSING_FACET);
			return false;
		}
		return ok;

	}

	// Nothing to do here
	protected IExpression createVarWithTypes(final String tag) {
		return null;
	}

	protected void validateChildren() {
		if ( children != null ) {
			for ( final IDescription child : children ) {
				child.validate();
			}
		}
	}

	@Override
	public final ISymbol compile() {
		final SymbolProto proto = getMeta();
		validate();
		ISymbol cs = proto.constructor.create(this);
		if ( cs == null ) { return null; }
		if ( proto.hasArgs ) {
			((IStatement.WithArgs) cs).setFormalArgs(((StatementDescription) this).validateArgs());
		}
		if ( proto.hasSequence && !keyword.equals(PRIMITIVE) ) {
			if ( proto.isRemoteContext ) {
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
		final List<ISymbol> lce = new ArrayList();
		if ( children != null ) {
			for ( final IDescription sd : children ) {
				final ISymbol s = sd.compile();
				if ( s != null ) {
					lce.add(s);
				}
			}
		}
		return lce;

	}

}
