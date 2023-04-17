/*******************************************************************************************************
 *
 * IGamlBuilderListener.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.validation;

import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;

/**
 * The class IGamlBuilder.
 *
 * @author drogoul
 * @since 2 mars 2012
 *
 */
public interface IGamlBuilderListener {

	/**
	 * Validation ended.
	 *
	 * @param model
	 *
	 * @param experiments
	 *            the experiments
	 * @param status
	 *            the status
	 */
	void validationEnded(ModelDescription model, final Iterable<? extends IDescription> experiments,
			final ValidationContext status);
}
