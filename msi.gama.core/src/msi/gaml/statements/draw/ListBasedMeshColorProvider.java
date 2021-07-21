package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.List;

/**
 * A simple implementation of the color provider that picks a color using the index of the cell being drawn (in a cyclic
 * manner so as to allow lists of colors with a smaller size than the field).
 *
 * @author drogoul
 *
 */
public class ListBasedMeshColorProvider implements IMeshColorProvider {

	private final double[] components;
	private final int size;

	public ListBasedMeshColorProvider(final List<? extends Color> colors) {
		this.size = colors.size();
		components = new double[size * 3];
		for (int i = 0; i < size; ++i) {
			components[i * 3] = colors.get(i).getRed() / 255d;
			components[i * 3 + 1] = colors.get(i).getGreen() / 255d;
			components[i * 3 + 2] = colors.get(i).getBlue() / 255d;
		}
	}

	@Override
	public double[] getColor(final int index, final double elevation, final double min, final double max,
			final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = new double[3]; }
		int i = index % size;
		result[0] = components[i * 3];
		result[1] = components[i * 3 + 1];
		result[2] = components[i * 3 + 2];
		return result;
	}

}
