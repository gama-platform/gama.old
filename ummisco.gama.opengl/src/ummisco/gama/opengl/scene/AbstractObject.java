
/*********************************************************************************************
 *
 *
 * 'AbstractObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

public abstract class AbstractObject {

	static int index = 0;
	static Color pickedColor = Color.red;

	protected final DrawingAttributes attributes;
	private final LayerObject layer;
	protected Double alpha;
	public final int pickingIndex = index++;
	private boolean picked = false;
	protected final Texture[] textures;

	public AbstractObject(final DrawingAttributes attributes, final LayerObject layer, final Texture[] textures) {
		this.attributes = attributes;
		this.layer = layer;
		this.alpha = layer == null ? 1 : layer.alpha;
		this.textures = textures;
	}

	public void dispose(final GL gl) {
		if (textures == null) {
			return;
		}
		for (int i = 0; i < textures.length; i++) {
			textures[i] = null;
		}
	}

	public AbstractObject(final DrawingAttributes attributes, final LayerObject layer) {
		this(attributes, layer, attributes.getTextures() != null ? new Texture[attributes.getTextures().size()] : null);
	}

	public Texture getTexture(final GL gl, final JOGLRenderer renderer, final int order) {
		if (textures == null) {
			return null;
		}
		if (order < 0 || order > textures.length - 1) {
			return null;
		}
		if (textures[order] == null) {
			textures[order] = computeTexture(gl, renderer, order);
		}
		return textures[order];
	}

	private Texture computeTexture(final GL gl, final JOGLRenderer renderer, final int order) {
		final Object obj = attributes.getTextures().get(order);
		if (obj instanceof BufferedImage) {
			return renderer.getCurrentScene().getTexture(gl, (BufferedImage) obj);
		} else if (obj instanceof GamaImageFile) {
			return renderer.getCurrentScene().getTexture(gl, (GamaImageFile) obj);
		}
		return null;
	}

	public boolean hasSeveralTextures() {
		return textures != null && textures.length > 1;
	}

	public boolean isTextured() {
		return textures != null && textures.length > 0;
	}

	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean isPicking) {
		final JOGLRenderer renderer = drawer.renderer;
		picked = renderer.getPickingState().isPicked(pickingIndex);
		if (isPicking)
			gl.glLoadName(pickingIndex);
		drawer.draw(gl, this);
		if (picked && !renderer.getPickingState().isMenuOn()) {
			renderer.getPickingState().setMenuOn(true);
			// System.out.println("Object " + pickingIndex + " showing menu");
			renderer.getSurface().selectAgent(attributes);
		}
	}

	public Color getColor() {
		if (picked) {
			return pickedColor;
		}
		return attributes.color;
	}

	public double getZ_fighting_id() {
		final AgentIdentifier id = attributes.getAgentIdentifier();
		final double offset = id == null ? 0 : 1 / (double) (id.getIndex() + 10);
		// final double offset = 0;
		return (layer == null ? 0 : layer.getOrder()) + offset;
	}

	public double getLayerZ() {
		return layer == null ? 0 : layer.getOffset().z;
	}

	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(final Double alpha) {
		this.alpha = alpha;
	}

	public void preload(final GL2 gl, final JOGLRenderer renderer) {
	}

	public boolean isFilled() {
		return !attributes.isEmpty();
	}

	public GamaPoint getLocation() {
		return attributes.location;
	}

	public GamaPoint getDimensions() {
		return attributes.size;
	}

	public Color getBorder() {
		return attributes.getBorder();
	}

	public double getHeight() {
		return attributes.getDepth();
	}

	// public IAgent getAgent() {
	// return attributes.getAgent();
	// }

	public double getRotationAngle() {
		if (attributes.rotation == null || attributes.rotation.key == null) {
			return 0;
		}
		// AD Change to a negative rotation to fix Issue #1514
		return -attributes.rotation.key;
	}
}
