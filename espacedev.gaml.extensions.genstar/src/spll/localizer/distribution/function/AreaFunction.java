package spll.localizer.distribution.function;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

public class AreaFunction implements ISpatialEntityFunction<Double> {

	@Override
	public Double apply(AGeoEntity<? extends IValue> t) {
		return t.getArea();
	}

	@Override
	public void updateFunctionState(AGeoEntity<? extends IValue> entity) {
		// TODO Auto-generated method stub
	}

}
