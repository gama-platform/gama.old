package msi.gama.jogl.scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import com.vividsolutions.jts.geom.Envelope;

public class DEMObject extends AbstractObject {

	public double[] dem;
	public BufferedImage demTexture;
	public BufferedImage demImg;
	public IAgent agent;
	public boolean isTextured;
	public boolean isTriangulated;
	public boolean isShowText;
	public boolean fromImage;
	public Envelope envelope;
	public double cellSize;
	public Double z_factor;
	public MyTexture texture;
	public String name;
	public int layerId;

	public DEMObject(final double[] dem, final BufferedImage demTexture, final BufferedImage demImg,
		final IAgent agent, final Envelope env, final boolean isTextured, final boolean isTriangulated,
		final boolean isShowText, final boolean fromImage, final Double z_factor, final Color c, final GamaPoint o,
		final GamaPoint s, final Double a, final double cellSize, final MyTexture texture, final String name,
		final int layerId) {
		super(c, o, s, a);
		this.dem = dem;
		this.demTexture = demTexture;
		this.demImg = demImg;
		this.agent = agent;
		this.isTextured = isTextured;
		this.isTriangulated = isTriangulated;
		this.isShowText = isShowText;
		this.fromImage = fromImage;
		this.envelope = env;
		this.z_factor = z_factor;
		this.cellSize = cellSize;
		this.texture = texture;
		this.name = name;
		this.layerId = layerId;
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
	public void draw(final ObjectDrawer drawer, final boolean picking) {
		if ( picking ) {
			JOGLAWTGLRenderer renderer = drawer.renderer;
			renderer.gl.glPushMatrix();
			renderer.gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( agent != null ) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					// The picked image is the grid
					if ( this.name != null ) {
						final GamaPoint pickedPoint =
							renderer.getIntWorldPointFromWindowPoint(new Point(renderer.camera
								.getLastMousePressedPosition().x, renderer.camera.getLastMousePressedPosition().y));
						IAgent ag = GAMA.run(new InScope<IAgent>() {

							@Override
							public IAgent run(final IScope scope) {
								return agent.getPopulationFor(name).getAgent(scope,
									new GamaPoint(pickedPoint.x, -pickedPoint.y));
							}
						});
						if ( ag != null ) {
							renderer.displaySurface.selectAgents(ag, layerId - 1);
						}

					} else {
						renderer.displaySurface.selectAgents(agent, layerId - 1);
					}
				}
			}
			super.draw(drawer, picking);
			renderer.gl.glPopMatrix();
		} else {
			super.draw(drawer, picking);
		}
	}
}
