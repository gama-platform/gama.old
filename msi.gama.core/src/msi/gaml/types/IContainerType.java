/*******************************************************************************************************
 *
 * msi.gaml.types.IContainerType.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.expressions.IExpression;

/**
 * Class IContainerType.
 *
 * @author drogoul
 * @since 28 janv. 2014
 *
 */
public interface IContainerType<T extends IContainer<?, ?>> extends IType<T> {

	@Override
	public IContainerType<T> getGamlType();

	@Override
	public IContainerType<?> typeIfCasting(final IExpression exp);

	@Override
	public T cast(IScope scope, Object obj, Object param, boolean copy);

	@Override
	public T cast(IScope scope, Object obj, Object param, IType<?> keyType, IType<?> contentType, boolean copy);

	/**
	 * Allows to build a parametric type
	 * 
	 * @param subs
	 * @return
	 */
	public IContainerType<?> of(IType<?>... subs);

}
