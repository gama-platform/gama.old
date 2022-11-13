package core.metamodel.value.numeric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.metamodel.value.numeric.RangeValue.RangeBound;
import core.metamodel.value.numeric.template.GSRangeTemplate;
import core.util.data.GSDataParser;
import core.util.data.GSDataParser.NumMatcher;
import core.util.exception.GSIllegalRangedData;
import core.util.data.GSEnumDataType;
import core.util.random.GenstarRandomUtils;

/**
 * Encapsulate pair of number that represents bottom and top value of a range. It also
 * provide a template reader to convert range value to original string and back to range value.
 * 
 * WARNING: when lower and upper bounds are not specified, max and min integer value are used
 * 
 * @see GSRangeTemplate
 * 
 * @author kevinchapuis
 *
 */
public class RangeSpace implements IValueSpace<RangeValue> {
	
	private static GSDataParser gsdp = new GSDataParser();
	
	private IAttribute<RangeValue> attribute;
	
	private GSRangeTemplate rt;
	private Number min, max;

	private List<RangeValue> values;
	private RangeValue emptyValue;
	private Set<String> excludedValues;
	
	/**
	 * Facilitates the costly retrieval of the value for a textual value
	 */
	private Map<String,RangeValue> textual2valueCached = new HashMap<>();
	
	/**
	 * 
	 * @param attribute
	 * @param rt
	 */
	public RangeSpace(IAttribute<RangeValue> attribute, GSRangeTemplate rt){
		this(attribute, rt, rt.getTheoreticalMin(), rt.getTheoreticalMax());
	}
	
	public RangeSpace(IAttribute<RangeValue> attribute, List<String> ranges,
			Number minValue, Number maxValue) throws GSIllegalRangedData{
		this(attribute, gsdp.getRangeTemplate(ranges, GSDataParser.DEFAULT_NUM_MATCH, NumMatcher.getDefault()),
				minValue, maxValue);
	}
	
	public RangeSpace(IAttribute<RangeValue> attribute, GSRangeTemplate rt, 
			Number minValue, Number maxValue) {
		this.attribute = attribute;
		this.rt = rt;
		this.min = minValue;
		this.max = maxValue;
		this.values = new ArrayList<>();
		this.excludedValues = new HashSet<>();
		this.emptyValue = new RangeValue(this, Double.NaN, Double.NaN);
	}
	
	// ------------------------------------------------------- //
	
	/**
	 * Return the range formatter that is able to transpose range value to string and way back
	 * 
	 * @return
	 */
	public GSRangeTemplate getRangeTemplate(){
		return rt;
	}
	
	@Override
	public RangeValue getInstanceValue(String value) {
		if(!rt.isValideRangeCandidate(value))
			throw new IllegalArgumentException("The string value "+value+" does not fit defined "
					+ "range "+rt);
		
		List<Number> currentVal = null;
		currentVal = gsdp.getNumbers(value, rt.getNumberMatcher());
		if(currentVal.stream().anyMatch(d -> d.doubleValue() < min.doubleValue()) || 
				currentVal.stream().anyMatch(d -> d.doubleValue() > max.doubleValue()))
			throw new IllegalArgumentException("Proposed values "+value+" are "
					+ (currentVal.stream().anyMatch(d -> d.doubleValue() < min.doubleValue()) ? "below" : "beyond") + " given bound ("
							+ (currentVal.stream().anyMatch(d -> d.doubleValue() < min.doubleValue()) ? min : max) + ")");
		
		return currentVal.size() == 1 ? 
				(rt.getBottomTemplate(currentVal.get(0)).equals(value) ? 
						new RangeValue(this, currentVal.get(0), RangeBound.LOWER) :
							new RangeValue(this, currentVal.get(0), RangeBound.UPPER)) :
			new RangeValue(this, currentVal.get(0), currentVal.get(1));
	}
	
