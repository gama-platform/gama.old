package msi.gaml.statements.draw;

import msi.gama.util.GamaColor;

public class ColorBasedMeshColorProvider implements IMeshColorProvider {

	final double r, g, b;

	public ColorBasedMeshColorProvider(final GamaColor c) {
		r = c.getRed() / 255d;
		g = c.getGreen() / 255d;
		b = c.getBlue() / 255d;
	}

	@Override
	public double[] getColor(final int index, final double z, final double min, final double max, final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = new double[3]; }
		double ratio = (z - min) * 1.5 / (max - min); // we lighten it a bit
		result[0] = r * ratio;
		result[1] = g * ratio;
		result[2] = b * ratio;
		return result;
	}

}
