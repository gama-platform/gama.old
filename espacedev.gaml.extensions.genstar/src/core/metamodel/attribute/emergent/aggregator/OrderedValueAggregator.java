package core.metamodel.attribute.emergent.aggregator;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.value.IValueSpace;
import core.metamodel.value.categoric.OrderedSpace;
import core.metamodel.value.categoric.OrderedValue;

@JsonTypeName(OrderedValueAggregator.SELF)
public class OrderedValueAggregator implements IAggregatorValueFunction<OrderedValue> {

	public static final String SELF = IAggregatorValueFunction.DEFAULT_TAG+"ORDERED VALUE AGGREGATOR";
	private static final OrderedValueAggregator INSTANCE = new OrderedValueAggregator();
	
	private OrderedValueAggregator() {}
	
	public static OrderedValueAggregator getInstance() {
		return INSTANCE;
	}
	
	@Override
	public OrderedValue aggregate(Collection<OrderedValue> values, IValueSpace<OrderedValue> valueSpace) {
		return ((OrderedSpace)valueSpace).addValue(this.getAggregate(values), 
				values.stream().map(v -> v.getStringValue()).collect(Collectors.joining(this.getDefaultCharConcat())));
	}
	
	@Override
	public String getType() {
		return SELF;
	}
	
	/*
	 * 
	 */
	private double getAggregate(Collection<OrderedValue> values) {
		double mean = values.stream().mapToDouble(v -> v.getOrder().doubleValue()).sum() / values.size();
		return Math.floor(mean) + (mean - Math.floor(mean) + 
				Double.valueOf("0."+values.stream().map(v -> 
					(int) Math.round(v.getOrder().doubleValue())).sorted(
							new Comparator<Integer>() {@Override
								public int compare(Integer o1, Integer o2) {return o1 > o2 ? -1 : o1 < o2 ? 1 : 0;}
							})
						.map(i -> i.toString()).collect(Collectors.joining())));
	}
	
}
