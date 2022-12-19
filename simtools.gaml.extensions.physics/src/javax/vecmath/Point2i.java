/*******************************************************************************************************
 *
 * Point2i.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;


/**
 * A 2-element point represented by signed integer x,y
 * coordinates.
 *
 * @since vecmath 1.4
 */
public class Point2i extends Tuple2i implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    static final long serialVersionUID = 9208072376494084954L;

    /**
     * Constructs and initializes a Point2i from the specified
     * x and y coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point2i(int x, int y) {
	super(x, y);
    }


    /**
     * Constructs and initializes a Point2i from the array of length 2.
     * @param t the array of length 2 containing x and y in order.
     */
    public Point2i(int[] t) {
	super(t);
    }


    /**
     * Constructs and initializes a Point2i from the specified Tuple2i.
     * @param t1 the Tuple2i containing the initialization x and y
     * data.
     */
    public Point2i(Tuple2i t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Point2i to (0,0).
     */
    public Point2i() {
	super();
    }

}
