package gospl.generator;

import java.util.Collection;
import java.util.stream.Collectors;

import gospl.GosplEntity;
import gospl.GosplPopulation;
import gospl.distribution.matrix.coordinate.GosplMultiLayerCoordinate;
import gospl.sampler.ISampler;

public class MultiLayerGenerator implements ISyntheticGosplPopGenerator {

	ISampler<GosplMultiLayerCoordinate> sampler;
	
	public MultiLayerGenerator(ISampler<GosplMultiLayerCoordinate> sampler) {
		this.sampler = sampler;
	}
	
	@Override
	public GosplPopulation generate(int numberOfIndividual) {
		GosplPopulation pop = new GosplPopulation();
		Collection<GosplMultiLayerCoordinate> coords = sampler.draw(numberOfIndividual);
		for(GosplMultiLayerCoordinate coord : coords) {
			GosplEntity parent = new GosplEntity(coord.getMap());
			parent.addChildren(coord.getChilds().stream()
					.map(c -> new GosplEntity(c.getMap()))
					.collect(Collectors.toSet()));
			pop.add(parent);
		}
		return pop;
	}

}
