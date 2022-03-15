package core.metamodel.attribute.emergent.aggregator;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.value.IValueSpace;
import core.metamodel.value.binary.BooleanValue;

@JsonTypeName(BooleanValueAggregator.SELF)
public class BooleanValueAggregator implements IAggregatorValueFunction<BooleanValue> {

	public static final String SELF = IAggregatorValueFunction.DEFAULT_TAG+"BOOL AGGREGATOR";
	private static final BooleanValueAggregator INSTANCE = new BooleanValueAggregator();
	
	private BooleanValueAggregator() {}
	
	@JsonCreator
	public static BooleanValueAggregator getInstance() {
		return INSTANCE;
	}
	
	public static enum BooleanAggregationStyle {
		MOST, ALL, ATLEASTONE;
		public static BooleanAggregationStyle getDefault() {return ALL;}
	}

	private BooleanAggregationStyle boolAgg = BooleanAggregationStyle.getDefault();
	
	/**
	 * Set boolean style aggregation
	 * @see BooleanAggregationStyle
	 * @param boolAgg
	 */
	public void setBoolAggregatorStyle(BooleanAggregationStyle boolAgg) {
		this.boolAgg = boolAgg;
	}
	
	@Override
	public BooleanValue aggregate(Collection<BooleanValue> values, IValueSpace<BooleanValue> spaceValue) {
		switch (boolAgg) {
		case MOST:
			return spaceValue.getValue(Boolean.toString(values.stream()
					.filter(v -> v.getActualValue()).count() >= values.size() / 2d));
		case ATLEASTONE:
			return spaceValue.getValue(Boolean
					.toString(values.stream().anyMatch(v -> v.getActualValue())));
		default:
			return spaceValue.getValue(Boolean
					.toString(values.stream().anyMatch(v -> v.getActualValue().equals(Boolean.FALSE))));
		}
	}

	@Override
	public String getType() {
		return SELF;
	}
	
}
