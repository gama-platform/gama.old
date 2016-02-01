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

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.utils.*;

/**
 *
 * The class ImageDrawer.
 *
 * @author Arnaud Grignard - Alexis Drogoul
 * @since 4 mai 2013
 *
 */
public class ImageDrawer extends ObjectDrawer<ImageObject> {

	// public float textureTop, textureBottom, textureLeft, textureRight;

	public ImageDrawer(final JOGLRenderer r) {
		super(r);
	}

	@Override
	protected void _draw(final GL2 gl, final ImageObject img) {
		Texture curTexture = img.getTexture(gl, renderer, 0);
		if ( curTexture == null ) { return; }
		double width = img.getDimensions().x;
		double height = img.getDimensions().y;

		double x = img.getLocation().x;
		double y = img.getLocation().y;
		double z = img.getLocation().z;
		// Binds the texture
		curTexture.bind(gl);
		gl.glColor4d(1.0d, 1.0d, 1.0d, img.getAlpha());
		TextureCoords textureCoords = curTexture.getImageTexCoords();
		float textureTop = textureCoords.top();
		float textureBottom = textureCoords.bottom();
		float textureLeft = textureCoords.left();
		float textureRight = textureCoords.right();
		if ( img.getAngle() != 0 ) {
			gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
			// FIXME:Check counterwise or not, and do we rotate
			// around the center or around a point.
			gl.glRotated(img.getAngle(), 0.0d, 0.0d, 1.0d);
			gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
		}

		if ( renderer.getComputeNormal() ) {
			Vertex[] vertices = new Vertex[4];
			for ( int i = 0; i < 4; i++ ) {
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
			GLUtilNormal.HandleNormal(vertices, null, img.getAlpha(), -1, renderer);
		}
		gl.glColor4d(1.0d, 1.0d, 1.0d, img.getAlpha());
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

		if ( img.getAngle() != 0 ) {
			gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
			gl.glRotated(img.getAngle(), 0.0d, 0.0d, 1.0d);
			gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
		}
		// curTexture.disable(gl);
	}
}