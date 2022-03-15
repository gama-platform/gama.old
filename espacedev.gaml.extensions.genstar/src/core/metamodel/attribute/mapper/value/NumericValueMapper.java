package core.metamodel.attribute.mapper.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.attribute.MappedAttribute;
import core.metamodel.attribute.mapper.IAttributeMapper;
import core.metamodel.value.IValue;
import core.metamodel.value.categoric.OrderedValue;
import core.metamodel.value.numeric.ContinuousValue;
import core.metamodel.value.numeric.IntegerValue;
import core.metamodel.value.numeric.RangeSpace;
import core.metamodel.value.numeric.RangeValue;
import core.metamodel.value.numeric.RangeValue.RangeBound;
import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;
import core.util.random.GenstarRandomUtils;

/**
 * Map an ordered value space with numerical value, including {@link IntegerValue}, 
 * {@link ContinuousValue} and {@link RangeValue}. One mapper can bind one ordered value
 * with any of the three. 
 * 
 * @author kevinchapuis
 *
 */
public class NumericValueMapper<K extends IValue> implements IAttributeMapper<K, OrderedValue> {
	
	private Map<IValue, OrderedValue> innerMapper;
	
	// Cached mapper with return type
	private boolean upToDate = false;
	private Map<K, OrderedValue> mapper;

	private MappedAttribute<K, OrderedValue> relatedAttribute;

	private static Attribute<ContinuousValue> CA = AttributeFactory.createNIU(ContinuousValue.class);
	private static Attribute<IntegerValue> IA = AttributeFactory.createNIU(IntegerValue.class);
	private static Attribute<RangeValue> RA = AttributeFactory.createNIU(RangeValue.class);
	private static GSDataParser GSDP = new GSDataParser();
	
	public NumericValueMapper() {
		this.innerMapper = new HashMap<>();
		this.mapper = new HashMap<>();
	}
	
	// --------
	
	/**
	 * Add a map between nominal value and range from {@code bValue} to {@code tValue}
	 *  
	 * @param nominal
	 * @param bValue
	 * @param tValue
	 */
	public void add(OrderedValue nominal, Number bValue, Number tValue) {
		innerMapper.put(RA.getValueSpace().proposeValue(bValue.toString()+" : "+tValue.toString()), nominal);
		upToDate = false;
	}
	
	/**
	 * Add a map between nominal value and range from one numerical value and defined lower/upper bound.
	 * The {@link RangeBound} {@code rb} argument describes what should be the other value, 
	 * either {@link RangeBound#LOWER} or {@link RangeBound#UPPER}.
	 * 
	 * @param nominal
	 * @param value
	 * @param rb
	 */
	public void add(OrderedValue nominal, Number value, RangeBound rb) {
		String theString = rb.equals(RangeBound.UPPER) ? 
				value.toString()+" : "+((RangeSpace)RA.getValueSpace()).getMax()
				: ((RangeSpace)RA.getValueSpace()).getMin()+" : "+value.toString();
		innerMapper.put(RA.getValueSpace().proposeValue(theString), nominal);
		upToDate = false;
	}
	
	/**
	 * Add a map between nominal value and one numerical value, either int or double (in fact any floating value)
	 * 
	 * @param nominal
	 * @param value
	 */
	public void add(OrderedValue nominal, Number value) {
		GSEnumDataType type = GSDP.getValueType(value.toString());
		switch (type) {
		case Integer:
			innerMapper.put(IA.getValueSpace().proposeValue(value.toString()), nominal);
			break;
		case Continue:
			innerMapper.put(CA.getValueSpace().proposeValue(value.toString()), nominal);
			break;
		default:
			throw new IllegalArgumentException(value+" cannot be transpose to any numerical value");
		}
		upToDate = false;
	}
	
	@Override
	public boolean add(IValue mapTo, OrderedValue mapWith) {
		if(Stream.of(GSEnumDataType.Continue, GSEnumDataType.Integer, GSEnumDataType.Range)
				.noneMatch(type -> type.equals(mapWith.getValueSpace().getType())))
			return false;
		innerMapper.put(mapTo, mapWith);
		upToDate = false;
		return true;
	}
	
	// --------
	
	/**
	 * 
	 * @param nominal
	 * @return
	 */
	public K getValue(OrderedValue nominal) {
		if(!upToDate)
			this.transposeInnerMapper();
		Optional<Entry<K, OrderedValue>> opt = mapper.entrySet().stream()
				.filter(entry -> entry.getValue().equals(nominal)).findFirst();
		if(opt.isPresent())
			return opt.get().getKey();
		throw new NullPointerException("There is no "+nominal+" value within this mapped attribute ("+this+")");
	}
	
