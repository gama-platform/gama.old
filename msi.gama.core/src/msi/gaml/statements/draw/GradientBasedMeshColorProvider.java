/*******************************************************************************************************
 *
 * GradientBasedMeshColorProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
		DEBUG.OFF();
	}

	/** The RGB+stop components. */
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
		// each pair color::float represents a stop from 0 to 1
		this.size = palette.size();

		components = new double[size * 5]; // last value is the stop position

		int i = 0;
		for (GamaColor c : palette.keySet()) {
			components[i * 4] = c.getRed() / 255d;
			components[i * 4 + 1] = c.getGreen() / 255d;
			components[i * 4 + 2] = c.getBlue() / 255d;
			components[i * 4 + 3] = c.getAlpha() / 255d;
			components[i * 4 + 4] = palette.get(c);
			i++;
		}
	}

	@Override
	public double[] getColor(final int index, final double z, final double min, final double max, final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = newArray(); }
		if (z <= min || max <= min) return components;
		double position = (z - min) / (max - min);
		// DEBUG.OUT("Position " + position + " corresponds to slot ", false);
		for (int s = 0; s < size - 1; s++) {
			var leftStop = components[s * 4 + 4];
			var rightStop = components[(s + 1) * 4 + 4];
			if (position <= leftStop) {
				// DEBUG.OUT(s);
				result[0] = components[4 * s];
				result[1] = components[4 * s + 1];
				result[2] = components[4 * s + 2];
				result[3] = components[4 * s + 3];
				return result;
			}
			if (position < rightStop) {
				double r = (position - leftStop) / (rightStop - leftStop);
				// DEBUG.OUT(s + " with a ratio of " + r);
				double ir = 1d - r;
				result[0] = components[s * 4] * ir + components[(s + 1) * 4] * r;
				result[1] = components[s * 4 + 1] * ir + components[(s + 1) * 4 + 1] * r;
				result[2] = components[s * 4 + 2] * ir + components[(s + 1) * 4 + 2] * r;
				result[3] = 1d; // Math.max(components[s * 4 + 3], components[(s + 1) * 4 + 3]);
				return result;
			}
		}
		// DEBUG.OUT(size - 1);
		result[0] = components[4 * (size - 1)];
		result[1] = components[4 * (size - 1) + 1];
		result[2] = components[4 * (size - 1) + 2];
		result[3] = 1d; // components[4 * (size - 1) + 3];
		return result;
	}

}
