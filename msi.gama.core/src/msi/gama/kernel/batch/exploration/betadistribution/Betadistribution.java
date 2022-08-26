package msi.gama.kernel.batch.exploration.betadistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import smile.stat.distribution.EmpiricalDistribution;

public class Betadistribution {
	
	String objective;
	double objMin = Double.MAX_VALUE; double objMax = Double.MIN_VALUE;
	double[] empiricalCDFGranularity;
	
	/** The parameters */
	List<Batch> parameters;
	
	/** The res outputs. */
	/* All the outputs for each simulation */
	Map<ParametersSet,List<Double>> sample;
	final EmpiricalDistribution Y;
	
	public Betadistribution(IMap<ParametersSet,List<Object>> sample, String obj) { this(sample,100,obj); }
	
	public Betadistribution(IMap<ParametersSet,List<Object>> sample, int granularity, String obj) {
		this.objective = obj;
		this.sample = new HashMap<>();
		for (ParametersSet ps : sample.keySet()) { 
			this.sample.put(ps, sample.get(ps).stream().mapToDouble(v -> Double.valueOf(v.toString())).boxed().toList());
			double min = Collections.min(this.sample.get(ps));
			double max = Collections.max(this.sample.get(ps));
			if (min < this.objMin) {this.objMin=min;}
			if (max > this.objMax) {this.objMax=max;}
		}
		
		this.empiricalCDFGranularity = granularity(granularity,this.objMin,this.objMax);
		this.Y = get_empirical_distribution(this.sample.values().stream().flatMap(List::stream).toList());
	}
	
	public Map<Batch,Double> evaluate() {
		Map<Batch,Double> betadKu = new HashMap<>();
		
		// Over each input parameter compute beta
		for (Batch p : parameters) {
			// For each value of 'theta' find the conditional 'y'
			Map<Object,List<Double>> conditional_in_out = new HashMap<>();
			for (ParametersSet ps : this.sample.keySet()) {
				Object o = ps.get(p.getName());
				if (!conditional_in_out.containsKey(o)) { conditional_in_out.put(o, new ArrayList<>()); }
				conditional_in_out.get(o).addAll(sample.get(o));
			}
			List<Double> betas = new ArrayList<>();
			
			// find the Kuiper distance DeltaP_t and DeltaP_s, to make the sum, for each 'theta_i' and store them
			for (List<Double> e : conditional_in_out.values()) {
				EmpiricalDistribution ed = get_empirical_distribution(e);
				List<Double> deltas = IntStream.range(0, Y.length()).mapToDouble(i -> ed.cdf(i) - Y.cdf(i)).boxed().toList();
				betas.add(Collections.max(deltas)+Math.abs(Collections.min(deltas)));
			}

			// compute expectancy - no differences in weight, then just the average - of all the betaKu to obtain betadKu
			betadKu.put(p, betas.stream().mapToDouble(Double::doubleValue).average().getAsDouble());
		}
		
		return betadKu;
	}
	 
	/*
	 * Get the distribution (cdf) from a sample
	 */
	EmpiricalDistribution get_empirical_distribution(List<Double> as) {
		 
		double[] prob = new double[this.empiricalCDFGranularity.length];
		
		for (int i = 0; i < prob.length; i++) {
			final int idx = i;
			if (i==0) {
				prob[i] = as.stream().filter(v -> v < empiricalCDFGranularity[idx]).count() / as.size(); 
			} else if (i==prob.length-1) {
				prob[i] = as.stream().filter(v -> v > empiricalCDFGranularity[idx]).count() / as.size();
			} else {
				prob[i] = as.stream().filter(v -> v > empiricalCDFGranularity[idx-1] && v < empiricalCDFGranularity[idx]).count() / as.size();
			}
		}
		
		return new EmpiricalDistribution(prob);
	}
	
	// ----- UTILS ----- //
	
	private double[] granularity(int bins, double min, double max) {
		double[] res = new double[bins];
		double incr = (max-min)/bins;
		res[0] = min + incr;
		for (int i = 1; i < bins; i++) { res[i] = res[i-1] + incr; }
		if (res[bins]+incr != max) { 
			throw GamaRuntimeException.error("The bins does not fit max val: "+(res[bins]+incr)
				+" is not the maximum expected value "+max, null);
		}
		return res;
	}
	
}
