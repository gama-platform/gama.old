/*******************************************************************************************************
 *
 * GosplConditionalDistribution.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.data.GSDataParser;
import core.util.exception.GenstarException;
import core.util.random.GenstarRandom;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.ASegmentedNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.control.ControlFrequency;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.distribution.matrix.coordinate.GosplCoordinate;

/**
 * A set of joint distributions with links of dependancy between them.
 *
 * @author Kevin Chapuis
 */
public class GosplConditionalDistribution extends ASegmentedNDimensionalMatrix<Double> {

	/**
	 * Instantiates a new gospl conditional distribution.
	 *
	 * @param jointDistributionSet
	 *            the joint distribution set
	 * @throws IllegalDistributionCreation
	 *             the illegal distribution creation
	 */
	protected GosplConditionalDistribution(final Set<AFullNDimensionalMatrix<Double>> jointDistributionSet)
			throws IllegalDistributionCreation {
		super(jointDistributionSet);
	}

	// --------------- Main contract --------------- //

	/**
	 * {@inheritDoc}
	 * <p>
	 * Provide the most informed control associated with the given set of aspects <br>
	 * describe the whole process
	 */
	@Override
	public AControl<Double> getVal(final Collection<IValue> aspects) {
		// Setup output with identity product value
		AControl<Double> conditionalProba = this.getIdentityProductVal();

		// Test whether requested aspects are part of mapped attribute
		// If there exists a referent attribute & referents is complete, then replace
		// mapped attribute aspects by referent ones
		Map<Attribute<? extends IValue>, Set<IValue>> mapAttToValues = aspects.stream()
				.filter(a -> !this.getDimension(a).getReferentAttribute().equals(a.getValueSpace().getAttribute())
						&& jointDistributionSet.stream().anyMatch(
								jd -> jd.getDimensions().contains(this.getDimension(a).getReferentAttribute())))
				.collect(Collectors.groupingBy(this::getDimension,
						Collectors.mapping(Function.identity(), Collectors.toSet())));

		mapAttToValues.forEach((mAtt, value) -> {
			Set<IValue> rValues =
					value.stream().flatMap(a -> mAtt.findMappedAttributeValues(a).stream()).collect(Collectors.toSet());
			Set<IValue> rToMValues = rValues.stream().flatMap(a -> mAtt.findMappedAttributeValues(a).stream())
					.collect(Collectors.toSet());
			if (!value.equals(rToMValues))
				throw new IllegalArgumentException("Elicit a value for which this n dimensional matrix " + "("
						+ this.getLabel() + ") has divergent information about");
			aspects.removeAll(value);
			aspects.addAll(rValues);
		});

		// Setup a record of visited dimension to avoid duplicated probabilities
		Set<Attribute<? extends IValue>> remainingDimension =
				aspects.stream().map(this::getDimension).collect(Collectors.toSet());

		// Select matrices that contains at least one concerned dimension and ordered them
		// in decreasing order of the number of matches
		List<AFullNDimensionalMatrix<Double>> concernedMatrices = jointDistributionSet.stream()
				.filter(matrix -> matrix.getDimensions().stream().anyMatch(remainingDimension::contains))
				.sorted((m1, m2) -> {
					int diffRef = (int) (m1.getDimensions().stream().filter(remainingDimension::contains).count()
							- m2.getDimensions().stream().filter(remainingDimension::contains).count());
					if (diffRef > 0 || diffRef == 0 && m1.getDimensions().stream()
							.filter(dim -> remainingDimension.contains(dim.getReferentAttribute())
									&& !dim.getReferentAttribute().equals(dim))
							.count() <= m2.getDimensions().stream()
									.filter(dim -> remainingDimension.contains(dim.getReferentAttribute())
											&& !dim.getReferentAttribute().equals(dim))
									.count())
						return -1;
					return 1;
				}).toList();

		// Store visited dimension to compute conditional probabilities
		Set<Attribute<? extends IValue>> assignedDimension = new HashSet<>();

		for (AFullNDimensionalMatrix<Double> mat : concernedMatrices) {
			if (mat.getDimensions().stream().noneMatch(remainingDimension::contains)) { continue; }

			// Setup concerned values
			Set<IValue> concernedValues =
					aspects.stream().filter(a -> mat.getDimensions().contains(a.getValueSpace().getAttribute()))
							.collect(Collectors.toSet());

			// COMPUTE CONDITIONAL PROBABILITY
			// Setup conditional values (known probability to compute conditional probability)
			Set<IValue> conditionalValues = concernedValues.stream()
					.filter(val -> assignedDimension.stream().anyMatch(dim -> dim.getValueSpace().contains(val)))
					.collect(Collectors.toSet());

			// add bottom up & top down conditional values
			// WARNING: make false assumption about probability manipulation
			// Hence issues arise: Either conditional probability are over or under estimated,
			// because referent binding is not force to be complete (a set of value referees to some other set,
			// while only a subset can be of target here)
			Map<Set<IValue>, AControl<Double>> bottomup =
					this.estimateBottomUpReferences(mat, aspects, assignedDimension);
			Map<Set<IValue>, AControl<Double>> topdown =
					this.estimateTopDownReferences(mat, aspects, assignedDimension);

			// WARNING: some error test to erase when finished
			if (Stream.concat(bottomup.values().stream(), topdown.values().stream())
					.anyMatch(control -> control.getValue() > 1d))
				throw new GenstarException("Some conditional probabilities exceed 1:" + "\nBottomup: "
						+ bottomup.entrySet().stream().map(Entry::toString).collect(Collectors.joining("\n"))
						+ "\nTopdown: "
						+ topdown.entrySet().stream().map(Entry::toString).collect(Collectors.joining("\n")));

			// If there is any empty value associated with mapped attribute, then exit with empty value
			// It means attributes has divergent encoding values
			if (Stream
					.concat(bottomup.keySet().stream().flatMap(Set::stream),
							topdown.keySet().stream().flatMap(Set::stream))
					.anyMatch(value -> this.getDimension(value).getEmptyValue().equals(value)))
				return this.getNulVal();

			Set<IValue> bottomAndTopConditional = Stream.concat(bottomup.keySet().stream().flatMap(Set::stream),
					topdown.keySet().stream().flatMap(Set::stream)).collect(Collectors.toSet());

			// Add new concerned bottomup/topdown values
			concernedValues.addAll(bottomAndTopConditional);
			AControl<Double> newProbability = mat.getVal(concernedValues, true);

			conditionalValues.addAll(bottomAndTopConditional);
			AControl<Double> conditionalProbability =
					conditionalValues.isEmpty() ? this.getIdentityProductVal() : mat.getVal(conditionalValues);

			// Useless ? only zero possible conditional probability is because mapped attribute
			// have divergent encoded values
			if (conditionalProbability.getValue().equals(this.getNulVal().getValue())) return this.getNulVal();

			/*
			 * // Adjust probability space definition: e.g. age from 15 to more than 65 & age from 0 to more than 100 //
			 * WARNING: impossible in probability theory / make assumption on the transition (uniformity)
			 * AControl<Double> adjustSpaceDefinition = this.getIdentityProductVal()
			 * .multiply(Stream.concat(bottomup.entrySet().stream(), topdown.entrySet().stream()) .map(e ->
			 * e.getValue().multiply(1/mat.getVal(e.getKey()).getValue())) .reduce(this.getIdentityProductVal(), (c1,
			 * c2) -> c1.multiply(c2)));
			 */

			// COMPUTE BRUT PROBABILITY
			newProbability.multiply(1d / conditionalProbability.getValue());
			// newProbability.multiply(adjustSpaceDefinition.getValue() / conditionalProbability.getValue());

			// Update conditional probability
			conditionalProba.multiply(newProbability);

			// Update visited dimension
			Set<Attribute<? extends IValue>> updateDimension = concernedValues.stream().filter(aspects::contains)
					.map(this::getDimension).collect(Collectors.toSet());
			assignedDimension.addAll(updateDimension);
			remainingDimension.removeAll(updateDimension);
		}
		return conditionalProba;
	}

