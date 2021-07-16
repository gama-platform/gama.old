
package msi.gama.ext.svgsalamander;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Parent of shape objects
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
abstract public class ShapeElement<T extends Shape> extends SVGElement implements IShapeElement {

	T shape;
	AffineTransform xform = null;

	protected Shape shapeToParent(final Shape shape) {
		if (xform == null) return shape;
		return xform.createTransformedShape(shape);
	}

	protected Rectangle2D boundsToParent(final Rectangle2D rect) {
		if (xform == null) return rect;
		return xform.createTransformedShape(rect).getBounds2D();
	}

	@Override
	public void loaderBuild() throws SVGException {
		final StyleAttribute sty = new StyleAttribute();
		if (getPres(sty.setName("transform"))) { xform = parseTransform(sty.getStringValue()); }
		build();
	}

	protected abstract void build() throws SVGException;

	public Shape getShape() {
		return shapeToParent(shape);
	}

}
