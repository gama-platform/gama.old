/*******************************************************************************************************
 *
 * SimulatedAnnealingDefaultTransitionFunction.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co.simannealing;

import core.util.random.GenstarRandom;

/**
 * Transition is a two step process:
 *
 * 1) If energy of candidate state is lower than current state energy, then it is accepted [energy = fitness] 2) If not,
 * candidate state is accepted with a probability equal to: Math.exp((current energy - candidateEnergy) / temperature)
 *
 * @author kevinchapuis
 *
 */
public class SimulatedAnnealingDefaultTransitionFunction implements ISimulatedAnnealingTransitionFunction {

	@Override
	public boolean getTransitionProbability(final double currentEnergy, final double candidateEnergy,
			final double temperature) {
		if (currentEnergy > candidateEnergy) return true;
		return Math.exp((currentEnergy - candidateEnergy) / temperature) < GenstarRandom.getInstance().nextDouble();
	}

}
