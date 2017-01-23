/*********************************************************************************************
 *
 * 'AxesLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import static msi.gama.metamodel.shape.IShape.Type.LINESTRING;
import static msi.gama.metamodel.shape.IShape.Type.POLYGON;

import java.awt.Color;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.JOGLRenderer;

public class AxesLayerObject extends StaticLayerObject.World {

	private final static String[] LABELS = new String[] { "X", "Y", "Z" };
	private final static GamaColor[] COLORS = new GamaColor[] { GamaColor.getInt(Color.red.getRGB()),
			GamaColor.getInt(Color.green.getRGB()), GamaColor.getInt(Color.blue.getRGB()) };
	private final static GamaPoint DEFAULT_SCALE = new GamaPoint(.15, .15, .15);
	private final static GamaPoint origin = new GamaPoint(0, 0, 0);

	public AxesLayerObject(final Abstract3DRenderer renderer) {
		super(renderer);
	}

	// @Override
	// public double getOrder() {
	// return 1;
	// }

	@Override
	public GamaPoint getScale() {
		return scale == null ? DEFAULT_SCALE : super.getScale();
	}

	@Override
	void fillWithObjects(final List<AbstractObject> list) {
		final double size = renderer.getMaxEnvDim();
		for (int i = 0; i < 3; i++) {
			final GamaPoint p = new GamaPoint(i == 0 ? size : 0, i == 1 ? size : 0, i == 2 ? size : 0);

			// build axis
			list.add(new GeometryObject(GamaGeometryType.buildLine(origin, p), COLORS[i], LINESTRING,
					2 * JOGLRenderer.getLineWidth()));

			// build labels
			final GamaFont font = new GamaFont("Helvetica", 0, 18); // 0 for plain, 18 for text size.
			final TextDrawingAttributes textDrawingAttr = new TextDrawingAttributes(new GamaPoint(1, 1, 1), null,
					p.times(1.2).yNegated(), COLORS[i], font, false);
			final StringObject strObj = new StringObject(LABELS[i], textDrawingAttr);
			list.add(strObj);

			// build arrows
			final GeometryObject arrow =
					new GeometryObject(GamaGeometryType.buildArrow(p.times(1.1), size / 6), COLORS[i], POLYGON, false);
			list.add(arrow);
		}
	}
}