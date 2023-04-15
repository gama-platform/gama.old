/*******************************************************************************************************
 *
 * IMeshColorProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.preferences.GamaPreferences;

/**
 * The Interface IMeshColorProvider.
 */
public interface IMeshColorProvider {

	/** The default. */
	IMeshColorProvider DEFAULT = new ColorBasedMeshColorProvider(GamaPreferences.Displays.CORE_COLOR.getValue());

	/** The grayscale. */
	IMeshColorProvider GRAYSCALE = new GrayscaleMeshColorProvider();

	/**
	 * The main method. Should fill an array of 4 rgb components, between 0 and 1
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
	 * @return the rgb array passed in parameter or a newly allocated array, filled with the red, green, blue and alpha
	 *         components of the color (each between 0 and 1)
	 */
	double[] getColor(int index, double z, double min, double max, double[] rgb);

	/**
	 * New array.
	 *
	 * @return the double[]
	 */
	default double[] newArray() {
		double[] d = new double[4];
		d[3] = 1;
		return d;
	}

}
