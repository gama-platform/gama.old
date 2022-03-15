package core.metamodel.attribute.emergent.aggregator;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.value.IValueSpace;
import core.metamodel.value.numeric.RangeSpace;
import core.metamodel.value.numeric.RangeValue;

@JsonTypeName(RangeValueAggregator.SELF)
public class RangeValueAggregator implements IAggregatorValueFunction<RangeValue> {

	public static final String SELF = IAggregatorValueFunction.DEFAULT_TAG+"RANGE AGGREGATOR";
	private static final RangeValueAggregator INSTANCE = new RangeValueAggregator();
	
	private RangeValueAggregator() {}

	public static RangeValueAggregator getInstance() {
		return RangeValueAggregator.INSTANCE;
	}
	
	@Override
	public RangeValue aggregate(Collection<RangeValue> values, IValueSpace<RangeValue> valueSpace) {
		Number bottom = values.stream().map(r -> r.getBottomBound())
				.reduce(0, (b1, b2) -> this.add(b1, b2));
		Number top = values.stream().map(r -> r.getTopBound())
				.reduce(0, (b1, b2) -> this.add(b1, b2));
		return valueSpace.getInstanceValue(
					((RangeSpace)valueSpace).getRangeTemplate()
				.getMiddleTemplate(bottom, top));
	}
	
	// ------------ UTILITIES ------------ // 
	
	private Number add(Number n1, Number n2) {
		if(n1.getClass().equals(Integer.class)
				&& n2.getClass().equals(Integer.class))
			return n1.intValue() + n2.intValue();
		return n1.doubleValue() + n2.doubleValue();		
	}

	@Override
	public String getType() {
		return SELF;
	}

	
}
