/*********************************************************************************************
 *
 *
 * 'ImageObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

public class ImageObject extends AbstractObject {

	// final public BufferedImage image;
	final private GamaImageFile file;
	final private BufferedImage image;

	public ImageObject(final GamaImageFile file, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer, new Texture[1]);
		this.file = file;
		this.image = null;
	}

	public ImageObject(final BufferedImage image, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer, new Texture[1]);
		this.image = image;
		this.file = null;
	}

	@Override
	public Texture getTexture(final GL gl, final JOGLRenderer renderer, final int order) {
		Texture texture = null;
		if (image == null) {
			texture = renderer.getCurrentScene().getTexture(gl, file);
		} else {
			texture = renderer.getCurrentScene().getTexture(gl, image);
		}
		if (getDimensions() == null) {
			attributes.size = new GamaPoint(renderer.data.getEnvWidth(), renderer.data.getEnvHeight());
		}
		return texture;
	}

	@Override
	public boolean isTextured() {
		return true;
	}

	@Override
	public boolean isFilled() {
		return true;
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean isPicking) {
		gl.glEnable(GL.GL_TEXTURE_2D);
		super.draw(gl, drawer, isPicking);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

}
