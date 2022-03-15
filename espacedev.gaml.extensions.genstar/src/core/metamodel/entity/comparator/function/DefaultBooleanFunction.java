package core.metamodel.entity.comparator.function;

import core.metamodel.value.IValue;
import core.metamodel.value.binary.BooleanValue;
import core.util.data.GSEnumDataType;

public class DefaultBooleanFunction implements IComparatorFunction<BooleanValue> {

	public static final String SELF = IComparatorFunction.DEFAULT_TAG+"BOOLEAN COMP";

	@Override
	public int compare(IValue v1, IValue v2, IValue empty) {
		int testEmpty = this.testEmpty(v1, v2, empty);
		if(testEmpty == 0) {
			boolean bool1 = v1.getActualValue();
			boolean bool2 = v2.getActualValue();
			return (bool1 && bool2) || (!bool1 && !bool2) ? 0 : bool1 && !bool2 ? -1 : 1;
		}
		return testEmpty == EMPTY ? 0 : testEmpty;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Boolean;
	}
	
	@Override
	public String getName() {
		return SELF;
	}


}
