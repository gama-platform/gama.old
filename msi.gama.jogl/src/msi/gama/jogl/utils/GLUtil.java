/*
 * GLUtil version v1.04 date 20.08.2010
 * This is simple class which contains static methods is create to you
 * build simple OpenGL program in java whihout writing a lot wate code.
 * You dont have create an object of this class.
 * New methods:
 * ->drawVec(GL gl,Wector point,Wector direction) - draw vector in specified location (point)
 * ->drawVec(GL gl,Wector direction)
 * ->drawCircle(GL gl,double size) - draw cirle with size radius on OXY plane in point 0,0,0
 * ->drawEmptyCircle(GL gl,double size) - draw dont filled cirle with size radius on OXY plane in point 0,0,0
 */
package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import msi.gama.jogl.scene.MyTexture;
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
	
	public static GamaPoint getIntWorldPointFromWindowPoint(final JOGLAWTGLRenderer renderer,final Point windowPoint) {
		GamaPoint p = GLUtil.getRealWorldPointFromWindowPoint(renderer,windowPoint);
		return new GamaPoint((int) p.x, (int) p.y);
	}
	
	public static GamaPoint getRealWorldPointFromWindowPoint(final JOGLAWTGLRenderer renderer, final Point windowPoint) {
		if ( renderer.glu == null ) { return null; }
		int realy = 0;// GL y coord pos
		double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords

		int x = (int) windowPoint.getX(), y = (int) windowPoint.getY();

		realy = renderer.viewport[3] - y;

		renderer.glu.gluUnProject(x, realy, 0.1,renderer. mvmatrix, 0, renderer.projmatrix, 0, renderer.viewport, 0, wcoord, 0);
		GamaPoint v1 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

		renderer.glu.gluUnProject(x, realy, 0.9, renderer.mvmatrix, 0, renderer.projmatrix, 0, renderer.viewport, 0, wcoord, 0);
		GamaPoint v2 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

		GamaPoint v3 = v2.minus(v1).normalized();
		float distance =
			(float) (renderer.camera.getPosition().getZ() / GamaPoint.dotProduct(new GamaPoint(0.0, 0.0, -1.0), v3));
		GamaPoint worldCoordinates = renderer.camera.getPosition().plus(v3.times(distance));

		return new GamaPoint(worldCoordinates.x, worldCoordinates.y);
	}
	
	
	public static Point2D.Double getWindowPointPointFromRealWorld(final JOGLAWTGLRenderer renderer,final Point realWorldPoint) {
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
	
	public static MyTexture createTexture(final JOGLAWTGLRenderer renderer,final BufferedImage image, final boolean isDynamic, final int index) {
		// Create a OpenGL Texture object from (URL, mipmap, file suffix)
		// need to have an opengl context valide
		renderer.getContext().makeCurrent();
		Texture texture;
		try {
			texture = TextureIO.newTexture(image, false /* true for mipmapping */);
		} catch (final GLException e) {
			return null;
		}
		// FIXME:need to see the effect of this bending
		renderer.gl.glBindTexture(GL.GL_TEXTURE_2D, index);
		texture.setTexParameteri(GL_TEXTURE_MIN_FILTER, renderer.minAntiAliasing);
		texture.setTexParameteri(GL_TEXTURE_MAG_FILTER, renderer.magAntiAliasing);
		final MyTexture curTexture = new MyTexture();
		curTexture.texture = texture;
		curTexture.isDynamic = isDynamic;
		// GuiUtils.debug("JOGLAWTGLRenderer.createTexture for " + image);
		renderer.getContext().release();
		return curTexture;
	}
	
	
	static void drawCubeDisplay(final JOGLAWTGLRenderer renderer,final float width) {
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
