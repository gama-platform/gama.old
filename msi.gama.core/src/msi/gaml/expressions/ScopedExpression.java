/*********************************************************************************************
 *
 *
 * 'ScopedExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

/**
 * Class ScopedExpression.
 *
 * @author drogoul
 * @since 20 janv. 2014
 *
 */
public class ScopedExpression implements IExpression {

	final IExpression wrapped;
	final IScope scope;

	public static ScopedExpression with(final IScope scope, final IExpression wrapped) {
		if ( wrapped == null ) { return null; }
		return new ScopedExpression(scope, wrapped);
	}

	private ScopedExpression(final IScope scope, final IExpression wrapped) {
		this.scope = scope;
		this.wrapped = wrapped;
	}

	/**
	 * Method dispose()
	 * @see msi.gaml.descriptions.IGamlDescription#dispose()
	 */
	@Override
	public void dispose() {
		wrapped.dispose();
	}

	@Override
	public String getDefiningPlugin() {
		return wrapped.getDefiningPlugin();
	}

	/**
	 * Method getTitle()
	 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		return wrapped.getTitle();
	}

	/**
	 * Method getDocumentation()
	 * @see msi.gaml.descriptions.IGamlDescription#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return wrapped.getDocumentation();
	}

	/**
	 * Method getName()
	 * @see msi.gaml.descriptions.IGamlDescription#getName()
	 */
	@Override
	public String getName() {
		return wrapped.getName();
	}

	@Override
	public void setName(final String name) {
		// Nothing
	}

	/**
	 * Method getType()
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType getType() {
		return wrapped.getType(); // Species in two different models ?
	}

	// /**
	// * Method getContentType()
	// * @see msi.gama.common.interfaces.ITyped#getContentType()
	// */
	// @Override
	// public IType getContentType() {
	// return wrapped.getContentType();
	// }
	//
	// /**
	// * Method getKeyType()
	// * @see msi.gama.common.interfaces.ITyped#getKeyType()
	// */
	// @Override
	// public IType getKeyType() {
	// return wrapped.getKeyType();
	// }

	/**
	 * Method value()
	 * @see msi.gaml.expressions.IExpression#value(msi.gama.runtime.IScope)
	 */
	@Override
	public Object value(final IScope unused) throws GamaRuntimeException {
		return wrapped.value(this.scope);
	}

	/**
	 * Method isConst()
	 * @see msi.gaml.expressions.IExpression#isConst()
	 */
	@Override
	public boolean isConst() {
		return wrapped.isConst();
	}

	/**
	 * Method toGaml()
	 * @see msi.gaml.expressions.IExpression#toGaml()
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		return wrapped.serialize(includingBuiltIn);
	}

	/**
	 * Method literalValue()
	 * @see msi.gaml.expressions.IExpression#literalValue()
	 */
	@Override
	public String literalValue() {
		return wrapped.literalValue();
	}

	/**
	 * Method resolveAgainst()
	 * @see msi.gaml.expressions.IExpression#resolveAgainst(msi.gama.runtime.IScope)
	 */
	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return wrapped.resolveAgainst(this.scope);
	}

	/**
	 * Method shouldBeParenthesized()
	 * @see msi.gaml.expressions.IExpression#shouldBeParenthesized()
	 */
	@Override
	public boolean shouldBeParenthesized() {
		return wrapped.shouldBeParenthesized();
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		wrapped.collectMetaInformation(meta);
	}

}
