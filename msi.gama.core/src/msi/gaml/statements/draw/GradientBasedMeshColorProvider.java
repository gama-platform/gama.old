/*******************************************************************************************************
 *
 * GradientBasedMeshColorProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.util.GamaColor;
import msi.gaml.operators.Colors.GamaGradient;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GradientBasedMeshColorProvider.
 */
public class GradientBasedMeshColorProvider implements IMeshColorProvider {

	static {
		DEBUG.ON();
	}

	/** The components. */
	final double[] components;

	/** The size. */
	final int size;

	/**
	 * Instantiates a new gradient based mesh color provider.
	 *
	 * @param palette
	 *            the palette
	 */
	public GradientBasedMeshColorProvider(final GamaGradient palette) {
		// each pair color::float represents an interval
		this.size = palette.size();

		components = new double[size * 4]; // last value is the weight

		int i = 0;
		for (GamaColor c : palette.keySet()) {
			components[i * 4] = c.getRed() / 255d;
			components[i * 4 + 1] = c.getGreen() / 255d;
			components[i * 4 + 2] = c.getBlue() / 255d;
			components[i * 4 + 3] = palette.get(c);
			i++;
		}
	}

	/**
	 * Lerp gradient.
	 *
	 * @param colors
	 *            the colors
	 * @return the color
	 */
	// Color lerpGradient(final List<Color> colors, List<double> stops, double t) {
	// for (var s = 0; s < stops.length - 1; s++) {
	// final leftStop = stops[s], rightStop = stops[s + 1];
	// final leftColor = colors[s], rightColor = colors[s + 1];
	// if (t <= leftStop) {
	// return leftColor;
	// } else if (t < rightStop) {
	// final sectionT = (t - leftStop) / (rightStop - leftStop);
	// return Color.lerp(leftColor, rightColor, sectionT);
	// }
	// }
	// return colors.last;
	// }

	@Override
	public double[] getColor(final int index, final double z, final double min, final double max, final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = new double[3]; }

		double position = (z - min) / (max - min);
		DEBUG.OUT("Position " + position + " corresponds to slot ", false);
		for (int s = 0; s < size - 1; s++) {
			var leftStop = components[s * 4 + 3];
			var rightStop = components[(s + 1) * 4 + 3];
			if (position <= leftStop) {
				DEBUG.OUT(s);
				result[0] = components[4 * s];
				result[1] = components[4 * s + 1];
				result[2] = components[4 * s + 2];
				return result;
			}
			if (position < rightStop) {
				double r = (position - leftStop) / (rightStop - leftStop);
				DEBUG.OUT(s + " with a ratio of " + r);
				double ir = 1d - r;
				result[0] = components[s * 4] * ir + components[(s + 1) * 4] * r;
				result[1] = components[s * 4 + 1] * ir + components[(s + 1) * 4 + 1] * r;
				result[2] = components[s * 4 + 2] * ir + components[(s + 1) * 4 + 2] * r;
				return result;
			}
		}
		DEBUG.OUT(size - 1);
		result[0] = components[4 * (size - 1)];
		result[1] = components[4 * (size - 1) + 1];
		result[2] = components[4 * (size - 1) + 2];
		return result;

		// if (min > max || z == min) {
		// result[0] = components[0];
		// result[1] = components[1];
		// result[2] = components[2];
		// } else if (z >= max) { // Can happen if multiple threads write and read the field !
		// result[0] = components[4 * (size - 1)];
		// result[1] = components[4 * (size - 1) + 1];
		// result[2] = components[4 * (size - 1) + 2];
		// } else {
		// double currentLimit = 0, previousLimit = 0;
		// for (int i = 0; i < size; i++) {
		// previousLimit = currentLimit;
		// currentLimit += components[i * 4 + 3];
		// if (position < currentLimit) {
		// double middle = (currentLimit - previousLimit) / 2;
		// if (position - previousLimit < middle && i > 0) {
		// previousLimit = previousLimit - components[(i - 1) * 4 + 3] / 2;
		// currentLimit = middle;
		// double r = (position - previousLimit) / (currentLimit - previousLimit);
		// double ir = 1d - r;
		// result[0] = components[(i - 1) * 4] * r + components[i * 4] * ir;
		// result[1] = components[(i - 1) * 4 + 1] * r + components[i * 4 + 1] * ir;
		// result[2] = components[(i - 1) * 4 + 2] * r + components[i * 4 + 2] * ir;
		// } else if (position - previousLimit > middle && i < size - 1) {
		// previousLimit = middle;
		// currentLimit = currentLimit + components[(i + 1) * 4 + 3] / 2;
		// double r = (position - previousLimit) / (currentLimit - previousLimit);
		// double ir = 1d - r;
		// result[0] = components[i * 4] * r + components[(i + 1) * 4] * ir;
		// result[1] = components[i * 4 + 1] * r + components[(i + 1) * 4 + 1] * ir;
		// result[2] = components[i * 4 + 2] * r + components[(i + 1) * 4 + 2] * ir;
		// } else {
		// // equals or we are in the extreme buckets
		// result[0] = components[4 * i];
		// result[1] = components[4 * i + 1];
		// result[2] = components[4 * i + 2];
		// }
		// break;
		// }
		// }
		// }
		// return result;
	}

}
