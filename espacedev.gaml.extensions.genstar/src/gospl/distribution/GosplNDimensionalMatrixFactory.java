/*******************************************************************************************************
 *
 * GosplNDimensionalMatrixFactory.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.IEntity;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import core.util.GSUtilAttribute;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.exception.IllegalNDimensionalMatrixAccess;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.ASegmentedNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.ISegmentedNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.control.ControlContingency;
import gospl.distribution.matrix.control.ControlFrequency;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.distribution.matrix.coordinate.GosplCoordinate;

/**
 * Factory to build various type of {@link INDimensionalMatrix} from many sources:
 * <p>
 * You can create distribution (or contingency table), empty or not, from a {@link IPopulation} or a set of population
 * or even just with a map of coordinate / control
 * <p>
 *
 * @author kevinchapuis
 * @author samuel Thiriot
 *
 */
public class GosplNDimensionalMatrixFactory {

	/** The epsilon. */
	public static double EPSILON = Math.pow(10, -3);

	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public static final GosplNDimensionalMatrixFactory getFactory() { return new GosplNDimensionalMatrixFactory(); }

	//////////////////////////////////////////////
	// EMPTY MATRIX //
	//////////////////////////////////////////////

	/**
	 * Create an empty distribution
	 *
	 * @param dimensions
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> createEmptyDistribution(final Set<Attribute<? extends IValue>> dimensions,
			final GSSurveyType type) {
		AFullNDimensionalMatrix<Double> matrix = new GosplJointDistribution(dimensions, type);
		matrix.addGenesis("created from scratch GosplNDimensionalMatrixFactory@createEmptyDistribution");
		return matrix;
	}

	/**
	 * Create an empty distribution
	 *
	 * @param dimensions
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> createEmptyDistribution(final Set<Attribute<? extends IValue>> dimensions) {
		return createEmptyDistribution(dimensions, GSSurveyType.GlobalFrequencyTable);
	}

	/**
	 *
	 * @param dimensions
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public AFullNDimensionalMatrix<Double> createEmptyDistribution(final Attribute<? extends IValue>... dimensions) {
		return createEmptyDistribution(new HashSet<>(Arrays.asList(dimensions)), GSSurveyType.GlobalFrequencyTable);
	}

	/**
	 * Create an empty segmented distribution
	 *
	 * @param segmentedDimensions
	 * @return
	 * @throws IllegalDistributionCreation
	 */
	public ISegmentedNDimensionalMatrix<Double> createEmptyDistribution(
			final Collection<Set<Attribute<? extends IValue>>> segmentedDimensions) throws IllegalDistributionCreation {
		return new GosplConditionalDistribution(
				segmentedDimensions.stream().map(this::createEmptyDistribution).collect(Collectors.toSet()));
	}

	/**
	 * Create a contingency matrix with given dimension and coordinates build from cartesian product of dimensions'
	 * aspects
	 *
	 * @param dimensions
	 * @param buildCoordinate
	 * @return an empty matrix with all coordinates build in
	 */
	public AFullNDimensionalMatrix<Integer> createEmtpyContingencies(final Set<Attribute<? extends IValue>> dimensions,
			final boolean buildCoordinate) {
		AFullNDimensionalMatrix<Integer> contingency = new GosplContingencyTable(dimensions);
		if (buildCoordinate) {
			for (List<? extends IValue> coordinate : Sets
					.cartesianProduct(dimensions.stream().map(dim -> dim.getValueSpace().getValues()).toList())) {
				contingency.addValue(new GosplCoordinate(coordinate.stream()
						.collect(Collectors.toMap(
								v -> dimensions.stream().filter(d -> d.getValueSpace().contains(v)).findFirst().get(),
								Function.identity()))),
						new ControlContingency(0));
			}
		}
		return contingency;
	}

	//////////////////////////////////////////////////
	// DISTRIBUTION MATRIX //
	//////////////////////////////////////////////////

