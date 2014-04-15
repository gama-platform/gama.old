/*********************************************************************************************
 * 
 *
 * 'GLUtil.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
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

import msi.gama.metamodel.shape.GamaPoint;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.*;
import com.vividsolutions.jts.geom.*;


import java.awt.*;
import java.awt.image.BufferedImage;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.GLU;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.swing.OutputSynchronizer;
import msi.gama.jogl.JOGLAWTDisplaySurface;
import msi.gama.jogl.scene.*;
import msi.gama.jogl.utils.Camera.*;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.GAMA;
import com.jogamp.opengl.util.FPSAnimator;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class GLUtil {

	static void drawROI(final GL2 gl, final double x1, final double y1, final double x2, final double y2,
		final boolean z_fighting, final double maxEnvDim) {

		if ( z_fighting ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			// Draw on top of everything
			gl.glPolygonOffset(0.0f, (float) -maxEnvDim);
			gl.glBegin(GL2.GL_POLYGON);

			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glVertex3d(x2, -y1, 0.0f);

			gl.glVertex3d(x2, -y1, 0.0f);
			gl.glVertex3d(x2, -y2, 0.0f);

			gl.glVertex3d(x2, -y2, 0.0f);
			gl.glVertex3d(x1, -y2, 0.0f);

			gl.glVertex3d(x1, -y2, 0.0f);
			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glEnd();
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
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
		renderer.gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, model);

		DoubleBuffer proj = DoubleBuffer.allocate(16);
		renderer.gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, proj);

		IntBuffer view = IntBuffer.allocate(4);
		renderer.gl.glGetIntegerv(GL.GL_VIEWPORT, view);

		DoubleBuffer winPos = DoubleBuffer.allocate(3);
		renderer.glu.gluProject(realWorldPoint.x, realWorldPoint.y, 0d, model.array(), model.arrayOffset(), proj.array(),
				proj.arrayOffset(), view.array(), view.arrayOffset(), winPos.array(), winPos.arrayOffset());

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
