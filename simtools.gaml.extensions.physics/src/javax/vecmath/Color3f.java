/*******************************************************************************************************
 *
 * Color3f.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;

import java.awt.Color;

/**
 * A three-element color value represented by single precision floating point x,y,z values. The x,y,z values represent
 * the red, green, and blue color values, respectively. Color components should be in the range of [0.0, 1.0].
 * <p>
 * Java 3D assumes that a linear (gamma-corrected) visual is used for all colors.
 *
 */
public class Color3f extends Tuple3f implements java.io.Serializable {

	/** The Constant serialVersionUID. */
	// Compatible with 1.1
	static final long serialVersionUID = -1861792981817493659L;

	/**
	 * Constructs and initializes a Color3f from the three xyz values.
	 *
	 * @param x
	 *            the red color value
	 * @param y
	 *            the green color value
	 * @param z
	 *            the blue color value
	 */
	public Color3f(final float x, final float y, final float z) {
		super(x, y, z);
	}

	/**
	 * Constructs and initializes a Color3f from the array of length 3.
	 *
	 * @param v
	 *            the array of length 3 containing xyz in order
	 */
	public Color3f(final float[] v) {
		super(v);
	}

	/**
	 * Constructs and initializes a Color3f from the specified Color3f.
	 *
	 * @param v1
	 *            the Color3f containing the initialization x y z data
	 */
	public Color3f(final Color3f v1) {
		super(v1);
	}

	/**
	 * Constructs and initializes a Color3f from the specified Tuple3f.
	 *
	 * @param t1
	 *            the Tuple3f containing the initialization x y z data
	 */
	public Color3f(final Tuple3f t1) {
		super(t1);
	}

	/**
	 * Constructs and initializes a Color3f from the specified Tuple3d.
	 *
	 * @param t1
	 *            the Tuple3d containing the initialization x y z data
	 */
	public Color3f(final Tuple3d t1) {
		super(t1);
	}

	/**
	 * Constructs and initializes a Color3f from the specified AWT Color object. The alpha value of the AWT color is
	 * ignored. No conversion is done on the color to compensate for gamma correction.
	 *
	 * @param color
	 *            the AWT color with which to initialize this Color3f object
	 *
	 * @since vecmath 1.2
	 */
	public Color3f(final Color color) {
		super(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
	}

	/**
	 * Constructs and initializes a Color3f to (0.0, 0.0, 0.0).
	 */
	public Color3f() {
	}

	/**
	 * Sets the r,g,b values of this Color3f object to those of the specified AWT Color object. No conversion is done on
	 * the color to compensate for gamma correction.
	 *
	 * @param color
	 *            the AWT color to copy into this Color3f object
	 *
	 * @since vecmath 1.2
	 */
	public final void set(final Color color) {
		x = color.getRed() / 255.0f;
		y = color.getGreen() / 255.0f;
		z = color.getBlue() / 255.0f;
	}

	/**
	 * Returns a new AWT color object initialized with the r,g,b values of this Color3f object.
	 *
	 * @return a new AWT Color object
	 *
	 * @since vecmath 1.2
	 */
	public final Color get() {
		int r = Math.round(x * 255.0f);
		int g = Math.round(y * 255.0f);
		int b = Math.round(z * 255.0f);

		return new Color(r, g, b);
	}

}
