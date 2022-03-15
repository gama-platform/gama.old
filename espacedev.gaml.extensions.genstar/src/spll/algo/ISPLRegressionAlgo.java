package spll.algo;

import java.util.Map;
import java.util.Set;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.datamapper.matcher.ISPLMatcher;
import spll.datamapper.variable.ISPLVariable;

/**
 * Encapsulate spatial regression algorithm
 * @author kevinchapuis
 *
 * @param <V>
 * @param <T>
 */
public interface ISPLRegressionAlgo<V extends ISPLVariable, T> {
	
	/**
	 * Retrieve regression parameter for each variable
	 * @return
	 */
	public Map<V, Double> getRegressionParameter();
	
	public Map<AGeoEntity<? extends IValue>, Double> getResidual();
	
	public double getIntercept();
	
	public void setupData(Map<AGeoEntity<? extends IValue>, Double> observations,
			Set<ISPLMatcher<V, T>> regressors);
	
}
