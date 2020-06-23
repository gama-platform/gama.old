/*********************************************************************************************
 * 
 *
 * 'IMoleExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.openmole;

import msi.gama.headless.core.IExperiment;

public interface IMoleExperiment extends IExperiment
{
    //keep to ensure compatibility with openMole	
	void play(int finalStep);
	void play(String exp, int finalStep);
}