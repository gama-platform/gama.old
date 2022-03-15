package gospl.sampler.multilayer.sr;

import gospl.algo.IGosplConcept;
import gospl.algo.IGosplConcept.EGosplGenerationConcept;
import gospl.distribution.matrix.coordinate.GosplMultiLayerCoordinate;
import gospl.sampler.ISampler;

/**
 * SR based abstract sampler for several layer of synthetic population
 * 
 * @author kevinchapuis
 *
 */
public interface ISRMultiLayerSampler extends ISampler<GosplMultiLayerCoordinate> {
	
	@Override
	default EGosplGenerationConcept getConcept() {
		return IGosplConcept.EGosplGenerationConcept.MULTILEVEL;
	}
	
	
}
