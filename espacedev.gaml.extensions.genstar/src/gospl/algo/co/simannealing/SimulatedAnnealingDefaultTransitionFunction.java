package gospl.algo.co.simannealing;

import core.util.random.GenstarRandom;

/**
 * Transition is a two step process:
 * 
 * 1) If energy of candidate state is lower than current state energy, then it is accepted [energy = fitness]
 * 2) If not, candidate state is accepted with a probability equal to: Math.exp((current energy - candidateEnergy) / temperature)
 * 
 * @author kevinchapuis
 *
 */
public class SimulatedAnnealingDefaultTransitionFunction implements ISimulatedAnnealingTransitionFunction {

	@Override
	public boolean getTransitionProbability(double currentEnergy, double candidateEnergy, double temperature) {
		if(currentEnergy > candidateEnergy)
			return true;
		return Math.exp((currentEnergy - candidateEnergy) / temperature) < GenstarRandom.getInstance().nextDouble();
	}


}
