/*******************************************************************************************************
 *
 * Writer.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.xml;

import msi.gama.headless.core.*;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.ListenedVariable;

/**
 * The Interface Writer.
 */
public interface Writer {

	/**
	 * Write simulation header.
	 *
	 * @param s the s
	 */
	public void writeSimulationHeader(ExperimentJob s);

	/**
	 * Write result step.
	 *
	 * @param step the step
	 * @param vars the vars
	 */
	public void writeResultStep(long step, ListenedVariable[] vars);

	/**
	 * Close.
	 */
	public void close();
}