	/**
	 * 
	 * @param nominal
	 * @return
	 */
	public Number getNumericValue(OrderedValue nominal) {
		IValue res = this.getValue(nominal);
		switch (res.getValueSpace().getType()) {
		case Integer:
			return Integer.valueOf(res.getStringValue());
		case Continue:
			return Double.valueOf(res.getStringValue());
		case Range:
			// TODO compute typical standard deviation
			double std = this.getStandardRange();
			RangeValue rv = (RangeValue) res;
			Number lb = rv.getBottomBound();
			Number ub = rv.getTopBound();
			if(lb.intValue() == Integer.MIN_VALUE ||
					lb.doubleValue() == Double.MIN_VALUE)
				lb = ub.doubleValue() - std;
			if(ub.intValue() == Integer.MAX_VALUE ||
					ub.doubleValue() == Double.MAX_VALUE)
				ub = lb.doubleValue() + std;
			return GenstarRandomUtils.rnd(lb, ub);
		default:
			throw new IllegalArgumentException("Cannot get numerical value of "+res);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public OrderedValue getNominal(Number value) {
		Optional<IValue> opt = innerMapper.keySet().stream().filter(k -> this.validate(value, k)).findFirst();
		if(opt.isPresent())
			return innerMapper.get(opt.get());
		throw new NoSuchElementException("There is no relevant numeric value mapper to "+value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public boolean contains(Number value) {
		return innerMapper.values().stream().anyMatch(v -> this.validate(value, v));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<? extends IValue> getMappedValues(IValue value) {
		IValue output = null;
		if(innerMapper.containsKey(value))
			output = innerMapper.get(value);
		else if(innerMapper.values().stream().anyMatch(v -> v.getStringValue().equals(value.getStringValue())))
			output = innerMapper.entrySet().stream()
						.filter(e -> e.getValue().getStringValue().equals(value.getStringValue()))
						.findFirst().get().getKey();
		if(output == null)
			return Collections.emptyList();
		return Collections.singleton(output);
	}
	
	@Override
	public void setRelatedAttribute(MappedAttribute<K, OrderedValue> relatedAttribute) {
		this.relatedAttribute = relatedAttribute;
	}

	@Override
	public MappedAttribute<K, OrderedValue> getRelatedAttribute() {
		return relatedAttribute;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: this method could lead to unstable result
	 */
	@Override
	public Map<Collection<K>, Collection<OrderedValue>> getRawMapper() {
		if(!upToDate)
			this.transposeInnerMapper();
		return mapper.entrySet().stream()
				.collect(Collectors.toMap(
						entry->Collections.singleton(entry.getKey()), 
						entry->Collections.singleton(entry.getValue())));
	}
	
	// ------------- UTILS
	
	/*
	 * 
	 */
	private boolean validate(Number v1, IValue v2) {
		switch (v2.getValueSpace().getType()) {
		case Continue:
			return v1.doubleValue() == Double.valueOf(v2.getStringValue());
		case Integer:
			return v1.intValue() == Integer.valueOf(v2.getStringValue());
		case Range:
			RangeValue rv = (RangeValue)v2;
			return v1.doubleValue() >= rv.getBottomBound().doubleValue()
					&& v1.doubleValue() <= rv.getTopBound().doubleValue();
		default:
			throw new IllegalArgumentException(v2.getValueSpace().getType()
					+" is not an acceptable value type for Numeric Value Mapper");
		}
	}
	
	/*
	 * 
	 */
	private double getStandardRange() {
		GSDataParser gsdp = new GSDataParser();
		List<Double> vals = new ArrayList<>();
		for(IValue v : innerMapper.keySet()) {
			switch (v.getType()) {
			case Range:
				RangeValue rv = (RangeValue) v;
				vals.add(rv.getBottomBound().doubleValue());
				vals.add(rv.getTopBound().doubleValue());
				break;
			default:
				vals.add(gsdp.getDouble(v.getStringValue()));
				break;
			}
		}
		Collections.sort(vals);
		Map<Double, Integer> ranges = new HashMap<>();
		for(int i = 1 ; i < vals.size(); i++) {
			double lRange = vals.get(i) - vals.get(i-1);
			if(ranges.containsKey(lRange))
				ranges.put(lRange, ranges.get(lRange)+1);
			else
				ranges.put(lRange,1);
		}
		double sor = ranges.entrySet().stream()
				.mapToDouble(entry -> entry.getKey()*Math.pow(entry.getValue(),1.5))
				.sum();
		double factor = ranges.values().stream()
				.mapToDouble(v -> Math.pow(v, 1.5)).sum();
		return sor/factor;
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void transposeInnerMapper() {
		Map<K, OrderedValue> theMapper = new HashMap<>();
		GSEnumDataType dataType = innerMapper.keySet().stream()
				.map(IValue::getType).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
			    .entrySet().stream().max(Comparator.comparing(Entry::getValue))
			    .get().getKey();
		switch (dataType) {
		case Continue:
			for(Entry<IValue, OrderedValue> entry : innerMapper.entrySet()) {
				theMapper.put((K)CA.getValueSpace().proposeValue(entry.getKey().getStringValue()), entry.getValue());
			}
			break;
		case Range:
			for(Entry<IValue, OrderedValue> entry : innerMapper.entrySet()) {
				theMapper.put((K)RA.getValueSpace().proposeValue(entry.getKey().getStringValue()), entry.getValue());
			}
			break;
		default:
			for(Entry<IValue, OrderedValue> entry : innerMapper.entrySet()) {
				theMapper.put((K)IA.getValueSpace().proposeValue(entry.getKey().getStringValue()), entry.getValue());
			}
			break;
		}
		
		this.mapper = theMapper;
		this.upToDate = true;
	}
	
}
