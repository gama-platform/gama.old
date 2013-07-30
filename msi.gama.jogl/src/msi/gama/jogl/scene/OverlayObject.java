package msi.gama.jogl.scene;

import java.awt.image.BufferedImage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class OverlayObject extends AbstractObject {

	public BufferedImage image;
	public IAgent agent;
	public double x;
	public double y;
	public double z;
	public double width;
	public double height;
	public Integer angle = 0;
	public boolean isDynamic;
	public MyTexture texture;

	public OverlayObject(BufferedImage image, IAgent agent, double x, double y, double z, Double alpha, double width,
		double height, Integer angle, GamaPoint offset, GamaPoint scale, boolean isDynamic, MyTexture texture) {
		super(null, offset, scale, alpha);
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
	}

}
