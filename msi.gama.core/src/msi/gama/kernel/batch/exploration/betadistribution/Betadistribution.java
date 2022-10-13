package msi.gama.kernel.batch.exploration.betadistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;

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
	
	public Betadistribution(IMap<ParametersSet,List<Object>> sample, List<Batch> inputs, String obj) { this(sample,inputs,10,obj); }
	
	public Betadistribution(IMap<ParametersSet,List<Object>> sample, List<Batch> inputs, int granularity, String obj) {
		this.objective = obj;
		this.sample = new HashMap<>();
		for (ParametersSet ps : sample.keySet()) { 
			this.sample.put(ps, sample.get(ps).stream().mapToDouble(v -> Double.valueOf(v.toString())).boxed().toList());
			double min = Collections.min(this.sample.get(ps));
			double max = Collections.max(this.sample.get(ps));
			if (min < this.objMin) {this.objMin=min;}
			if (max > this.objMax) {this.objMax=max;}
		}
		this.parameters = inputs;
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
				conditional_in_out.get(o).addAll(sample.get(ps));
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
				prob[i] = as.stream().filter(v -> v.doubleValue() < empiricalCDFGranularity[idx]).count() * 1d / as.size(); 
			} else if (i==prob.length-1) {
				prob[i] = as.stream().filter(v -> v.doubleValue() >= empiricalCDFGranularity[idx]).count() * 1d / as.size();
			} else {
				prob[i] = as.stream().filter(v -> v.doubleValue() >= empiricalCDFGranularity[idx-1] && 
						v.doubleValue() < empiricalCDFGranularity[idx]).count() * 1d / as.size();
			}
		}
		
		if (Math.ulp(Arrays.stream(prob).sum()) != Math.ulp(1.0)) {
			throw new IllegalArgumentException("The sum of probability does not sum to 1: "+Arrays.stream(prob).sum());
		}
		
		return new EmpiricalDistribution(prob);
	}
	
	// ----- UTILS ----- //
	
	private double[] granularity(int bins, double min, double max) {
		double[] res = new double[bins-1];
		double incr = (max-min)/bins;
		res[0] = min + incr;
		for (int i = 1; i < bins-1; i++) { res[i] = res[i-1] + incr; }
		if (Math.ulp(res[bins-2]+incr) > Math.ulp(max)) { 
			throw GamaRuntimeException.error("The bins does not fit max val: "+(res[bins-2]+incr)
				+" is not the maximum expected value "+max+" (diff = "+Math.abs(res[bins-2]+incr - max)+")", null);
		}
		return res;
	}
	
	/*
	 * Empirical distribution based on the implementation of smile API:
	 * https://github.com/haifengl/smile/blob/master/base/src/main/java/smile/stat/distribution/EmpiricalDistribution.java
	 */
	public class EmpiricalDistribution {
		double[] p;
		private final double[] cdf;
		
		public EmpiricalDistribution(double[] prob) {
			p = new double[prob.length];
	        cdf = new double[prob.length];
			cdf[0] = prob[0];
			for (int i = 0; i < prob.length; i++) {
	            if (prob[i] < 0 || prob[i] > 1) {
	                throw new IllegalArgumentException("Invalid probability " + p[i]);
	            }

	            p[i] = prob[i];

	            if (i > 0) {
	                cdf[i] = cdf[i - 1] + p[i];
	            }

	        }

	        if (Math.abs(cdf[cdf.length - 1] - 1.0) > 1E-7) {
	            throw new IllegalArgumentException("The sum of probabilities is not 1: "+cdf[cdf.length - 1]);
	        }
		}
		
	    public int length() { return p.length; }
	    public double cdf(int k) { return cdf[k]; } 
	}
	
}
