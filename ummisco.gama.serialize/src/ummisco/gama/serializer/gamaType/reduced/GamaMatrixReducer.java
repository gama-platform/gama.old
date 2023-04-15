/*******************************************************************************************************
 *
 * GamaMatrixReducer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IType;

/**
 * The Class GamaMatrixReducer.
 */
@SuppressWarnings ({ "rawtypes" })
public class GamaMatrixReducer {

	/** The content type matrix reducer. */
	private final IType contentTypeMatrixReducer;

	/** The values matrix reducer. */
	private final IList valuesMatrixReducer;

	/** The n rows. */
	private final int nRows;

	/** The n cols. */
	private final int nCols;

	/**
	 * Instantiates a new gama matrix reducer.
	 *
	 * @param scope
	 *            the scope
	 * @param m
	 *            the m
	 */
	public GamaMatrixReducer(final IScope scope, final IMatrix<?> m) {
		contentTypeMatrixReducer = m.getGamlType().getContentType();
		nRows = m.getRows(null);
		nCols = m.getCols(null);
		valuesMatrixReducer = m.listValue(scope, contentTypeMatrixReducer, true);
	}

	/**
	 * Construct object.
	 *
	 * @param scope
	 *            the scope
	 * @return the gama matrix
	 */
	public GamaMatrix constructObject(final IScope scope) {
		return (GamaMatrix) GamaMatrixType.from(scope, valuesMatrixReducer, contentTypeMatrixReducer,
				new GamaPoint(nCols, nRows));

	}
}
