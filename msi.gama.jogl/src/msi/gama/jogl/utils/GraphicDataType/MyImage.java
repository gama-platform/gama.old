package msi.gama.jogl.utils.GraphicDataType;

import java.awt.image.BufferedImage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class MyImage {

	public BufferedImage image;
	public IAgent agent;
	public double x;
	public double y;
	public double z;
	public double alpha;
	public double width;
	public double height;
	public Integer angle = 0;
	public GamaPoint offSet = new GamaPoint(0, 0, 0);
	public GamaPoint scale = new GamaPoint(1, 1, 1);

	public MyImage(BufferedImage image, IAgent agent, double x, double y, double z, double alpha, double width,
		double height, Integer angle, GamaPoint offSet, GamaPoint scale) {
		super();
		this.image = image;
		this.agent = agent;
		this.x = x;
		this.y = y;
		this.z = z;
		this.alpha = alpha;
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.offSet = offSet;
		if ( scale != null ) {
			this.scale = scale;
		}
	}

}
