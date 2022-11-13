package gospl.sampler.co;

import java.util.Collection;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.GosplPopulation;
import gospl.algo.co.metamodel.IOptimizationAlgorithm;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;
import gospl.algo.co.metamodel.solution.SyntheticPopulationSolution;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.sampler.IEntitySampler;

/**
 * Define higher order behavior for {@link IEntitySampler}. It relies on {@link MicroDataSampler}
 * 
 * @author kevinchapuis
 *
 * @param <A>
 */
public class CombinatorialOptimizationSampler<A extends IOptimizationAlgorithm<GosplPopulation, ISyntheticPopulationSolution<GosplPopulation>>> 
	implements IEntitySampler<GosplPopulation> {

	private MicroDataSampler basicSampler;
	private A algorithm;
	
	public CombinatorialOptimizationSampler(A algorithm, GosplPopulation sample) {
		this.algorithm = algorithm;
		this.basicSampler = new MicroDataSampler();
		this.setSample(sample,false);
	}
	
	@Override
	public ADemoEntity draw() {
		return basicSampler.draw();
	}
	
	@Override
	public Collection<ADemoEntity> draw(int numberOfDraw) {
		return this.algorithm.run(new SyntheticPopulationSolution(this.basicSampler.draw(numberOfDraw))).getSolution();
	}
	
	@Override
	public void setSample(GosplPopulation sample, boolean weights) {
		this.basicSampler.setSample(sample,weights);
		this.algorithm.setSample(sample);
	}

	@Override
	public void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		this.algorithm.addObjectives(objectives);
	}
	
	@Override
	public String toCsv(String csvSeparator) {
		
		return null;
	}

}
