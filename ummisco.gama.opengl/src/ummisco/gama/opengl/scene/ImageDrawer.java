/*********************************************************************************************
 *
 *
 * 'ImageDrawer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.utils.GLUtilNormal;
import ummisco.gama.opengl.utils.Vertex;

/**
 *
 * The class ImageDrawer.
 *
 * @author Arnaud Grignard - Alexis Drogoul
 * @since 4 mai 2013
 *
 */
public class ImageDrawer extends ObjectDrawer<ImageObject> {

	public ImageDrawer(final JOGLRenderer r) {
		super(r);
	}

	@Override
	protected void _draw(final GL2 gl, final ImageObject img) {
		final Texture curTexture = img.getTexture(gl, renderer, 0);
		if (curTexture == null) {
			return;
		}
		final double width = img.getDimensions().x;
		final double height = img.getDimensions().y;
		double x = 0, y = 0, z = 0;
		if (img.getLocation() != null) {
			x = img.getLocation().x;
			y = img.getLocation().y;
			z = img.getLocation().z;
		}
		// System.out.println("Drawing at " + x + " " + y + " " + z);
		// Binds the texture
		curTexture.bind(gl);
		renderer.setCurrentColor(gl, Color.white, img.getAlpha());
		// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 1.0f, 1.0f,
		// img.getAlpha().floatValue());
		final TextureCoords textureCoords = curTexture.getImageTexCoords();
		final float textureTop = textureCoords.top();
		final float textureBottom = textureCoords.bottom();
		final float textureLeft = textureCoords.left();
		final float textureRight = textureCoords.right();
		double angle = img.getRotationAngle();
		if (angle != 0) {
			gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
			// FIXME:Check counterwise or not, and do we rotate
			// around the center or around a point.
			gl.glRotated(angle, 0.0d, 0.0d, 1.0d);
			gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
		}

		if (renderer.getComputeNormal()) {
			final Vertex[] vertices = new Vertex[4];
			for (int i = 0; i < 4; i++) {
				vertices[i] = new Vertex();
			}
			vertices[0].x = x;
			vertices[0].y = -(y + height);
			vertices[0].z = z;

			vertices[1].x = x + width;
			vertices[1].y = -(y + height);
			vertices[1].z = z;

			vertices[2].x = x + width;
			vertices[2].y = -y;
			vertices[2].z = z;

			vertices[3].x = x;
			vertices[3].y = -y;
			vertices[3].z = z;
			GLUtilNormal.HandleNormal(vertices, -1, renderer);
		}
		renderer.setCurrentColor(gl, Color.white, img.getAlpha());
		// GLUtilGLContext.SetCurrentColor(gl, new float[] { 1.0f, 1.0f, 1.0f,
		// (float) (double) img.getAlpha() });
		gl.glBegin(GL2ES3.GL_QUADS);
		// bottom-left of the texture and quad
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3d(x, -(y + height), z);
		// bottom-right of the texture and quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3d(x + width, -(y + height), z);
		// top-right of the texture and quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3d(x + width, -y, z);
		// top-left of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3d(x, -y, z);
		gl.glEnd();
		angle = img.getRotationAngle();
		if (angle != 0) {
			gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
			gl.glRotated(angle, 0.0d, 0.0d, 1.0d);
			gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
		}
		// curTexture.disable(gl);
	}
}