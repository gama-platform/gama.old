package msi.gaml.statements.draw;

public class GrayscaleMeshColorProvider implements IMeshColorProvider {

	@Override
	public double[] getColor(final int index, final double z, final double min, final double max, final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = new double[3]; }
		result[0] = result[1] = result[2] = (z - min) / (max - min);
		return result;
	}

}
