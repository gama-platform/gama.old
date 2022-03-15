package gospl.sampler.evaluation;

import java.util.Set;

/**
 * The result of the evaluation of the 
 * 
 * @author Samuel Thiriot
 *
 */
public interface ISamplingEvaluation {

	/**
	 * returns a measure that reflects how much bias was introduced by the sampling.
	 * @return
	 */
	public double getOverallBias();
	
	/**
	 * returns the size of the generated population. 
	 * @return
	 */
	public int getGeneratedPopulationSize();
	
	/**
	 * Returns the keys of the elements available in the evaluation
	 * @return
	 */
	public Set<String> getEvaluationKeys();
	
	/**
	 * returns the evaluation corresponding to this key
	 * @param key
	 * @return
	 */
	public <T> T getEvaluationResult(String key);
	
	
}