	/**
	 * Create a distribution from a map: key are mapped to matrix coordinate and value to matrix control value
	 *
	 * @param sampleDistribution
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> createDistribution(final Set<Attribute<? extends IValue>> dimensions,
			final Map<Set<IValue>, Double> sampleDistribution) {
		if (sampleDistribution.isEmpty()) throw new IllegalArgumentException("Sample distribution cannot be empty");
		AFullNDimensionalMatrix<Double> distribution = this.createEmptyDistribution(dimensions);
		sampleDistribution.entrySet().stream()
				.forEach(entry -> distribution.addValue(
						new GosplCoordinate(dimensions.stream()
								.collect(Collectors.toMap(Function.identity(), att -> entry.getKey().stream()
										.filter(val -> att.getValueSpace().contains(val)).findFirst().get()))),
						new ControlFrequency(entry.getValue())));
		return distribution;
	}

	/**
	 * Changes a contingency table to a global frequency table
	 *
	 * @param contigency
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> createDistribution(final AFullNDimensionalMatrix<Integer> contigency) {
		// Init the output matrix
		AFullNDimensionalMatrix<Double> matrix =
				new GosplJointDistribution(contigency.getDimensions(), GSSurveyType.GlobalFrequencyTable);
		matrix.addGenesis("created from distribution GosplNDimensionalMatrixFactory@createDistribution");

		int total = (int) Math.round(contigency.getVal().getValue().doubleValue());

		// Normalize increments to global frequency
		contigency.getMatrix().keySet().stream().forEach(coord -> matrix.setValue(coord,
				new ControlFrequency(contigency.getVal(coord).getValue().doubleValue() / total)));

		return matrix;
	}

	/**
	 * Convert an unknonw distribution into a joint distribution with uniform hypothesis for missing statistical
	 * relationships <\p> WARNING : Result might not be consistent with original matrix !
	 *
	 * @param distribution
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> createDistribution(
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, ? extends Number> distribution) {

		if (GSSurveyType.LocalFrequencyTable.equals(distribution.getMetaDataType())) throw new IllegalArgumentException(
				"Cannot generate a full distribution from a " + GSSurveyType.LocalFrequencyTable);

		// Init the output matrix
		AFullNDimensionalMatrix<Double> matrix =
				new GosplJointDistribution(distribution.getDimensions(), GSSurveyType.GlobalFrequencyTable);
		matrix.addGenesis("created from distribution GosplNDimensionalMatrixFactory@createDistribution");

		int total = (int) Math.round(distribution.getVal().getValue().doubleValue());

		// Normalize increments to global frequency
		distribution.getMatrix().keySet().stream().forEach(coord -> matrix.setValue(coord,
				new ControlFrequency(distribution.getVal(coord).getValue().doubleValue() / total)));

		return matrix;
	}

	/**
	 * Create a frequency matrix from entities' population characteristics.
	 *
	 * @param population
	 * @return
	 */
	public AFullNDimensionalMatrix<Double>
			createDistribution(final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {
		// Init the output matrix
		AFullNDimensionalMatrix<Double> matrix =
				new GosplJointDistribution(population.getPopulationAttributes(), GSSurveyType.GlobalFrequencyTable);
		matrix.addGenesis("created from population GosplNDimensionalMatrixFactory@createDistribution");

		double unitFreq = 1d / population.size();

		// Transpose each entity into a coordinate and adds it to the matrix by means of increments
		for (ADemoEntity entity : population) {
			ACoordinate<Attribute<? extends IValue>, IValue> entityCoord =
					new GosplCoordinate(entity.getAttributeMap());
			if (!matrix.addValue(entityCoord, new ControlFrequency(unitFreq))) {
				matrix.getVal(entityCoord).add(unitFreq);
			}
		}

		return matrix;
	}

	/**
	 * Create a frequency matrix from entities' population subset of characteristics given as a parameter.
	 *
	 * @param attributesToMeasure
	 * @param population
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> createDistribution(
			final Set<Attribute<? extends IValue>> attributesToMeasure,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {

		// Init the output matrix
		AFullNDimensionalMatrix<Double> matrix =
				new GosplJointDistribution(attributesToMeasure, GSSurveyType.GlobalFrequencyTable);
		matrix.addGenesis("created from population GosplNDimensionalMatrixFactory@createDistribution");

		double unitFreq = 1d / population.size();

		// iterate the whole population
		for (ADemoEntity entity : population) {
			ACoordinate<Attribute<? extends IValue>, IValue> entityCoord = new GosplCoordinate(entity.getAttributeMap()
					.entrySet().stream().filter(entry -> attributesToMeasure.contains(entry.getKey()))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
			if (!matrix.addValue(entityCoord, new ControlFrequency(unitFreq))) {
				matrix.getVal(entityCoord).add(unitFreq);
			}
		}

		return matrix;
	}

	/**
	 * Create a frequency matrix from inner map collection
	 * <p>
	 * WARNING: there is not any guarantee on inner map collection consistency
	 *
	 * @param matrix
	 * @return
	 */
	public AFullNDimensionalMatrix<Double>
			createDistribution(final Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Double>> matrix) {
		return new GosplJointDistribution(matrix);
	}

	/**
	 * Transpose an unknown matrix into a full matrix. If matrix passed in argument is a segmented matrix then, the
	 * algorithm will end up making unknown relationship between attribute independent
	 *
	 * @param unknownDistribution
	 * @param gspu:
	 *            in order to track the process from the outside
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> createDistribution(
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> unknownDistribution,
			final GSPerformanceUtil gspu) {

		if (!unknownDistribution.isSegmented()) return this.createDistribution(unknownDistribution.getMatrix());

		// Reject attribute with referent, to only account for referent attribute
		Set<Attribute<? extends IValue>> targetedDimensions = unknownDistribution.getDimensions().stream()
				.filter(att -> att.getReferentAttribute().equals(att)).collect(Collectors.toSet());

		// Setup the matrix to estimate
		AFullNDimensionalMatrix<Double> freqMatrix =
				new GosplNDimensionalMatrixFactory().createEmptyDistribution(targetedDimensions);

		gspu.sysoStempMessage("Creation of matrix with attributes: " + Arrays.toString(targetedDimensions.toArray()));

		// Extrapolate the whole set of coordinates
		Collection<Map<Attribute<? extends IValue>, IValue>> coordinates =
				GSUtilAttribute.getValuesCombination(targetedDimensions);

		gspu.sysoStempPerformance(1, this);
		gspu.sysoStempMessage("Start writting down collpased distribution of size " + coordinates.size());

		for (Map<Attribute<? extends IValue>, IValue> coordinate : coordinates) {
			AControl<Double> nulVal = freqMatrix.getNulVal();
			ACoordinate<Attribute<? extends IValue>, IValue> coord = new GosplCoordinate(coordinate);
			AControl<Double> freq = unknownDistribution.getVal(coord);
			if (!nulVal.getValue().equals(freq.getValue())) {
				freqMatrix.addValue(coord, freq);
			} else {
				// HINT: MUST INTEGRATE COORDINATE WITH EMPTY VALUE, e.g. age under 5 & empty occupation
				gspu.sysoStempMessage(
						"Goes into a referent empty correlate: " + Arrays.toString(coordinate.values().toArray()));
				ACoordinate<Attribute<? extends IValue>, IValue> newCoord = new GosplCoordinate(coord.getDimensions()
						.stream()
						.collect(Collectors.toMap(Function.identity(),
								att -> unknownDistribution.getEmptyReferentCorrelate(coord).stream()
										.anyMatch(val -> val.getValueSpace().getAttribute().equals(att))
												? att.getValueSpace().getEmptyValue() : coord.getMap().get(att))));
				if (newCoord.equals(coord)) {
					freqMatrix.addValue(coord, freq);
				} else {
					freqMatrix.addValue(newCoord,
							unknownDistribution.getVal(newCoord.values().stream().filter(
									value -> !unknownDistribution.getDimension(value).getEmptyValue().equals(value))
									.collect(Collectors.toSet())));
				}
			}
		}

		gspu.sysoStempMessage("Distribution has been created succefuly");

		return freqMatrix;
	}

	/**
	 * Clone the distribution so the value in it are not linked to one another (like it is the case in
	 * createDistribution method)
	 *
	 * @param distribution
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> cloneDistribution(final AFullNDimensionalMatrix<Double> distribution) {
		AFullNDimensionalMatrix<Double> matrix =
				new GosplJointDistribution(distribution.getDimensions(), GSSurveyType.GlobalFrequencyTable);

		distribution.getMatrix().keySet().forEach(coordinate -> matrix.setValue(coordinate,
				new ControlFrequency(distribution.getVal(coordinate).getValue())));

		return matrix;
	}

	//////////////////////////////////////////////////
	// SEGMENTED MATRIX //
	//////////////////////////////////////////////////

	/**
	 * Create a segmented matrix from multiple population, each beeing a full dimensional matrix
	 *
	 * @param populations
	 * @return
	 * @throws IllegalDistributionCreation
	 */
	public ISegmentedNDimensionalMatrix<Double> createDistributionFromPopulations(
			final Set<IPopulation<ADemoEntity, Attribute<? extends IValue>>> populations)
			throws IllegalDistributionCreation {
		return new GosplConditionalDistribution(
				populations.stream().map(this::createDistribution).collect(Collectors.toSet()));
	}

	/**
	 * Create a segmented matrix from multiple full matrix
	 *
	 * @param innerDistributions
	 * @return
	 * @throws IllegalDistributionCreation
	 */
	public ASegmentedNDimensionalMatrix<Double> createDistributionFromDistributions(
			final Set<AFullNDimensionalMatrix<Double>> innerDistributions) throws IllegalDistributionCreation {
		return new GosplConditionalDistribution(innerDistributions);
	}

	/**
	 * Create a segmented matrix from multiple full matrix
	 *
	 * @param innerDistributions
	 * @return
	 * @throws IllegalDistributionCreation
	 */
	@SuppressWarnings ("unchecked")
	public ASegmentedNDimensionalMatrix<Double> createDistributionFromDistributions(
			final AFullNDimensionalMatrix<Double>... innerDistributions) throws IllegalDistributionCreation {
		return createDistributionFromDistributions(new HashSet<>(Arrays.asList(innerDistributions)));
	}

	//////////////////////////////////////////////////
	// CONTINGENCY MATRIX //
	//////////////////////////////////////////////////

	/**
	 * Create a contingency matrix from entities' population characteristics
	 *
	 * @param seed
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer>
			createContingency(final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {
		// Init the output matrix
		AFullNDimensionalMatrix<Integer> matrix = new GosplContingencyTable(population.getPopulationAttributes());
		matrix.addGenesis("Created from a population GosplNDimensionalMatrixFactory@createContigency");

		// Transpose each entity into a coordinate and adds it to the matrix by means of increments
		for (ADemoEntity entity : population) {
			ACoordinate<Attribute<? extends IValue>, IValue> entityCoord =
					new GosplCoordinate(entity.getAttributeMap());
			if (!matrix.addValue(entityCoord, new ControlContingency(1))) { matrix.getVal(entityCoord).add(1); }
		}
		return matrix;
	}

	/**
	 * Create a contingency matrix from approximatively any set of entity
	 * </p>
	 * WARNING : involves a lot of nasty casting
	 *
	 * @param population
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer>
			createContingency(final Collection<? extends IEntity<? extends IAttribute<? extends IValue>>> population) {
		// Init the output matrix
		@SuppressWarnings ("unchecked") Set<Attribute<? extends IValue>> att =
				population.stream().flatMap(e -> e.getAttributes().stream())
						.map(atttribute -> (Attribute<? extends IValue>) atttribute).collect(Collectors.toSet());

		AFullNDimensionalMatrix<Integer> matrix = new GosplContingencyTable(att);
		matrix.addGenesis("Created from a population GosplNDimensionalMatrixFactory@createContigency");

		// Transpose each entity into a coordinate and adds it to the matrix by means of increments
		for (IEntity<? extends IAttribute<? extends IValue>> entity : population) {
			@SuppressWarnings ("unchecked") ACoordinate<Attribute<? extends IValue>, IValue> entityCoord =
					new GosplCoordinate((Map<Attribute<? extends IValue>, IValue>) entity.getAttributeMap());
			if (!matrix.addValue(entityCoord, new ControlContingency(1))) { matrix.getVal(entityCoord).add(1); }
		}
		return matrix;
	}

	/**
	 * Create a contingency matrix from entities of a population, but taking into account only the set of attributes
	 * given in parameter
	 *
	 * @param attributesToMeasure
	 * @param population
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer> createContingency(
			final Set<Attribute<? extends IValue>> attributesToMeasure,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {

		Set<Attribute<? extends IValue>> matchingAttributes = attributesToMeasure.stream()
				.filter(att -> population.getPopulationAttributes().stream().anyMatch(popAtt -> popAtt.isLinked(att)))
				.collect(Collectors.toSet());

		if (matchingAttributes.isEmpty()) throw new IllegalArgumentException(
				"The given population must have " + "at least one matching attribute with the set given in argument: \n"
						+ attributesToMeasure.stream().map(Attribute::getAttributeName)
								.collect(Collectors.joining(";  "))
						+ "\n" + population.getPopulationAttributes().stream().map(Attribute::getAttributeName)
								.collect(Collectors.joining(";  ")));

		// Init the output matrix
		AFullNDimensionalMatrix<Integer> matrix = new GosplContingencyTable(matchingAttributes);
		matrix.addGenesis("created from a population GosplNDimensionalMatrixFactory@createContigency");

		final GSPerformanceUtil gspu =
				new GSPerformanceUtil(
						"Create a contingency matrix from a population on " + matchingAttributes.stream()
								.map(Attribute::getAttributeName).collect(Collectors.joining("; ")) + " attribute set",
						Level.TRACE);

		gspu.sysoStempMessage("Test createContingency with given attributes: "
				+ matchingAttributes.stream().map(Attribute::getAttributeName).collect(Collectors.joining("; ")));

		Map<Attribute<? extends IValue>, Attribute<? extends IValue>> mappedAtt =
				matchingAttributes.stream().collect(Collectors.toMap(Function.identity(), att -> population
						.getPopulationAttributes().stream().filter(popAtt -> popAtt.isLinked(att)).findFirst().get()));

		gspu.sysoStempMessage("Attribute mapping from required to population : " + mappedAtt.entrySet().stream()
				.map(entry -> entry.getKey().getAttributeName() + "::" + entry.getValue().getAttributeName())
				.collect(Collectors.joining("; ")));

		// iterate the whole population
		for (ADemoEntity entity : population) {
			Collection<ACoordinate<Attribute<? extends IValue>, IValue>> entityCoords =
					matrix.getOrCreateCoordinates(matchingAttributes.stream()
							.flatMap(att -> att
									.findMappedAttributeValues(entity.getValueForAttribute(mappedAtt.get(att)))
									.stream())
							.collect(Collectors.toSet()));
			for (ACoordinate<Attribute<? extends IValue>, IValue> entityCoord : entityCoords) {
				if (!matrix.addValue(entityCoord, new ControlContingency(1))) { matrix.getVal(entityCoord).add(1); }
			}
		}

		gspu.sysoStempMessage("Output matrix dimensions are: "
				+ matrix.getDimensions().stream().map(Attribute::getAttributeName).collect(Collectors.joining("; ")));

		return matrix;
	}

	/**
	 * Create a full contingency table from an unknown type contingency matrix
	 *
	 * @param unknownMatrix
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer>
			createContingency(final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> unknownMatrix) {
		AFullNDimensionalMatrix<Integer> matrix = new GosplContingencyTable(unknownMatrix.getDimensions());
		unknownMatrix.getMatrix().keySet().forEach(coordinate -> matrix.addValue(coordinate,
				new ControlContingency(unknownMatrix.getVal(coordinate).getValue())));
		return matrix;
	}

	/**
	 * Clone a matrix
	 *
	 * @param matrix
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer> cloneContingency(final AFullNDimensionalMatrix<Integer> matrix) {
		Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Integer>> m = matrix.getMatrix();
		return new GosplContingencyTable(m.keySet().stream().collect(
				Collectors.toMap(Function.identity(), coord -> new ControlContingency(m.get(coord).getValue()))));
	}

	/**
	 * Clone a matrix with a sub-set of attributes
	 *
	 * @param marginalAttributes
	 * @param contingencies
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer> cloneContingency(final Set<Attribute<? extends IValue>> marginalAttributes,
			final AFullNDimensionalMatrix<Integer> contingencies) {
		if (!contingencies.getDimensions().containsAll(marginalAttributes))
			throw new IllegalArgumentException("Cannot reduce a contingency to unknown attributes: "
					+ marginalAttributes.stream().filter(att -> !contingencies.getDimensions().contains(att))
							.map(Attribute::getAttributeName).collect(Collectors.joining("; ")));

		AFullNDimensionalMatrix<Integer> matrix = this.createEmtpyContingencies(marginalAttributes, true);
		Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Integer>> m = matrix.getMatrix();

		m.keySet().forEach(coord -> matrix.setValue(coord, contingencies.getVal(coord.values())));

		return matrix;
	}

	/**
	 * Update matrix values with contingencies from the population (not concerned value remain untouched)
	 *
	 * Population and matrix _attribute_ and _value_ must match
	 *
	 * @param matrix
	 * @param population
	 * @return the input matrix updated with population contingencies
	 */
	public AFullNDimensionalMatrix<Integer> fillInContingency(final AFullNDimensionalMatrix<Integer> matrix,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {
		Set<Attribute<? extends IValue>> mattributes =
				matrix.getDimensions().stream().filter(dim -> population.getPopulationAttributes().stream()
						.anyMatch(att -> !att.equals(dim) && att.isLinked(dim))).collect(Collectors.toSet());

		for (ADemoEntity e : population) {
			Collection<IValue> matchingValues =
					e.getValues().stream().filter(val -> matrix.getAspects().contains(val)).collect(Collectors.toSet());
			Collection<IValue> relatedValues =
					mattributes.stream()
							.flatMap(att -> att.findMappedAttributeValues(e.getValueForAttribute(
									e.getAttributes().stream().filter(dim -> dim.isLinked(att)).findFirst().get()))
									.stream())
							.collect(Collectors.toSet());
			if (relatedValues.stream().anyMatch(val -> !matrix.getAspects().contains(val)))
				throw new IllegalNDimensionalMatrixAccess(
						"Try to access to a coordinates related to one of these values: "
								+ Arrays.asList(relatedValues).toString() + " but matrix values are: "
								+ Arrays.asList(matrix.getAspects()).toString());
			matrix.getVal(Stream.concat(matchingValues.stream(), relatedValues.stream()).collect(Collectors.toSet()),
					true).add(1);
		}

		return matrix;
	}

	//////////////////////////////////////////////////
	// SAMPLE MATRIX //
	//////////////////////////////////////////////////

	/**
	 *
	 *
	 * @param attributesToMeasure
	 * @param population
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer> createSample(final Set<Attribute<? extends IValue>> attributesToMeasure,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {

		// Init the output matrix
		AFullNDimensionalMatrix<Integer> matrix = new GosplContingencyTable(attributesToMeasure);

		matrix.addGenesis("created from a population GosplNDimensionalMatrixFactory@createContigency");

		// iterate the whole population
		for (ADemoEntity entity : population) {
			ACoordinate<Attribute<? extends IValue>, IValue> entityCoord =
					new GosplCoordinate(entity.getAttributeMap());
			if (!matrix.addValue(entityCoord, new ControlContingency(1))) { matrix.getVal(entityCoord).add(1); }
		}

		return matrix;
	}

}
