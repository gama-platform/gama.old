/*******************************************************************************************************
 *
 * TexCoord3f.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;


/**
 * A 3 element texture coordinate that is represented by single precision
 * floating point x,y,z coordinates.
 *
 */
public class TexCoord3f extends Tuple3f implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Combatible with 1.1
    static final long serialVersionUID = -3517736544731446513L;

    /**
     * Constructs and initializes a TexCoord3f from the specified xyz
     * coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public TexCoord3f(float x, float y, float z)
    {
        super(x,y,z);
    }


    /**
     * Constructs and initializes a TexCoord3f from the array of length 3.
     * @param v the array of length 3 containing xyz in order
     */
    public TexCoord3f(float[] v)
    {
       super(v);
    }


    /**
     * Constructs and initializes a TexCoord3f from the specified TexCoord3f.
     * @param v1 the TexCoord3f containing the initialization x y z data
     */
    public TexCoord3f(TexCoord3f v1)
    {
       super(v1);
    }


    /**
     * Constructs and initializes a TexCoord3f from the specified Tuple3f.
     * @param t1 the Tuple3f containing the initialization x y z data
     */
    public TexCoord3f(Tuple3f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord3f from the specified Tuple3d.
     * @param t1 the Tuple3d containing the initialization x y z data
     */
    public TexCoord3f(Tuple3d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord3f to (0,0,0).
     */
    public TexCoord3f()
    {
        super();
    }

}
