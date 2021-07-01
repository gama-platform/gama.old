package msi.gaml.statements.draw;

import msi.gama.util.GamaColor;
import msi.gaml.operators.Colors.GamaPalette;

public class PaletteBasedMeshColorProvider implements IMeshColorProvider {

	final double[] components;
	final int size;

	public PaletteBasedMeshColorProvider(final GamaPalette palette) {
		this.size = palette.size();
		components = new double[size * 3];
		for (int i = 0; i < size; i++) {
			GamaColor c = palette.get(i);
			components[i * 3] = c.getRed() / 255d;
			components[i * 3 + 1] = c.getGreen() / 255d;
			components[i * 3 + 2] = c.getBlue() / 255d;
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
			result[0] = components[3 * (size - 1)];
			result[1] = components[3 * (size - 1) + 1];
			result[2] = components[3 * (size - 1) + 2];
		} else {
			double intervalSize = (max - min) / (size - 1);
			double interval = (z - min) / intervalSize;
			double r = interval - (int) interval, ir = 1 - r;
			result[0] = components[(int) interval * 3] * ir + components[(int) (interval + 1) * 3] * r;
			result[1] = components[(int) interval * 3 + 1] * ir + components[(int) (interval + 1) * 3 + 1] * r;
			result[2] = components[(int) interval * 3 + 2] * ir + components[(int) (interval + 1) * 3 + 2] * r;
		}
		return result;
	}

}
