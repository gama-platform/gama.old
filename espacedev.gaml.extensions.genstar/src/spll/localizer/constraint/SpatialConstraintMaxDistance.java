package spll.localizer.constraint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

public class SpatialConstraintMaxDistance extends ASpatialConstraint {

	private Map<AGeoEntity<? extends IValue>, Double> distanceToEntities;

	public SpatialConstraintMaxDistance(Collection<AGeoEntity<? extends IValue>> distanceToEntities,
			Double distance) {
		this.distanceToEntities = distanceToEntities.stream().collect(Collectors
				.toMap(Function.identity(), entity -> distance));
	}
	
	public SpatialConstraintMaxDistance(Map<AGeoEntity<? extends IValue>, Double> distanceToEntities) {
		this.distanceToEntities = distanceToEntities;
	}
	
	@Override
	public List<AGeoEntity<? extends IValue>> getCandidates(List<AGeoEntity<? extends IValue>> nests) {
		return nests.stream().filter(nest -> distanceToEntities.keySet()
				.stream().anyMatch(entity -> nest.getGeometry()
						.buffer(distanceToEntities.get(entity))
						.intersects(entity.getGeometry()))).toList();
		
		/*		.sorted((c1, c2) -> Double.compare(
						distanceToEntities.keySet().stream().mapToDouble(entity -> c1.getGeometry().getCentroid()
								.distance(entity.getGeometry())).min().getAsDouble(),
						distanceToEntities.keySet().stream().mapToDouble(entity -> c2.getGeometry().getCentroid()
								.distance(entity.getGeometry())).min().getAsDouble()))*/
				
	}

	@Override
	public boolean updateConstraint(AGeoEntity<? extends IValue> nest) {
		return false;
	}

	@Override
	public void relaxConstraintOp(Collection<AGeoEntity<? extends IValue>> distanceToEntities) {
		distanceToEntities.stream().forEach(entity -> 
			this.distanceToEntities.put(entity, 
					this.distanceToEntities.get(entity)+this.increaseStep));

	}

}
