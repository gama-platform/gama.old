package spll.localizer.distribution.function;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.localizer.constraint.SpatialConstraintMaxNumber;

public class CapacityFunction implements ISpatialEntityFunction<Integer> {

	private SpatialConstraintMaxNumber scNumber;

	public CapacityFunction(SpatialConstraintMaxNumber scNumber) {
		this.scNumber = scNumber;
	}
	
	@Override
	public Integer apply(AGeoEntity<? extends IValue> t) {
		return scNumber.getNestCapacities().get(t.getGenstarName());
	}

	@Override
	public void updateFunctionState(AGeoEntity<? extends IValue> entity) {
		int capacity = scNumber.getNestCapacities().get(entity.getGenstarName());
		scNumber.getNestCapacities().put(entity.getGenstarName(), capacity == 0 ? 0 : capacity - 1);
	}

}
