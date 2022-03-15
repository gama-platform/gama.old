package spll.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.GLSMultipleLinearRegression;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.datamapper.matcher.ISPLMatcher;
import spll.datamapper.variable.SPLVariable;

/**
 * WARNING: not functional
 * FIXME
 * 
 * @author kevinchapuis
 *
 */
public class LMRegressionGLS extends GLSMultipleLinearRegression implements ISPLRegressionAlgo<SPLVariable, Double> {

	private List<SPLVariable> regVars;
	private List<AGeoEntity<? extends IValue>> observation;
	
	private Map<SPLVariable, Double> regression;
	private double intercept;

	@Override
	public void setupData(Map<AGeoEntity<? extends IValue>, Double> observations, 
			Set<ISPLMatcher<SPLVariable, Double>> regressors){
		this.regVars = new ArrayList<>(regressors
				.parallelStream().map(varfm -> varfm.getVariable())
				.collect(Collectors.toSet()));
		this.observation = new ArrayList<>(observations.size());
		double[] instances = new double[regVars.size() * observations.size() + observations.size()];
		int instanceCount = 0;
		for(AGeoEntity<? extends IValue> geoEntity : observations.keySet()){
			observation.add(geoEntity);
			instances[instanceCount++] = observations.get(geoEntity);
			for(int i = 0; i < regVars.size(); i++){
				int idx = i;
				Optional<ISPLMatcher<SPLVariable, Double>> optVar = regressors.parallelStream()
						.filter(varfm -> varfm.getEntity().equals(geoEntity) 
								&& varfm.getVariable().equals(regVars.get(idx)))
						.findFirst();
				instances[instanceCount++] = optVar.isPresent() ? optVar.get().getValue() : 0d;
			}
		}

		super.newSampleData(instances, observations.size(), regVars.size());
	}

	@Override
	public Map<SPLVariable, Double> getRegressionParameter() {
		if(regression == null){
			regression = new HashMap<>();
			double[] rVec = super.estimateRegressionParameters();
			intercept = rVec[0];
			for(int i = 0; i < regVars.size(); i++)
				regression.put(regVars.get(i), rVec[i+1]);
		}
		return regression;
	}

	@Override
	public Map<AGeoEntity<? extends IValue>, Double> getResidual() {
		Map<AGeoEntity<? extends IValue>, Double> residual = new HashMap<>();
		double[] rVec = super.estimateResiduals();
		for(int i = 0; i < observation.size(); i++)
			residual.put(observation.get(i), rVec[i]);
		return residual;
	}
	
	@Override
	public double getIntercept(){
		return intercept;
	}

	public RealVector getSampleData(){
		return super.getY();
	}

	public RealMatrix getObservations(){
		return super.getX();
	}

}
