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
import msi.gama.common.util.ErrorCollector;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.GamlException;
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
		final Facets facets, final List<IDescription> children, final ISyntacticElement source,
		final SymbolMetaDescription md) {
		initFields();
		this.facets = facets;
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

	@Override
	public void flagError(final GamlException e) {
		if ( getSource() == null ) { return; }
		ErrorCollector collect = getSource().getErrorCollector();
		if ( collect != null ) {
			collect.add(e);
		}
	}

	@Override
	public void flagWarning(final GamlException e) {
		e.setWarning(true);
		flagError(e);
	}

	@Override
	public String getKeyword() {
		return facets.getString(IKeyword.KEYWORD);
	}

	@Override
	public String getName() {
		return facets.getString(IKeyword.NAME);
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
		return (ModelDescription) enclosing.getModelDescription();
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
		IDescription cc = child.shallowCopy(this);
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

	@Override
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

	@Override
	public IDescription getDescriptionDeclaringAspect(final String name) {
		return hasAspect(name) ? this : enclosing == null ? null : enclosing
			.getDescriptionDeclaringAspect(name);
	}

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
	public IDescription getSpeciesContext() {
		if ( enclosing == null ) { return null; }
		return enclosing.getSpeciesContext();
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getSpeciesDescription(java.lang.String)
	 */
	@Override
	public IDescription getSpeciesDescription(final String actualSpecies) {
		IDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getSpeciesDescription(actualSpecies);
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getAction(java.lang.String)
	 */
	@Override
	public IDescription getAction(final String name) {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getWorldSpecies()
	 */
	@Override
	public IDescription getWorldSpecies() {
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
