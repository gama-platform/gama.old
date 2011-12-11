/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.descriptions;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;

/**
 * Written by drogoul Modified on 16 mars 2010
 * 
 * @todo Description
 * 
 */
public class SymbolDescription /* extends Base */implements IDescription {

	protected final Facets			facets;
	protected ISyntacticElement		source;
	protected IDescription			enclosing	= null;
	protected List<IDescription>	children;

	// protected ModelDescription model;

	public SymbolDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final ISyntacticElement source)
		throws GamlException {
		initFields();
		this.facets = facets;
		facets.putAsLabel(ISymbol.KEYWORD, keyword);
		this.source = source;
		setSuperDescription(superDesc);
		copyChildren(children);
	}

	@Override
	public String getKeyword() {
		return facets.getString(ISymbol.KEYWORD);
	}

	@Override
	public String getName() {
		return facets.getString(ISymbol.NAME);
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

	protected void copyChildren(final List<IDescription> originalChildren) throws GamlException {
		children = new ArrayList();
		addChildren(originalChildren);
	}

	// To add children from outside
	@Override
	public void addChildren(final List<IDescription> originalChildren) throws GamlException {
		for ( IDescription c : originalChildren ) {
			try {
				addChild(c);
			} catch (GamlException g) {
				// add source code info for instant error in editor
				g.addSource(c.getSourceInformation());
				throw g;
			}
		}
	}

	@Override
	public IDescription addChild(final IDescription child) throws GamlException {
		IDescription cc = child.shallowCopy(this);
		cc.setSuperDescription(this);
		children.add(cc);
		return cc;
	}

	@Override
	public void setSuperDescription(final IDescription desc) throws GamlException {
		enclosing = desc;
	}

	@Override
	public ISyntacticElement getSourceInformation() {
		return source;
	}

	@Override
	public IDescription shallowCopy(final IDescription superDesc) throws GamlException {
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
	public IExpression getVarExpr(final String name, final IExpressionFactory f) {
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
		return null;
	}

}
