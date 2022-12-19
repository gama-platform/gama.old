/*******************************************************************************************************
 *
 * TexCoord4f.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;


/**
 * A 4 element texture coordinate that is represented by single precision
 * floating point x,y,z,w coordinates.
 *
 * @since vecmath 1.3
 */
public class TexCoord4f extends Tuple4f implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Combatible with 1.1
    static final long serialVersionUID = -3517736544731446513L;

    /**
     * Constructs and initializes a TexCoord4f from the specified xyzw
     * coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w coordinate
     */
    public TexCoord4f(float x, float y, float z, float w)
    {
        super(x,y,z,w);
    }


    /**
     * Constructs and initializes a TexCoord4f from the array of length 4.
     * @param v the array of length w containing xyzw in order
     */
    public TexCoord4f(float[] v)
    {
       super(v);
    }


    /**
     * Constructs and initializes a TexCoord4f from the specified TexCoord4f.
     * @param v1 the TexCoord4f containing the initialization x y z w data
     */
    public TexCoord4f(TexCoord4f v1)
    {
       super(v1);
    }


    /**
     * Constructs and initializes a TexCoord4f from the specified Tuple4f.
     * @param t1 the Tuple4f containing the initialization x y z w data
     */
    public TexCoord4f(Tuple4f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord4f from the specified Tuple4d.
     * @param t1 the Tuple4d containing the initialization x y z w data
     */
    public TexCoord4f(Tuple4d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord4f to (0,0,0,0).
     */
    public TexCoord4f()
    {
        super();
    }

}
