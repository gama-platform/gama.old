package core.metamodel.entity.comparator.function;

import core.metamodel.value.IValue;
import core.metamodel.value.categoric.OrderedSpace;
import core.metamodel.value.categoric.OrderedValue;
import core.util.data.GSEnumDataType;

public class DefaultOrderedValueFunction implements IComparatorFunction<OrderedValue> {

	public static final String SELF = IComparatorFunction.DEFAULT_TAG+"OREDRED VALUE COMP";
	
	@Override
	public String getName() {
		return SELF;
	}
	
	@Override
	public int compare(IValue v1, IValue v2, IValue empty) {
		int testEmpty = this.testEmpty(v1, v2, empty);
		if(testEmpty == 0)
			return ((OrderedSpace) empty.getValueSpace())
					.compare((OrderedValue)v1, (OrderedValue)v2);
		return testEmpty == EMPTY ? 0 : testEmpty;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Order;
	}

}
