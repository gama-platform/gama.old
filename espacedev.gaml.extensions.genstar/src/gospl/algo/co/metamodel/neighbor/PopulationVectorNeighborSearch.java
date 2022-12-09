/*******************************************************************************************************
 *
 * PopulationVectorNeighborSearch.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel.neighbor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.comparator.HammingEntityComparator;
import core.metamodel.value.IValue;
import gospl.GosplEntity;
import gospl.GosplPopulation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.validation.GosplIndicatorFactory;

/**
 * The matrix predicate represent TAE on each combination of values (e.g. {age=1;gender=male} = -23 means, that there is
 * 23 lacking male of 1 years old)
 *
 * TODO : make test, it seems to have a bug
 *
 * @author kevinchapuis
 *
 */
public class PopulationVectorNeighborSearch implements
		IPopulationNeighborSearch<GosplPopulation, INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> {

	/** The sample. */
	private IPopulation<ADemoEntity, Attribute<? extends IValue>> sample;

	/** The objectives. */
	private Collection<AFullNDimensionalMatrix<Integer>> objectives;

	/** The predicates. */
	private Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> predicates;

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> predicate, final int size) {
		return this.getPairwisedEntities(population, predicate, size, false);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population, final int size,
			final boolean childSizeConsistant) {
		return this.getPairwisedEntities(population, this.predicates.iterator().next(), size, false);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> predicate, int size,
			final boolean childSizeConsistant) {

		if (predicate.getDimensions().stream()
				.anyMatch(dim -> population.getPopulationAttributes().stream().noneMatch(att -> att.isLinked(dim))))
			throw new IllegalArgumentException("Population must share attributes ("
					+ population.getPopulationAttributes().stream().map(Attribute::getAttributeName)
							.collect(Collectors.joining("; "))
					+ ") with predicat matrix: " + predicate.getDimensions().stream().map(Attribute::getAttributeName)
							.collect(Collectors.joining("; ")));

		// Store a map of related attributes to transpose pop att > predicate att
		Map<Attribute<? extends IValue>, Attribute<? extends IValue>> predicateToPopAttributes = new HashMap<>();
		for (Attribute<? extends IValue> predicAtt : predicate.getDimensions()) {
			if (population.getPopulationAttributes().contains(predicAtt)) {
				predicateToPopAttributes.put(predicAtt, predicAtt);
			} else {
				predicateToPopAttributes.put(predicAtt, population.getPopulationAttributes().stream()
						.filter(popAtt -> popAtt.isLinked(predicAtt)).findAny().orElse(null));
			}
		}

		Map<ADemoEntity, ADemoEntity> pair = new HashMap<>();

		LinkedHashMap<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Integer>> sortedPredicates =
				new LinkedHashMap<>();
		predicate.getMatrix().entrySet().stream().sorted(Comparator.comparing(Entry::getValue))
				.forEachOrdered(e -> sortedPredicates.put(e.getKey(), e.getValue()));

		List<ACoordinate<Attribute<? extends IValue>, IValue>> toRemove = new ArrayList<>(sortedPredicates.keySet());
		List<ACoordinate<Attribute<? extends IValue>, IValue>> toAdd = new ArrayList<>(toRemove);
		Collections.reverse(toRemove);

		List<ADemoEntity> entitiesToRemove = null;
		List<ADemoEntity> entitiesToAdd = null;

		// If there is mapped attribute (hell damn, its a fu**ing bad idea)
		if (!predicateToPopAttributes.isEmpty()) {
			entitiesToRemove = new ArrayList<>();
			for (ACoordinate<Attribute<? extends IValue>, IValue> coord : toRemove) {
				Set<IValue> vals = coord.getMap().entrySet().stream()
						.flatMap(e -> predicateToPopAttributes.containsKey(e.getKey())
								? e.getKey().findMappedAttributeValues(e.getValue()).stream() : Stream.of(e.getValue()))
						.collect(Collectors.toSet());
				entitiesToRemove.add(population.stream()
						.sorted(new HammingEntityComparator(vals.toArray(new IValue[vals.size()]))).findFirst().get());
			}
			entitiesToAdd = new ArrayList<>();
			for (ACoordinate<Attribute<? extends IValue>, IValue> coord : toAdd) {
				Set<IValue> vals = coord.getMap().entrySet().stream()
						.flatMap(e -> predicateToPopAttributes.containsKey(e.getKey())
								? e.getKey().findMappedAttributeValues(e.getValue()).stream() : Stream.of(e.getValue()))
						.collect(Collectors.toSet());
				entitiesToAdd.add(population.stream()
						.sorted(new HammingEntityComparator(vals.toArray(new IValue[vals.size()]))).findFirst().get());
			}
		} else {
			entitiesToRemove = toRemove.stream()
					.map(coord -> population.stream()
							.sorted(new HammingEntityComparator(new GosplEntity(coord.getMap()))).findFirst().get())
					.toList();
			entitiesToAdd = toAdd.stream()
					.map(coord -> this.sample.stream()
							.sorted(new HammingEntityComparator(new GosplEntity(coord.getMap()))).findFirst().get())
					.toList();
		}

		int rSize = entitiesToRemove.size();
		int aSize = entitiesToAdd.size();

		size = size > rSize || size > aSize ? rSize > aSize ? aSize : rSize : size;

		for (int n = 0; n < size; n++) {
			ADemoEntity eToRemove = entitiesToRemove.get(n);
			ADemoEntity eToAdd = entitiesToAdd.get(n);
			if (childSizeConsistant) {
				eToAdd = entitiesToAdd.stream().filter(e -> e.getChildren().size() == eToRemove.getChildren().size())
						.findFirst().orElse(eToAdd);
			}
			pair.put(eToRemove, eToAdd);
		}

		return pair;
	}

	@Override
	public Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> getPredicates() {
		return Collections.unmodifiableCollection(predicates);
	}

	@Override
	public void setPredicates(
			final Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> predicates) {
		if (predicates.isEmpty()) throw new IllegalArgumentException("Predicat cannot be empty");
		this.predicates = predicates;
	}

	@Override
	public void updatePredicates(final GosplPopulation population) {
		Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> obj = objectives.stream()
				.filter(mat -> mat.getDimensions().stream().anyMatch(
						att -> population.getPopulationAttributes().stream().anyMatch(dim -> dim.isLinked(att))))
				.collect(Collectors.toSet());
		this.setPredicates(Collections.singleton(
				GosplIndicatorFactory.getFactory().getAbsoluteErrors(population, predicates.iterator().next(), obj)));
	}

	@Override
	public void setSample(final GosplPopulation sample) { this.sample = sample; }

}
