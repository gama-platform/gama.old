/*******************************************************************************************************
 *
 * Point2d.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;


/**
 * A 2 element point that is represented by double precision floating
 * point x,y coordinates.
 *
 */
public class Point2d extends Tuple2d implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    // Compatible with 1.1
    static final long serialVersionUID = 1133748791492571954L;

    /**
     * Constructs and initializes a Point2d from the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point2d(double x, double y)
    {
         super(x,y);
    }


    /**
     * Constructs and initializes a Point2d from the specified array.
     * @param p the array of length 2 containing xy in order
     */
    public Point2d(double[] p)
    {
         super(p);
    }


    /**
     * Constructs and initializes a Point2d from the specified Point2d.
     * @param p1 the Point2d containing the initialization x y data
     */
    public Point2d(Point2d p1)
    {
        super(p1);
    }


    /**
     * Constructs and initializes a Point2d from the specified Point2f.
     * @param p1 the Point2f containing the initialization x y data
     */
    public Point2d(Point2f p1)
    {
        super(p1);
    }


    /**
     * Constructs and initializes a Point2d from the specified Tuple2d.
     * @param t1 the Tuple2d containing the initialization x y data
     */
    public Point2d(Tuple2d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Point2d from the specified Tuple2f.
     * @param t1 the Tuple2f containing the initialization x y data
     */
    public Point2d(Tuple2f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Point2d to (0,0).
     */
    public Point2d()
    {
       super();
    }

  /**
   * Computes the square of the distance between this point and point p1.
   * @param p1 the other point
   */
  public final double distanceSquared(Point2d p1)
    {
      double dx, dy;

      dx = this.x-p1.x;
      dy = this.y-p1.y;
      return dx*dx+dy*dy;
    }

  /**
   * Computes the distance between this point and point p1.
   * @param p1 the other point
   */
  public final double distance(Point2d p1)
    {
      double  dx, dy;

      dx = this.x-p1.x;
      dy = this.y-p1.y;
      return Math.sqrt(dx*dx+dy*dy);
    }


  /**
    * Computes the L-1 (Manhattan) distance between this point and
    * point p1.  The L-1 distance is equal to abs(x1-x2) + abs(y1-y2).
    * @param p1 the other point
    */
  public final double distanceL1(Point2d p1)
    {
      return( Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y));
    }

  /**
    * Computes the L-infinite distance between this point and
    * point p1.  The L-infinite distance is equal to
    * MAX[abs(x1-x2), abs(y1-y2)].
    * @param p1 the other point
    */
  public final double distanceLinf(Point2d p1)
    {
      return(Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y)));
    }

}