	@Override
	public RangeValue proposeValue(String value) {
		return getInstanceValue(value);
	}
	
	// -------------------- SETTERS & GETTER CAPACITIES -------------------- //

	
	@Override
	public RangeValue addValue(String value) throws IllegalArgumentException {
		if(excludedValues.contains(value))
			return this.getEmptyValue();
		RangeValue iv = null;
		try {
			iv = getValue(value);
		} catch (NullPointerException e) {
			// create the value on the fly
			iv = this.getInstanceValue(value);
			this.values.add(iv);
			this.textual2valueCached.put(iv.getStringValue(), iv);
		}
		return iv;
	}	
	
	@Override
	public RangeValue getValue(String value) throws NullPointerException {
		
		// quick version
		RangeValue val = textual2valueCached.get(value);
		if (val != null)
			return val;
		
		// search for the string value 
		Optional<RangeValue> opValue = values.stream()
				.filter(v -> v.getStringValue().equals(value)).findAny();
		if(opValue.isPresent()) {
			val = opValue.get();
			textual2valueCached.put(value, val);
			return val;
		}
		opValue = values.stream()
				.filter(v -> v.getActualValue().toString().equals(value)).findAny();
		if(opValue.isPresent()) {
			val = opValue.get();
			textual2valueCached.put(value, val);
			return val;
		}
		
		throw new NullPointerException("The string value \""+value+"\" is not contained "
				+ "in the value space "+this.toPrettyString());
	}
	
	@Override
	public Set<RangeValue> getValues(){
		return new HashSet<>(values);
	}
	
	@Override
	public boolean contains(IValue value) {
		if(!value.getClass().equals(RangeValue.class))
			return false;
		return values.contains(value);
	}
	
	@Override
	public boolean contains(String valueStr) {
		// returns true if we have a value for this string
		// relies on the efficient cache which manages the getValue
		return textual2valueCached.get(valueStr) != null;
	}
	
	@Override
	public boolean containsAllLabels(Collection<String> valuesStr) {
		return this.values
				.stream()
				.allMatch(val -> valuesStr.contains(val.getStringValue()));
	}
	
	@Override
	public RangeValue getEmptyValue() {
		return emptyValue;
	}

	@Override
	public void setEmptyValue(String value) {
		if(rt.isValideRangeCandidate(value)){
			try {
				getValue(value);
			} catch (Exception e) {
				List<Double> currentVal = null;
				try {
					currentVal = gsdp.getRangedDoubleData(value, rt.getNumberMatcher());
				} catch (GSIllegalRangedData e1) {
					
					throw new IllegalArgumentException("SHOULD NOT HAPPEN");
				}
				this.emptyValue = currentVal.size() == 1 ? 
						(rt.getBottomTemplate(currentVal.get(0)).equals(value) ? 
								new RangeValue(this, currentVal.get(0), RangeBound.LOWER) :
									new RangeValue(this, currentVal.get(0), RangeBound.UPPER)) :
					new RangeValue(this, currentVal.get(0), currentVal.get(1));
			}
		}
	}
	
	@Override
	public void addExceludedValue(String value) {
		this.excludedValues.add(value);
	}
	
	@Override
	public boolean isValidCandidate(String value){
		return rt.isValideRangeCandidate(value);
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Range;
	}

	@Override
	public IAttribute<RangeValue> getAttribute() {
		return this.attribute;
	}
	
	@Override
	public Class<RangeValue> getTypeClass() {
		return RangeValue.class;
	}
	
	/**
	 * Get the minimum value
	 * @return Number : minimum
	 */
	public Number getMin() {return min;}
	
	/**
	 * Get the maximum value
	 * @return Number : maximum
	 */
	public Number getMax() {return max;}
	
	/**
	 * Get all the Number inside every stored ranges
	 * @return a list of number
	 */
	public List<Number> getNumbers() {
		return values.stream()
				.flatMap(range -> Stream.of(range.getBottomBound(),range.getTopBound()))
				.toList();
	}
	
