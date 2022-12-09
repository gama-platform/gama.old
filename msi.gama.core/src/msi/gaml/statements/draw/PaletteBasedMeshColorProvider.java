/*******************************************************************************************************
 *
 * PaletteBasedMeshColorProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gaml.operators.Colors.GamaPalette;

/**
 * The Class PaletteBasedMeshColorProvider.
 */
public class PaletteBasedMeshColorProvider implements IMeshColorProvider {

	/** The components. */
	final double[] components;

	/** The size. */
	final int size;

	/**
	 * Instantiates a new palette based mesh color provider.
	 *
	 * @param palette
	 *            the palette
	 */
	public PaletteBasedMeshColorProvider(final GamaPalette palette) {
		this.size = palette.size();
		components = new double[size * 3];
		for (var i = 0; i < size; i++) {
			var c = palette.get(i);
			components[i * 3] = c.getRed() / 255d;
			components[i * 3 + 1] = c.getGreen() / 255d;
			components[i * 3 + 2] = c.getBlue() / 255d;
		}
	}

	@Override
	public double[] getColor(final int index, final double z, final double min, final double max, final double[] rgb) {
		var result = rgb;
		if (result == null) { result = new double[3]; }
		if (min > max || z <= min) {
			result[0] = components[0];
			result[1] = components[1];
			result[2] = components[2];
		} else if (z >= max) { // Can happen if multiple threads write and read the field !
			result[0] = components[3 * (size - 1)];
			result[1] = components[3 * (size - 1) + 1];
			result[2] = components[3 * (size - 1) + 2];
		} else {
			double intervalSize = (max - min) / (size - 1);
			double intervald = (z - min) / intervalSize;
			int intervali = (int) intervald;
			double r = intervald - intervali, ir = 1d - r;
			result[0] = components[intervali * 3] * ir + components[(intervali + 1) * 3] * r;
			result[1] = components[intervali * 3 + 1] * ir + components[(intervali + 1) * 3 + 1] * r;
			result[2] = components[intervali * 3 + 2] * ir + components[(intervali + 1) * 3 + 2] * r;
		}
		return result;
	}

}
