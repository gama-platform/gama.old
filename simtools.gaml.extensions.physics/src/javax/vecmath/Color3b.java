/*******************************************************************************************************
 *
 * Color3b.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * A three-byte color value represented by byte x, y, and z values. The
 * x, y, and z values represent the red, green, and blue values,
 * respectively.
 * <p>
 * Note that Java defines a byte as a signed integer in the range
 * [-128, 127]. However, colors are more typically represented by values
 * in the range [0, 255]. Java 3D recognizes this and for color
 * treats the bytes as if the range were [0, 255]---in other words, as
 * if the bytes were unsigned.
 * <p>
 * Java 3D assumes that a linear (gamma-corrected) visual is used for
 * all colors.
 *
 */
public class Color3b extends Tuple3b implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Compatible with 1.1
    static final long serialVersionUID = 6632576088353444794L;

    /**
     * Constructs and initializes a Color3b from the specified three values.
     * @param c1 the red color value
     * @param c2 the green color value
     * @param c3 the blue color value
     */
    public Color3b(byte c1, byte c2, byte c3) {
	super(c1,c2,c3);
    }


    /**
     * Constructs and initializes a Color3b from input array of length 3.
     * @param c the array of length 3 containing the r,g,b data in order
     */
    public Color3b(byte[] c) {
	super(c);
    }


    /**
     * Constructs and initializes a Color3b from the specified Color3b.
     * @param c1 the Color3b containing the initialization r,g,b data
     */
    public Color3b(Color3b c1) {
	super(c1);
    }


    /**
     * Constructs and initializes a Color3b from the specified Tuple3b.
     * @param t1 the Tuple3b containing the initialization r,g,b data
     */
    public Color3b(Tuple3b t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Color3b from the specified AWT
     * Color object.  The alpha value of the AWT color is ignored.
     * No conversion is done on the color to compensate for
     * gamma correction.
     *
     * @param color the AWT color with which to initialize this
     * Color3b object
     *
     * @since vecmath 1.2
     */
    public Color3b(Color color) {
	super((byte)color.getRed(),
	      (byte)color.getGreen(),
	      (byte)color.getBlue());
    }


    /**
     * Constructs and initializes a Color3b to (0,0,0).
     */
    public Color3b() {
	super();
    }


    /**
     * Sets the r,g,b values of this Color3b object to those of the
     * specified AWT Color object.
     * No conversion is done on the color to compensate for
     * gamma correction.
     *
     * @param color the AWT color to copy into this Color3b object
     *
     * @since vecmath 1.2
     */
    public final void set(Color color) {
	x = (byte)color.getRed();
	y = (byte)color.getGreen();
	z = (byte)color.getBlue();
    }


    /**
     * Returns a new AWT color object initialized with the r,g,b
     * values of this Color3b object.
     *
     * @return a new AWT Color object
     *
     * @since vecmath 1.2
     */
    public final Color get() {
	int r = x & 0xff;
	int g = y & 0xff;
	int b = z & 0xff;

	return new Color(r, g, b);
    }

}
