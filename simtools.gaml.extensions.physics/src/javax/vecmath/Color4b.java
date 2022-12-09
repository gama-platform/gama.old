/*******************************************************************************************************
 *
 * Color4b.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * A four-byte color value represented by byte x, y, z, and w values.
 * The x, y, z, and w values represent the red, green, blue, and alpha
 * values, respectively.
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
public class Color4b extends Tuple4b implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Compatible with 1.1
    static final long serialVersionUID = -105080578052502155L;

    /**
     * Constructs and initializes a Color4b from the four specified values.
     * @param b1 the red color value
     * @param b2 the green color value
     * @param b3 the blue color value
     * @param b4 the alpha value
     */
    public Color4b(byte b1, byte b2, byte b3, byte b4) {
	super(b1,b2,b3,b4);
    }


    /**
     * Constructs and initializes a Color4b from the array of length 4.
     * @param c the array of length 4 containing r, g, b, and alpha in order
     */
    public Color4b(byte[] c) {
	super(c);
    }


    /**
     * Constructs and initializes a Color4b from the specified Color4b.
     * @param c1 the Color4b containing the initialization r,g,b,a
     * data
     */
    public Color4b(Color4b c1) {
        super(c1);
    }


    /**
     * Constructs and initializes a Color4b from the specified Tuple4b.
     * @param t1 the Tuple4b containing the initialization r,g,b,a
     * data
     */
    public Color4b(Tuple4b t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Color4b from the specified AWT
     * Color object.
     * No conversion is done on the color to compensate for
     * gamma correction.
     *
     * @param color the AWT color with which to initialize this
     * Color4b object
     *
     * @since vecmath 1.2
     */
    public Color4b(Color color) {
	super((byte)color.getRed(),
	      (byte)color.getGreen(),
	      (byte)color.getBlue(),
	      (byte)color.getAlpha());
    }


    /**
     * Constructs and initializes a Color4b to (0,0,0,0).
     */
    public Color4b() {
        super();
    }


    /**
     * Sets the r,g,b,a values of this Color4b object to those of the
     * specified AWT Color object.
     * No conversion is done on the color to compensate for
     * gamma correction.
     *
     * @param color the AWT color to copy into this Color4b object
     *
     * @since vecmath 1.2
     */
    public final void set(Color color) {
	x = (byte)color.getRed();
	y = (byte)color.getGreen();
	z = (byte)color.getBlue();
	w = (byte)color.getAlpha();
    }


    /**
     * Returns a new AWT color object initialized with the r,g,b,a
     * values of this Color4b object.
     *
     * @return a new AWT Color object
     *
     * @since vecmath 1.2
     */
    public final Color get() {
	int r = x & 0xff;
	int g = y & 0xff;
	int b = z & 0xff;
	int a = w & 0xff;

	return new Color(r, g, b, a);
    }

}
