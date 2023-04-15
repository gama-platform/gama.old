/*******************************************************************************************************
 *
 * GaussianBlurMeshSmoothProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

/**
 * The Class GaussianBlurMeshSmoothProvider. A "smoothing" algorithm based on an efficient approximation of a Gaussian
 * Blur. See http://blog.ivank.net/fastest-gaussian-blur.html *
 */
public class GaussianBlurMeshSmoothProvider implements IMeshSmoothProvider {

	@Override
	public double[] smooth(final int cols, final int rows, final double[] data, final double noData, final int passes) {
		final var result = data.clone();
		int nbBoxes = 3;
		var wIdeal = Math.sqrt(12 * passes * passes / nbBoxes + 1); // Ideal averaging filter width
		double wl = Math.floor(wIdeal);
		if (wl % 2 == 0) { wl--; }
		double wu = wl + 2;
		var mIdeal = (12 * passes * passes - nbBoxes * wl * wl - 4 * nbBoxes * wl - 3 * nbBoxes) / (-4 * wl - 4);
		var m = Math.round(mIdeal);
		double[] sizes = new double[nbBoxes];
		for (var i = 0; i < nbBoxes; i++) { sizes[i] = i < m ? wl : wu; } // number of "boxes"
		var boxes = sizes;
		int r = (int) Math.round((boxes[0] - 1) / 2);
		boxBlurHorizontal(cols, rows, result, r);
		boxBlurVertical(cols, rows, result, r);
		r = (int) Math.round((boxes[1] - 1) / 2);
		boxBlurHorizontal(cols, rows, result, r);
		boxBlurVertical(cols, rows, result, r);
		r = (int) Math.round((boxes[2] - 1) / 2);
		boxBlurHorizontal(cols, rows, result, r);
		boxBlurVertical(cols, rows, result, r);
		return result;
	}

	/**
	 * Box blur operating vertically
	 */
	void boxBlurVertical(final int cols, final int rows, final double[] scl, final int r) {
		double iarr = 1d / (r + r + 1);
		for (var i = 0; i < rows; i++) {
			var ti = i * cols;
			var li = ti;
			var ri = ti + r;
			var fv = scl[ti];
			var lv = scl[ti + cols - 1];
			var val = (r + 1) * fv;
			for (var j = 0; j < r; j++) { val += scl[ti + j]; }
			for (var j = 0; j <= r; j++) {
				val += scl[ri++] - fv;
				scl[ti++] = val * iarr;
			}
			for (var j = r + 1; j < cols - r; j++) {
				val += scl[ri++] - scl[li++];
				scl[ti++] = val * iarr;
			}
			for (var j = cols - r; j < cols; j++) {
				val += lv - scl[li++];
				scl[ti++] = val * iarr;
			}
		}
	}

	/**
	 * Box blur operating horizontally
	 *
	 * @return the function
	 */
	void boxBlurHorizontal(final int cols, final int rows, final double[] scl, final int r) {
		double iarr = 1d / (r + r + 1);
		for (var i = 0; i < cols; i++) {
			var ti = i;
			var li = ti;
			var ri = ti + r * cols;
			var fv = scl[ti];
			var lv = scl[ti + cols * (rows - 1)];
			var val = (r + 1) * fv;
			for (var j = 0; j < r; j++) { val += scl[ti + j * rows]; }
			for (var j = 0; j <= r; j++) {
				val += scl[ri] - fv;
				scl[ti] = val * iarr;
				ri += cols;
				ti += cols;
			}
			for (var j = r + 1; j < rows - r; j++) {
				val += scl[ri] - scl[li];
				scl[ti] = val * iarr;
				li += cols;
				ri += cols;
				ti += cols;
			}
			for (var j = rows - r; j < rows; j++) {
				val += lv - scl[li];
				scl[ti] = val * iarr;
				li += cols;
				ti += cols;
			}
		}
	}

}
