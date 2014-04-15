/*********************************************************************************************
 * 
 *
 * 'GLUtil.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.utils;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.nio.*;
import javax.media.opengl.GL;
import msi.gama.metamodel.shape.GamaPoint;

public class GLUtil {

	static void drawROI(final GL gl, final double x1, final double y1, final double x2, final double y2,
		final boolean z_fighting, final double maxEnvDim) {

		if ( z_fighting ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
			// Draw on top of everything
			gl.glPolygonOffset(0.0f, (float) -maxEnvDim);
			gl.glBegin(GL.GL_POLYGON);

			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glVertex3d(x2, -y1, 0.0f);

			gl.glVertex3d(x2, -y1, 0.0f);
			gl.glVertex3d(x2, -y2, 0.0f);

			gl.glVertex3d(x2, -y2, 0.0f);
			gl.glVertex3d(x1, -y2, 0.0f);

			gl.glVertex3d(x1, -y2, 0.0f);
			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glEnd();
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		} else {
			gl.glBegin(GL.GL_LINES);

			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glVertex3d(x2, -y1, 0.0f);

			gl.glVertex3d(x2, -y1, 0.0f);
			gl.glVertex3d(x2, -y2, 0.0f);

			gl.glVertex3d(x2, -y2, 0.0f);
			gl.glVertex3d(x1, -y2, 0.0f);

			gl.glVertex3d(x1, -y2, 0.0f);
			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glEnd();
		}

	}

	public static GamaPoint getIntWorldPointFromWindowPoint(final JOGLAWTGLRenderer renderer, final Point windowPoint) {
		GamaPoint p = GLUtil.getRealWorldPointFromWindowPoint(renderer, windowPoint);
		return new GamaPoint((int) p.x, (int) p.y);
	}

	public static GamaPoint getRealWorldPointFromWindowPoint(final JOGLAWTGLRenderer renderer, final Point windowPoint) {
		if ( renderer.glu == null ) { return null; }
		int realy = 0;// GL y coord pos
		double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords

		int x = (int) windowPoint.getX(), y = (int) windowPoint.getY();

		realy = renderer.viewport[3] - y;

		renderer.glu.gluUnProject(x, realy, 0.1, renderer.mvmatrix, 0, renderer.projmatrix, 0, renderer.viewport, 0,
			wcoord, 0);
		GamaPoint v1 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

		renderer.glu.gluUnProject(x, realy, 0.9, renderer.mvmatrix, 0, renderer.projmatrix, 0, renderer.viewport, 0,
			wcoord, 0);
		GamaPoint v2 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

		GamaPoint v3 = v2.minus(v1).normalized();
		float distance =
			(float) (renderer.camera.getPosition().getZ() / GamaPoint.dotProduct(new GamaPoint(0.0, 0.0, -1.0), v3));
		GamaPoint worldCoordinates = renderer.camera.getPosition().plus(v3.times(distance));

		return new GamaPoint(worldCoordinates.x, worldCoordinates.y);
	}

	public static Point2D.Double getWindowPointPointFromRealWorld(final JOGLAWTGLRenderer renderer,
		final Point realWorldPoint) {
		if ( renderer.glu == null ) { return null; }

		DoubleBuffer model = DoubleBuffer.allocate(16);
		renderer.gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model);

		DoubleBuffer proj = DoubleBuffer.allocate(16);
		renderer.gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, proj);

		IntBuffer view = IntBuffer.allocate(4);
		renderer.gl.glGetIntegerv(GL.GL_VIEWPORT, view);

		DoubleBuffer winPos = DoubleBuffer.allocate(3);
		renderer.glu.gluProject(realWorldPoint.x, realWorldPoint.y, 0, model, proj, view, winPos);

		final Point2D.Double WindowPoint = new Point2D.Double(winPos.get(), renderer.viewport[3] - winPos.get());
		return WindowPoint;
	}


	static void drawCubeDisplay(final JOGLAWTGLRenderer renderer, final float width) {
		final float envMaxDim = width;
		renderer.drawModel();
		renderer.gl.glTranslatef(envMaxDim, 0, 0);
		renderer.gl.glRotatef(90, 0, 1, 0);
		renderer.drawModel();
		renderer.gl.glTranslatef(envMaxDim, 0, 0);
		renderer.gl.glRotatef(90, 0, 1, 0);
		renderer.drawModel();
		renderer.gl.glTranslatef(envMaxDim, 0, 0);
		renderer.gl.glRotatef(90, 0, 1, 0);
		renderer.drawModel();
		renderer.gl.glTranslatef(envMaxDim, 0, 0);
		renderer.gl.glRotatef(90, 0, 1, 0);
		renderer.gl.glRotatef(-90, 1, 0, 0);
		renderer.gl.glTranslatef(0, envMaxDim, 0);
		renderer.drawModel();
		renderer.gl.glTranslatef(0, -envMaxDim, 0);
		renderer.gl.glRotatef(90, 1, 0, 0);
		renderer.gl.glRotatef(90, 1, 0, 0);
		renderer.gl.glTranslatef(0, 0, envMaxDim);
		renderer.drawModel();
		renderer.gl.glTranslatef(0, 0, -envMaxDim);
		renderer.gl.glRotatef(-90, 1, 0, 0);
	}

}
