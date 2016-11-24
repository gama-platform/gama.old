/*********************************************************************************************
 *
 * 'LightPropertiesStructure.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs;

import javax.vecmath.Vector3f;

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
	
	public Vector3f getColor() {
		return new Vector3f((float)(color.red()/255.0),(float)(color.green()/255.0),(float)(color.blue()/255.0));
	}
	
	public Vector3f getPosition() {
		return new Vector3f((float)position.x,(float)position.y,(float)position.z);
	}
	
	public Vector3f getDirection() {
		return new Vector3f((float)direction.x,(float)direction.y,(float)direction.z);
	}
	
	public int getId() {
		return id;
	}

	public boolean isActive() {
		return active;
	}

	public TYPE getType() {
		return type;
	}

	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	public float getQuadraticAttenuation() {
		return quadraticAttenuation;
	}

	public boolean isDrawLight() {
		return drawLight;
	}

	public float getSpotAngle() {
		return spotAngle;
	}

	public static enum TYPE { DIRECTION, POINT, SPOT };
}
