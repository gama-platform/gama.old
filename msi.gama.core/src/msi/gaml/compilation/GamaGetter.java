/*******************************************************************************************************
 *
 * GamaGetter.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import msi.gama.runtime.IScope;

/**
 * The Interface GamaGetter.
 *
 * @param <T> the generic type
 */
@FunctionalInterface
public interface GamaGetter<T> {
	
	/**
	 * Gets the.
	 *
	 * @param scope the scope
	 * @param arguments the arguments
	 * @return the t
	 */
	T get(IScope scope, Object... arguments);

}
