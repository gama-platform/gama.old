/*
 * Rect.java
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
 * Created on January 26, 2004, 5:25 PM
 */

package msi.gama.ext.svgsalamander;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Path extends ShapeElement {

	int fillRule = GeneralPath.WIND_NON_ZERO;
	String d = "";
	GeneralPath path;

	/** Creates a new instance of Rect */
	public Path() {}

	@Override
	protected void build() throws SVGException {
		super.build();

		final StyleAttribute sty = new StyleAttribute();

		final String fillRuleStrn = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
		fillRule = fillRuleStrn.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO;

		if (getPres(sty.setName("d"))) {
			d = sty.getStringValue();
		}

		path = buildPath(d, fillRule);

	}

	@Override
	public void render(final Graphics2D g) throws SVGException {
		beginLayer(g);
		renderShape(g, path);
		finishLayer(g);
	}

	@Override
	public Shape getShape() {
		return shapeToParent(path);
	}

	@Override
	public Rectangle2D getBoundingBox() throws SVGException {
		return boundsToParent(includeStrokeInBounds(path.getBounds2D()));
	}

}
