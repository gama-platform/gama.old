package core.metamodel.entity.comparator.function;

import core.metamodel.value.IValue;
import core.metamodel.value.numeric.IntegerValue;
import core.util.data.GSEnumDataType;

public class DefaultIntegerFunction implements IComparatorFunction<IntegerValue> {

	public static final String SELF = IComparatorFunction.DEFAULT_TAG+"INTEGER COMP";
	
	@Override
	public String getName() {
		return SELF;
	}
	
	
	@Override
	public int compare(IValue v1, IValue v2, IValue empty) {
		int testEmpty = this.testEmpty(v1, v2, empty);
		if(testEmpty == 0) {
			int int1 = v1.getActualValue();
			int int2 = v2.getActualValue();
			return int1 < int2 ? -1 : int1 > int2 ? 1 : 0;
		}
		return testEmpty == EMPTY ? 0 : testEmpty;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Integer;
	}

}
