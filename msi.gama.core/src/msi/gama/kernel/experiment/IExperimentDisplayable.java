package msi.gama.kernel.experiment;

import com.google.common.primitives.Ints;

import msi.gama.runtime.IScope;

public interface IExperimentDisplayable extends Comparable<IExperimentDisplayable> {

	public abstract String getName();

	public abstract String getTitle();

	public abstract String getCategory();

	public abstract String getUnitLabel(IScope scope);

	public void setUnitLabel(String label);

	public int getOrder();

	@Override
	public default int compareTo(final IExperimentDisplayable p) {
		return Ints.compare(getOrder(), p.getOrder());
	}

}
