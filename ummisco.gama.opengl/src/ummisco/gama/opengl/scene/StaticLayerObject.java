/*********************************************************************************************
 *
 *
 * 'StaticLayerObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.JOGLRenderer;

public class StaticLayerObject extends LayerObject {

	static Geometry NULL_GEOM = GamaGeometryType.buildRectangle(0, 0, new GamaPoint(0, 0)).getInnerGeometry();
	static final GamaPoint WORLD_OFFSET = new GamaPoint();
	static final GamaPoint WORLD_SCALE = new GamaPoint(1, 1, 1);
	static final Double WORLD_ALPHA = 1d;

	public StaticLayerObject(final JOGLRenderer renderer) {
		super(renderer, null);
	}

	@Override
	protected ISceneObjects buildSceneObjects(final ObjectDrawer drawer) {
		return new SceneObjects.Static(drawer);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void clear(final GL gl) {
	}

	public static class WaitingLayerObject extends StaticLayerObject {

		public WaitingLayerObject(final JOGLRenderer renderer) {
			super(renderer);
			setAlpha(WORLD_ALPHA);
			setOffset(WORLD_OFFSET);
			setScale(WORLD_SCALE);
		}

		@Override
		public void draw(final GL2 gl, final JOGLRenderer renderer) {
			super.draw(gl, renderer);

			gl.glDisable(GL.GL_BLEND);
			gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
			gl.glRasterPos3d(-renderer.getDisplayWidth() / 10d, renderer.getHeight() / 10d, 0);
			gl.glScaled(8.0d, 8.0d, 8.0d);
			final GLUT glut = new GLUT();
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "Loading...");
			gl.glScaled(0.5d, 0.5d, 0.5d);
			gl.glEnable(GL.GL_BLEND);
			gl.glColor4d(1, 1, 1, 1);
		}

	}

	public static class WordLayerObject extends StaticLayerObject {

		private final double startTime;
		private int frameCount = 0;
		private double currentTime = 0;
		private double previousTime = 0;
		public float fps = 00.00f;
		public boolean axesDrawn = false;
		public boolean planDrawn = false;

		protected GamaPoint pivotPoint = null;
		protected double axisSize = 0;
		protected List<GeometryObject> geomObjList = new ArrayList<GeometryObject>();
		protected List<StringObject> stringObjList = new ArrayList<StringObject>();

		public WordLayerObject(final JOGLRenderer renderer) {
			super(renderer);
			startTime = System.currentTimeMillis();
			setAlpha(WORLD_ALPHA);
			setOffset(WORLD_OFFSET);
			setScale(WORLD_SCALE);
		}

		public void computeFrameRate() {
			frameCount++;
			currentTime = System.currentTimeMillis() - startTime;
			final int timeInterval = (int) (currentTime - previousTime);
			if (timeInterval > 1000) {
				fps = frameCount / (timeInterval / 1000.0f);
				previousTime = currentTime;
				frameCount = 0;
			}
		}

		@Override
		public void draw(final GL2 gl, final JOGLRenderer renderer) {
			gl.glDisable(GL2.GL_LIGHTING);
			super.draw(gl, renderer);
			if (renderer.data.isDrawEnv() && !planDrawn) {
				drawXYPlan(renderer.data.getEnvWidth(), renderer.data.getEnvHeight());
				planDrawn = true;
			}
			if (renderer.data.isDrawEnv() && (!axesDrawn || pivotPoint != null)) {
				drawAxes(renderer.data.getEnvWidth(), renderer.data.getEnvHeight());
				if (pivotPoint == null)
					axesDrawn = true;
			}
			// GL2 gl = GLContext.getCurrentGL().getGL2();

			if (renderer.data.isShowfps()) {
				computeFrameRate();
				gl.glDisable(GL.GL_BLEND);
				// renderer.getContext().makeCurrent();
				gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
				gl.glRasterPos3d(-renderer.getWidth() / 10d, renderer.getHeight() / 10d, 0);
				gl.glScaled(8.0d, 8.0d, 8.0d);
				final GLUT glut = new GLUT();
				glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "fps : " + fps);
				gl.glScaled(0.125d, 0.125d, 0.125d);
				gl.glEnable(GL.GL_BLEND);
			}
			gl.glColor4d(1, 1, 1, 1);
			gl.glEnable(GL2.GL_LIGHTING);
		}

		public void drawXYPlan(final double w, final double h) {
			// add the world
			final GamaColor c = new GamaColor(150, 150, 150, 255);
			final GamaPoint origin = new GamaPoint();

			IShape g = GamaGeometryType.buildLine(origin, new GamaPoint(w, 0));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			g = GamaGeometryType.buildLine(new GamaPoint(w, 0), new GamaPoint(w, h));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			g = GamaGeometryType.buildLine(new GamaPoint(w, h), new GamaPoint(0, h));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			g = GamaGeometryType.buildLine(new GamaPoint(0, h), origin);
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
		}

		public void drawAxes(final double w, final double h) {
			for (final GeometryObject geomObj : geomObjList) {
				geometries.remove(geomObj);
			}
			for (final StringObject strObj : stringObjList) {
				strings.remove(strObj);
			}
			// geometries.clear(gl, getTrace(), false);
			double size = (w > h ? w : h) / 10;
			// add the world
			GamaColor c = new GamaColor(150, 150, 150, 255);
			GamaPoint origin = new GamaPoint();
			if (pivotPoint != null) {
				origin = pivotPoint;
				size = axisSize;
			}

			// build the lines
			c = GamaColor.getInt(Color.red.getRGB());
			IShape g = GamaGeometryType.buildLine(new GamaPoint(origin.x, -origin.y, origin.z),
					new GamaPoint(size + origin.x, 0 - origin.y, 0 + origin.z));
			GeometryObject geomObj = new GeometryObject(g, c, IShape.Type.LINESTRING, this);
			geomObjList.add(geomObj);
			geometries.add(geomObj);
			c = GamaColor.getInt(Color.green.getRGB());
			g = GamaGeometryType.buildLine(new GamaPoint(origin.x, -origin.y, origin.z),
					new GamaPoint(0 + origin.x, size - origin.y, 0 + origin.z));
			geomObj = new GeometryObject(g, c, IShape.Type.LINESTRING, this);
			geomObjList.add(geomObj);
			geometries.add(geomObj);
			c = GamaColor.getInt(Color.blue.getRGB());
			g = GamaGeometryType.buildLine(new GamaPoint(origin.x, -origin.y, origin.z),
					new GamaPoint(0 + origin.x, 0 - origin.y, size + origin.z));
			geomObj = new GeometryObject(g, c, IShape.Type.LINESTRING, this);
			geomObjList.add(geomObj);
			geometries.add(geomObj);

			// add the legends
			StringObject strObj = new StringObject("X",
					new GamaPoint(1.2f * size + origin.x, 0.0d + origin.y, 0.0d + origin.z), this);
			stringObjList.add(strObj);
			strings.add(strObj);
			strObj = new StringObject("Y", new GamaPoint(0.0d + origin.x, -1.2f * size + origin.y, 0.0 + origin.z),
					this);
			stringObjList.add(strObj);
			strings.add(strObj);
			strObj = new StringObject("Z", new GamaPoint(0.0d + origin.x, 0.0d + origin.y, 1.2f * size + origin.z),
					this);
			stringObjList.add(strObj);
			strings.add(strObj);

			// add the triangles
			c = GamaColor.getInt(Color.red.getRGB());
			g = GamaGeometryType.buildArrow(new GamaPoint(origin.x, -origin.y, origin.z),
					new GamaPoint(size + size / 10 + origin.x, 0 - origin.y, 0 + origin.z), size / 6, size / 6, true);
			geomObj = new GeometryObject(g, c, IShape.Type.POLYGON, this);
			geomObjList.add(geomObj);
			geometries.add(geomObj);
			c = GamaColor.getInt(Color.green.getRGB());
			g = GamaGeometryType.buildArrow(new GamaPoint(origin.x, -origin.y, origin.z),
					new GamaPoint(0 + origin.x, size + size / 10 - origin.y, 0 + origin.z), size / 6, size / 6, true);
			geomObj = new GeometryObject(g, c, IShape.Type.POLYGON, this);
			geomObjList.add(geomObj);
			geometries.add(geomObj);
			c = GamaColor.getInt(Color.blue.getRGB());
			g = GamaGeometryType.buildArrow(new GamaPoint(origin.x, -origin.y, origin.z),
					new GamaPoint(0 + origin.x, 0 - origin.y, size + size / 10 + origin.z), size / 6, size / 6, true);
			// FIXME See Issue 832: depth cannot be applied here.
			geomObj = new GeometryObject(g, c, IShape.Type.POLYGON, this);
			geomObjList.add(geomObj);
			geometries.add(geomObj);

		}

		public void startDrawRotationHelper(final GamaPoint pivotPosition, final double size) {
			pivotPoint = pivotPosition;
			axisSize = size;
		}

		public void stopDrawRotationHelper() {
			pivotPoint = null;
			axesDrawn = false;
		}
	}

}