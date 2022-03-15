package spll.localizer.constraint;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

public class SpatialConstraintMaxDensity extends SpatialConstraintMaxNumber{

	protected Map<String, Double> nestInitDensity;
	
   //maxVal: global value for the max density of entities per nest
	public SpatialConstraintMaxDensity(Collection<? extends AGeoEntity<? extends IValue>> nests, Double maxVal) {
		super(nests, maxVal);
		
	} 
		
	 //keyAttMax: name of the attribute that contains the max density of entities in the nest file
	public SpatialConstraintMaxDensity(Collection<? extends AGeoEntity<? extends IValue>> nests, String keyAttMax) {
		super(nests, keyAttMax);
	}
	
	@Override
	public void relaxConstraintOp(Collection<AGeoEntity<? extends IValue>> nests) {
		for (AGeoEntity<? extends IValue> n : nests )
			nestCapacities.put(n.getGenstarName(), (int)Math.round(
					nestCapacities.get(n.getGenstarName()) 
					- (int)(Math.round(nestInitDensity.get(n.getGenstarName()) * n.getArea())))
					+ (int)(Math.round((nestInitDensity.get(n.getGenstarName()) + increaseStep *(1 + nbIncrements)) * n.getArea())));
	}
		
	
	
	protected Map<String, Integer> computeMaxPerNest(Collection<? extends AGeoEntity<? extends IValue>> nests, String keyAttMax){
		nestInitDensity = nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName, 
			a -> a.getNumericValueForAttribute(keyAttMax).doubleValue()));
		
		return nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName, 
							a-> (int)(Math.round(a.getNumericValueForAttribute(keyAttMax).doubleValue() * a.getArea()))));
	}
	
	protected Map<String, Integer> computeMaxPerNest(Collection<? extends AGeoEntity<? extends IValue>> nests, Double maxVal){
		nestInitDensity = nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName, a-> maxVal));
		return nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName, a-> (int)(Math.round(maxVal * a.getArea()))));
	}

}
