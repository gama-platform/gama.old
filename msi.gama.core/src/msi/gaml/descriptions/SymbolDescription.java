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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 16 mars 2010
 * 
 * @todo Description
 * 
 */
public class SymbolDescription /* extends Base */implements IDescription {

	protected final Facets facets;
	private ISyntacticElement source;
	protected IDescription enclosing = null;
	protected List<IDescription> children;
	protected SymbolMetaDescription meta;

	// protected ModelDescription model;

	public SymbolDescription(final String keyword, final IDescription superDesc,
		final List<IDescription> children, final ISyntacticElement source,
		final SymbolMetaDescription md) {
		initFields();
		this.facets = source.getFacets();
		facets.putAsLabel(IKeyword.KEYWORD, keyword);
		setSource(source);
		meta = md;
		setSuperDescription(superDesc);
		copyChildren(children);
	}

	@Override
	public SymbolMetaDescription getMeta() {
		return meta;
	}

	private void flagError(final String s, final boolean warning, final Object facet)
		throws GamaRuntimeException {
		// GuiUtils.debug((warning ? "Warning" : "Error") + " flagged in " + this + " : " + s +
		// " for facet " + facet);
		ISyntacticElement e =
			facet instanceof ISyntacticElement ? (ISyntacticElement) facet : getSource();
		IDescription desc = this;
		while (e == null && desc != null) {
			desc = desc.getSuperDescription();
			if ( desc != null ) {
				e = desc.getSourceInformation();
			}
		}
		// throws a runtime exception if there is no way to signal the error in the source
		// (i.e. we are probably in a runtime scenario)
		if ( e == null /* || e.getErrorCollector() == null */) { throw new GamaRuntimeException(s,
			warning); }
		// ErrorCollector collect = e.getErrorCollector();
		GamlCompilationError ge = new GamlCompilationError(s, e, warning);
		ge.setObjectOfInterest(facet);
		// collect.add(ge);
	}

	@Override
	public void flagError(final String s) {
		flagError(s, null);
	}

	@Override
	public void flagError(final String s, final Object facet) {
		flagError(s, false, facet);
	}

	@Override
	public void flagWarning(final String s) {
		flagError(s, true, null);
	}

	@Override
	public void flagWarning(final String s, final Object facet) {
		flagError(s, true, facet);
	}

	@Override
	public String getKeyword() {
		return facets.getLabel(IKeyword.KEYWORD);
	}

	@Override
	public String getName() {
		return facets.getLabel(IKeyword.NAME);
	}

	protected void initFields() {};

	@Override
	public void dispose() {
		facets.dispose();
		for ( IDescription c : children ) {
			c.dispose();
		}
		children.clear();
		// model = null;
	}

	@Override
	public ModelDescription getModelDescription() {
		if ( enclosing == null ) { return null; }
		return enclosing.getModelDescription();
	}

	protected void copyChildren(final List<IDescription> originalChildren) {
		children = new ArrayList();
		addChildren(originalChildren);
	}

	// To add children from outside
	@Override
	public void addChildren(final List<IDescription> originalChildren) {
		for ( IDescription c : originalChildren ) {
			if ( c != null ) {
				addChild(c);
			}
		}
	}

	@Override
	public IDescription addChild(final IDescription child) {
		// GuiUtils.debug("Adding child " + child + " to " + this);
		IDescription cc = ((SymbolDescription) child).shallowCopy(this);
		cc.setSuperDescription(this);
		children.add(cc);
		return cc;
	}

	@Override
	public void setSuperDescription(final IDescription desc) {
		enclosing = desc;
	}

	@Override
	public ISyntacticElement getSourceInformation() {
		return getSource();
	}

	public IDescription shallowCopy(final IDescription superDesc) {
		return this;
	}

	@Override
	public Facets getFacets() {
		return facets;
	}

	@Override
	public List<IDescription> getChildren() {
		return children;
	}

	@Override
	public IDescription getSuperDescription() {
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
		return hasVar(name) ? this : enclosing == null ? null : enclosing
			.getDescriptionDeclaringVar(name);
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name) {
		return hasAction(name) ? this : enclosing == null ? null : enclosing
			.getDescriptionDeclaringAction(name);
	}

	// @Override
	// public IDescription getDescriptionDeclaringAspect(final String name) {
	// return hasAspect(name) ? this : enclosing == null ? null : enclosing
	// .getDescriptionDeclaringAspect(name);
	// }

	@Override
	public IExpression getVarExpr(final String name, final IExpressionFactory factory) {
		return null;
	}

	@Override
	public IExpression addTemp(final String name, final IType type, final IType contentType,
		final IExpressionFactory f) {
		return null;
	}

	@Override
	public void copyTempsAbove() {
		// Nothing to do
	}

	@Override
	public IType getTypeOf(final String s) {
		ModelDescription m = getModelDescription();
		if ( m == null ) { return Types.get(s); }
		return m.getTypeOf(s);
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
	public SpeciesDescription getSpeciesContext() {
		if ( enclosing == null ) { return null; }
		return enclosing.getSpeciesContext();
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getSpeciesDescription(java.lang.String)
	 */
	@Override
	public SpeciesDescription getSpeciesDescription(final String actualSpecies) {
		IDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getSpeciesDescription(actualSpecies);
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getAction(java.lang.String)
	 */
	@Override
	public CommandDescription getAction(final String name) {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getWorldSpecies()
	 */
	@Override
	public SpeciesDescription getWorldSpecies() {
		IDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getWorldSpecies();
	}

	protected ISyntacticElement getSource() {
		return source;
	}

	protected void setSource(final ISyntacticElement source) {
		this.source = source;
	}

}
