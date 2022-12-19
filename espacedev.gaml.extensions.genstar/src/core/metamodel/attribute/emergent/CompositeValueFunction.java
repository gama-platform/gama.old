/*******************************************************************************************************
 *
 * CompositeValueFunction.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.attribute.emergent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.emergent.filter.predicate.GSMatchPredicate;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * The Class CompositeValueFunction.
 *
 * @param <V> the value type
 */
public class CompositeValueFunction<V extends IValue>
		implements IGSValueFunction<IEntity<? extends IAttribute<? extends IValue>>, V> {

	/** The referent. */
	private Attribute<V> referent;

	/** The predicates. */
	private final Map<Collection<GSMatchPredicate<?, ?>>, V> predicates;

	/**
	 * Instantiates a new composite value function.
	 *
	 * @param referent the referent
	 */
	public CompositeValueFunction(final Attribute<V> referent) {
		this.referent = referent;
		this.predicates = new HashMap<>();
		referent.getValueSpace().getValues().stream().forEach(value -> this.predicates.put(new ArrayList<>(), value));
	}

	// Main (Java) Function (Class) contract

	@Override
	public V apply(final IEntity<? extends IAttribute<? extends IValue>> superEntity) {
		return predicates.entrySet().stream()
				.filter(ps -> ps.getKey().stream()
						.allMatch(predicate -> predicate.validate(predicate.getMatchType(), superEntity)))
				.findFirst().get().getValue();
	}

	@Override
	public Collection<Map<IAttribute<? extends IValue>, IValue>>
			reverse(final Map<IAttribute<? extends IValue>, IValue> entities) {

		return null;
	}

	// Other part

	@Override
	public Attribute<V> getReferent() { return referent; }

	@Override
	public void setReferent(final Attribute<V> referent) { this.referent = referent; }

	/**
	 * Add one predicate to be validated for a particular value to occur
	 *
	 * @param transposer
	 * @param relatedValue
	 */
	public void addPredicate(final GSMatchPredicate<?, ?> transposer, final V relatedValue) {
		Optional<Collection<GSMatchPredicate<?, ?>>> opt = predicates.entrySet().stream()
				.filter(entry -> entry.getValue().equals(relatedValue)).map(Entry::getKey).findFirst();
		if (opt.isPresent()) {
			opt.get().add(transposer);
		} else {
			this.predicates.put(Stream.of(transposer).collect(Collectors.toList()), relatedValue);
		}
	}

}
