package gospl.algo.co.simannealing;

/**
 * Define the random transition function for simulated annealing: it says if candidate energy 
 * should be elicited or not. If it returns true, the candidate energy should be the new energy of the system. 
 * Otherwise, the energy of the system should remain the same.
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
	public boolean getTransitionProbability(double currentEnergy, double candidateEnergy, double temperature);
	
}
