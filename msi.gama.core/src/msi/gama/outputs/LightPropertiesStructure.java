package msi.gama.outputs;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;

public class LightPropertiesStructure {
	public int id;
	public boolean active = false;
	public GamaColor color = new GamaColor(127,127,127,255);
	public GamaPoint position = new GamaPoint(0,0,20);
	public TYPE type = TYPE.DIRECTION;
	public float linearAttenuation = 0;
	public float quadraticAttenuation = 0;
	public boolean drawLight = false;
	public GamaPoint direction = new GamaPoint(0.5,0.5,-1);
	public float spotAngle = 45.0f;
	
	public static enum TYPE { DIRECTION, POINT, SPOT };
}
