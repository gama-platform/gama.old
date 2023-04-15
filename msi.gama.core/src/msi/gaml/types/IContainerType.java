/*******************************************************************************************************
 *
 * IContainerType.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/**
	 * Gets the gaml type.
	 *
	 * @return the gaml type
	 */
	@Override
	IContainerType<T> getGamlType();

	/**
	 * Type if casting.
	 *
	 * @param exp the exp
	 * @return the i container type
	 */
	@Override
	IContainerType<?> typeIfCasting(final IExpression exp);

	/**
	 * Cast.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param param the param
	 * @param copy the copy
	 * @return the t
	 */
	@Override
	T cast(IScope scope, Object obj, Object param, boolean copy);

	/**
	 * Cast.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param param the param
	 * @param keyType the key type
	 * @param contentType the content type
	 * @param copy the copy
	 * @return the t
	 */
	@Override
	T cast(IScope scope, Object obj, Object param, IType<?> keyType, IType<?> contentType, boolean copy);

	/**
	 * Allows to build a parametric type
	 *
	 * @param subs
	 * @return
	 */
	IContainerType<?> of(IType<?> sub1);

	/**
	 * Of.
	 *
	 * @param sub1 the sub 1
	 * @param sub2 the sub 2
	 * @return the i container type
	 */
	IContainerType<?> of(IType<?> sub1, IType<?> sub2);

}