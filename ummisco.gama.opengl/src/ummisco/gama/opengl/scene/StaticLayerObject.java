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
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.metamodel.shape.*;
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
	protected ISceneObjects buildSceneObjects(final ObjectDrawer drawer, final boolean asList, final boolean asVBO) {
		return new SceneObjects.Static(drawer, asList, asVBO);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void clear(final GL gl) {}

	public static class WaitingLayerObject extends StaticLayerObject {

		public WaitingLayerObject(final JOGLRenderer renderer) {
			super(renderer);
			setAlpha(WORLD_ALPHA);
			setOffset(WORLD_OFFSET);
			setScale(WORLD_SCALE);
		}

		@Override
		public void draw(final GL2 gl, final JOGLRenderer renderer, final boolean picking) {
			super.draw(gl, renderer, picking);

			gl.glDisable(GL.GL_BLEND);
			gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
			gl.glRasterPos3d(-renderer.getDisplayWidth() / 10d, renderer.getHeight() / 10d, 0);
			gl.glScaled(8.0d, 8.0d, 8.0d);
			GLUT glut = new GLUT();
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
			int timeInterval = (int) (currentTime - previousTime);
			if ( timeInterval > 1000 ) {
				fps = frameCount / (timeInterval / 1000.0f);
				previousTime = currentTime;
				frameCount = 0;
			}
		}

		@Override
		public void draw(final GL2 gl, final JOGLRenderer renderer, final boolean picking) {
			super.draw(gl, renderer, picking);
			if ( renderer.data.isDrawEnv() && !axesDrawn ) {
				drawAxes(renderer.data.getEnvWidth(), renderer.data.getEnvHeight());
				axesDrawn = true;
			}
			// GL2 gl = GLContext.getCurrentGL().getGL2();

			if ( renderer.data.isShowfps() ) {
				computeFrameRate();
				gl.glDisable(GL.GL_BLEND);
				// renderer.getContext().makeCurrent();
				gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
				gl.glRasterPos3d(-renderer.getWidth() / 10d, renderer.getHeight() / 10d, 0);
				gl.glScaled(8.0d, 8.0d, 8.0d);
				GLUT glut = new GLUT();
				glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "fps : " + fps);
				gl.glScaled(0.125d, 0.125d, 0.125d);
				gl.glEnable(GL.GL_BLEND);
			}
			gl.glColor4d(1, 1, 1, 1);
		}

		public void drawAxes(final double w, final double h) {
			double size = (w > h ? w : h) / 10;
			// add the world
			GamaColor c = new GamaColor(150, 150, 150, 255);
			GamaPoint origin = new GamaPoint();
			IShape g = GamaGeometryType.buildLine(origin, new GamaPoint(w, 0));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			g = GamaGeometryType.buildLine(new GamaPoint(w, 0), new GamaPoint(w, h));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			g = GamaGeometryType.buildLine(new GamaPoint(w, h), new GamaPoint(0, h));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			g = GamaGeometryType.buildLine(new GamaPoint(0, h), origin);
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));

			// build the lines
			c = GamaColor.getInt(Color.red.getRGB());
			g = GamaGeometryType.buildLine(origin, new GamaPoint(size, 0, 0));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			c = GamaColor.getInt(Color.green.getRGB());
			g = GamaGeometryType.buildLine(origin, new GamaPoint(0, size, 0));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			c = GamaColor.getInt(Color.blue.getRGB());
			g = GamaGeometryType.buildLine(origin, new GamaPoint(0, 0, size));
			geometries.add(new GeometryObject(g, c, IShape.Type.LINESTRING, this));
			// add the legends
			strings.add(new StringObject("X", new GamaPoint(1.2f * size, 0.0d, 0.0d), this));
			strings.add(new StringObject("Y", new GamaPoint(0.0d, -1.2f * size, 0.0d), this));
			strings.add(new StringObject("Z", new GamaPoint(0.0d, 0.0d, 1.2f * size), this));
			// add the triangles
			c = GamaColor.getInt(Color.red.getRGB());
			g = GamaGeometryType.buildArrow(origin, new GamaPoint(size + size / 10, 0, 0), size / 6, size / 6, true);
			geometries.add(new GeometryObject(g, c, IShape.Type.POLYGON, this));
			c = GamaColor.getInt(Color.green.getRGB());
			g = GamaGeometryType.buildArrow(origin, new GamaPoint(0, size + size / 10, 0), size / 6, size / 6, true);
			geometries.add(new GeometryObject(g, c, IShape.Type.POLYGON, this));
			c = GamaColor.getInt(Color.blue.getRGB());
			g = GamaGeometryType.buildArrow(origin, new GamaPoint(0, 0, size + size / 10), size / 6, size / 6, true);
			// FIXME See Issue 832: depth cannot be applied here.
			geometries.add(new GeometryObject(g, c, IShape.Type.POLYGON, this));

		}
	}

}