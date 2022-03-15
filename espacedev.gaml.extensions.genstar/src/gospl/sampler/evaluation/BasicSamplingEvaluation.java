package gospl.sampler.evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BasicSamplingEvaluation implements ISamplingEvaluation {

	protected double overallBias;
	protected int generatedPopulationSize;
	protected final Map<String,Object> key2value = new HashMap<>();
	
	public BasicSamplingEvaluation(int generatedPopulationSize) {
		
		this.generatedPopulationSize = generatedPopulationSize;
	}

	public BasicSamplingEvaluation(int generatedPopulationSize, double overallBias) {

		this.generatedPopulationSize = generatedPopulationSize;
		this.overallBias = overallBias;
	}

	public double getOverallBias() {
		return overallBias;
	}

	@Override
	public int getGeneratedPopulationSize() {
		return generatedPopulationSize;
	}

	@Override
	public Set<String> getEvaluationKeys() {
		return key2value.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getEvaluationResult(String key) {
		return (T) key2value.get(key);
	}

	public void setOverallBias(double overallBias) {
		this.overallBias = overallBias;
	}
	
}
