/*******************************************************************************************************
 *
 * ISimulatedAnnealingTransitionFunction.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co.simannealing;

/**
 * Define the random transition function for simulated annealing: it says if candidate energy should be elicited or not.
 * If it returns true, the candidate energy should be the new energy of the system. Otherwise, the energy of the system
 * should remain the same.
 *
 * @author kevinchapuis
 *
 */
public interface ISimulatedAnnealingTransitionFunction {

	/**
	 * The transition function
	 *
	 * @param currentEnergy
	 * @param candidateEnergy
	 * @param temperature
	 * @return
	 */
	boolean getTransitionProbability(double currentEnergy, double candidateEnergy, double temperature);

}
