/*******************************************************************************************************
 *
 * IMoleExperiment.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.openmole;

import msi.gama.headless.core.IExperiment;

/**
 * The Interface IMoleExperiment.
 */
public interface IMoleExperiment extends IExperiment
{
    
    /**
     * Play.
     *
     * @param finalStep the final step
     */
    //keep to ensure compatibility with openMole	
	void play(int finalStep);
	
	/**
	 * Play.
	 *
	 * @param exp the exp
	 * @param finalStep the final step
	 */
	void play(String exp, int finalStep);
}