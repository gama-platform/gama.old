/*******************************************************************************************************
 *
 * TexCoord2f.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;


/**
 * A 2-element vector that is represented by single-precision floating
 * point x,y coordinates.
 *
 */
public class TexCoord2f extends Tuple2f implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Combatible with 1.1
    static final long serialVersionUID = 7998248474800032487L;

    /**
     * Constructs and initializes a TexCoord2f from the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public TexCoord2f(float x, float y)
    {
         super(x,y);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified array.
     * @param v the array of length 2 containing xy in order
     */
    public TexCoord2f(float[] v)
    {
         super(v);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified TexCoord2f.
     * @param v1 the TexCoord2f containing the initialization x y data
     */
    public TexCoord2f(TexCoord2f v1)
    {
        super(v1);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified Tuple2f.
     * @param t1 the Tuple2f containing the initialization x y data
     */
    public TexCoord2f(Tuple2f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord2f to (0,0).
     */
    public TexCoord2f()
    {
       super();
    }


}
