package gospl.sampler.multilayer.co;

import java.util.Collection;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.GosplMultitypePopulation;
import gospl.algo.IGosplConcept;
import gospl.algo.IGosplConcept.EGosplGenerationConcept;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.sampler.IEntitySampler;

/**
 * Main contract sampler to draw from multi-layered samples
 * 
 * @author kevinchapuis
 *
 */
public interface ICOMultiLayerSampler extends IEntitySampler<GosplMultitypePopulation<ADemoEntity>> {

	/**
	 * Specified each layer population (samples), if we should draw using weights and the layer to sample entity from
	 * 
	 * @param samples
	 * @param withWeigths
	 * @param layer
	 */
	public void setSample(GosplMultitypePopulation<ADemoEntity> sample, boolean withWeigths, int layer);

	/**
	 * Add a fiting criterion for a given layer
	 * 
	 * @param objectives
	 * @param layer
	 */
	public void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives, int layer);
	
	/**
	 * draw an entity from the given layer, with 0 being the entities without any child and the largest one having no parent
	 * 
	 * @param layer
	 * @return
	 */
	public ADemoEntity drawFromLayer(int layer);
	
	/**
	 * draw {@code numberOfDraw} entities from the given layer <\p>
	 * @see #drawFromLayer(int)
	 * 
	 * @param layer
	 * @return
	 */
	public Collection<ADemoEntity> drawFromLayer(int layer, int numberOfDraw);
	
	/**
	 * True if layer is taken into account by this sampler
	 * @param layer
	 * @return
	 */
	public boolean checkLayer(int layer);
	
	@Override
	default EGosplGenerationConcept getConcept() {
		return IGosplConcept.EGosplGenerationConcept.MULTILEVEL;
	}
	
}
