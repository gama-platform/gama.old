package gospl.sampler;

/**
 * A completion sampler can take an existing individual 
 * and add missing information using sampling.
 * 
 * @author Samuel Thiriot
 *
 * @param <T>
 */
public interface ICompletionSampler<T> {
	
	// ---------------- main contract ---------------- //
	
	/**
	 * Main method that return a random draw given a pseudo-random engine 
	 * and a distribution of probability
	 * 
	 * @return
	 */
	public T complete(T originalEntity);
	
	/**
	 * Should give an overview of the underlying distribution
	 * 
	 * @param csvSeparator
	 * @return
	 */
	public String toCsv(String csvSeparator);

}
