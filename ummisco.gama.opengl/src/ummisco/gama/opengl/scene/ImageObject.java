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

import java.awt.Color;
import java.awt.image.BufferedImage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import ummisco.gama.opengl.JOGLRenderer;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;

public class ImageObject extends AbstractObject {

	final public BufferedImage image;
	final public IAgent agent;
	final public int layerId;
	final public GamaPoint location, dimensions;
	final public Double angle;
	final public boolean isDynamic;
	final public String name;

	public ImageObject(final BufferedImage image, final IAgent agent, final int layerId, final GamaPoint location,
		final Double alpha, final GamaPoint dimensions, final Double angle, final boolean isDynamic, final String name) {
		super(null, alpha);
		setZ_fighting_id((double) layerId);
		this.image = image;
		this.agent = agent;
		this.location = location;
		this.dimensions = dimensions;
		this.angle = angle;
		this.isDynamic = isDynamic;
		this.layerId = layerId;
		this.name = name;
	}

	@Override
	public void unpick() {
		picked = false;
	}

	public void pick() {
		picked = true;
	}

	@Override
	public Color getColor() {
		if ( picked ) { return pickedColor; }
		return super.getColor();
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean picking) {
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		if ( picking ) {
			JOGLRenderer renderer = drawer.renderer;
			gl.glPushMatrix();
			gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( agent != null ) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					// The picked image is the grid
					if ( this.name != null ) {
						final GamaPoint pickedPoint =
							renderer.getIntWorldPointFromWindowPoint(renderer.camera.getLastMousePressedPosition());
						IAgent ag = GAMA.run(new InScope<IAgent>() {

							@Override
							public IAgent run(final IScope scope) {
								return agent.getPopulationFor(name).getAgent(scope,
									new GamaPoint(pickedPoint.x, -pickedPoint.y));
							}
						});
						renderer.displaySurface.selectAgent(ag);
					} else {
						renderer.displaySurface.selectAgent(agent);
					}
				}
			}
			super.draw(gl, drawer, picking);
			gl.glPopMatrix();
		} else {
			super.draw(gl, drawer, picking);
		}
	}

	/**
	 * Method computeTexture()
	 * @see ummisco.gama.opengl.scene.AbstractObject#computeTexture(ummisco.gama.opengl.utils.JOGLAWTGLRenderer)
	 */
	@Override
	protected Texture computeTexture(final GL gl, final JOGLRenderer renderer) {
		if ( image == null ) { return null; }
		return renderer.getCurrentScene().getTexture(gl, image);
	}

}
