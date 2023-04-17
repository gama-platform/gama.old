/*******************************************************************************************************
 *
 * IRichExperiment.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.core;

import msi.gama.headless.job.ExperimentJob.OutputType;
import msi.gama.headless.job.ListenedVariable;

/**
 * The Interface IRichExperiment.
 */
public interface IRichExperiment extends IExperiment{
	
	/**
	 * Gets the rich output.
	 *
	 * @param v the v
	 * @return the rich output
	 */
	public RichOutput getRichOutput(final ListenedVariable v);
	
	/**
	 * Gets the type of.
	 *
	 * @param name the name
	 * @return the type of
	 */
	public OutputType getTypeOf(final String name);
}
