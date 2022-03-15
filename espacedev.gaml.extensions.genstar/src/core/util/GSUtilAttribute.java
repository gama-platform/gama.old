package core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;

public class GSUtilAttribute {
	
	// TODO Optimize
	public static <A extends IAttribute<? extends IValue>> Collection<Map<A, IValue>> getValuesCombination(
			Collection<A> attributes){
		if(attributes.size() < 1)
			throw new IllegalArgumentException("You need at least one attribute to define value combination");
		List<A> tmpAttribtues = new ArrayList<>(attributes);
		Set<Map<A, IValue>> combination = new HashSet<>();
		A att = tmpAttribtues.remove(0);
		for(IValue val : att.getValueSpace().getValues()) {
			Map<A, IValue> map = new HashMap<>();
			map.put(att, val);
			combination.add(map);
		}
		for(A attribute : tmpAttribtues) {
			Set<Map<A, IValue>> newCombination = new HashSet<>();
			for(IValue value : attribute.getValueSpace().getValues()) {
				for(Map<A, IValue> comb : combination) {
					Map<A, IValue> newComb = new HashMap<>(comb);
					newComb.put(attribute, value);
					newCombination.add(newComb);
				}
			}
			combination = newCombination;
		}
		return combination;
	}
	
	public static <A extends IAttribute<? extends IValue>> Collection<Map<A, IValue>> getValuesCombination(
			@SuppressWarnings("unchecked") A... attributes){
		return getValuesCombination(Arrays.asList(attributes));
	}
	
	public static Collection<IValue> getIValues(IAttribute<? extends IValue> attribute, String... values){
		return Stream.of(values).map(val -> attribute.getValueSpace().getValue(val)).collect(Collectors.toSet());
	}
	
}
