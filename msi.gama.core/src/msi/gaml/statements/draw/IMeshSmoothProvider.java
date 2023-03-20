/*******************************************************************************************************
 *
 * IMeshSmoothProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.util.matrix.IField;

/**
 * The Interface IMeshSmoothProvider.
 */
public interface IMeshSmoothProvider {

	/**
	 * For. If smooth is not asked for, we return the null implementation. If we have a complete field (i.e. no "no
	 * data" cells) we opt for the super-fast gaussian blur. If not we go back to the slow, but robust, 3x3 convolution
	 *
	 * @param smooth
	 *            the smooth
	 * @param noData
	 *            the no data
	 * @return the i mesh smooth provider
	 */
	static IMeshSmoothProvider FOR(final int smooth, final double noData) {
		return smooth == 0 ? NULL : noData == IField.NO_NO_DATA ? FAST : SLOW;
	}

	/** An implementation that does not change the data. */
	IMeshSmoothProvider NULL = (cols, rows, data, noData, passes) -> data;

	/** The fast. */
	IMeshSmoothProvider FAST = new GaussianBlurMeshSmoothProvider();

	/** The slow. */
	IMeshSmoothProvider SLOW = new ConvolutionBasedMeshSmoothProvider();

	/**
	 * Smooth. Applies a "smoothing" algorithm to the data so as to soften the visualisation of fields. Usually implies
	 * some diffusion too.
	 *
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @param data
	 *            the data
	 * @param noData
	 *            the no data
	 * @param passes
	 *            the passes
	 */
	double[] smooth(final int cols, final int rows, final double[] data, double noData, final int passes);

	/**
	 * A safe way to access the value of the data
	 *
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @param data
	 *            the data
	 * @param x0
	 *            the x 0
	 * @param y0
	 *            the y 0
	 * @return the double
	 */
	default double get(final int cols, final int rows, final double[] data, final int x0, final int y0) {
		var x = x0 < 0 ? 0 : x0 > cols - 1 ? cols - 1 : x0;
		var y = y0 < 0 ? 0 : y0 > rows - 1 ? rows - 1 : y0;
		return data[y * cols + x];
	}

}
