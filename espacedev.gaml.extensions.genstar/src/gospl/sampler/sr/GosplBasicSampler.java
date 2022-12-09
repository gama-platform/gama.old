/*******************************************************************************************************
 *
 * GosplBasicSampler.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.sampler.sr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.random.roulette.ARouletteWheelSelection;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;

/**
 * Basic Monte Carlo sampler based on {@link ARouletteWheelSelection} implementation using a gospl distribution, i.e.
 * {@link AFullNDimensionalMatrix} and drawing {@link ACoordinate} from it
 *
 * @author kevinchapuis
 *
 */
public class GosplBasicSampler implements IDistributionSampler {

	/** The sampler. */
	ARouletteWheelSelection<Double, ACoordinate<Attribute<? extends IValue>, IValue>> sampler;

	/** The epsilon. */
	private final double EPSILON = Math.pow(10, -6);

	// -------------------- setup methods -------------------- //

	@Override
	public void setDistribution(final AFullNDimensionalMatrix<Double> distribution) {
		if (distribution == null) throw new NullPointerException();
		if (distribution.getMatrix().isEmpty()) throw new IllegalArgumentException(
				"Cannot setup a sampler with an empty distribution matrix " + distribution);

		Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Double>> mat = distribution.getMatrix();

		List<ACoordinate<Attribute<? extends IValue>, IValue>> keys = new ArrayList<>(mat.keySet());
		List<Double> probabilities = new ArrayList<>(distribution.size());

		double sumOfProbabilities = 0d;
		for (ACoordinate<Attribute<? extends IValue>, IValue> key : keys) {
			double proba = mat.get(key).getValue();
			sumOfProbabilities += proba;
			probabilities.add(proba);
		}

		if (Math.abs(sumOfProbabilities - 1d) > EPSILON) // move to a BigDecimal distribution requirement
			throw new IllegalArgumentException(
					"Sum of probabilities for this sampler is not equal to 1 (SOP = " + sumOfProbabilities + ")");

		sampler = RouletteWheelSelectionFactory.getRouletteWheel(probabilities, keys);
	}

	// -------------------- main contract -------------------- //

	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> draw() {
		return sampler.drawObject();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: make use of {@link Stream#parallel()}
	 */
	@Override
	public final Collection<ACoordinate<Attribute<? extends IValue>, IValue>> draw(final int numberOfDraw) {
		return IntStream.range(0, numberOfDraw).mapToObj(i -> draw()).toList();
	}

	// -------------------- utility -------------------- //

	@Override
	public String toCsv(final String csvSeparator) {
		List<Attribute<? extends IValue>> attributs = new ArrayList<>(sampler.getKeys().parallelStream()
				.flatMap(coord -> coord.getDimensions().stream()).collect(Collectors.toSet()));
		StringBuilder s = new StringBuilder("Basic sampler: ").append(sampler.getKeys().size())
				.append(" discret probabilities\n");
		s.append(String.join(csvSeparator, attributs.stream().map(Attribute::getAttributeName).toList()))
				.append("; Probability\n");
		for (ACoordinate<Attribute<? extends IValue>, IValue> coord : sampler.getKeys()) {
			StringBuilder line = new StringBuilder();
			for (Attribute<? extends IValue> att : attributs) {
				if (coord.getDimensions().contains(att)) {
					if (line.isEmpty()) {
						line.append(coord.getMap().get(att).getStringValue());
					} else {
						line.append(csvSeparator).append(coord.getMap().get(att).getStringValue());
					}
				} else if (line.isEmpty()) {
					line.append(" ");
				} else {
					line.append(csvSeparator).append(" ");
				}
			}
			s.append(line).append(csvSeparator).append(sampler.getValue(coord)).append("\n");
		}
		return s.toString();
	}

}
