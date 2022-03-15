package gospl.sampler;

import java.util.Collection;

import gospl.algo.IGosplConcept.EGosplGenerationConcept;

/**
 * The global contract for sampler -- the part of generation process responsible the creation
 * of the entity with pre-defined rules such as provided by SR or CO algorithm. Most of the time
 * {@link ISampler} that will be used in SR are Monte Carlo sampling methods using a pre-computed
 * joint distribution (using IPF), but can also take the form of Markov Chains or Bayesian Network.
 * In CO perspective, the sampler will be the entity responsible for drawing records into a sample
 * of the real population, with parameter such as drawing with or without replacement.
 * 
 * @author kevinchapuis
 *
 * @param <T>
 */
public interface ISampler<T> {
	
	// ---------------- main contract ---------------- //
	
	/**
	 * Main method that return a random draw given a pseudo-random engine 
	 * and a distribution of probability
	 * 
	 * @return
	 */
	public T draw();
	
	/**
	 * Return {@code numberOfDraw} number of draw. Due to performance optimization,
	 * it could be based on another method than {@link #draw()}
	 * 
	 * @param numberOfDraw
	 * @return
	 */
	public Collection<T> draw(int numberOfDraw);
	
	/**
	 * Should give an overview of the underlying distribution
	 * 
	 * @param csvSeparator
	 * @return
	 */
	public String toCsv(String csvSeparator);
	
	/**
	 * Gives the main synthetic population concept behind this sampler
	 * 
	 * @return
	 */
	public EGosplGenerationConcept getConcept();

}
