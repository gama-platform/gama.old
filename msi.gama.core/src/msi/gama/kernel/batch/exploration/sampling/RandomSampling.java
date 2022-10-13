package msi.gama.kernel.batch.exploration.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.runtime.IScope;

/**
 * A sampling method from a given parameter space with uniformally chosen points
 * 
 * @author kevinchapuis
 *
 */
public class RandomSampling extends SamplingUtils {
	
	public static List<ParametersSet> UniformSampling(IScope scope, int sample, List<Batch> parameters){
		List<ParametersSet>  sampling= new ArrayList<>();
		
		List<Map<String,Double>> rSample = new ArrayList<>();
		int i = 0;
		while (i++ < sample) {
			Map<String,Double> point = new HashMap<>();
			for (Batch p : parameters) { point.put(p.getName(), scope.getRandom().next()); }
			rSample.add(point);
		}
		
        sampling=BuildParametersSetfromSample(scope,parameters,rSample);
        return sampling;
	}
	
}
