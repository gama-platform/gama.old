/*******************************************************************************************************
 *
 * MathConstantSupplier.java, in ummisco.gaml.extensions.maths, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode;

import msi.gaml.constants.IConstantAcceptor;
import msi.gaml.constants.IConstantsSupplier;

/**
 * The Class MathConstantSupplier.
 */
public class MathConstantSupplier implements IConstantsSupplier {

	@Override
	public void supplyConstantsTo(final IConstantAcceptor acceptor) {
		browse(MathConstants.class, acceptor);
	}

}
