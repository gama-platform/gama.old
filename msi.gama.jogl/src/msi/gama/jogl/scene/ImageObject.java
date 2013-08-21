package msi.gama.jogl.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class ImageObject extends AbstractObject {

    public BufferedImage image;
	public IAgent agent;
	public int layerId;
	public double x;
	public double y;
	public double z;
	public double width;
	public double height;
	public Integer angle = 0;
	public boolean isDynamic;
	public MyTexture texture;

	

	public ImageObject(BufferedImage image, IAgent agent, double z_layer, int layerId, double x, double y, double z, Double alpha, double width,
		double height, Integer angle, GamaPoint offset, GamaPoint scale, boolean isDynamic, MyTexture texture) {
		super(null, offset, scale, alpha);
    	setZ_fighting_id((double) (layerId));
		if((agent !=null && (agent.getLocation().getZ() == 0 ) && (height == 0 ))){
			System.out.println("image" + layerId);
	    	setZ_fighting_id((double) (layerId *1000000 + agent.getIndex()));
	    }
		this.image = image;
		this.agent = agent;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.isDynamic = isDynamic;
		this.texture = texture;
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
		if ( picked ) {
			return pickedColor;
		}
		return super.getColor();
	}
	
	@Override
	public void draw(final ObjectDrawer drawer, final boolean picking) {
		if ( picking ) {
			JOGLAWTGLRenderer renderer = drawer.renderer;
			renderer.gl.glPushMatrix();
			renderer.gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( agent != null /* && !picked */) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					renderer.displaySurface.selectAgents(0, 0, agent, layerId - 1);
				}
			}
			super.draw(drawer, picking);
			renderer.gl.glPopMatrix();
		} else {
			super.draw(drawer, picking);
		}
	}

}
