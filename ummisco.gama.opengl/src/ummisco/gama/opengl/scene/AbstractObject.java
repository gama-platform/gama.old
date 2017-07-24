
/*********************************************************************************************
 *
 * 'AbstractObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;

public abstract class AbstractObject {

	public static enum DrawerType {
		GEOMETRY, STRING, FIELD
	}

	protected final DrawingAttributes attributes;
	protected final int[] textures;

	public AbstractObject(final DrawingAttributes attributes) {
		this.attributes = attributes;
		if (attributes.getTextures() != null) {
			textures = new int[attributes.getTextures().size()];
			Arrays.fill(textures, OpenGL.NO_TEXTURE);
		} else
			textures = null;
	}

	public abstract DrawerType getDrawerType();

	public int[] getTexturesId(final OpenGL gl) {
		if (textures == null) { return null; }
		// final int[] result = new int[textures.length];
		for (int i = 0; i < textures.length; i++) {
			final int t = getTexture(gl, i);
			textures[i] = t == OpenGL.NO_TEXTURE ? 0 : t;
		}
		return textures;
	}

	/**
	 * Returns the id of the texture at index 1
	 * 
	 * @param gl
	 * @return the id of the texture or Integer.MAX_VALUE if none is defined
	 */
	public int getAlternateTexture(final OpenGL gl) {
		return getTexture(gl, 1);
	}

	/**
	 * Returns the id of the texture at index 0
	 * 
	 * @param gl
	 * @return the id of the texture or Integer.MAX_VALUE if none is defined
	 */
	public int getPrimaryTexture(final OpenGL gl) {
		return getTexture(gl, 0);
	}

	private int getTexture(final OpenGL gl, final int order) {
		if (textures == null) { return OpenGL.NO_TEXTURE; }
		if (order < 0 || order > textures.length - 1) { return OpenGL.NO_TEXTURE; }
		if (isAnimated() || textures[order] == OpenGL.NO_TEXTURE) {
			Object obj = null;
			try {
				obj = attributes.getTextures().get(order);
			} catch (final IndexOutOfBoundsException e) {// do nothing. Can arrive in the new shader architecture
			}
			if (obj instanceof BufferedImage) {
				textures[order] = gl.getTexture((BufferedImage) obj).getTextureObject();
			} else if (obj instanceof GamaImageFile) {
				final FileDrawingAttributes fd = (FileDrawingAttributes) attributes;
				textures[order] = gl.getTexture((GamaImageFile) obj, fd.useCache()).getTextureObject();
			}
		}
		return textures[order];
	}

	protected boolean isAnimated() {
		return attributes.isAnimated();
	}

	public boolean isTextured() {
		return textures != null && textures.length > 0;
	}

	public final void draw(final OpenGL gl, final ObjectDrawer<AbstractObject> drawer, final boolean isPicking) {
		if (isPicking)
			gl.registerForSelection(attributes.getIndex());
		drawer.draw(this);
		if (isPicking) {
			gl.markIfSelected(attributes);
		}
	}

	public GamaColor getColor() {
		return attributes.getColor();
	}

	public boolean isFilled() {
		return !attributes.isEmpty();
	}

	public GamaPoint getLocation() {
		return attributes.getLocation();
	}

	public Scaling3D getDimensions() {
		return attributes.getSize();
	}

	public GamaColor getBorder() {
		return attributes.getBorder();
	}

	public Double getHeight() {
		return attributes.getHeight();
	}

	public AxisAngle getRotation() {
		return attributes.getRotation();
	}

	public double getLineWidth() {
		return attributes.getLineWidth();
	}

	public GamaMaterial getMaterial() {
		return attributes.getMaterial();
	}

	public int getIndex() {
		return attributes.getIndex();
	}

}
