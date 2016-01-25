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

import java.awt.*;
import java.awt.image.BufferedImage;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import ummisco.gama.opengl.JOGLRenderer;

public class DEMObject extends AbstractObject {

	final public double[] dem;
	final public BufferedImage textureImage;
	final public BufferedImage demImg;
	final public IAgent agent;
	final public boolean isTextured, isTriangulated, isShowText, fromImage, isDynamic, isGrayScaled;
	// The height of the envelope represents the z_factor (between 0 and 1).
	final public Envelope3D envelope;
	final public Envelope3D cellSize;
	final public String name;
	final public Color lineColor;

	// final public int layerId;

	public DEMObject(final double[] dem, final BufferedImage demTexture, final BufferedImage demImg, final IAgent agent,
		final Envelope3D env, final boolean isTextured, final boolean isTriangulated, final boolean isGrayScaled,
		final boolean isShowText, final boolean fromImage, final boolean isDynamic, final Color c, final Double a,
		final Envelope3D cellSize, final String name, final Color lineColor) {
		super(c, a);
		this.dem = dem;
		this.textureImage = demTexture;
		if ( demImg != null ) {
			this.demImg = FlipRightSideLeftImage(FlipUpSideDownImage(demImg));
		} else {
			this.demImg = null;
		}
		this.agent = agent;
		this.isTextured = isTextured;
		this.isTriangulated = isTriangulated;
		this.isGrayScaled = isGrayScaled;
		this.isShowText = isShowText;
		this.fromImage = fromImage;
		this.isDynamic = isDynamic;
		this.envelope = env;
		this.cellSize = cellSize;
		this.name = name;
		this.lineColor = lineColor;
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

		if ( picking ) {
			JOGLRenderer renderer = drawer.renderer;
			// renderer.gl.glPushMatrix();
			// GL2 gl = GLContext.getCurrentGL().getGL2();
			gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( agent != null ) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					// The picked image is the grid
					if ( this.name != null ) {
						final GamaPoint pickedPoint = renderer
							.getIntWorldPointFromWindowPoint(new Point(renderer.camera.getLastMousePressedPosition().x,
								renderer.camera.getLastMousePressedPosition().y));
						IAgent ag = GAMA.run(new InScope<IAgent>() {

							@Override
							public IAgent run(final IScope scope) {
								return agent.getPopulationFor(name).getAgent(scope,
									new GamaPoint(pickedPoint.x, -pickedPoint.y));
							}
						});
						if ( ag != null ) {
							renderer.displaySurface.selectAgent(ag);
						}

					} else {
						renderer.displaySurface.selectAgent(agent);
					}
				}
			}
			super.draw(gl, drawer, picking);
			// renderer.gl.glPopMatrix();
		} else {
			super.draw(gl, drawer, picking);
		}
	}

	private BufferedImage FlipUpSideDownImage(final BufferedImage img) {
		ImageUtil.flipImageVertically(img);
		// java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(1, -1);
		// tx.translate(0, -img.getHeight(null));
		// AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		// img = op.filter(img, null);
		return img;

	}

	private BufferedImage FlipRightSideLeftImage(final BufferedImage img) {
		// java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(-1, 1);
		// tx.translate(-img.getWidth(null), 0);
		// AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		// img = op.filter(img, null);
		return img;

	}

	/**
	 * Method computeTexture()
	 * @see ummisco.gama.opengl.scene.AbstractObject#computeTexture()
	 */
	@Override
	protected Texture computeTexture(final GL gl, final JOGLRenderer renderer) {
		if ( textureImage == null ) { return null; }
		return renderer.getCurrentScene().getTexture(gl, textureImage);
	}
}
