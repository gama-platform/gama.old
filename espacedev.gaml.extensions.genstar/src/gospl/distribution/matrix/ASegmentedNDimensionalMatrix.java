/*******************************************************************************************************
 *
 * ASegmentedNDimensionalMatrix.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.exception.IllegalNDimensionalMatrixAccess;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;

/**
 * Represent the higher order abstraction for segmented matrix: it is a record of matrices with diverging dimensional
 * coverage. Look at the exemple, where matrices are refer as {@code m} and dimensions as {@code d}:
 * <p>
 * {@code
 * m_seg = m1,m2,m3;} the segmented matrix contains 3 inner full matrices <br>
 * {@code
 * m_seg.getDimensions() == d1,d2,d3,d4,d5;} the segmented matrix contains 5 dimensions<br>
 * {@code
 * m1.getDimensions() == d1,d2,d3;} 1st inner full matrix contains 3 dimensions<br>
 * {@code
 * m2.getDimensions() == d1,d4;} 2nd inner full matrix contains 2 dimensions<br>
 * {@code
 * m1.getDimensions() == d5;} 3th inner full matrix contains only one dimensions <br>
 * <p>
 *
 * WARNING: Abstract segmented matrix only concerns distribution matrix, i.e. matrix that store probabilities
 *
 * @author kevinchapuis
 *
 * @param <T>
 */
public abstract class ASegmentedNDimensionalMatrix<T extends Number> implements ISegmentedNDimensionalMatrix<T> {

	/** The joint distribution set. */
	protected final Set<AFullNDimensionalMatrix<T>> jointDistributionSet;

	/** The label. */
	protected String label = null;

	/** The genesis. */
	protected List<String> genesis = new LinkedList<>();

	// -------------------- Constructor -------------------- //

	/**
	 * Instantiates a new a segmented N dimensional matrix.
	 */
	private ASegmentedNDimensionalMatrix() {
		jointDistributionSet = new HashSet<>();
	}

	/**
	 * Instantiates a new a segmented N dimensional matrix.
	 *
	 * @param jointDistributionSet
	 *            the joint distribution set
	 * @throws IllegalDistributionCreation
	 *             the illegal distribution creation
	 */
	protected ASegmentedNDimensionalMatrix(final Set<AFullNDimensionalMatrix<T>> jointDistributionSet)
			throws IllegalDistributionCreation {
		this();
		if (jointDistributionSet.isEmpty())
			throw new IllegalArgumentException("Not any distributions to fill in the conditional distribution");
		this.jointDistributionSet.addAll(jointDistributionSet);
		if (jointDistributionSet.stream().map(AFullNDimensionalMatrix::getMetaDataType).collect(Collectors.toSet())
				.size() > 1)
			throw new IllegalDistributionCreation("Divergent frame of reference among sub joint distribution");
	}

