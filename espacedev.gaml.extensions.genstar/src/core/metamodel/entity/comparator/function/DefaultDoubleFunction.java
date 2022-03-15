package core.metamodel.entity.comparator.function;

import core.metamodel.value.IValue;
import core.metamodel.value.numeric.ContinuousValue;
import core.util.data.GSEnumDataType;

public class DefaultDoubleFunction implements IComparatorFunction<ContinuousValue> {

	public static final String SELF = IComparatorFunction.DEFAULT_TAG+"CONTINUOUS COMP";
	
	@Override
	public String getName() {
		return SELF;
	}
	
	@Override
	public int compare(IValue v1, IValue v2, IValue empty) {
		int testEmpty = this.testEmpty(v1, v2, empty);
		if(testEmpty == 0) {
			double d1 = v1.getActualValue();
			double d2 = v2.getActualValue();
			return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
		}
		return testEmpty == EMPTY ? 0 : testEmpty;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Continue;
	}

}
