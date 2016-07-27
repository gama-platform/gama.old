package ummisco.gama.opengl.scene;

import static msi.gama.metamodel.shape.IShape.Type.LINESTRING;
import static msi.gama.metamodel.shape.IShape.Type.POLYGON;

import java.awt.Color;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class AxesLayerObject extends StaticLayerObject.World {

	final static String[] LABELS = new String[] { "X", "Y", "Z" };
	final static GamaColor[] COLORS = new GamaColor[] { GamaColor.getInt(Color.red.getRGB()),
			GamaColor.getInt(Color.green.getRGB()), GamaColor.getInt(Color.blue.getRGB()) };
	final static GamaPoint DEFAULT_SCALE = new GamaPoint(.15, .15, .15);

	public AxesLayerObject(final Abstract3DRenderer renderer) {
		super(renderer);
	}

	@Override
	public GamaPoint getScale() {
		return scale == null ? DEFAULT_SCALE : super.getScale();
	}

	@Override
	void fillWithObjects(final List<AbstractObject> list) {
		final double size = renderer.getMaxEnvDim();
		for (int i = 0; i < 3; i++) {
			final GamaPoint p = new GamaPoint(i == 0 ? size : 0, i == 1 ? size : 0, i == 2 ? size : 0);
			list.add(new GeometryObject(GamaGeometryType.buildLine(p), COLORS[i], LINESTRING, this));
			list.add(new StringObject(LABELS[i], p.times(1.2).yNegated(), this));
			GeometryObject arrow = new GeometryObject(GamaGeometryType.buildArrow(p.times(1.1), size / 6 ), COLORS[i], POLYGON, this);
			arrow.disableLightInteraction();
			list.add(arrow);
		}
	}
}