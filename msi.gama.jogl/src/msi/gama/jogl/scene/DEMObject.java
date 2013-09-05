package msi.gama.jogl.scene;

import java.awt.Color;
import java.awt.Point;

import java.awt.image.BufferedImage;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.matrix.GamaFloatMatrix;

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
	public int cellSize;
	public Double z_factor;
	public MyTexture texture;
	public String name;
	public int layerId;
	
	
	
	public DEMObject(double[] dem, BufferedImage demTexture,BufferedImage demImg,final IAgent agent,Envelope env, boolean isTextured, boolean isTriangulated,boolean isShowText,  
			boolean fromImage, Double z_factor, Color c, GamaPoint o, GamaPoint s, Double a, int cellSize,final MyTexture texture, final String name, final int layerId) {
		super(c, o, s, a);
		this.dem = dem;
		this.demTexture = demTexture;
		this.demImg = demImg;
		this.agent = agent;
		this.isTextured = isTextured;
		this.isTriangulated = isTriangulated;
		this.isShowText = isShowText;
		this.fromImage = fromImage;
		this.envelope=env;
		this.z_factor = z_factor;
		this.cellSize = cellSize;
		this.texture= texture;
		this.name = name;
		this.layerId= layerId;
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
						Point pickedPoint =
							renderer.getIntWorldPointFromWindowPoint(new Point(renderer.camera.getLastxPressed(),
								renderer.camera.getLastyPressed()));
						IAgent ag =
							agent.getPopulationFor(this.name).getAgent(new GamaPoint(pickedPoint.x, -pickedPoint.y));
						if(ag!=null){
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
