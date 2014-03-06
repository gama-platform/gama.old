/**
 * Created by drogoul, 3 mars 2014
 * 
 */
package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_BLEND;
import java.awt.*;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.GAMA;
import msi.gaml.types.GamaGeometryType;
import com.sun.opengl.util.GLUT;
import com.vividsolutions.jts.geom.Geometry;

public class StaticLayerObject extends LayerObject {

	static Geometry NULL_GEOM = GamaGeometryType.buildRectangle(0, 0, new GamaPoint(0, 0)).getInnerGeometry();
	static final GamaPoint WORLD_OFFSET = new GamaPoint();
	static final GamaPoint WORLD_SCALE = new GamaPoint(1, 1, 1);
	static final Double WORLD_ALPHA = 1d;

	public StaticLayerObject(final JOGLAWTGLRenderer renderer, final Integer id) {
		super(renderer, id);
	}

	@Override
	protected SceneObjects buildSceneObjects(final ObjectDrawer drawer, final boolean asList, final boolean asVBO) {
		return new SceneObjects.Static(drawer, asList, asVBO);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void clear(final int traceSize) {}

	public static class WordLayerObject extends StaticLayerObject {

		private final double startTime;
		private int frameCount = 0;
		private double currentTime = 0;
		private double previousTime = 0;
		public float fps = 00.00f;

		public WordLayerObject(final JOGLAWTGLRenderer renderer) {
			super(renderer, 0);
			startTime = System.currentTimeMillis();
			setTrace(0);
			setFading(false);
			setAlpha(WORLD_ALPHA);
			setOffset(WORLD_OFFSET);
			setScale(WORLD_SCALE);
			if ( renderer.getDrawEnv() ) {
				drawAxes(renderer.displaySurface.getEnvWidth(), renderer.displaySurface.getEnvHeight());
			}
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
		public void draw(final JOGLAWTGLRenderer renderer, final boolean picking) {
			super.draw(renderer, picking);
			GL gl = renderer.gl;
			if ( renderer.getShowFPS() ) {
				computeFrameRate();
				gl.glDisable(GL_BLEND);
				renderer.getContext().makeCurrent();
				gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
				gl.glRasterPos3d(-renderer.getWidth() / 10, renderer.getHeight() / 10, 0);
				gl.glScaled(8.0d, 8.0d, 8.0d);
				renderer.glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "fps : " + fps);
				gl.glScaled(0.125d, 0.125d, 0.125d);
				gl.glEnable(GL_BLEND);
			}
			gl.glColor4d(1, 1, 1, 1);
		}

		public void drawAxes(final double w, final double h) {
			double size = (w > h ? w : h) / 10;
			// add the world
			Geometry g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(w / 2, h / 2)).getInnerGeometry();
			Color c = new Color(150, 150, 150);
			addGeometry(g, GAMA.getSimulation().getAgent(), c, false, c, false, null, 0, size / 20, false,
				IShape.Type.ENVIRONMENT, 0);
			// build the lines
			GamaPoint origin = new GamaPoint();
			g = GamaGeometryType.buildLine(origin, new GamaPoint(size, 0, 0)).getInnerGeometry();
			addGeometry(g, null, Color.red, true, Color.red, false, null, 0, size / 20, false,
				IShape.Type.LINECYLINDER, 0);
			g = GamaGeometryType.buildLine(origin, new GamaPoint(0, size, 0)).getInnerGeometry();
			addGeometry(g, null, Color.green, true, Color.green, false, null, 0, size / 20, false,
				IShape.Type.LINECYLINDER, 0);
			g = GamaGeometryType.buildLine(origin, new GamaPoint(0, 0, size)).getInnerGeometry();
			addGeometry(g, null, Color.blue, true, Color.blue, false, null, 0, size / 20, false,
				IShape.Type.LINECYLINDER, 0);
			// add the legends
			addString("X", new GamaPoint(1.2f * size, 0.0d, 0.0d), 12, 12d, Color.black, "Arial", Font.BOLD, 0d, false);
			addString("Y", new GamaPoint(0.0d, -1.2f * size, 0.0d), 12, 12d, Color.black, "Arial", Font.BOLD, 0d, false);
			addString("Z", new GamaPoint(0.0d, 0.0d, 1.2f * size), 12, 12d, Color.black, "Arial", Font.BOLD, 0d, false);
			// add the triangles
			g =
				GamaGeometryType.buildArrow(origin, new GamaPoint(size + size / 10, 0, -size / 20), size / 4, size / 4,
					true).getInnerGeometry();
			addGeometry(g, null, Color.red, true, Color.red, false, null, 0, size / 9, false, IShape.Type.POLYGON, 0);
			g =
				GamaGeometryType.buildArrow(origin, new GamaPoint(0, size + size / 10, -size / 20), size / 4, size / 4,
					true).getInnerGeometry();
			addGeometry(g, null, Color.green, true, Color.green, false, null, 0, size / 9, false, IShape.Type.POLYGON,
				0);
			g =
				GamaGeometryType.buildArrow(origin, new GamaPoint(0, 0, size + size / 10), size / 4, size / 4, true)
					.getInnerGeometry();
			// FIXME See Issue 832: depth cannot be applied here.
			addGeometry(g, null, Color.blue, true, Color.blue, false, null, 0, 0, false, IShape.Type.POLYGON, 0);

		}

	}

}