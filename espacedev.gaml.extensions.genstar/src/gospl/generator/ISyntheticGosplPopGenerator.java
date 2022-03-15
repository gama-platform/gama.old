package gospl.generator;

import gospl.GosplPopulation;

/**
 * Light and unprescriptive super interface for synthetic population generator: only need to be able
 * to create a {@link GosplPopulation} whith n individual entity
 * 
 * @author kevinchapuis
 *
 */
public interface ISyntheticGosplPopGenerator {

	/**
	 * Generate a synthetic population of type {@link GosplPopulation} with
	 * parametric number of individual entity
	 * 
	 * @param numberOfIndividual
	 * @return
	 */
	public GosplPopulation generate(int numberOfIndividual);
	
}
