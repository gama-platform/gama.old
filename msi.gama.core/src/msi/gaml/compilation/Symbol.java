/*********************************************************************************************
 *
 * 'Symbol.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 13 mai 2010 A simple class to serve as the
 * root of all Gaml Symbols
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

	public Symbol(final IDescription desc) {
		description = desc;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if (description == null) {
			return "";
		}
		return description.serialize(includingBuiltIn);
	}

	@Override
	public String getKeyword() {
		if (description == null) {
			return null;
		}
		return description.getKeyword();
	}

	@Override
	public final IExpression getFacet(final String... keys) {
		if (description == null) {
			return null;
		}
		return description.getFacetExpr(keys);
	}

	public Object getFacetValue(final IScope scope, final String key) throws GamaRuntimeException {
		return getFacetValue(scope, key, null);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getFacetValue(final IScope scope, final String key, final T defaultValue)
			throws GamaRuntimeException {
		final IExpression exp = getFacet(key);
		return (T) (exp == null ? defaultValue : exp.value(scope));
	}

	public String getLiteral(final String key) {
		return getLiteral(key, null);
	}

	public String getLiteral(final String key, final String defaultValue) {
		final IExpression exp = getFacet(key);
		return exp == null ? defaultValue : exp.literalValue();
	}

	protected void setFacet(final String key, final IExpressionDescription expr) {
		if (description == null) {
			return;
		}
		description.setFacet(key, expr);
	}

	@Override
	public boolean hasFacet(final String s) {
		return description == null ? false : description.hasFacet(s);
	}

	@Override
	public void setName(final String n) {
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void dispose() {

	}

	@Override
	public String getTrace(final IScope scope) {
		return new SymbolTracer().trace(scope, this);
	}

	@Override
	public void setEnclosing(final ISymbol enclosing) {
		// Nothing to do by default
	}

}
