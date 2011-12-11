/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gama.kernel;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.*;

/**
 * Written by drogoul Modified on 13 mai 2010 A simple class to serve as the root of all Gaml
 * Symbols
 * 
 * @todo Description
 * 
 */
public abstract class Symbol implements ISymbol {

	protected String name;
	protected final IDescription description;

	@Override
	public IDescription getDescription() {
		return description;
	}

	public Symbol(final IDescription description) {
		this.description = description;
		initFields();
	}

	protected void initFields() {}

	@Override
	public IExpression getFacet(final String key) {
		return description == null ? null : description.getFacets().getExpr(key);
	}

	public IExpression getFacet(final String key, final IExpression ifAbsent) {
		return description == null ? ifAbsent : description.getFacets().getExpr(key, ifAbsent);
	}

	public Object getFacetValue(final IScope scope, final String key) throws GamaRuntimeException {
		return getFacetValue(scope, key, null);
	}

	public Object getFacetValue(final IScope scope, final String key, final Object defaultValue)
		throws GamaRuntimeException {
		IExpression exp = getFacet(key);
		return exp == null ? defaultValue : exp.value(scope);
	}

	public String getLiteral(final String key) {
		return getLiteral(key, null);
	}

	public String getLiteral(final String key, final String defaultValue) {
		IExpression exp = getFacet(key);
		return exp == null ? defaultValue : exp.literalValue();
	}

	protected void setFacet(final String key, final IExpression expr) {
		if ( description == null ) { return; }
		description.getFacets().put(key, expr);
	}

	@Override
	public boolean hasFacet(final String s) {
		return getFacet(s) != null;
	}

	@Override
	public abstract void setChildren(final List<? extends ISymbol> commands) throws GamlException;

	@Override
	public void setName(final String n) {
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void dispose() {}

}
