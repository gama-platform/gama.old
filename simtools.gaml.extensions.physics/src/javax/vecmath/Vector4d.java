/*******************************************************************************************************
 *
 * Vector4d.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;


/**
 * A 4-element vector represented by double-precision floating point
 * x,y,z,w coordinates.
 *
 */
public class Vector4d extends Tuple4d implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Compatible with 1.1
    static final long serialVersionUID = 3938123424117448700L;

    /**
     * Constructs and initializes a Vector4d from the specified xyzw coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w coordinate
     */
    public Vector4d(double x, double y, double z, double w)
    {
        super(x,y,z,w);
    }

    /**
     * Constructs and initializes a Vector4d from the coordinates contained
     * in the array.
     * @param v the array of length 4 containing xyzw in order
     */
    public Vector4d(double[] v)
    {
        super(v);
    }

    /**
     * Constructs and initializes a Vector4d from the specified Vector4d.
     * @param v1 the Vector4d containing the initialization x y z w data
     */
    public Vector4d(Vector4d v1)
    {
         super(v1);
    }

    /**
     * Constructs and initializes a Vector4d from the specified Vector4f.
     * @param v1 the Vector4f containing the initialization x y z w data
     */
    public Vector4d(Vector4f v1)
    {
       super(v1);
    }

    /**
     * Constructs and initializes a Vector4d from the specified Tuple4f.
     * @param t1 the Tuple4f containing the initialization x y z w data
     */
    public Vector4d(Tuple4f t1)
    {
       super(t1);
    }

    /**
     * Constructs and initializes a Vector4d from the specified Tuple4d.
     * @param t1 the Tuple4d containing the initialization x y z w data
     */
    public Vector4d(Tuple4d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Vector4d from the specified Tuple3d.
     * The x,y,z components of this vector are set to the corresponding
     * components of tuple t1.  The w component of this vector
     * is set to 0.
     * @param t1 the tuple to be copied
     *
     * @since vecmath 1.2
     */
    public Vector4d(Tuple3d t1) {
	super(t1.x, t1.y, t1.z, 0.0);
    }


    /**
     * Constructs and initializes a Vector4d to (0,0,0,0).
     */
    public Vector4d()
    {
       super();
    }


    /**
     * Sets the x,y,z components of this vector to the corresponding
     * components of tuple t1.  The w component of this vector
     * is set to 0.
     * @param t1 the tuple to be copied
     *
     * @since vecmath 1.2
     */
    public final void set(Tuple3d t1) {
	this.x = t1.x;
	this.y = t1.y;
	this.z = t1.z;
	this.w = 0.0;
    }


    /**
     * Returns the length of this vector.
     * @return the length of this vector
     */
    public final double length()
    {
        return Math.sqrt(this.x*this.x + this.y*this.y +
                              this.z*this.z + this.w*this.w);
    }


    /**
     * Returns the squared length of this vector.
     * @return the squared length of this vector
     */
    public final double lengthSquared()
    {
        return (this.x*this.x + this.y*this.y +
                this.z*this.z + this.w*this.w);
    }


  /**
   * Returns the dot product of this vector and vector v1.
   * @param v1 the other vector
   * @return the dot product of this vector and vector v1
   */
    public final double dot(Vector4d v1)
    {
      return (this.x*v1.x + this.y*v1.y + this.z*v1.z + this.w*v1.w);
    }


   /**
     * Sets the value of this vector to the normalization of vector v1.
     * @param v1 the un-normalized vector
     */
    public final void normalize(Vector4d v1)
    {
        double norm;

        norm = 1.0/Math.sqrt(v1.x*v1.x + v1.y*v1.y + v1.z*v1.z + v1.w*v1.w);
        this.x = v1.x*norm;
        this.y = v1.y*norm;
        this.z = v1.z*norm;
        this.w = v1.w*norm;
    }


    /**
     * Normalizes this vector in place.
     */
    public final void normalize()
    {
        double norm;

        norm = 1.0/Math.sqrt(this.x*this.x + this.y*this.y +
                              this.z*this.z + this.w*this.w);
        this.x *= norm;
        this.y *= norm;
        this.z *= norm;
        this.w *= norm;
    }


  /**
    *   Returns the (4-space) angle in radians between this vector and
    *   the vector parameter; the return value is constrained to the
    *   range [0,PI].
    *   @param v1    the other vector
    *   @return   the angle in radians in the range [0,PI]
    */
   public final double angle(Vector4d v1)
   {
      double vDot = this.dot(v1) / ( this.length()*v1.length() );
      if( vDot < -1.0) vDot = -1.0;
      if( vDot >  1.0) vDot =  1.0;
      return((Math.acos( vDot )));
   }

}
