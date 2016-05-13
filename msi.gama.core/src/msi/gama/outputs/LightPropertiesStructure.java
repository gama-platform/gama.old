package msi.gama.outputs;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;

public class LightPropertiesStructure {
	public int id;
	public boolean active = false;
	public GamaColor color = new GamaColor(255,255,255,255);
	public GamaColor specularColor = null;
	public GamaPoint position;
	public TYPE type = TYPE.SPOT;
	public float linearAttenuation = 0;
	public float quadraticAttenuation = 0;
	public boolean drawLight = true;
	public GamaPoint spotDirection = new GamaPoint(0,0,-1);
	public float spotAngle = 45.0f;
	
	public static enum TYPE { DIRECTION, POINT, SPOT };
}
