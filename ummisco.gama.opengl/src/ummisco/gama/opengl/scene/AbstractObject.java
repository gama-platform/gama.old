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
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import ummisco.gama.opengl.JOGLRenderer;

public abstract class AbstractObject implements ISceneObject {

	static int index = 0;
	static Color pickedColor = Color.red;

	private Color color;
	private Double z_fighting_id = 0.0;
	private Double alpha = 1d;
	public int pickingIndex = index++;
	public boolean picked = false;
	public boolean fill = true;
	private Texture texture;

	public AbstractObject(final Color c, final Double a) {
		setColor(c);
		if ( a != null ) {
			setAlpha(a);
		}
	}

	public Texture getTexture(final GL gl, final JOGLRenderer renderer) {
		if ( texture == null ) {
			setTexture(computeTexture(gl, renderer));
		}
		return texture;
	}

	/**
	 * @param computeTexture
	 */
	private void setTexture(final Texture computedTexture) {
		texture = computedTexture;
	}

	/**
	 * @return
	 */
	abstract protected Texture computeTexture(final GL gl, final JOGLRenderer renderer);

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean picking) {
		drawer.draw(gl, this);
	}

	@Override
	public void unpick() {}

	public Double getZ_fighting_id() {
		return z_fighting_id;
	}

	public void setZ_fighting_id(final Double z_fighting_id) {
		this.z_fighting_id = z_fighting_id;
	}

	@Override
	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(final Double alpha) {
		this.alpha = alpha;
	}

	public void preload(final GL2 gl, final JOGLRenderer renderer) {

	}

	// public void dispose(final JOGLRenderer renderer) {
	// if ( texture != null ) {
	// texture.dispose();
	// texture = null;
	// }
	// }

}
