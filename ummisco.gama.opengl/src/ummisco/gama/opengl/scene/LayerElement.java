
/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.AbstractObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.OpenGL;

public class LayerElement<T, ATT extends DrawingAttributes> implements IDisposable {

	public enum DrawerType {
		GEOMETRY, STRING, FIELD, RESOURCE
	}

	private static PoolUtils.ObjectPool<LayerElement> ELEMENT_POOL =
			PoolUtils.create("LayerElements", true, () -> new LayerElement(), null);

	public static <T, ATT extends DrawingAttributes> LayerElement<T, ATT> createLayerElement(final T object,
			final ATT attributes, final DrawerType type) {
		final LayerElement<T, ATT> element = ELEMENT_POOL.get(); // new LayerElement<>();
		element.init(object, attributes, type);
		return element;
	}

	private ATT attributes;
	protected int[] textures;
	protected T object;
	protected DrawerType type;

	private LayerElement() {}

	public void init(final T object, final ATT attributes, final DrawerType type) {
		this.object = object;
		this.attributes = attributes;
		if (attributes.getTextures() != null) {
			textures = new int[attributes.getTextures().size()];
			Arrays.fill(textures, OpenGL.NO_TEXTURE);
		} else {
			textures = null;
		}
		this.type = type;
	}

	/**
	 * Called to clean the object so that it can be reused
	 */
	@Override
	public void dispose() {
		attributes.dispose();
		attributes = null;
		textures = null;
		object = null;
		type = null;
		ELEMENT_POOL.release(this);
	}

	public T getObject() {
		return object;
	}

	public DrawerType getDrawerType() {
		return type;
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
			} catch (final IndexOutOfBoundsException e) {}
			if (obj instanceof BufferedImage) {
				textures[order] = gl.getTextureId((BufferedImage) obj);
			} else if (obj instanceof GamaImageFile) {
				final DrawingAttributes fd = (DrawingAttributes) getAttributes();
				textures[order] = gl.getTextureId((GamaImageFile) obj, fd.useCache());
			}
		}
		return textures[order];
	}

	protected boolean isAnimated() {
		return getAttributes().isAnimated();
	}

	public boolean isTextured() {
		return textures != null && textures.length > 0;
	}

	@SuppressWarnings ("unchecked")
	public final <T extends LayerElement<?, ?>> void draw(final OpenGL gl, final ObjectDrawer<T> drawer,
			final boolean isPicking) {
		if (isPicking) {
			gl.registerForSelection(getAttributes().getIndex());
		}
		final boolean previous = gl.setLighting(getAttributes().isLighting());
		drawer.draw((T) this);
		gl.setLighting(previous);
		if (isPicking) {
			gl.markIfSelected(getAttributes());
		}
	}

	public boolean isFilled() {
		return !getAttributes().isEmpty();
	}

	public Envelope3D getEnvelope(final OpenGL gl) {
		return gl.getEnvelopeFor(getObject());
	}

	public ATT getAttributes() {
		return attributes;
	}

	public void getTranslationInto(final GamaPoint p) {
		final GamaPoint explicitLocation = getAttributes().getLocation();
		if (explicitLocation == null) {
			p.setLocation(0, 0, 0);
			return;
		}
		if (type == DrawerType.GEOMETRY) {
			getContourCoordinates((Geometry) getObject()).getCenter(p);
			p.negate();
			p.add(explicitLocation);
		} else {
			p.setLocation(explicitLocation);
		}
	}

	public void getTranslationForRotationInto(final GamaPoint p) {
		if (type == DrawerType.GEOMETRY) {
			final GamaPoint explicitLocation = getAttributes().getLocation();
			if (explicitLocation == null) {
				GeometryUtils.getContourCoordinates((Geometry) getObject()).getCenter(p);
			} else {
				p.setLocation(explicitLocation);
			}
		} else {
			getTranslationInto(p);
		}
	}

	public void getTranslationForScalingInto(final GamaPoint p) {
		if (type == DrawerType.GEOMETRY) {
			GeometryUtils.getContourCoordinates((Geometry) getObject()).getCenter(p);
		} else {
			p.setLocation(0, 0, 0);
		}
	}

}
