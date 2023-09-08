/*******************************************************************************************************
 *
 * IExperimentDisplayable.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import com.google.common.primitives.Ints;

import msi.gama.common.interfaces.IColored;
import msi.gama.common.interfaces.INamed;
import msi.gama.runtime.IScope;

/**
 * The Interface IExperimentDisplayable.
 */
public interface IExperimentDisplayable extends INamed, IColored, Comparable<IExperimentDisplayable> {

	/** The default simulation category. */
	String DEFAULT_SIMULATION_CATEGORY = "Simulation";

	/** The default experiment category. */
	String DEFAULT_EXPERIMENT_CATEGORY = "Experiment";

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	String getTitle();

	/**
	 * Gets the unit label.
	 *
	 * @param scope
	 *            the scope
	 * @return the unit label
	 */
	String getUnitLabel(IScope scope);

	/**
	 * Sets the unit label.
	 *
	 * @param label
	 *            the new unit label
	 */
	default void setUnitLabel(final String label) {}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	int getOrder();

	/**
	 * Compare to.
	 *
	 * @param p
	 *            the p
	 * @return the int
	 */
	@Override
	default int compareTo(final IExperimentDisplayable p) {
		return Ints.compare(getOrder(), p.getOrder());
	}

	/**
	 * Checks if is defined in experiment.
	 *
	 * @return true, if is defined in experiment
	 */
	boolean isDefinedInExperiment();

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	default String getCategory() {
		return isDefinedInExperiment() ? DEFAULT_EXPERIMENT_CATEGORY : DEFAULT_SIMULATION_CATEGORY;
	}
}
