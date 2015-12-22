/*********************************************************************************************
 *
 *
 * 'IOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs;

import msi.gama.common.interfaces.IStepable;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * This interface represents the objects, declared in a model, which perform various types of computations and return information supposed to be displayed or saved during simulations. Outputs are not
 * in charge of displaying/outputting information on a concrete support, only computing it. They however control whatever concrete support they represent (opening, closing, pausing, updating and
 * refreshing it).
 * @author Alexis Drogoul, IRD
 * @revised in Dec. 2015 to simplify and document the interface of outputs
 */
public interface IOutput extends ISymbol, IStepable {

	/**
	 * The output should pause its operations when the parameter passed is true and resume them when it is false. Setting pause to true (resp. false) when the output is already paused (resp. resumed)
	 * should not have any effect.
	 * @param paused true if the output should pause, false if it should resume
	 */
	public void setPaused(boolean paused);

	/**
	 * Returns whether the output is paused or running.
	 * @return true if the output has been set to pause, false otherwise
	 */
	public boolean isPaused();

	/**
	 * In response to this message, the output is supposed to open its concrete support, whether it is a view or a file. Sending open() to an already opened output should not have any effect.
	 */
	public void open();

	/**
	 * In response to this message, the output is supposed to close its concrete support, whether it is a view or a file. A closed output cannot resume its operations unless 'open()' is called again.
	 */
	public void close();

	/**
	 * The output should return its refresh rate in terms of the number of cycles of the simulation (min. is 1) between two updates
	 * @return an integer >=1 that represents how many cycles separate two updates
	 */
	public int getRefreshRate();

	/**
	 * The output should set its refresh rate to the parameter passed. Mainly called by the interface
	 * @param rate, an integer normally >= 1. The output is free to consider if a negative or null rate makes any sense
	 */
	public void setRefreshRate(int rate);

	/*
	 * Called by the output thread to perform the actual "refresh" of the concrete support of the output (whereas step(), from IStepable, performs the computations described in GAML, that will serve
	 * as a model for this refresh).
	 */
	public void update() throws GamaRuntimeException;

	/**
	 * Returns the scope of the output, i.e. the scope it uses to perform its computations, independently of the main simulation scope. Access to this scope should be limited to a strict necessity
	 * @return the scope of the output, which should never be null when the output is open. It might be null otherwise
	 */
	public IScope getScope();

}
