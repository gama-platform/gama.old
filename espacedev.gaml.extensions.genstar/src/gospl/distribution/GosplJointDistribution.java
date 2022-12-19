/*******************************************************************************************************
 *
 * GosplJointDistribution.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution;

import java.util.Map;
import java.util.Set;

import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import core.util.data.GSDataParser;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.control.ControlFrequency;
import gospl.distribution.matrix.coordinate.ACoordinate;

/**
 *
 * @author kevinchapuis
 *
 */
public class GosplJointDistribution extends AFullNDimensionalMatrix<Double> {

	/** The min. */
	private final Double min = Math.pow(10, -8);

	/**
	 * Instantiates a new gospl joint distribution.
	 *
	 * @param matrix
	 *            the matrix
	 */
	protected GosplJointDistribution(
			final Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Double>> matrix) {
		super(matrix);
	}

	/**
	 * Instantiates a new gospl joint distribution.
	 *
	 * @param dimensions
	 *            the dimensions
	 * @param metaDataType
	 *            the meta data type
	 */
	public GosplJointDistribution(final Set<Attribute<? extends IValue>> dimensions, final GSSurveyType metaDataType) {
		super(dimensions, metaDataType);
	}

	// ----------------------- SETTER CONTRACT ----------------------- //

	@Override
	public boolean addValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinates,
			final AControl<? extends Number> value) {
		if (matrix.containsKey(coordinates)) return false;
		return setValue(coordinates, value);
	}

	@Override
	public final boolean addValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinates,
			final Double value) {
		return addValue(coordinates, new ControlFrequency(value));
	}

	@Override
	public boolean setValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate,
			final AControl<? extends Number> value) {
		if (isCoordinateCompliant(coordinate)) {
			coordinate.setHashIndex(matrix.size());
			matrix.put(coordinate, new ControlFrequency(value.getValue().doubleValue()));
			return true;
		}
		return false;
	}

	@Override
	public final boolean setValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate,
			final Double value) {
		return setValue(coordinate, new ControlFrequency(value));
	}

	// ----------------------- CONTRACT ----------------------- //

	@Override
	public AControl<Double> getNulVal() { return new ControlFrequency(0d); }

	@Override
	public AControl<Double> getIdentityProductVal() { return new ControlFrequency(1d); }

	@Override
	public AControl<Double> getAtomicVal() { return new ControlFrequency(min / this.size()); }

	@Override
	public AControl<Double> parseVal(final GSDataParser parser, final String val) {
		if (!parser.getValueType(val).isNumericValue()) return getNulVal();
		return new ControlFrequency(parser.getDouble(val));
	}

	@Override
	public void normalize() throws IllegalArgumentException {

		Double total = getVal().getValue();

		for (AControl<Double> c : getMatrix().values()) { c.multiply(1 / total); }

	}

}
