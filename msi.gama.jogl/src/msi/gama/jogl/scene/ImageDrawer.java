/*********************************************************************************************
 * 
 *
 * 'ImageDrawer.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.scene;

import static javax.media.opengl.GL.*;
import msi.gama.jogl.utils.*;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * 
 * The class ImageDrawer.
 * 
 * @author drogoul
 * @since 4 mai 2013
 * 
 */
public class ImageDrawer extends ObjectDrawer<ImageObject> {

	public float textureTop, textureBottom, textureLeft, textureRight;

	public ImageDrawer(final JOGLAWTGLRenderer r) {
		super(r);
	}

	@Override
	protected void _draw(final ImageObject img) {

		MyTexture curTexture = img.getTexture(renderer);
		if ( curTexture == null ) { return; }
		double width = img.dimensions.x;
		double height = img.dimensions.y;
		double x = img.location.x;
		double y = img.location.y;
		double z = img.location.z;
		// Enable the texture
		curTexture.bindTo(renderer);
		renderer.gl.glColor4d(1.0d, 1.0d, 1.0d, img.getAlpha());
		TextureCoords textureCoords;
		textureCoords = curTexture.getTexture().getImageTexCoords();
		textureTop = textureCoords.top();
		textureBottom = textureCoords.bottom();
		textureLeft = textureCoords.left();
		textureRight = textureCoords.right();
		if ( img.angle != 0 ) {
			renderer.gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
			// FIXME:Check counterwise or not, and do we rotate
			// around the center or around a point.
			renderer.gl.glRotated(-img.angle, 0.0d, 0.0d, 1.0d);
			renderer.gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
		}

		if ( renderer.computeNormal ) {
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

		renderer.gl.glColor4d(1.0d, 1.0d, 1.0d, img.getAlpha());
		renderer.gl.glBegin(GL_QUADS);
		// bottom-left of the texture and quad
		renderer.gl.glTexCoord2f(textureLeft, textureBottom);
		renderer.gl.glVertex3d(x, -(y + height), z);
		// bottom-right of the texture and quad
		renderer.gl.glTexCoord2f(textureRight, textureBottom);
		renderer.gl.glVertex3d(x + width, -(y + height), z);
		// top-right of the texture and quad
		renderer.gl.glTexCoord2f(textureRight, textureTop);
		renderer.gl.glVertex3d(x + width, -y, z);
		// top-left of the texture and quad
		renderer.gl.glTexCoord2f(textureLeft, textureTop);
		renderer.gl.glVertex3d(x, -y, z);
		renderer.gl.glEnd();

		if ( img.angle != 0 ) {
			renderer.gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
			renderer.gl.glRotated(img.angle, 0.0d, 0.0d, 1.0d);
			renderer.gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
		}

		renderer.gl.glDisable(GL_TEXTURE_2D);
	}
}