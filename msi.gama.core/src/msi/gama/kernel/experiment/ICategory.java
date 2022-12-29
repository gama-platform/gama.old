/*******************************************************************************************************
 *
 * ICategory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import msi.gama.runtime.IScope;

/**
 * The Interface ICategory.
 */
public interface ICategory extends IExperimentDisplayable {

	/**
	 * Checks if is expanded.
	 *
	 * @return true, if is expanded
	 */
	boolean isExpanded(IScope scope);

}
