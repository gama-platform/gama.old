/*******************************************************************************************************
 *
 * Point4i.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;


/**
 * A 4 element point represented by signed integer x,y,z,w
 * coordinates.
 *
 * @since vecmath 1.2
 */
public class Point4i extends Tuple4i implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Combatible with 1.2
    static final long serialVersionUID = 620124780244617983L;

    /**
     * Constructs and initializes a Point4i from the specified
     * x, y, z, and w coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w coordinate
     */
    public Point4i(int x, int y, int z, int w) {
	super(x, y, z, w);
    }


    /**
     * Constructs and initializes a Point4i from the array of length 4.
     * @param t the array of length 4 containing x, y, z, and w in order.
     */
    public Point4i(int[] t) {
	super(t);
    }


    /**
     * Constructs and initializes a Point4i from the specified Tuple4i.
     * @param t1 the Tuple4i containing the initialization x, y, z,
     * and w data.
     */
    public Point4i(Tuple4i t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Point4i to (0,0,0,0).
     */
    public Point4i() {
	super();
    }

}
