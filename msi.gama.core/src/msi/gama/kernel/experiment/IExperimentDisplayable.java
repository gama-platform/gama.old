/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.IExperimentDisplayable.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import com.google.common.primitives.Ints;

import msi.gama.common.interfaces.INamed;
import msi.gama.runtime.IScope;

public interface IExperimentDisplayable extends INamed, Comparable<IExperimentDisplayable> {

	String DEFAULT_SIMULATION_CATEGORY = "Simulation";
	String DEFAULT_EXPERIMENT_CATEGORY = "Experiment";

	String getTitle();

	String getUnitLabel(IScope scope);

	void setUnitLabel(String label);

	int getOrder();

	@Override
	default int compareTo(final IExperimentDisplayable p) {
		return Ints.compare(getOrder(), p.getOrder());
	}

	boolean isDefinedInExperiment();

	default String getCategory() {
		return isDefinedInExperiment() ? DEFAULT_EXPERIMENT_CATEGORY : DEFAULT_SIMULATION_CATEGORY;
	}
}