	/**
	 * Get the usual difference between upper and lower range values in the entire range set. If the range is not uniform, then return NaN
	 * @return Number
	 */
	public Double getUsualRange() {
		Set<Double> regRange = values.stream().map(range -> range.getTopBound().doubleValue() - range.getBottomBound().doubleValue())
				.collect(Collectors.toSet());
		if (regRange.size() > 1) { return Double.NaN; }
		else { return regRange.iterator().next(); }
	}
	
	/**
	 * Deal with ranges that share a common bound, e.g. ("below 5 years old" and "5 to 9 years old")
	 */
	public void consolidateRanges() {
		
		Map<Number, Long> the_count = getNumbers().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		Double usualRange = this.getUsualRange();
		
		for(Number conflictingNumber : the_count.keySet().stream()
				.filter(num -> the_count.get(num) > 1)
				.toList()) {
			RangeValue conflictingRange;
			List<RangeValue> conflictingRanges = values.stream()
					.filter(v -> v.getBottomBound().doubleValue() == conflictingNumber.doubleValue() ||
						v.getTopBound().doubleValue() == conflictingNumber.doubleValue())
					.toList();
			double modifier = 1;
			if (usualRange==Double.NaN) {
				conflictingRange = conflictingRanges.stream()
						.filter(r -> r.getTopBound().doubleValue() - r.getBottomBound().doubleValue() > usualRange)
						.findFirst().get();
				modifier = conflictingRange.getTopBound().doubleValue() - conflictingRange.getBottomBound().doubleValue() - usualRange.doubleValue();
				
			} else {
				conflictingRange = GenstarRandomUtils.oneOf(conflictingRanges);
			}
			if (conflictingRange.getBottomBound().doubleValue() == conflictingNumber.doubleValue())
				conflictingRange.setBottomBound(conflictingRange.getBottomBound().doubleValue() + modifier);
			else
				conflictingRange.setTopBound(conflictingRange.getTopBound().doubleValue() - modifier);
		}
			

	}
	
	// ------------- RANDOM VALUE IN RANGE --------------- //
	
	public Number getRandom(RangeValue rv) {
		double std = this.getStandardRange();
		Number lb = rv.getBottomBound();
		Number ub = rv.getTopBound();
		if(lb.intValue() == Integer.MIN_VALUE)
			lb = (int) (ub.intValue() - Math.round(Math.round(std)));
		else if(lb.doubleValue() == Double.MIN_VALUE)
			lb = ub.doubleValue() - std;
		else if(ub.intValue() == Integer.MAX_VALUE)
			ub = (int) (lb.intValue() + Math.round(Math.round(std)));
		else if(ub.doubleValue() == Double.MAX_VALUE)
			ub = lb.doubleValue() + std;
		return GenstarRandomUtils.rnd(lb, ub);
	}
	
	/*
	 * 
	 */
	private double getStandardRange() {
		List<Double> vals = values.stream()
				.flatMap(v -> Stream.of(v.getBottomBound(), v.getTopBound()))
				.mapToDouble(v -> v.doubleValue()).boxed()
				.toList();
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
	
	// ----------------------------------------------- //
	
	@Override
	public int hashCode() {
		return this.getHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return isEqual(obj);
	}
	
	@Override
	public String toString() {
		return this.toPrettyString();
	}

	@Override
	public String toPrettyString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(this.getType().toString()).append("] ");
		sb.append(
				values.stream()
					.map(r -> (r.getActualValue() == null ? "": r.getActualValue().toString() + ":") + r.getStringValue())
					.collect(Collectors.joining(","))
					);
		return sb.toString();
		
		
	}

	@Override
	public IValueSpace<RangeValue> clone(IAttribute<RangeValue> newReferent) {
		return new RangeSpace(newReferent, getRangeTemplate(), getMin(), getMax());
	}
	
}
