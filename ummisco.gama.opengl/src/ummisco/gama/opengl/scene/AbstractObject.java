
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMaterial;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.Abstract3DRenderer.PickingState;

public abstract class AbstractObject {

	public static enum DrawerType {
		GEOMETRY, STRING, FIELD, RESOURCE
	}

	static int INDEX = 0;
	static final GamaColor PICKED_COLOR = new GamaColor(Color.red);

	protected final DrawingAttributes attributes;
	public final int index;
	private boolean picked = false;
	protected final Texture[] textures;
	private double zFightingOffset;

	public AbstractObject(final DrawingAttributes attributes, final Texture[] textures) {
		this.attributes = attributes;
		this.index = INDEX++;
		this.textures = textures;
	}

	public abstract DrawerType getDrawerType();

	public AbstractObject(final DrawingAttributes attributes) {
		this(attributes, attributes.getTextures() != null ? new Texture[attributes.getTextures().size()] : null);
	}

	public void dispose(final GL gl) {
		if (textures == null) { return; }
		Arrays.fill(textures, null);
	}

	public int[] getTexturesId(final GL2 gl, final Abstract3DRenderer renderer) {
		if (textures == null) { return null; }
		final int[] result = new int[textures.length];
		for (int i = 0; i < textures.length; i++) {
			final Texture t = getTexture(gl, renderer, i);
			result[i] = t == null ? 0 : t.getTextureObject();
		}
		return result;
	}

	public Texture getAlternateTexture(final GL gl, final Abstract3DRenderer renderer) {
		return getTexture(gl, renderer, 1);
	}

	public Texture getPrimaryTexture(final GL gl, final Abstract3DRenderer renderer) {
		return getTexture(gl, renderer, 0);
	}

	private Texture getTexture(final GL gl, final Abstract3DRenderer renderer, final int order) {
		if (textures == null) { return null; }
		if (order < 0 || order > textures.length - 1) { return null; }
		if (textures[order] == null) {
			textures[order] = computeTexture(gl, renderer, order);
		}
		return textures[order];
	}

	private Texture computeTexture(final GL gl, final Abstract3DRenderer renderer, final int order) {
		Object obj = null;
		try {
			obj = attributes.getTextures().get(order);
		} catch (final IndexOutOfBoundsException e) {// do nothing. Can arrive in the new shader architecture
		}
		if (obj instanceof BufferedImage) {
			return renderer.getCurrentScene().getTexture(gl, (BufferedImage) obj);
		} else if (obj instanceof GamaImageFile) { return renderer.getCurrentScene().getTexture(gl,
				(GamaImageFile) obj); }
		return null;
	}

	public boolean isTextured() {
		return textures != null && textures.length > 0;
	}

	public boolean isPicked() {
		return picked;
	}

	public void draw(final GL2 gl, final ObjectDrawer<AbstractObject> drawer, final boolean isPicking) {
		final Abstract3DRenderer renderer = drawer.renderer;
		if (isPicking)
			gl.glLoadName(index);
		drawer.draw(gl, this);
		if (isPicking) {
			final PickingState state = renderer.getPickingState();
			picked = state.isPicked(index);
			if (picked && !state.isMenuOn()) {
				state.setMenuOn(true);
				renderer.getSurface().selectAgent(attributes);
			}
		}
	}

	public GamaColor getColor() {
		return picked ? PICKED_COLOR : attributes.getColor();
	}

	public double getZFightingOffset() {
		return zFightingOffset;
	}

	public boolean isFilled() {
		return !attributes.isEmpty();
	}

	public GamaPoint getLocation() {
		return attributes.getLocation();
	}

	public GamaPoint getDimensions() {
		return attributes.getSize();
	}

	public GamaColor getBorder() {
		return attributes.getBorder();
	}

	public Double getHeight() {
		return attributes.getDepth();
	}

	public Double getRotationAngle() {
		if (attributes.getAngle() == null) { return null; }
		// AD Change to a negative rotation to fix Issue #1514
		return -attributes.getAngle();
	}

	public double getLineWidth() {
		return attributes.getLineWidth();
	}

	public GamaPoint getRotationAxis() {
		return attributes.getAxis();
	}

	public GamaMaterial getMaterial() {
		return attributes.getMaterial();
	}

	public void setZFightingOffset(final double d) {
		zFightingOffset = d;
	}

}
