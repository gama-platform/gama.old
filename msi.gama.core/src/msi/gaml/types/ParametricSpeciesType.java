/*******************************************************************************************************
 *
 * ParametricSpeciesType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;

/**
 * The Class ParametricSpeciesType.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 21 janv. 2024
 */
public class ParametricSpeciesType extends ParametricType {

	/**
	 * Instantiates a new parametric species type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param t
	 *            the t
	 * @param kt
	 *            the kt
	 * @param ct
	 *            the ct
	 * @date 21 janv. 2024
	 */
	public ParametricSpeciesType(final GamaAgentType type) {
		super(Types.SPECIES, Types.INT, type);
	}

	@Override
	public SpeciesDescription getDenotedSpecies(final IDescription context) {
		return getContentType().getSpecies(context);
	}
	//
	// @Override
	// public SpeciesDescription getSpecies(final IDescription context) {
	// return getContentType().getSpecies(context);
	// }

	@Override
	public String getSpeciesName() { return getContentType().getSpeciesName(); }

	// @Override
	// public boolean isAgentType() {
	// // Verify this
	// return getContentType().isAgentType();
	// }

}
