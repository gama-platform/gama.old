package msi.gama.util.file;

import msi.gama.runtime.IScope;
import msi.gama.util.matrix.GamaFloatMatrix;

/**
 * An interface for all the files able to return either a GamaFloatMatrix or an array of double, suitable to be used in
 * a field, from their contents. Should allow to bypass the creation of useless structures (like geometries in grid
 * files) if only the numerical contents is required
 *
 * @author Alexis Drogoul 2021
 *
 */
public interface IFieldMatrixProvider {

	/**
	 * Returns the matrix provided by this provider. No assumption should be made on the status of this matrix (whether
	 * it can be modified in place, should be cloned...)
	 *
	 * @param scope
	 * @return
	 */
	default GamaFloatMatrix getMatrix(final IScope scope) {
		return new GamaFloatMatrix(getCols(scope), getRows(scope), getFieldData(scope));
	}

	int getRows(IScope scope);

	int getCols(IScope scope);

	int getBands(IScope scope);

	default double[] getFieldData(final IScope scope) {
		return getBand(scope, 0);
	}

	double[] getBand(IScope scope, int index);

}
