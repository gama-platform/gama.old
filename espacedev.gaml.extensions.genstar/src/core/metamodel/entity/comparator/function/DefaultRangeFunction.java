package core.metamodel.entity.comparator.function;

import core.metamodel.value.IValue;
import core.metamodel.value.numeric.RangeValue;
import core.util.data.GSEnumDataType;

public class DefaultRangeFunction implements IComparatorFunction<RangeValue> {

	public static final String SELF = IComparatorFunction.DEFAULT_TAG+"RANGE COMP";
	
	@Override
	public String getName() {
		return SELF;
	}
	
	@Override
	public int compare(IValue v1, IValue v2, IValue empty) {
		int testEmpty = this.testEmpty(v1, v2, empty);
		if(testEmpty == 0) {
			RangeValue r1 = (RangeValue) v1;
			RangeValue r2 = (RangeValue) v2;
			double rd1 = (r1.getBottomBound().doubleValue() + r1.getTopBound().doubleValue()) / 2;
			double rd2 = (r2.getBottomBound().doubleValue() + r2.getTopBound().doubleValue()) / 2;
			return rd1 < rd2 ? -1 : rd1 > rd2 ? 1 : 0;
		}
		return testEmpty == EMPTY ? 0 : testEmpty;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Range;
	}

}
