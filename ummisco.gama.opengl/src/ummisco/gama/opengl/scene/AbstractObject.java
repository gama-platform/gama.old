
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
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

public abstract class AbstractObject implements ISceneObject {

	static int index = 0;
	static Color pickedColor = Color.red;

	protected final DrawingAttributes attributes;
	protected LayerObject layer;
	protected Double alpha;
	public int pickingIndex = index++;
	public boolean picked = false;
	protected final Texture[] textures;

	public AbstractObject(final DrawingAttributes attributes, final LayerObject layer) {
		this.attributes = attributes;
		this.layer = layer;
		this.alpha = layer.alpha;
		textures = attributes.textures != null ? new Texture[attributes.textures.size()] : null;
	}

	public Texture getTexture(final GL gl, final JOGLRenderer renderer, final int order) {
		if ( textures == null ) { return null; }
		if ( order < 0 || order > textures.length - 1 ) { return null; }
		if ( textures[order] == null ) {
			textures[order] = computeTexture(gl, renderer, order);
		}
		return textures[order];
	}

	protected Texture computeTexture(final GL gl, final JOGLRenderer renderer, final int order) {
		return renderer.getCurrentScene().getTexture(gl, (BufferedImage) attributes.textures.get(order));
	}

	public boolean hasSeveralTextures() {
		return textures != null && textures.length > 1;
	}

	public boolean isTextured() {
		return textures != null && textures.length > 0;
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean picking) {
		drawer.draw(gl, this);
	}

	@Override
	public void unpick() {
		picked = false;
	}

	@Override
	public void pick() {
		picked = true;
	}

	@Override
	public Color getColor() {
		if ( picked ) { return pickedColor; }
		return attributes.color;
	}

	public double getZ_fighting_id() {
		return layer.getOrder();
	}

	public double getLayerZ() {
		return layer.getOffset().z;
	}

	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(final Double alpha) {
		this.alpha = alpha;
	}

	public void preload(final GL2 gl, final JOGLRenderer renderer) {}

	public boolean isFilled() {
		return !attributes.empty;
	}

	public boolean isPicked() {
		return picked;
	}

	public GamaPoint getLocation() {
		return attributes.location;
	}

	public GamaPoint getDimensions() {
		return attributes.size;
	}

	public Color getBorder() {
		return attributes.border;
	}

	public double getHeight() {
		return attributes.depth == null ? 0 : attributes.depth.doubleValue();
	}

	public IAgent getAgent() {
		return attributes.agent;
	}
}
