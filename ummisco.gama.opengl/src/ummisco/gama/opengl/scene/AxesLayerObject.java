package ummisco.gama.opengl.scene;

import static msi.gama.metamodel.shape.IShape.Type.LINESTRING;
import static msi.gama.metamodel.shape.IShape.Type.POLYGON;

import java.awt.Color;

import com.jogamp.opengl.GL2;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.JOGLRenderer;

public class AxesLayerObject extends StaticLayerObject {

	final static String[] LABELS = new String[] { "X", "Y", "Z" };
	final static GamaColor[] COLORS = new GamaColor[] { GamaColor.getInt(Color.red.getRGB()),
			GamaColor.getInt(Color.green.getRGB()), GamaColor.getInt(Color.blue.getRGB()) };
	final static GamaPoint DEFAULT_SCALE = new GamaPoint(.15, .15, .15);
	final double size;

	public AxesLayerObject(final Abstract3DRenderer renderer) {
		super(renderer);
		size = renderer.getMaxEnvDim();
		setAlpha(WORLD_ALPHA);
		setOffset(WORLD_OFFSET);
		setScale(WORLD_SCALE);
	}

	@Override
	public GamaPoint getScale() {
		return scale == null ? DEFAULT_SCALE : scale;
	}

	@Override
	public void drawWithoutShader(final GL2 gl, final JOGLRenderer renderer) {
		gl.glDisable(GL2.GL_LIGHTING);
		if (currentList.isEmpty()) {
			for (int i = 0; i < 3; i++)
				drawAxis(i);
		}
		super.drawWithoutShader(gl, renderer);
		gl.glEnable(GL2.GL_LIGHTING);
	}

	private void drawAxis(final int i) {
		final GamaPoint p = new GamaPoint(i == 0 ? size : 0, i == 1 ? size : 0, i == 2 ? size : 0);
		currentList.add(new GeometryObject(GamaGeometryType.buildLine(p), COLORS[i], LINESTRING, this));
		currentList.add(new StringObject(LABELS[i], p.times(1.2).yNegated(), this));
		currentList
				.add(new GeometryObject(GamaGeometryType.buildArrow(p.times(1.1), size / 6), COLORS[i], POLYGON, this));
	}
}