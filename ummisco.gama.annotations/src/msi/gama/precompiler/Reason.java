/*******************************************************************************************************
 *
 * Reason.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

/**
 * Enumerates the reasons why tests are not written for a specific GAML artefact.
 *
 * @see msi.gama.precompiler.GamlAnnotations.no_test
 * @author drogoul
 *
 */
public enum Reason {
	/**
	 * The test on this specific artefact are actually made somewhere else, usually in custom test files written by hand
	 */
	ALREADY_TESTED,
	/**
	 * This artifact cannot be tested because, either it makes no sense (e.g., the `diff` in equations) or it is
	 * impossible to do (e.g., complex geometrical operators, for instance)
	 */
	IMPOSSIBLE_TO_TEST,
	/**
	 * This artifact is deprecated
	 */
	DEPRECATED,
	/**
	 * This artifact is internal and not exposed to users
	 */
	INTERNAL,
	/**
	 * No reason provided (the default for @no_test)
	 */

	NONE
}