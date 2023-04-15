/*******************************************************************************************************
 *
 * ISafeConsumer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

/**
 * The Interface ISafeConsumer.
 *
 * @param <T>
 *            the generic type
 */
public interface ISafeConsumer<T> {

	/**
	 * Accept.
	 *
	 * @param t
	 *            the t
	 * @throws Throwable
	 *             the throwable
	 */
	void accept(T t) throws Throwable;

}
