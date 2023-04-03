
/*******************************************************************************************************
 *
 * AbstractObject.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.DrawingAttributes.DrawerType;
import ummisco.gama.opengl.OpenGL;

/**
 * The Class AbstractObject.
 *
 * @param <T>
 *            the generic type
 * @param <ATT>
 *            the generic type
 */
public abstract class AbstractObject<T, ATT extends DrawingAttributes> implements IDisposable {

	/** The attributes. */
	private final ATT attributes;

	/** The textures. */
	protected final int[] textures;

	/** The object. */
	protected final T object;

	/** The type. */
	public final DrawerType type;

	/**
	 * Instantiates a new abstract object.
	 *
	 * @param object
	 *            the object
	 * @param attributes
	 *            the attributes
	 * @param type
	 *            the type
	 */
	public AbstractObject(final T object, final ATT attributes, final DrawerType type) {
		this.object = object;
		this.type = type;
		this.attributes = attributes;
		if (attributes.getTextures() != null) {
			textures = new int[attributes.getTextures().size()];
			Arrays.fill(textures, OpenGL.NO_TEXTURE);
		} else {
			textures = null;
		}
	}

	@Override
	public void dispose() {}

	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public T getObject() { return object; }

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

	/**
	 * Gets the texture.
	 *
	 * @param gl
	 *            the gl
	 * @param order
	 *            the order
	 * @return the texture
	 */
	private int getTexture(final OpenGL gl, final int order) {
		if (textures == null || order < 0 || order > textures.length - 1) return OpenGL.NO_TEXTURE;
		if (isAnimated() || textures[order] == OpenGL.NO_TEXTURE) {
			Object obj = null;
			try {
				obj = getAttributes().getTextures().get(order);
			} catch (final IndexOutOfBoundsException e) {// do nothing. Can arrive in the new shader architecture
			}
			if (obj instanceof IImageProvider im) {
				final DrawingAttributes fd = getAttributes();
				textures[order] = gl.getTextureId(im, fd.useCache());
			} else if (obj instanceof BufferedImage im) { textures[order] = gl.getTextureId(im); }
		}
		return textures[order];
	}

	/**
	 * Checks if is animated.
	 *
	 * @return true, if is animated
	 */
	protected boolean isAnimated() { return getAttributes().isAnimated(); }

	/**
	 * Checks if is textured.
	 *
	 * @return true, if is textured
	 */
	public boolean isTextured() { return textures != null && textures.length > 0; }

	/**
	 * Checks if is filled.
	 *
	 * @return true, if is filled
	 */
	public boolean isFilled() { return !getAttributes().isEmpty(); }

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public ATT getAttributes() { return attributes; }

	/**
	 * Gets the translation into.
	 *
	 * @param p
	 *            the p
	 * @return the translation into
	 */
	public void getTranslationInto(final GamaPoint p) {
		final var explicitLocation = getAttributes().getLocation();
		if (explicitLocation == null) {
			p.setLocation(0, 0, 0);
		} else {
			p.setLocation(explicitLocation);
		}
	}

	/**
	 * Gets the translation for rotation into.
	 *
	 * @param p
	 *            the p
	 * @return the translation for rotation into
	 */
	public void getTranslationForRotationInto(final GamaPoint p) {
		getTranslationInto(p);
	}

	/**
	 * Gets the translation for scaling into.
	 *
	 * @param p
	 *            the p
	 * @return the translation for scaling into
	 */
	public void getTranslationForScalingInto(final GamaPoint p) {
		p.setLocation(0, 0, 0);
	}

	/**
	 * Checks if is bordered.
	 *
	 * @return true, if is bordered
	 */
	public boolean isBordered() { return getAttributes().getBorder() != null; }

}
