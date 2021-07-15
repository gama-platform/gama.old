package msi.gaml.statements.draw;

import msi.gama.util.GamaColor;
import msi.gaml.operators.Colors.GamaGradient;

public class GradientBasedMeshColorProvider implements IMeshColorProvider {

	final double[] components;
	final int size;

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

	@Override
	public double[] getColor(final int index, final double z, final double min, final double max, final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = new double[3]; }
		if (min > max || z == min) {
			result[0] = components[0];
			result[1] = components[1];
			result[2] = components[2];
		} else if (z >= max) { // Can happen if multiple threads write and read the field !
			result[0] = components[4 * (size - 1)];
			result[1] = components[4 * (size - 1) + 1];
			result[2] = components[4 * (size - 1) + 2];
		} else {
			double position = (z - min) / (max - min);
			double currentLimit = 0, previousLimit = 0;
			for (int i = 0; i < size; i++) {
				previousLimit = currentLimit;
				currentLimit += components[i * 4 + 3];
				if (position < currentLimit) {
					double middle = (currentLimit - previousLimit) / 2;
					if (position - previousLimit < middle && i > 0) {
						previousLimit = previousLimit - components[(i - 1) * 4 + 3] / 2;
						currentLimit = middle;
						double r = (position - previousLimit) / (currentLimit - previousLimit);
						double ir = 1d - r;
						result[0] = components[(i - 1) * 4] * r + components[i * 4] * ir;
						result[1] = components[(i - 1) * 4 + 1] * r + components[i * 4 + 1] * ir;
						result[2] = components[(i - 1) * 4 + 2] * r + components[i * 4 + 2] * ir;
					} else if (position - previousLimit > middle && i < size - 1) {
						previousLimit = middle;
						currentLimit = currentLimit + components[(i + 1) * 4 + 3] / 2;
						double r = (position - previousLimit) / (currentLimit - previousLimit);
						double ir = 1d - r;
						result[0] = components[i * 4] * r + components[(i + 1) * 4] * ir;
						result[1] = components[i * 4 + 1] * r + components[(i + 1) * 4 + 1] * ir;
						result[2] = components[i * 4 + 2] * r + components[(i + 1) * 4 + 2] * ir;
					} else {
						// equals or we are in the extreme buckets
						result[0] = components[4 * i];
						result[1] = components[4 * i + 1];
						result[2] = components[4 * i + 2];
					}
					break;
				}
			}
		}
		return result;
	}

}
