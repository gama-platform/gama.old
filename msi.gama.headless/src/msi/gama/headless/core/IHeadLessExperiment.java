/*********************************************************************************************
 * 
 *
 * 'IHeadLessExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.core;

import msi.gama.kernel.experiment.IExperimentSpecies;

public interface IHeadLessExperiment extends IExperimentSpecies {

	public void start(int nbStep);

	// @Override
	public void setParameterValue(String name, Object value);

	//
	// public Object getParameterWithName(String name) throws GamaRuntimeException,
	// InterruptedException;

	public void setSeed(double seed);
}
