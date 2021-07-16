/*
 * SVGRoot.java
 *
 *
 * The Salamander Project - 2D and 3D graphics libraries in Java Copyright (C) 2004 Mark McKay
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Mark McKay can be contacted at mark@kitfox.com. Salamander and other projects can be found at http://www.kitfox.com
 *
 * Created on February 18, 2004, 5:33 PM
 */

package msi.gama.ext.svgsalamander;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * The root element of an SVG tree.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGRoot extends Group {
	NumberWithUnits x;
	NumberWithUnits y;
	NumberWithUnits width;
	NumberWithUnits height;
	Rectangle2D boundingBox;
	Rectangle2D.Float viewBox = null;

	public static final int PA_X_NONE = 0;
	public static final int PA_X_MIN = 1;
	public static final int PA_X_MID = 2;
	public static final int PA_X_MAX = 3;

	public static final int PA_Y_NONE = 0;
	public static final int PA_Y_MIN = 1;
	public static final int PA_Y_MID = 2;
	public static final int PA_Y_MAX = 3;

	public static final int PS_MEET = 0;
	public static final int PS_SLICE = 1;

	int parSpecifier = PS_MEET;
	int parAlignX = PA_X_MID;
	int parAlignY = PA_Y_MID;

	final AffineTransform viewXform = new AffineTransform();
	final Rectangle2D.Float clipRect = new Rectangle2D.Float();

	@Override
	public void build() throws SVGException {
		super.build();
		final StyleAttribute sty = new StyleAttribute();
		if (getPres(sty.setName("x"))) { x = sty.getNumberWithUnits(); }
		if (getPres(sty.setName("y"))) { y = sty.getNumberWithUnits(); }
		if (getPres(sty.setName("width"))) { width = sty.getNumberWithUnits(); }
		if (getPres(sty.setName("height"))) { height = sty.getNumberWithUnits(); }
		if (getPres(sty.setName("viewBox"))) {
			final float[] coords = sty.getFloatList();
			viewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);
		}
		if (getPres(sty.setName("preserveAspectRatio"))) {
			final String preserve = sty.getStringValue();
			if (contains(preserve, "none")) {
				parAlignX = PA_X_NONE;
				parAlignY = PA_Y_NONE;
			} else if (contains(preserve, "xMinYMin")) {
				parAlignX = PA_X_MIN;
				parAlignY = PA_Y_MIN;
			} else if (contains(preserve, "xMidYMin")) {
				parAlignX = PA_X_MID;
				parAlignY = PA_Y_MIN;
			} else if (contains(preserve, "xMaxYMin")) {
				parAlignX = PA_X_MAX;
				parAlignY = PA_Y_MIN;
			} else if (contains(preserve, "xMinYMid")) {
				parAlignX = PA_X_MIN;
				parAlignY = PA_Y_MID;
			} else if (contains(preserve, "xMidYMid")) {
				parAlignX = PA_X_MID;
				parAlignY = PA_Y_MID;
			} else if (contains(preserve, "xMaxYMid")) {
				parAlignX = PA_X_MAX;
				parAlignY = PA_Y_MID;
			} else if (contains(preserve, "xMinYMax")) {
				parAlignX = PA_X_MIN;
				parAlignY = PA_Y_MAX;
			} else if (contains(preserve, "xMidYMax")) {
				parAlignX = PA_X_MID;
				parAlignY = PA_Y_MAX;
			} else if (contains(preserve, "xMaxYMax")) {
				parAlignX = PA_X_MAX;
				parAlignY = PA_Y_MAX;
			}

			if (contains(preserve, "meet")) {
				parSpecifier = PS_MEET;
			} else if (contains(preserve, "slice")) { parSpecifier = PS_SLICE; }

		}

		prepareViewport();
	}

	private boolean contains(final String text, final String find) {
		return text.indexOf(find) != -1;
	}

	protected void prepareViewport() {

		Rectangle2D defaultBounds;
		try {
			defaultBounds = getBoundingBox();
		} catch (final SVGException ex) {
			defaultBounds = new Rectangle2D.Float();
		}

		// Determine destination rectangle
		float xx, yy, ww, hh;
		if (width != null) {
			xx = x == null ? 0 : StyleAttribute.convertUnitsToPixels(x.getUnits(), x.getValue());
			if (width.getUnits() == NumberWithUnits.UT_PERCENT) {
				ww = width.getValue() * 100;
			} else {
				ww = StyleAttribute.convertUnitsToPixels(width.getUnits(), width.getValue());
			}
		} else if (viewBox != null) {
			xx = viewBox.x;
			ww = viewBox.width;
			width = new NumberWithUnits(ww, NumberWithUnits.UT_PX);
			x = new NumberWithUnits(xx, NumberWithUnits.UT_PX);
		} else {
			// Estimate size from scene bounding box
			xx = (float) defaultBounds.getX();
			ww = (float) defaultBounds.getWidth();
			width = new NumberWithUnits(ww, NumberWithUnits.UT_PX);
			x = new NumberWithUnits(xx, NumberWithUnits.UT_PX);
		}

		if (height != null) {
			yy = y == null ? 0 : StyleAttribute.convertUnitsToPixels(y.getUnits(), y.getValue());
			if (height.getUnits() == NumberWithUnits.UT_PERCENT) {
				hh = height.getValue() * 100;
			} else {
				hh = StyleAttribute.convertUnitsToPixels(height.getUnits(), height.getValue());
			}
		} else if (viewBox != null) {
			yy = viewBox.y;
			hh = viewBox.height;
			height = new NumberWithUnits(hh, NumberWithUnits.UT_PX);
			y = new NumberWithUnits(yy, NumberWithUnits.UT_PX);
		} else {
			// Estimate size from scene bounding box
			yy = (float) defaultBounds.getY();
			hh = (float) defaultBounds.getHeight();
			height = new NumberWithUnits(hh, NumberWithUnits.UT_PX);
			y = new NumberWithUnits(yy, NumberWithUnits.UT_PX);
		}

		clipRect.setRect(xx, yy, ww, hh);

		if (viewBox == null) {
			viewXform.setToIdentity();
		} else {
			viewXform.setToTranslation(clipRect.x, clipRect.y);
			viewXform.scale(clipRect.width, clipRect.height);
			viewXform.scale(1 / viewBox.width, 1 / viewBox.height);
			viewXform.translate(-viewBox.x, -viewBox.y);
		}

	}

	@Override
	public Shape getShape() {
		final Shape shape = super.getShape();
		return viewXform.createTransformedShape(shape);
	}

	public Rectangle2D getDeviceRect(final Rectangle2D rect) {
		rect.setRect(clipRect);
		return rect;
	}

	/**
	 * Retrieves the cached bounding box of this group
	 */
	public Rectangle2D getBoundingBox() throws SVGException {
		if (boundingBox == null) { calcBoundingBox(); }
		// calcBoundingBox();
		return boundingBox;
	}

	/**
	 * Recalculates the bounding box by taking the union of the bounding boxes of all children. Caches the result.
	 */
	public void calcBoundingBox() throws SVGException {
		Rectangle2D retRect = null;
		for (final Object element : children) {
			final SVGElement ele = (SVGElement) element;
			if (ele instanceof ShapeElement) {
				final ShapeElement rendEle = (ShapeElement) ele;
				final Rectangle2D bounds = rendEle.getShape().getBounds2D();
				if (bounds != null) {
					if (retRect == null) {
						retRect = bounds;
					} else {
						retRect = retRect.createUnion(bounds);
					}
				}
			}
		}
		// If no contents, use degenerate rectangle
		if (retRect == null) { retRect = new Rectangle2D.Float(); }
		boundingBox = xform == null ? retRect : xform.createTransformedShape(retRect).getBounds2D();
	}

}
