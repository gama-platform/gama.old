/*********************************************************************************************
 * 
 *
 * 'ISimulationStateProvider.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.runtime;

/**
 * The class ISimulationStateProvider. 
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface ISimulationStateProvider {

	/**
	 * Change the UI state based on the state of the simulation (none, stopped, running or notready)
	 */
	public abstract void updateStateTo(final String state);

}