/*******************************************************************************************************
 *
 * GosplContingencyTable.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.control.ControlContingency;
import gospl.distribution.matrix.coordinate.ACoordinate;

/**
 * Complete n dimensional matrix with contingency cell, which means internal storage data are integers.
 *
 * @see AFullNDimensionalMatrix
 *
 * @author kevinchapuis
 *
 */
public class GosplContingencyTable extends AFullNDimensionalMatrix<Integer> {

	/**
	 * Instantiates a new gospl contingency table.
	 *
	 * @param attributes the attributes
	 */
	public GosplContingencyTable(final Set<Attribute<? extends IValue>> attributes) {
		super(attributes, GSSurveyType.ContingencyTable);
	}

	/**
	 * Instantiates a new gospl contingency table.
	 *
	 * @param matrix the matrix
	 */
	protected GosplContingencyTable(
			final Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Integer>> matrix) {
		super(new ConcurrentHashMap<>(matrix));
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
			final Integer value) {
		return addValue(coordinates, new ControlContingency(value));
	}

	@Override
	public boolean setValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate,
			final AControl<? extends Number> value) {
		if (isCoordinateCompliant(coordinate)) {
			coordinate.setHashIndex(matrix.size());
			matrix.put(coordinate, new ControlContingency(value.getValue().intValue()));
			return true;
		}
		return false;
	}

	@Override
	public final boolean setValue(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate,
			final Integer value) {
		return setValue(coordinate, new ControlContingency(value));
	}

	// ----------------------- SIDE CONTRACT ----------------------- //

	@Override
	public AControl<Integer> getNulVal() { return new ControlContingency(0); }

	@Override
	public AControl<Integer> getIdentityProductVal() { return new ControlContingency(1); }

	@Override
	public AControl<Integer> getAtomicVal() { return new ControlContingency(1); }

	@Override
	public AControl<Integer> parseVal(final GSDataParser parser, final String val) {
		if (!GSEnumDataType.Integer.equals(parser.getValueType(val))) return getNulVal();
		return new ControlContingency(Integer.valueOf(val));
	}

	@Override
	public boolean checkGlobalSum() {
		// always true
		return true;
	}

	@Override
	public void normalize() throws IllegalArgumentException {

		throw new IllegalArgumentException("should not normalize a " + getMetaDataType());

	}

}
