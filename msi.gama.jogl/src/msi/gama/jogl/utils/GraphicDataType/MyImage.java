package msi.gama.jogl.utils.GraphicDataType;

import java.awt.image.BufferedImage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class MyImage {

	public BufferedImage image;
	public IAgent agent;
	public int textureId;
	public boolean isTextured;
	public float x;
	public float y;
	public double z;
	public float alpha;
	public double width;
	public double height;
	// public boolean isDynamic;
	public Integer angle = 0;
	public String name;
	public GamaPoint offSet = new GamaPoint(0, 0, 0);
	// public Texture texture;
}
