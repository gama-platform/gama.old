package msi.gama.jogl.utils.GraphicDataType;


import java.awt.image.BufferedImage;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;





public class MyImage {
	
	public BufferedImage image;
	public IAgent agent;
	public int textureId;
	public float x;
	public float y;
	public float z;
	public float alpha;
	public float width;
	public float height;
	public Integer angle = 0;
	public String name;
	public GamaPoint offSet = new GamaPoint(0,0,0);
}
