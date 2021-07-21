package msi.gaml.statements.draw;

import java.util.List;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IField;

/**
 * An implementation of the color provider that picks a color using the index of the cell being drawn in the R, G, and B
 * bands defined in the field. Invoked by using: 'color: my_field.bands'
 *
 * @author drogoul
 *
 */
public class BandsBasedMeshColorProvider implements IMeshColorProvider {

	private final double[] components;
	private final int size;

	public BandsBasedMeshColorProvider(final List<IField> bands) {
		if (bands.size() < 4)
			throw GamaRuntimeException.error("Number of bands should be at least 3 ", GAMA.getRuntimeScope());
		IField primary = bands.get(0);
		this.size = primary.getMatrix().length;
		components = new double[size * 3];
		for (int i = 0; i < size; ++i) {
			components[i * 3] = bands.get(1).getMatrix()[i] / 255d;
			components[i * 3 + 1] = bands.get(2).getMatrix()[i] / 255d;
			components[i * 3 + 2] = bands.get(3).getMatrix()[i] / 255d;
		}
	}

	@Override
	public double[] getColor(final int index, final double elevation, final double min, final double max,
			final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = new double[3]; }
		result[0] = components[index * 3];
		result[1] = components[index * 3 + 1];
		result[2] = components[index * 3 + 2];
		return result;
	}

}