	// ------------------------- META DATA ------------------------ //

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getMetaDataType()
	 */
	@Override
	public GSSurveyType getMetaDataType() {
		Set<GSSurveyType> mdtSet =
				jointDistributionSet.stream().map(AFullNDimensionalMatrix::getMetaDataType).collect(Collectors.toSet());
		if (mdtSet.size() != 1) return null;
		return mdtSet.iterator().next();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#isSegmented()
	 */
	@Override
	public boolean isSegmented() { return true; }

	@Override
	public int getDegree() {
		return this.jointDistributionSet.stream().mapToInt(AFullNDimensionalMatrix::getDegree).sum();
	}

	// ---------------- Getters ---------------- //

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getDimensions()
	 */
	@Override
	public Set<Attribute<? extends IValue>> getDimensions() {
		return jointDistributionSet.stream().flatMap(jd -> jd.getDimensions().stream()).collect(Collectors.toSet());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getDimensionsAsAttributesAndValues()
	 */
	@Override
	public Map<Attribute<? extends IValue>, Set<? extends IValue>> getDimensionsAsAttributesAndValues() {
		Map<Attribute<? extends IValue>, Set<? extends IValue>> res = new HashMap<>();
		for (AFullNDimensionalMatrix<T> m : jointDistributionSet) {
			res.putAll(m.getDimensionsAsAttributesAndValues());
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getDimension(core.metamodel.pop.IValue)
	 */
	@Override
	public Attribute<? extends IValue> getDimension(final IValue aspect) {
		if (getDimensions().stream()
				.noneMatch(d -> d.getValueSpace().contains(aspect) || d.getValueSpace().getEmptyValue().equals(aspect)))
			throw new NullPointerException("Trying to access to aspect value \"" + aspect + "\" (\""
					+ aspect.getValueSpace().getAttribute().getAttributeName() + "\") while this "
					+ "matrix does not contains this dimension");
		return getDimensions().stream()
				.filter(d -> d.getValueSpace().contains(aspect) || d.getValueSpace().getEmptyValue().equals(aspect))
				.findFirst().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getAspects()
	 */
	@Override
	public Set<IValue> getAspects() {
		return getDimensions().stream().flatMap(d -> d.getValueSpace().getValues().stream())
				.collect(Collectors.toSet());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getAspects(core.metamodel.pop.APopulationAttribute)
	 */
	@Override
	public Set<IValue> getAspects(final Attribute<? extends IValue> dimension) {
		return Collections.unmodifiableSet(dimension.getValueSpace().getValues());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#size()
	 */
	@Override
	public int size() {
		return jointDistributionSet.stream().mapToInt(AFullNDimensionalMatrix::size).sum();
	}

	// ---------------------- Matrix coordinates ---------------------- //

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getEmptyCoordinate()
	 */
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> getEmptyCoordinate() {
		return jointDistributionSet.iterator().next().getEmptyCoordinate();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getCoordinates(java.util.Set)
	 */
	@Override
	public Collection<ACoordinate<Attribute<? extends IValue>, IValue>> getCoordinates(final Set<IValue> values) {
		Map<Attribute<? extends IValue>, Set<IValue>> attValues = values.stream().collect(
				Collectors.groupingBy(this::getDimension, Collectors.mapping(Function.identity(), Collectors.toSet())));
		List<AFullNDimensionalMatrix<T>> concernedJointDistributions =
				jointDistributionSet.stream().filter(matrix -> matrix.getDimensions().stream()
						.anyMatch(dim -> attValues.keySet().stream().anyMatch(dim::isLinked))).toList();
		if (concernedJointDistributions.size() == 1) return concernedJointDistributions.get(0).getCoordinates(values);

		Map<Attribute<? extends IValue>, Set<IValue>> overallMapLink = new HashMap<>();
		Set<Attribute<? extends IValue>> overallAttributes = concernedJointDistributions.stream()
				.flatMap(mat -> mat.getDimensions().stream()).collect(Collectors.toSet());
		for (Attribute<? extends IValue> attribute : attValues.keySet()) {
			for (Attribute<? extends IValue> mapAtt : overallAttributes.stream().filter(att -> att.isLinked(attribute))
					.toList()) {
				Set<IValue> mapVals = new HashSet<>();
				for (IValue val : attValues.get(attribute)) { mapVals.addAll(mapAtt.findMappedAttributeValues(val)); }
				overallMapLink.put(mapAtt, mapVals);
			}
		}

		Collection<ACoordinate<Attribute<? extends IValue>, IValue>> coords = new ArrayList<>();
		for (ACoordinate<Attribute<? extends IValue>, IValue> coord : concernedJointDistributions.stream()
				.flatMap(matrix -> matrix.getMatrix().keySet().stream()).collect(Collectors.toSet())) {
			if (coord.getMap().entrySet().stream().filter(entry -> overallMapLink.containsKey(entry.getKey()))
					.allMatch(entry -> overallMapLink.get(entry.getKey()).contains(entry.getValue()))) {
				coords.add(coord);
			}
		}

		return coords;
	}

	@Override
	public Collection<ACoordinate<Attribute<? extends IValue>, IValue>>
			getOrCreateCoordinates(final Set<IValue> values) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getCoordinates(java.lang.String)
	 */
	@Override
	public Collection<ACoordinate<Attribute<? extends IValue>, IValue>> getCoordinates(final String... keyAndVal)
			throws IllegalArgumentException {

		return getCoordinates(getValues(keyAndVal));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: Segmented matrix is made of several full dimensional matrix; hence the coordinate do reflect only a
	 * limited range of the overall matrix dimensionality
	 */
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> getCoordinate(final Set<IValue> values)
			throws NullPointerException {
		Optional<AFullNDimensionalMatrix<T>> mat =
				jointDistributionSet.stream().filter(matrix -> matrix.getAspects().containsAll(values)).findFirst();

		if (mat.isPresent()) return mat.get().getCoordinate(values);
		throw new NullPointerException("Trying to access coordinate with values " + Arrays.toString(values.toArray())
				+ " with not any correlates in the matrix " + this.getLabel());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getCoordinate(java.lang.String)
	 */
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> getCoordinate(final String... keyAndVal)
			throws IllegalArgumentException {

		Collection<ACoordinate<Attribute<? extends IValue>, IValue>> s = getCoordinates(keyAndVal);

		if (s.size() > 1)
			throw new IllegalArgumentException("these coordinates do not map to a single cell of the matrix");

		if (s.isEmpty()) throw new IllegalArgumentException("these coordinates do not map to any cell in the matrix");

		return s.iterator().next();
	}

	// ---------------------- Matrix accessors ---------------------- //

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getMatrices()
	 */
	@Override
	public Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>> getMatrices() {
		return Collections.unmodifiableSet(jointDistributionSet);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getMatrix()
	 */
	@Override
	public Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> getMatrix() {
		Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> matrix = new HashMap<>();
		for (AFullNDimensionalMatrix<T> jd : jointDistributionSet) { matrix.putAll(jd.getMatrix()); }
		return matrix;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getOrderedMatrix()
	 */
	@Override
	public LinkedHashMap<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> getOrderedMatrix() {
		LinkedHashMap<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> matrix = new LinkedHashMap<>();
		for (AFullNDimensionalMatrix<T> jd : jointDistributionSet) { matrix.putAll(jd.getOrderedMatrix()); }
		return matrix;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getVal()
	 */
	@Override
	public AControl<T> getVal() {
		AControl<T> result = this.getNulVal();
		for (AFullNDimensionalMatrix<T> distribution : jointDistributionSet) {
			for (AControl<T> control : distribution.matrix.values()) { getSummedControl(result, control); }
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getVal(gospl.distribution.matrix.coordinate.ACoordinate)
	 */
	@Override
	public AControl<T> getVal(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate) {
		return getVal(new HashSet<>(coordinate.values()), true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getVal(core.metamodel.pop.IValue)
	 */
	@Override
	public AControl<T> getVal(final IValue aspect) throws IllegalNDimensionalMatrixAccess {
		return this.getVal(aspect, false);
	}

	@Override
	public AControl<T> getVal(final IValue aspect, final boolean defaultToNull) {
		AControl<T> val = null;
		for (AFullNDimensionalMatrix<T> distribution : jointDistributionSet.stream()
				.filter(jd -> jd.getDimensions().contains(aspect.getValueSpace().getAttribute())).toList())
			if (val == null) {
				val = distribution.getVal(aspect, defaultToNull);
			} else if (!val.equalsVal(distribution.getVal(aspect), Math.pow(10, -4)))
				throw new IllegalNDimensionalMatrixAccess(
						"Incongruent probability in underlying distributions: " + "pv = " + val.getValue().doubleValue()
								+ " | nv = " + distribution.getVal(aspect).getValue().doubleValue());
		return val;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getVal(core.metamodel.pop.IValue)
	 */
	@Override
	public final AControl<T> getVal(final IValue... aspects) {
		return getVal(new HashSet<>(Arrays.asList(aspects)));
	}

	@Override
	public final AControl<T> getVal(final String... coordinate) {
		return getVal(this.getValues(coordinate));
	}

	@Override
	public final AControl<T> getVal(final Collection<IValue> aspects, final boolean defaultToNul) {
		return getVal(aspects);
	}

	// ---------------------- Inner utilities ---------------------- //

	/**
	 * Inner utility method that add an encapsulated {@link AControl} value into the first given in argument
	 *
	 * @param controlOne
	 * @param controlTwo
	 * @return
	 */
	private AControl<T> getSummedControl(final AControl<T> controlOne, final AControl<T> controlTwo) {
		return controlOne.add(controlTwo);
	}

	// ----------------------- utility ----------------------- //

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#toString()
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("Segmented matrix with ").append(jointDistributionSet.size())
				.append(" inner full matrices:\n");
		s.append(jointDistributionSet.stream().map(m -> m.toString() + "\n").reduce("", (s1, s2) -> s1 + s2));
		return s.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#toCsv(char)
	 */
	@Override
	public String toCsv(final char csvSeparator) {
		StringBuilder s = new StringBuilder();
		for (AFullNDimensionalMatrix<T> matrix : jointDistributionSet) {
			if (!s.isEmpty()) { s.append("\n"); }
			s.append("-- Matrix: ").append(matrix.getDimensions().size()).append(" dimensions and ").append(
					matrix.getDimensions().stream().mapToInt(dim -> dim.getValueSpace().getValues().size()).sum())
					.append(" aspects (theoretical size:").append(this.size()).append(")--\n");
			s.append(matrix.toCsv(csvSeparator));
		}
		return s.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getMatricesInvolving(core.metamodel.pop.
	 * APopulationAttribute)
	 */
	@Override
	public Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>>
			getMatricesInvolving(final Attribute<? extends IValue> att) {
		return this.jointDistributionSet.stream().filter(matrix -> matrix.getDimensions().contains(att))
				.collect(Collectors.toSet());
	}

	// ----------------------- String accessors ----------------------- //

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getValues(java.lang.String)
	 */
	@Override
	public Set<IValue> getValues(final String... keyAndVal) throws IllegalArgumentException {

		Set<IValue> coordinateValues = new HashSet<>();

		// collect all the attributes and index their names
		Map<String, Attribute<? extends IValue>> name2attribute = getDimensionsAsAttributesAndValues().keySet().stream()
				.collect(Collectors.toMap(Attribute::getAttributeName, Function.identity()));

		if (keyAndVal.length % 2 != 0)
			throw new IllegalArgumentException("you should pass pairs of attribute name and corresponding value, "
					+ "such as attribute 1 name, value for attribute 1, attribute 2 name, value for attribute 2...");

		// lookup values
		for (int i = 0; i < keyAndVal.length; i = i + 2) {
			final String attributeName = keyAndVal[i];
			final String attributeValueStr = keyAndVal[i + 1];

			Attribute<? extends IValue> attribute = name2attribute.get(attributeName);
			if (attribute == null) throw new IllegalArgumentException("unknown attribute " + attributeName);
			coordinateValues.add(attribute.getValueSpace().addValue(attributeValueStr)); // will raise exception if the
																							// value is not ok

		}

		return coordinateValues;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gospl.distribution.matrix.ISegmentedNDimensionalMatrix#getDimension(java.lang.String)
	 */
	@Override
	public Attribute<? extends IValue> getDimension(final String name) throws IllegalArgumentException {

		for (AFullNDimensionalMatrix<T> m : jointDistributionSet) {
			for (Attribute<? extends IValue> a : m.getDimensions()) { if (a.getAttributeName().equals(name)) return a; }
		}

		throw new IllegalArgumentException(
				"No dimension named " + name + "; available dimensions are: "
						+ jointDistributionSet.stream().map(m -> m.getDimensions().stream()
								.map(Attribute::getAttributeName).reduce("", (u, t) -> u + "," + t))
								.reduce("", (u, t) -> u + "," + t));

	}

	@Override
	public String getLabel() { return label; }

	/**
	 * Returns the genesis of the matrix, that is the successive steps that brought it to its current state. Useful to
	 * expose meaningful error messages to the user.
	 *
	 * @return
	 */
	public List<String> getGenesisAsList() { return Collections.unmodifiableList(genesis); }

	/**
	 * Returns the genesis of the matrix, that is the successive steps that brought it to its current state. Useful to
	 * expose meaningful error messages to the user.
	 *
	 * @return
	 */
	@Override
	public String getGenesisAsString() { return String.join("->", genesis); }

	/**
	 * imports into this matrix the genesis of another one. Should be called after creating a matrix to keep a memory of
	 * where it comes from.
	 *
	 * @param o
	 */
	@Override
	public void inheritGenesis(final AFullNDimensionalMatrix<?> o) {
		genesis.addAll(o.getGenesisAsList());
	}

	/**
	 * add one line to the genesis (history) of this matrix. This line should better be kept quiet short for
	 * readibility.
	 *
	 * @param step
	 */
	@Override
	public void addGenesis(final String step) {
		genesis.add(step);
	}

}
