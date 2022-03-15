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
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.datamapper.matcher.ISPLMatcher;
import spll.datamapper.variable.SPLVariable;

public class LMRegressionOLS extends OLSMultipleLinearRegression implements ISPLRegressionAlgo<SPLVariable, Double> {

	private List<SPLVariable> regVars;
	private List<AGeoEntity<? extends IValue>> observation;
	
	private Map<SPLVariable, Double> regression;
	private double intercept;

	@Override
	public void setupData(Map<AGeoEntity<? extends IValue>, Double> observations,
			Set<ISPLMatcher<SPLVariable, Double>> regressors){
		// Reset regression if already been calculated with another setup
		this.regression = null;
		this.regVars = new ArrayList<>(regressors
				.parallelStream().map(varfm -> varfm.getVariable())
				.collect(Collectors.toSet()));
		this.observation = new ArrayList<>(observations.size());
		double[] y = new double[observations.size()];
		double[][] x = new double[observations.size()][];
		int observationIdx = 0;
		for(AGeoEntity<? extends IValue> geoEntity : observations.keySet()){
			observation.add(geoEntity);
			y[observationIdx] = observations.get(geoEntity);
			x[observationIdx] = new double[regVars.size()];
			for(int i = 0; i < regVars.size(); i++){
				int index = i;
				Optional<ISPLMatcher<SPLVariable, Double>> optVar = regressors.parallelStream()
						.filter(varfm -> varfm.getEntity().equals(geoEntity) 
								&& varfm.getVariable().equals(regVars.get(index)))
						.findFirst();
				x[observationIdx][index] = optVar.isPresent() ? optVar.get().getValue() : 0d;
			}
			observationIdx++;
		}
		super.newSampleData(y, x);
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
	public double getIntercept() {
		return intercept;
	}

	public RealVector getSampleData(){
		return super.getY();
	}

	public RealMatrix getObservations(){
		return super.getX();
	}
}
