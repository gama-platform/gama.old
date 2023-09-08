/*******************************************************************************************************
 *
 * IScopedStepable.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

/**
 * Represent GAMA stepables that generate their own scope
 *
 * @author A. Drogoul
 *
 */
public interface IScopedStepable extends IScoped, IStepable {

	/**
	 * Step.
	 *
	 * @return true, if successful
	 */
	default boolean step() {
		return getScope().step(this).passed();
	}

	/**
	 * Inits the.
	 *
	 * @return true, if successful
	 */
	default boolean init() {
		return getScope().init(this).passed();
	}

}
