package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.JOGLRenderer;

public class AxesLayerObject extends StaticLayerObject {

	public AxesLayerObject(final Abstract3DRenderer renderer) {
		super(renderer);
		setAlpha(WORLD_ALPHA);
		setOffset(WORLD_OFFSET);
		setScale(WORLD_SCALE);
	}

	@Override
	public void drawWithoutShader(final GL2 gl, final JOGLRenderer renderer) {
		gl.glDisable(GL2.GL_LIGHTING);
		if (currentList.isEmpty())
			drawAxes(renderer.data.getEnvWidth(), renderer.data.getEnvHeight());
		super.drawWithoutShader(gl, renderer);
		renderer.setCurrentColor(gl, Color.white);
		gl.glEnable(GL2.GL_LIGHTING);
	}

	public void drawAxes(final double w, final double h) {
		final List<AbstractObject> objects = new ArrayList<AbstractObject>();
		final double size = w > h ? w : h;
		// add the world
		GamaColor c = new GamaColor(150, 150, 150, 255);

		// build the lines
		c = GamaColor.getInt(Color.red.getRGB());
		IShape g = GamaGeometryType.buildLine(new GamaPoint(), new GamaPoint(size, 0, 0));
		GeometryObject geomObj = new GeometryObject(g, c, IShape.Type.LINESTRING, this);
		objects.add(geomObj);
		c = GamaColor.getInt(Color.green.getRGB());
		g = GamaGeometryType.buildLine(new GamaPoint(), new GamaPoint(0, size, 0));
		geomObj = new GeometryObject(g, c, IShape.Type.LINESTRING, this);
		objects.add(geomObj);
		c = GamaColor.getInt(Color.blue.getRGB());
		g = GamaGeometryType.buildLine(new GamaPoint(), new GamaPoint(0, 0, size));
		geomObj = new GeometryObject(g, c, IShape.Type.LINESTRING, this);
		objects.add(geomObj);

		// add the legends
		StringObject strObj = new StringObject("X", new GamaPoint(1.2 * size, 0, 0), this);
		objects.add(strObj);
		strObj = new StringObject("Y", new GamaPoint(0, -1.2 * size, 0), this);
		objects.add(strObj);
		strObj = new StringObject("Z", new GamaPoint(0, 0, 1.2 * size), this);
		objects.add(strObj);

		// add the triangles
		c = GamaColor.getInt(Color.red.getRGB());
		final double s = size / 6;
		g = GamaGeometryType.buildArrow(new GamaPoint(), new GamaPoint(size + size / 10, 0, 0), s, s, true);
		geomObj = new GeometryObject(g, c, IShape.Type.POLYGON, this);
		objects.add(geomObj);
		c = GamaColor.getInt(Color.green.getRGB());
		g = GamaGeometryType.buildArrow(new GamaPoint(), new GamaPoint(0, size + size / 10, 0), s, s, true);
		geomObj = new GeometryObject(g, c, IShape.Type.POLYGON, this);
		objects.add(geomObj);
		c = GamaColor.getInt(Color.blue.getRGB());
		g = GamaGeometryType.buildArrow(new GamaPoint(), new GamaPoint(0, 0, size + size / 10), s, s, true);
		// FIXME See Issue 832: depth cannot be applied here.
		geomObj = new GeometryObject(g, c, IShape.Type.POLYGON, this);
		objects.add(geomObj);
		currentList.addAll(objects);
	}

}