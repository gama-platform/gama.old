/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.IExperimentDisplayable.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
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
