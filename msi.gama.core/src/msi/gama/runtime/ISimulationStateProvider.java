/*******************************************************************************************************
 *
 * msi.gama.runtime.ISimulationStateProvider.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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