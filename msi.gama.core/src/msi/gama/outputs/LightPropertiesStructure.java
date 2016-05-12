package msi.gama.outputs;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;

public class LightPropertiesStructure {
	public int id;
	public boolean active;
	public GamaColor color;
	public GamaPoint position;
	public String type;
	public float linearAttenuation;
}
