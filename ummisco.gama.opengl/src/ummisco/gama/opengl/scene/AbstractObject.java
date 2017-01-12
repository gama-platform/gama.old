
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

import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.Abstract3DRenderer;

public abstract class AbstractObject {

	static int index = 0;
	static Color pickedColor = Color.red;

	protected final DrawingAttributes attributes;
	private final LayerObject layer;
	protected Double alpha;
	public final int pickingIndex = index++;
	private boolean picked = false;
	protected final Texture[] textures;
	protected final double zFightingId;

	protected boolean overlay = false;

	public void enableOverlay(final boolean value) {
		overlay = value;
	}

	public boolean isOverlay() {
		return overlay;
	}

	private boolean lightInteraction = true;

	public AbstractObject(final DrawingAttributes attributes, final LayerObject layer, final Texture[] textures) {
		this.attributes = attributes;
		this.layer = layer;
		this.alpha = layer == null ? 1 : layer.alpha;
		this.textures = textures;
		this.zFightingId = computeZFightingId();
	}

	public void dispose(final GL gl) {
		if (textures == null) { return; }
		Arrays.fill(textures, null);
	}

	public AbstractObject(final DrawingAttributes attributes, final LayerObject layer) {
		this(attributes, layer, attributes.getTextures() != null ? new Texture[attributes.getTextures().size()] : null);
	}

	public DrawingAttributes getAttributes() {
		return attributes;
	}

	public Texture[] getTextures(final GL gl, final Abstract3DRenderer renderer) {
		if (textures == null) { return null; }
		final Texture[] result = new Texture[textures.length];
		for (int i = 0; i < textures.length; i++) {
			result[i] = getTexture(gl, renderer, i);
		}
		return result;
	}

	public Texture getAlternateTexture(final GL gl, final Abstract3DRenderer renderer) {
		return getTexture(gl, renderer, 1);
	}

	public Texture getPrimaryTexture(final GL gl, final Abstract3DRenderer renderer) {
		return getTexture(gl, renderer, 0);
	}

	public int getNumberOfTexture() {
		if (textures == null) { return 0; }
		return textures.length;
	}

	public Texture getTexture(final GL gl, final Abstract3DRenderer renderer, final int order) {
		if (textures == null) { return null; }
		if (order < 0 || order > textures.length - 1) { return null; }
		if (textures[order] == null) {
			textures[order] = computeTexture(gl, renderer, order);
		}
		return textures[order];
	}

	public String[] getTexturePaths(final IScope scope) {
		if (attributes.getTextures() == null) { return null; }
		final int numberOfTextures = attributes.getTextures().size();
		final String[] result = new String[numberOfTextures];
		for (int i = 0; i < numberOfTextures; i++) {
			final Object obj = attributes.getTextures().get(i);
			if (obj instanceof GamaImageFile) {
				result[i] = ((GamaImageFile) obj).getPath(scope);
			}
		}
		return result;
	}

	private Texture computeTexture(final GL gl, final Abstract3DRenderer renderer, final int order) {
		final Object obj = attributes.getTextures().get(order);
		if (obj instanceof BufferedImage) {
			return renderer.getCurrentScene().getTexture(gl, (BufferedImage) obj);
		} else if (obj instanceof GamaImageFile) { return renderer.getCurrentScene().getTexture(gl,
				(GamaImageFile) obj); }
		return null;
	}

	public boolean hasSeveralTextures() {
		return textures != null && textures.length > 1;
	}

	public boolean isTextured() {
		return textures != null && textures.length > 0;
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean isPicking) {
		final Abstract3DRenderer renderer = drawer.renderer;
		if (isPicking && layer.isPickable())
			gl.glLoadName(pickingIndex);
		drawer.draw(gl, this);
		picked = isPicked(renderer);
		if (picked)
			if (!renderer.getPickingState().isMenuOn()) {
				renderer.getPickingState().setMenuOn(true);
				renderer.getSurface().selectAgent(attributes);
			}
	}

	public boolean isPicked(final Abstract3DRenderer renderer) {
		return renderer.getPickingState().isPicked(pickingIndex) && layer.isPickable();
	}

	public Color getColor() {
		if (picked) { return pickedColor; }
		return attributes.getColor();
	}

	public double computeZFightingId() {
		final AgentIdentifier id = attributes.getAgentIdentifier();
		final double offset = id == null ? 0 : 1 / (double) (id.getIndex() + 10);
		return (layer == null ? 0 : layer.getOrder()) + offset;
	}

	public double getZFightingId() {
		return zFightingId;
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

	public void preload(final GL2 gl, final Abstract3DRenderer renderer) {
		// Make sure textures are loaded
		getPrimaryTexture(gl, renderer);
		getAlternateTexture(gl, renderer);
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

	public Color getBorder() {
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

	public void enableLightInteraction() {
		lightInteraction = true;
	}

	public void disableLightInteraction() {
		lightInteraction = false;
	}

	public boolean isLightInteraction() {
		return lightInteraction;
	}

	public double getLineWidth() {
		return attributes.getLineWidth();
	}
}
