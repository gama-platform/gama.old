package gospl.algo.co.metamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.GosplMultitypePopulation;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;
import gospl.algo.co.metamodel.solution.MultiLayerSPSolution;
import gospl.distribution.matrix.INDimensionalMatrix;

public abstract class AMultiLayerOptimizationAlgorithm implements IOptimizationAlgorithm<GosplMultitypePopulation<ADemoEntity>, MultiLayerSPSolution> {

	/*
	 * Constraint on searching for an optimal solution
	 */
	final private Map<Integer, Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>>> layeredObjectives;
	final private Map<Integer,Integer> layerSizeConstraint;

	private GosplMultitypePopulation<ADemoEntity> sample;
	private IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>,?> neighborSearch;
	private int layer;

	private double fitnessThreshold;
	private double k_neighborRatio;
	
	public AMultiLayerOptimizationAlgorithm(IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>,?> neighborSearch, double fitnessThreshold) {
		this.neighborSearch = neighborSearch; 
		this.fitnessThreshold = fitnessThreshold;
		this.layeredObjectives = new HashMap<>();
		this.layerSizeConstraint = new HashMap<>();
	}
	
	@Override
	public Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> getObjectives() {
		return layeredObjectives.get(0);
	}
	
	public Map<Integer, Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>>> getLayeredObjectives() {
		return layeredObjectives;
	}

	@Override
	public void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		this.addObjectives(0, objectives);
	}
	
	public void addObjectives(int layer, INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		if(layeredObjectives.containsKey(layer))
			this.layeredObjectives.get(layer).add(objectives);
		else {
			this.layeredObjectives.put(layer, new HashSet<>());
			this.layeredObjectives.get(layer).add(objectives);
		}
		this.layerSizeConstraint.put(layer, objectives.getVal().getValue());
	}
	
	@Override
	public IPopulation<ADemoEntity, Attribute<? extends IValue>> getSample(){
		return sample;
	}
	
	public IPopulation<ADemoEntity, Attribute<? extends IValue>> getSample(int layer){
		return this.sample.getSubPopulation(layer);
	}
	
	@Override
	public void setSample(GosplMultitypePopulation<ADemoEntity> sample) {
		this.sample = sample;
		this.neighborSearch.setSample(sample);
	}
	
	@Override
	public IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, ?> getNeighborSearchAlgorithm(){
		return neighborSearch;
	}
	
	@Override
	public void setNeighborSearch(IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, ?> neighborSearch) {
		this.neighborSearch = neighborSearch;
	}

	@Override
	public double getFitnessThreshold() {
		return fitnessThreshold;
	}

	@Override
	public void setFitnessThreshold(double fitnessThreshold) {
		this.fitnessThreshold = fitnessThreshold;
	}

	@Override
	public double getK_neighborRatio() {
		return k_neighborRatio;
	}

	@Override
	public void setK_neighborRatio(double k_neighborRatio) {
		this.k_neighborRatio = k_neighborRatio;
	}
	
	public int getSampledLayer() {return layer;}
	
	public void setSampledLayer(int layer) {this.layer = layer;}
	
	public int computeBuffer(double fitness, ISyntheticPopulationSolution<GosplMultitypePopulation<ADemoEntity>> solution) {
		return Math.round(Math.round(solution.getSolution().getSubPopulationSize(layer) * k_neighborRatio /* Math.log(1+fitness*k_neighborRatio)*/));
	}

}
