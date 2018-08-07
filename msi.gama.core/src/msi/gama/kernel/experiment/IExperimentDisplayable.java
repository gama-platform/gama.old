package msi.gama.kernel.experiment;

import com.google.common.primitives.Ints;

import msi.gama.common.interfaces.INamed;
import msi.gama.runtime.IScope;

public interface IExperimentDisplayable extends INamed, Comparable<IExperimentDisplayable> {

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
