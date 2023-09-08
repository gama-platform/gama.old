/*******************************************************************************************************
 *
 * ConvolutionBasedMeshSmoothProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

/**
 * The Class ConvolutionBasedMeshSmoothProvider. A "smoothing" algorithm based on convolution. Slow, but can take into
 * account the noData value
 */
public class ConvolutionBasedMeshSmoothProvider implements IMeshSmoothProvider {

	@Override
	public double[] smooth(final int cols, final int rows, final double[] data, final double noData, final int passes) {
		var input = data;
		var output = new double[input.length];
		for (var i = 0; i < passes; i++) {
			for (var y = 0; y < rows; ++y) {
				for (var x = 0; x < cols; ++x) {
					double z00 = get(cols, rows, input, x - 1, y - 1), z01 = get(cols, rows, input, x - 1, y - 1),
							z02 = get(cols, rows, input, x + 1, y - 1), z03 = get(cols, rows, input, x - 1, y),
							z = get(cols, rows, input, x, y), z05 = get(cols, rows, input, x + 1, y),
							z06 = get(cols, rows, input, x - 1, y + 1), z07 = get(cols, rows, input, x, y + 1),
							z08 = get(cols, rows, input, x + 1, y + 1);
					if (z00 == noData || z01 == noData || z02 == noData || z03 == noData || z == noData || z05 == noData
							|| z06 == noData || z07 == noData || z08 == noData) {
						continue;
					}
					// Sample a 3x3 filtering grid based on surrounding neighbors
					output[x + y * cols] = (z00 + z01 + z02 + z03 + z + z05 + z06 + z07 + z08) / 9d;
				}
			}
			input = output;
		}
		return output;

	}

}
