package gospl.sampler.sr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandom;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;

/******************************************************************************
 * File: AliasMethod.java Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of the alias method implemented using Vose's algorithm. The alias method allows for efficient
 * sampling of random values from a discrete probability distribution (i.e. rolling a loaded die) in O(1) time each
 * after O(n) preprocessing time.
 *
 * For a complete writeup on the alias method, including the intuition and important proofs, please see the article
 * "Darts, Dice, and Coins: Smpling from a Discrete Distribution" at
 *
 * http://www.keithschwarz.com/darts-dice-coins/
 *
 */
public class GosplAliasSampler implements IDistributionSampler {

	private List<ACoordinate<Attribute<? extends IValue>, IValue>> indexedKey;
	private List<Double> initProba;

	/* The probability and alias tables. */
	private int[] alias;
	private double[] probability;

	// -------------------- setup methods -------------------- //

	@Override
	public void setDistribution(final AFullNDimensionalMatrix<Double> distribution) {
		if (distribution == null) throw new NullPointerException();
		if (distribution.getMatrix().isEmpty()) throw new IllegalArgumentException(
				"Cannot setup a sampler with an empty distribution matrix " + distribution);

		Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Double>> orderedDistribution =
				distribution.getOrderedMatrix();

		this.indexedKey = new ArrayList<>(orderedDistribution.keySet());
		this.initProba = orderedDistribution.values().stream().map(AControl::getValue).toList();

		/* Allocate space for the probability and alias tables. */
		probability = new double[distribution.size()];
		alias = new int[distribution.size()];

		/* Compute the average probability and cache it for later use. */
		final double average = 1.0 / distribution.size();

		/*
		 * Make a copy of the probabilities list, since we will be making changes to it.
		 */
		List<Double> probabilities = new ArrayList<>(initProba);

		/* Create two stacks to act as worklists as we populate the tables. */
		Deque<Integer> small = new ArrayDeque<>();
		Deque<Integer> large = new ArrayDeque<>();

		/* Populate the stacks with the input probabilities. */
		for (int i = 0; i < probabilities.size(); ++i) {
			/*
			 * If the probability is below the average probability, then we add it to the small list; otherwise we add
			 * it to the large list.
			 */
			if (probabilities.get(i) >= average) {
				large.add(i);
			} else {
				small.add(i);
			}
		}

		/*
		 * As a note: in the mathematical specification of the algorithm, we will always exhaust the small list before
		 * the big list. However, due to floating point inaccuracies, this is not necessarily true. Consequently, this
		 * inner loop (which tries to pair small and large elements) will have to check that both lists aren't empty.
		 */
		while (!small.isEmpty() && !large.isEmpty()) {
			/* Get the index of the small and the large probabilities. */
			int less = small.removeLast();
			int more = large.removeLast();

			/*
			 * These probabilities have not yet been scaled up to be such that 1/n is given weight 1.0. We do this here
			 * instead.
			 */
			probability[less] = probabilities.get(less) * probabilities.size();
			alias[less] = more;

			/*
			 * Decrease the probability of the larger one by the appropriate amount.
			 */
			probabilities.set(more, probabilities.get(more) + probabilities.get(less) - average);

			/*
			 * If the new probability is less than the average, add it into the small list; otherwise add it to the
			 * large list.
			 */
			if (probabilities.get(more) >= 1.0 / probabilities.size()) {
				large.add(more);
			} else {
				small.add(more);
			}
		}

		/*
		 * At this point, everything is in one list, which means that the remaining probabilities should all be 1/n.
		 * Based on this, set them appropriately. Due to numerical issues, we can't be sure which stack will hold the
		 * entries, so we empty both.
		 */
		while (!small.isEmpty()) { probability[small.removeLast()] = 1.0; }
		while (!large.isEmpty()) { probability[large.removeLast()] = 1.0; }
	}

	// -------------------- main contract -------------------- //

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: make use of {@link Stream#parallel()}
	 */
	@Override
	public final Collection<ACoordinate<Attribute<? extends IValue>, IValue>> draw(final int numberOfDraw) {
		return IntStream.range(0, numberOfDraw).mapToObj(i -> draw()).toList();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *
	 * @return A random value sampled from the underlying distribution.
	 */
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> draw() {
		/* Generate a fair die roll to determine which column to inspect. */
		int column = GenstarRandom.getInstance().nextInt(probability.length);

		/* Generate a biased coin toss to determine which option to pick. */
		boolean coinToss = GenstarRandom.getInstance().nextDouble() < probability[column];

		return indexedKey.get(coinToss ? column : alias[column]);
	}

	@Override
	public String toCsv(final String csvSeparator) {
		List<Attribute<? extends IValue>> attributs = new ArrayList<>(indexedKey.parallelStream()
				.flatMap(coord -> coord.getDimensions().stream()).collect(Collectors.toSet()));
		StringBuilder s = new StringBuilder().append(String.join(csvSeparator,
				attributs.stream().map(Attribute::getAttributeName).toList()));
		s.append("; Probability\n");
		for (ACoordinate<Attribute<? extends IValue>, IValue> coord : indexedKey) {
			String line = "";
			for (Attribute<? extends IValue> att : attributs) {
				if (coord.getDimensions().contains(att)) {
					if (line.isEmpty()) {
						s.append(csvSeparator).append(coord.getMap().get(att));
					} else {
						s.append(csvSeparator).append(coord.getMap().get(att).getStringValue());
					}
				} else if (line.isEmpty()) {
					s.append(" ");
				} else {
					s.append(csvSeparator).append(" ");
				}
			}
			s.append(line).append(csvSeparator).append(initProba.get(indexedKey.indexOf(coord))).append("\n");
		}
		return s.toString();
	}

}
