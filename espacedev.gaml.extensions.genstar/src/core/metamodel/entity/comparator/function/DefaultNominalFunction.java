package core.metamodel.entity.comparator.function;

import core.metamodel.value.IValue;
import core.metamodel.value.categoric.NominalValue;
import core.util.data.GSEnumDataType;

public class DefaultNominalFunction implements IComparatorFunction<NominalValue> {

	public static final String SELF = IComparatorFunction.DEFAULT_TAG+"NOMINAL COMP";
	
	@Override
	public String getName() {
		return SELF;
	}
	
	@Override
	public int compare(IValue v1, IValue v2, IValue empty) {
		int testEmpty = this.testEmpty(v1, v2, empty);
		if(testEmpty == 0) {
			String s1 = v1.getActualValue();
			String s2 = v2.getActualValue();
			return s1.compareTo(s2);
		}
		return testEmpty == EMPTY ? 0 : testEmpty;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Nominal;
	}

}
