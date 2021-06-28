package msi.gama.util.file;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaField;
import msi.gama.util.matrix.IField;

/**
 * An interface for all the files/structures able to return either a GamaFloatMatrix or an array of double, suitable to
 * be used in a field, from their contents. Should allow to bypass the creation of useless structures (like geometries
 * in grid files) if only the numerical contents is required
 *
 * @author Alexis Drogoul 2021
 *
 */
public interface IFieldMatrixProvider {

	/**
	 * Returns the field provided by this provider. No assumption should be made on the status of this matrix (whether
	 * it can be modified in place, should be cloned...). By default builds a new field that will use the information
	 * provided by the other methods.
	 *
	 * @param scope
	 * @return
	 */
	@Nonnull
	default IField getField(final IScope scope) {
		return new GamaField(scope, this);
	}

	/**
	 * Returns the value that will serve as a reference for the cells with "no value". Default is Double.MAX_VALUE.
	 * Grid/raster files can define other values.
	 *
	 * @param scope
	 * @return a value representing "no value"
	 */
	default double getNoData(final IScope scope) {
		return Double.MAX_VALUE;
	}

	/**
	 * Returns the number of rows of the receiver
	 *
	 * @param scope
	 * @return
	 */
	@Nonnegative
	int getRows(IScope scope);

	/**
	 * Returns the number of columns of the receiver
	 *
	 * @param scope
	 * @return
	 */
	@Nonnegative
	int getCols(IScope scope);

	/**
	 * Returns the number of bands of the receiver. Default is 1 (i.e. only primary data)
	 *
	 * @param scope
	 * @return the number of bands.
	 */

	@Nonnegative
	default int getBandsNumber(final IScope scope) {
		return 1;
	}

	/**
	 * Returns the array of double values that will make up the contents of the field. By default, returns the 1st band
	 * of the receiver.
	 *
	 * @param scope
	 * @return an array of double, possibly empty, never null
	 */
	@Nonnull
	default double[] getFieldData(final IScope scope) {
		return getBand(scope, 0);
	}

	/**
	 * Returns one of the "bands" (when it makes sense) of the receiver. The first band corresponds to the primary data.
	 * Should throw a runtime exception when attempt is made to get non-existing bands (i.e. bands with an index >
	 * getBands(..) - 1)
	 *
	 * @param scope
	 * @param index
	 * @return
	 */
	double[] getBand(IScope scope, int index) throws GamaRuntimeException;

}
