package spll.localizer.constraint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

public class SpatialConstraintMaxNumber extends ASpatialConstraint {

	protected Map<String, Integer> nestCapacities;
	
	
	/**
	 * Constraint on maximum number of entities in every nest
	 * 
	 * @param nests
	 * @param maxVal : global value for the max number of entities per nest
	 */
	public SpatialConstraintMaxNumber(Collection<? extends AGeoEntity<? extends IValue>> nests, Double maxVal) {
		super();
		nestCapacities = computeMaxPerNest(nests, maxVal);
	}
	
	 /**
	  * Constraint on maximum number of entities for each a nest
	  * @param nests
	  * @param keyAttMax : name of the attribute that contains the max number of entities in the nest file
	  */
	public SpatialConstraintMaxNumber(Collection<? extends AGeoEntity<? extends IValue>> nests, String keyAttMax) {
		super();
		nestCapacities = computeMaxPerNest(nests, keyAttMax);
	}
	
	@Override
	public void relaxConstraintOp(Collection<AGeoEntity<? extends IValue>> nests) {
		for (AGeoEntity<? extends IValue> n : nests )
			nestCapacities.put(n.getGenstarName(), (int)Math.round(nestCapacities.get(n.getGenstarName()) + increaseStep));
	}


	@Override
	public List<AGeoEntity<? extends IValue>> getCandidates(List<AGeoEntity<? extends IValue>> nests) {
		List<AGeoEntity<? extends IValue>> candidates = nests.stream().filter(a -> nestCapacities.get(a.getGenstarName()) > 0).toList();
		return candidates;
		/*return candidates.stream().sorted((n1, n2) -> Integer.compare(-1 * nestCapacities.get(n1.getGenstarName()),
				-1 * nestCapacities.get(n2.getGenstarName()))).toList();*/
	}
	
	@Override
	public boolean updateConstraint(AGeoEntity<? extends IValue> nest) {
		int capacity = nestCapacities.get(nest.getGenstarName());
		nestCapacities.put(nest.getGenstarName(), capacity - 1);
		if (capacity <= 1) return true;
		return false;
			
	}

	
	public Map<String, Integer> getNestCapacities() {
		return nestCapacities;
	}

	protected Map<String, Integer> computeMaxPerNest(Collection<? extends AGeoEntity<? extends IValue>> nests, String keyAttMax){
		return nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName, a-> a.getNumericValueForAttribute(keyAttMax).intValue()));
	}
	
	protected Map<String, Integer> computeMaxPerNest(Collection<? extends AGeoEntity<? extends IValue>> nests, Double maxVal){
		return nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName, a-> (int)(Math.round(maxVal))));
	}
	
}
