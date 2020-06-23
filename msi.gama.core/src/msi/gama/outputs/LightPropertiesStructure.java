/*******************************************************************************************************
 *
 * msi.gama.outputs.LightPropertiesStructure.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;

public class LightPropertiesStructure {
	public int id;
	public boolean active = false;
	public GamaColor color = new GamaColor(127, 127, 127, 255);
	public GamaPoint position = new GamaPoint(0, 0, 20);
	public TYPE type = TYPE.DIRECTION;
	public float linearAttenuation = 0;
	public float quadraticAttenuation = 0;
	public boolean drawLight = false;
	public GamaPoint direction = new GamaPoint(0.5, 0.5, -1);
	public float spotAngle = 45.0f;

	public GamaPoint getColor() {
		return new GamaPoint(color.red() / 255.0, color.green() / 255.0, color.blue() / 255.0);
	}

	public GamaPoint getPosition() {
		return position;
	}

	public GamaPoint getDirection() {
		return direction;
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

	public enum TYPE {
		DIRECTION, POINT, SPOT
	}
}
