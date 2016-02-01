/*********************************************************************************************
 *
 *
 * 'DEMObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Point;
import java.awt.image.BufferedImage;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;

public class DEMObject extends AbstractObject {

	final public double[] dem;
	final public BufferedImage textureImage;
	final public BufferedImage demImg;
	// final public IAgent agent;
	final public boolean isTriangulated, isShowText, fromImage, isGrayScaled;
	// The height of the envelope represents the z_factor (between 0 and 1).
	final public Envelope3D envelope;
	final public Envelope3D cellSize;

	// FIXME AD: This class has not been reworked correctly to work with the new API of SceneObjects. Basically, it has been made compatible, but not more.

	public DEMObject(final double[] dem, final BufferedImage demTexture, final BufferedImage demImg, final IAgent agent,
		final Envelope3D env, final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText,
		final boolean fromImage, final Envelope3D cellSize, final String name, final GamaColor lineColor,
		final LayerObject layer) {
		super(new DrawingAttributes(null, null, lineColor), layer);
		attributes.agent = agent;
		attributes.speciesName = name;
		attributes.empty = demTexture == null;
		this.dem = dem;
		this.textureImage = demTexture;
		this.demImg = demImg;
		if ( demImg != null ) {
			ImageUtil.flipImageVertically(this.demImg);
		}
		this.isTriangulated = isTriangulated;
		this.isGrayScaled = isGrayScaled;
		this.isShowText = isShowText;
		this.fromImage = fromImage;
		this.envelope = env;
		this.cellSize = cellSize;
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean picking) {

		if ( picking ) {
			JOGLRenderer renderer = drawer.renderer;
			gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( getAgent() != null ) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					// The picked image is the grid
					if ( attributes.speciesName != null ) {
						final GamaPoint pickedPoint = renderer
							.getIntWorldPointFromWindowPoint(new Point(renderer.camera.getLastMousePressedPosition().x,
								renderer.camera.getLastMousePressedPosition().y));
						IAgent ag = GAMA.run(new InScope<IAgent>() {

							@Override
							public IAgent run(final IScope scope) {
								return getAgent().getPopulationFor(attributes.speciesName).getAgent(scope,
									new GamaPoint(pickedPoint.x, -pickedPoint.y));
							}
						});
						if ( ag != null ) {
							renderer.displaySurface.selectAgent(ag);
						}

					} else {
						renderer.displaySurface.selectAgent(getAgent());
					}
				}
			}
			super.draw(gl, drawer, picking);
			// renderer.gl.glPopMatrix();
		} else {
			super.draw(gl, drawer, picking);
		}
	}

	@Override
	public Texture getTexture(final GL gl, final JOGLRenderer renderer, final int order) {
		if ( textureImage == null ) { return null; }
		return renderer.getCurrentScene().getTexture(gl, textureImage);
	}

}
