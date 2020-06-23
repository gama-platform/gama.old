/*******************************************************************************************************
 *
 * msi.gaml.compilation.Symbol.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 13 mai 2010 A simple class to serve as the root of all Gaml Symbols
 *
 * @todo Description
 *
 */
public abstract class Symbol implements ISymbol {

	protected String name;
	protected final IDescription description;
	protected int order;

	@Override
	public IDescription getDescription() {
		return description;
	}

	@Override
	public URI getURI() {
		if (description == null) { return null; }
		final EObject object = description.getUnderlyingElement();
		return object == null ? null : EcoreUtil.getURI(object);
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(final int i) {
		order = i;
	}

	public Symbol(final IDescription desc) {
		description = desc;
		if (desc != null) {
			order = desc.getOrder();
		} else {
			order = SymbolDescription.ORDER++;
			// DEBUG.LOG("Order of " + desc.getName() + " = " + order);
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if (description == null) { return ""; }
		return description.serialize(includingBuiltIn);
	}

	@Override
	public String getKeyword() {
		if (description == null) { return null; }
		return description.getKeyword();
	}

	@Override
	public final IExpression getFacet(final String... keys) {
		if (description == null) { return null; }
		return description.getFacetExpr(keys);
	}

	public Object getFacetValue(final IScope scope, final String key) throws GamaRuntimeException {
		return getFacetValue(scope, key, null);
	}

	@SuppressWarnings ("unchecked")
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
		if (description == null) { return; }
		description.setFacet(key, expr);
	}

	@Override
	public boolean hasFacet(final String s) {
		return description != null && description.hasFacet(s);
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
