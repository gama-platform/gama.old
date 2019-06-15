/*
 * FillElement.java
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
 * Created on March 18, 2004, 6:52 AM
 */

package msi.gama.util.file.svg;

import java.net.URL;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class FilterEffects extends SVGElement {
	public static final int FP_SOURCE_GRAPHIC = 0;
	public static final int FP_SOURCE_ALPHA = 1;
	public static final int FP_BACKGROUND_IMAGE = 2;
	public static final int FP_BACKGROUND_ALPHA = 3;
	public static final int FP_FILL_PAINT = 4;
	public static final int FP_STROKE_PAINT = 5;
	public static final int FP_CUSTOM = 5;

	float x = 0f;
	float y = 0f;
	float width = 1f;
	float height = 1f;

	String result = "defaultFilterName";

	URL href = null;

	/** Creates a new instance of FillElement */
	public FilterEffects() {}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

}