	// ------------------ Setters ------------------ //

	@Override
	public boolean addValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinates,
			final AControl<? extends Number> value) {
		Set<AFullNDimensionalMatrix<Double>> jds = jointDistributionSet.stream()
				.filter(jd -> jd.getDimensions().equals(coordinates.getDimensions())).collect(Collectors.toSet());
		return jds.iterator().next().addValue(coordinates, value);
	}

	@Override
	public final boolean addValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinates,
			final Double value) {
		return addValue(coordinates, new ControlFrequency(value));
	}

	@Override
	public final boolean addValue(final Double value, final String... coordinates) {

		return this.addValue(this.getCoordinate(coordinates), value);
	}

	@Override
	public boolean setValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinates,
			final AControl<? extends Number> value) {
		Set<AFullNDimensionalMatrix<Double>> jds = jointDistributionSet.stream()
				.filter(jd -> jd.getDimensions().equals(coordinates.getDimensions())).collect(Collectors.toSet());
		if (jds.size() != 1) return false;
		return jds.iterator().next().setValue(coordinates, value);
	}

	@Override
	public final boolean setValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate,
			final Double value) {
		return setValue(coordinate, new ControlFrequency(value));
	}

	@Override
	public final boolean setValue(final Double value, final String... coordinates) {
		return setValue(GosplCoordinate.createCoordinate(this.getDimensions(), coordinates), value);
	}

	// ------------------ Side contract ------------------ //

	@Override
	public AControl<Double> getNulVal() { return new ControlFrequency(0d); }

	@Override
	public AControl<Double> getIdentityProductVal() { return new ControlFrequency(1d); }

	@Override
	public AControl<Double> getAtomicVal() { return jointDistributionSet.iterator().next().getAtomicVal(); }

	// -------------------- Utilities -------------------- //

	@Override
	public boolean isCoordinateCompliant(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate) {
		return jointDistributionSet.stream().anyMatch(jd -> jd.isCoordinateCompliant(coordinate));
	}

	@Override
	public Set<IValue> getEmptyReferentCorrelate(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate) {
		Set<IValue> knownRef = new HashSet<>();
		for (AFullNDimensionalMatrix<Double> mat : jointDistributionSet)
			if (mat.getEmptyReferentCorrelate(coordinate).isEmpty()) {
				knownRef.addAll(coordinate.values().stream()
						.filter(val -> mat.getDimensions().stream()
								.anyMatch(dim -> this.getDimension(val).getReferentAttribute().equals(dim)))
						.collect(Collectors.toSet()));
			}
		Set<IValue> emptyRef = jointDistributionSet.stream()
				.flatMap(jd -> jd.getEmptyReferentCorrelate(coordinate).stream()).collect(Collectors.toSet());
		emptyRef.removeAll(knownRef.stream().map(val -> this.getDimension(val).getReferentAttribute().getEmptyValue())
				.collect(Collectors.toSet()));
		return emptyRef;
	}

	@Override
	public AControl<Double> parseVal(final GSDataParser parser, final String val) {
		if (!parser.getValueType(val).isNumericValue()) return getNulVal();
		return new ControlFrequency(Double.valueOf(val));
	}

	@Override
	public void normalize() throws IllegalArgumentException {

		throw new IllegalArgumentException("should not normalize a " + getMetaDataType());

	}

	@Override
	public boolean checkAllCoordinatesHaveValues() {
		return false;
	}

	@Override
	public boolean checkGlobalSum() {

		return jointDistributionSet.stream().allMatch(AFullNDimensionalMatrix::checkGlobalSum);
	}

	// -------------------- Inner Utilities -------------------- //

	/**
	 * Estimate bottom up references.
	 *
	 * @param mat
	 *            the mat
	 * @param aspects
	 *            the aspects
	 * @param assignedDimension
	 *            the assigned dimension
	 * @return the map
	 */
	/*
	 * Setup conditional bottom up values: value for which this matrix has partial bottom-up information, i.e. one
	 * attribute of this matrix has for referent one value attribute for which probability has already be defined
	 */
	private Map<Set<IValue>, AControl<Double>> estimateBottomUpReferences(final AFullNDimensionalMatrix<Double> mat,
			final Collection<IValue> aspects, final Set<Attribute<? extends IValue>> assignedDimension) {
		Map<Attribute<? extends IValue>, Set<Attribute<? extends IValue>>> ratb = mat.getDimensions().stream()
				.filter(att -> !att.getReferentAttribute().equals(att)
						&& assignedDimension.contains(att.getReferentAttribute()))
				.collect(Collectors.groupingBy(Attribute::getReferentAttribute,
						Collectors.mapping(Function.identity(), Collectors.toSet())));

		Map<Attribute<? extends IValue>, Attribute<? extends IValue>> refAttributeToBottomup = new HashMap<>();

		ratb.forEach((da, set) -> refAttributeToBottomup.put(da, set.stream()
				.sorted((a1, a2) -> a1.getValueSpace().getValues().size() < a2.getValueSpace().getValues().size() ? -1
						: a1.getValueSpace().getValues().size() > a2.getValueSpace().getValues().size() ? 1
						: GenstarRandom.getInstance().nextDouble() > 0.5 ? -1 : 1)
				.findFirst().orElse(null)));

		if (refAttributeToBottomup.isEmpty()) return Collections.emptyMap();
		// Transpose top down value set to control proportional referent
		Map<Set<IValue>, AControl<Double>> res =
				computeControlReferences(refAttributeToBottomup, aspects, assignedDimension);
		// Transpose back from top down to bottom up for conditional value to fit **mat** dimensions
		Set<Attribute<? extends IValue>> noneAllignedAttribute = res.keySet().stream().flatMap(Set::stream)
				.filter(a -> !mat.getDimensions().contains(a.getValueSpace().getAttribute())).map(this::getDimension)
				.collect(Collectors.toSet());
		if (noneAllignedAttribute.stream().anyMatch(
				att -> mat.getDimensions().stream().noneMatch(matAtt -> matAtt.getReferentAttribute().equals(att))))
			throw new GenstarException("Estimated bottom up reference targeted dimension out of the concerned matrix:"
					+ "\nConcerned matrix dimensions = " + Arrays.toString(mat.getDimensions().toArray())
					+ "\nTargeted dimensions = " + res.keySet().stream()
							.flatMap(set -> set.stream().map(val -> val.getValueSpace().getAttribute())).toList());
		return res;
	}

	/**
	 * Estimate top down references.
	 *
	 * @param mat
	 *            the mat
	 * @param aspects
	 *            the aspects
	 * @param assignedDimension
	 *            the assigned dimension
	 * @return the map
	 */
	/*
	 * Setup conditional top down values: value for which this matrix has partial top down information, i.e. one
	 * attribute of this matrix is the referent of one value attribute for which probability has already be defined
	 */
	private Map<Set<IValue>, AControl<Double>> estimateTopDownReferences(final AFullNDimensionalMatrix<Double> mat,
			final Collection<IValue> aspects, final Set<Attribute<? extends IValue>> assignedDimension) {
		Map<Attribute<? extends IValue>, Attribute<? extends IValue>> assignedAttributeToTopdown =
				assignedDimension.stream()
						.filter(att -> !att.getReferentAttribute().equals(att)
								&& mat.getDimensions().contains(att.getReferentAttribute()))
						.collect(Collectors.toMap(Function.identity(), Attribute::getReferentAttribute));
		if (assignedAttributeToTopdown.isEmpty()) return Collections.emptyMap();
		// Transpose bottom up value set to control proportional referent
		Map<Set<IValue>, AControl<Double>> res =
				computeControlReferences(assignedAttributeToTopdown, aspects, assignedDimension);
		if (res.keySet().stream().flatMap(Set::stream)
				.anyMatch(a -> !mat.getDimensions().contains(a.getValueSpace().getAttribute())))
			throw new GenstarException("Estimated bottom up reference targeted dimension out of the concerned matrix:"
					+ "\nConcerned matrix dimensions = " + Arrays.toString(mat.getDimensions().toArray())
					+ "\nTargeted dimensions = " + res.keySet().stream().flatMap(Set::stream).toList());
		return res;
	}

	/**
	 * Compute control references.
	 *
	 * @param assignedAttToCurrentAtt
	 *            the assigned att to current att
	 * @param aspects
	 *            the aspects
	 * @param assignedDimension
	 *            the assigned dimension
	 * @return the map
	 */
	private Map<Set<IValue>, AControl<Double>> computeControlReferences(
			final Map<Attribute<? extends IValue>, Attribute<? extends IValue>> assignedAttToCurrentAtt,
			final Collection<IValue> aspects, final Set<Attribute<? extends IValue>> assignedDimension) {
		// Map of referent attribute and their already defined value
		Map<Attribute<? extends IValue>, Set<IValue>> currentAttToAssignedValues = aspects.stream()
				.filter(as -> assignedDimension.contains(as.getValueSpace().getAttribute())
						&& assignedAttToCurrentAtt.containsKey(as.getValueSpace().getAttribute()))
				.collect(Collectors.groupingBy(as -> assignedAttToCurrentAtt.get(as.getValueSpace().getAttribute()),
						Collectors.mapping(Function.identity(), Collectors.toSet())));
		// Transpose bottom up value set to control proportional referent
		Map<Set<IValue>, AControl<Double>> output = new HashMap<>();
		assignedAttToCurrentAtt.forEach((assignedAtt, attValue) -> {

			// Distinguish bottomup & topdown attribute from assigned & current attribute
			Attribute<? extends IValue> bottomupAtt =
					assignedAtt.getReferentAttribute().equals(attValue) ? assignedAtt : attValue;
			// Map bottomup & topdown attribute to according current & assigned values
			Set<IValue> currentMappedVals = currentAttToAssignedValues.get(attValue).stream()
					.flatMap(val -> bottomupAtt.findMappedAttributeValues(val).stream()).collect(Collectors.toSet());
			Set<IValue> assignedMappedVals = currentMappedVals.stream()
					.flatMap(val -> bottomupAtt.findMappedAttributeValues(val).stream()).collect(Collectors.toSet());
			// Find the proper matrix to compute conditional probability
			AFullNDimensionalMatrix<Double> matrix = this.jointDistributionSet.stream()
					.filter(m -> m.getDimensions().contains(assignedAtt)).findAny().orElse(null);
			// Use two information: the probability to have assigned values, knowing that current mapped values
			// probability
			// in this particular matrix (that is we need to know how current mapped values mapped to assigned values)
			Set<IValue> assignedValues = currentAttToAssignedValues.get(attValue);

			/*
			 * HINT: some syso to debug System.out.println("Assigned: "+Arrays.toString(assignedValues.toArray()));
			 * System.out.println("Current: "+Arrays.toString(currentMappedVals.toArray()));
			 * System.out.println("Assigned mapped: "+Arrays.toString(assignedMappedVals.toArray()));
			 */
			if (matrix != null) {
				if (assignedMappedVals.stream().anyMatch(val -> this.getDimension(val).getEmptyValue().equals(val))) {
					output.put(currentMappedVals, matrix.getNulVal());
				} else {
					output.put(currentMappedVals, matrix.getVal(assignedValues).multiply(
							matrix.getVal(assignedValues).multiply(1d / matrix.getVal(assignedMappedVals).getValue())));
					// Pretty slow to multiply twice
				}
			}

		});
		return output;
	}

}
