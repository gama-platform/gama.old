/*******************************************************************************************************
 *
 * IGamlable.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.interfaces;

/**
 * The interface IGamlable. Represents objects that can represent themselves in terms of GAML descriptions
 * (serialization).
 *
 * @author A. Drogoul
 * @since 13 dec. 2011
 *
 */
public interface IGamlable {

	/**
	 * Returns the serialization in GAML of this object, taking into account (or not) built-in structures
	 *
	 * @param includingBuiltIn
	 *            whether built-in structures should be part of the serialization in GAML (for instance, built-in
	 *            species within a model)
	 * @return a string that can be reinterpreted in GAML to reproduce the object
	 */
	default String serializeToGaml(final boolean includingBuiltIn) {
		return toString();
	}

	// /**
	// * Deserialize. Returns the Object represented by this string in GAML. A scope is necessary to know how to
	// interpret
	// * it.
	// *
	// * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	// * @param gaml
	// * the gaml
	// * @return the object
	// * @date 27 oct. 2023
	// */
	// default Object deserializeFromGaml(final IScope scope, final String gaml) {
	// return GAML.evaluateExpression(gaml, scope.getAgent());
	// }
}
