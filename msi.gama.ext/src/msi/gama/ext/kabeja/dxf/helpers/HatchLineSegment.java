/*******************************************************************************************************
 *
 * HatchLineSegment.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf.helpers;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class HatchLineSegment {
	
	/** The start point. */
	protected Point startPoint;
	
	/** The direction. */
	protected Vector direction;
	
	/** The angle. */
	protected double angle;
	
	/** The total length. */
	protected double totalLength;
	
	/** The length. */
	protected double length;
	
	/** The current length. */
	protected double currentLength;
	
	/** The pattern. */
	protected double[] pattern;
	
	/** The l. */
	protected double l;
	
	/** The index. */
	protected int index;
	
	/** The line. */
	protected ParametricLine line;

	/**
	 *
	 * @param startPoint
	 * @param angle
	 *            the angle in degrees
	 */
	public HatchLineSegment(final Point startPoint, final double angle, final double length) {
		this.startPoint = startPoint;
		this.angle = Math.toRadians(angle);
		this.totalLength = length;
	}

	/**
	 * Instantiates a new hatch line segment.
	 *
	 * @param startPoint the start point
	 * @param direction the direction
	 * @param length the length
	 */
	public HatchLineSegment(final Point startPoint, final Vector direction, final double length) {
		this.startPoint = startPoint;
		this.direction = direction;
		this.totalLength = length;
	}

	/**
	 * Instantiates a new hatch line segment.
	 *
	 * @param line the line
	 * @param length the length
	 * @param startLength the start length
	 * @param pattern the pattern
	 */
	public HatchLineSegment(final ParametricLine line, final double length, final double startLength,
			final double[] pattern) {
		this.startPoint = line.getStartPoint();
		// this.angle = Math.toRadians(angle);
		this.totalLength = length;
		this.currentLength = startLength;
		this.pattern = pattern;
		this.line = line;
		this.initialize(startLength);
	}

	/**
	 * Gets the start point.
	 *
	 * @return the start point
	 */
	public Point getStartPoint() { return this.startPoint; }

	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public double getLength() { return this.totalLength; }

	/**
	 * Gets the point.
	 *
	 * @param offset the offset
	 * @return the point
	 */
	public Point getPoint(final double offset) {
		Point p = new Point();
		p.setX(this.startPoint.getX() + Math.cos(this.angle) * this.totalLength);
		p.setY(this.startPoint.getY() + Math.sin(this.angle) * this.totalLength);

		return p;
	}

	/**
	 * Gets the point at.
	 *
	 * @param para the para
	 * @return the point at
	 */
	public Point getPointAt(final double para) {
		return line.getPointAt(para);
	}

	/**
	 * Checks for next.
	 *
	 * @return true, if successful
	 */
	public boolean hasNext() {
		return this.length <= totalLength;
	}

	/**
	 * Next.
	 *
	 * @return the double
	 */
	public double next() {
		double l = this.currentLength;
		this.length += Math.abs(this.currentLength);

		if (index == pattern.length) { index = 0; }

		this.currentLength = pattern[index];
		index++;

		return l;
	}

	/**
	 * Initialize.
	 *
	 * @param startLength the start length
	 */
	protected void initialize(final double startLength) {
		double l = 0;

		for (int i = 0; i < pattern.length; i++) {
			l += Math.abs(pattern[i]);

			// System.out.println("test Pattern part:"+pattern[i]+" startLength="+startLength+" currentLength:"+l);
			if (l > startLength) {
				this.currentLength = l - startLength;

				if (pattern[i] < 0) {
					// System.out.println("is empty");
					this.currentLength *= -1;
				}

				// System.out.println("pattern startet bei="+i+" mit length="+this.currentLength);
				this.index = i + 1;

				return;
			}
		}
	}

	/**
	 * Checks if is solid.
	 *
	 * @return true, if is solid
	 */
	public boolean isSolid() { return pattern.length == 0; }
}
