package msi.gaml.statements.draw;

import msi.gama.common.preferences.GamaPreferences;

public interface IMeshColorProvider {

	IMeshColorProvider DEFAULT = new ColorBasedMeshColorProvider(GamaPreferences.Displays.CORE_COLOR.getValue());
	IMeshColorProvider GRAYSCALE = new GrayscaleMeshColorProvider();

	/**
	 * The main method. Should fill an array of rgb components, between 0 and 1
	 *
	 * @param index
	 *            the index of the cell to be drawn
	 * @param z
	 *            the altitude at this index
	 * @param min
	 *            the minimal altitude in the whole field
	 * @param max
	 *            the maximal altitude in the whole field
	 * @param rgb
	 *            the components of the color (to be filled). Can be null, in which case a new array should be
	 *            allocated.
	 * @return the rgb array passed in parameter or a newly allocated array, filled with the red, green and blue
	 *         components of the color (each between 0 and 1)
	 */
	double[] getColor(int index, double z, double min, double max, double[] rgb);

}
