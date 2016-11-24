/*********************************************************************************************
 *
 * 'AdvancedPath.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

/**
 * AdvancedPath, a Path object that contains extra paths
 * @see Path
 */
public class AdvancedPath extends Path {

	/**
	 * Contructor
	 * 
	 * @param device
	 */
	public AdvancedPath(final Device device) {
		super(device);
	}

	/**
	 * Adds to the receiver the circle specified by x, y, radius
	 * 
	 * @param x the x coordinate of the rectangle to add
	 * @param y the y coordinate of the rectangle to add
	 * @param radius the width of the radius
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 *                </ul>
	 */
	public void addCircle(final float x, final float y, final float radius) {
		if ( this.isDisposed() ) {
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		}
		this.addArc(x, y, radius, radius, 0, 360);
	}

	/**
	 * Adds to the receiver the round-cornered rectangle specified by x, y, width and height.
	 * 
	 * @param x the x coordinate of the rectangle to add
	 * @param y the y coordinate of the rectangle to add
	 * @param width the width of the rectangle to add
	 * @param height the height of the rectangle to add
	 * @param arcWidth the width of the arc
	 * @param arcHeight the height of the arc
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 *                </ul>
	 */
	public void addRoundRectangle(final float x, final float y, final float width, final float height,
		final float arcWidth, final float arcHeight) {
		if ( this.isDisposed() ) {
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		}

		// Top left corner
		this.cubicTo(x, y, x, y, x, y + arcHeight);
		this.cubicTo(x, y, x, y, x + arcWidth, y);

		// Top right corner
		this.cubicTo(x + width, y, x + width, y, x + width - arcWidth, y);
		this.cubicTo(x + width, y, x + width, y, x + width, y + arcHeight);

		// Bottom right corner
		this.cubicTo(x + width, y + height, x + width, y + height, x + width, y + height - arcHeight);
		this.cubicTo(x + width, y + height, x + width, y + height, x + width - arcWidth, y + height);

		// Bottom left corner
		this.cubicTo(x, y + height, x, y + height, x + arcWidth, y + height);
		this.cubicTo(x, y + height, x, y + height, x, y + height - arcHeight);
	}

	/**
	 * Adds to the receiver the rectangle specified by x, y, width and height.<br/>
	 * This rectangle is round-cornered on the left, and straight on the right.
	 * 
	 * @param x the x coordinate of the rectangle to add
	 * @param y the y coordinate of the rectangle to add
	 * @param width the width of the rectangle to add
	 * @param height the height of the rectangle to add
	 * @param arcWidth the width of the arc
	 * @param arcHeight the height of the arc
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 *                </ul>
	 */
	public void addRoundRectangleStraightRight(final float x, final float y, final float width, final float height,
		final float arcWidth, final float arcHeight) {
		if ( this.isDisposed() ) {
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		}

		// Top left corner
		this.cubicTo(x, y, x, y, x, y + arcHeight);
		this.cubicTo(x, y, x, y, x + arcWidth, y);

		// Top right corner
		this.lineTo(x + width, y);

		// Bottom right corner
		this.lineTo(x + width, y + height);

		// Bottom left corner
		this.cubicTo(x, y + height, x, y + height, x + arcWidth, y + height);
		this.cubicTo(x, y + height, x, y + height, x, y + height - arcHeight);
	}

	/**
	 * Adds to the receiver the rectangle specified by x, y, width and height.<br/>
	 * This rectangle is round-cornered on the right, and straight on the left.
	 * 
	 * @param x the x coordinate of the rectangle to add
	 * @param y the y coordinate of the rectangle to add
	 * @param width the width of the rectangle to add
	 * @param height the height of the rectangle to add
	 * @param arcWidth the width of the arc
	 * @param arcHeight the height of the arc
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 *                </ul>
	 */
	public void addRoundRectangleStraightLeft(final float x, final float y, final float width, final float height,
		final float arcWidth, final float arcHeight) {
		if ( this.isDisposed() ) {
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		}

		// Top left corner
		moveTo(x, y);
		lineTo(x + width - arcWidth, y);

		// Top right corner
		this.cubicTo(x + width, y, x + width, y, x + width - arcWidth, y);
		this.cubicTo(x + width, y, x + width, y, x + width, y + arcHeight);

		// Bottom right corner
		this.cubicTo(x + width, y + height, x + width, y + height, x + width, y + height - arcHeight);
		this.cubicTo(x + width, y + height, x + width, y + height, x + width - arcWidth, y + height);

		// Bottom left corner
		lineTo(x, y + height);
		lineTo(x, y);
	}
}