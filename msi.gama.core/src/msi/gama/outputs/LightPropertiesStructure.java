/*******************************************************************************************************
 *
 * LightPropertiesStructure.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;

/**
 * The Class LightPropertiesStructure.
 */
public class LightPropertiesStructure {
	
	/** The id. */
	public int id;
	
	/** The active. */
	public boolean active = false;
	
	/** The color. */
	public GamaColor color = new GamaColor(127, 127, 127, 255);
	
	/** The position. */
	public GamaPoint position = new GamaPoint(0, 0, 20);
	
	/** The type. */
	public TYPE type = TYPE.DIRECTION;
	
	/** The linear attenuation. */
	public float linearAttenuation = 0;
	
	/** The quadratic attenuation. */
	public float quadraticAttenuation = 0;
	
	/** The draw light. */
	public boolean drawLight = false;
	
	/** The direction. */
	public GamaPoint direction = new GamaPoint(0.5, 0.5, -1);
	
	/** The spot angle. */
	public float spotAngle = 45.0f;

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public GamaPoint getColor() {
		return new GamaPoint(color.red() / 255.0, color.green() / 255.0, color.blue() / 255.0);
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public GamaPoint getPosition() {
		return position;
	}

	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public GamaPoint getDirection() {
		return direction;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * Gets the linear attenuation.
	 *
	 * @return the linear attenuation
	 */
	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	/**
	 * Gets the quadratic attenuation.
	 *
	 * @return the quadratic attenuation
	 */
	public float getQuadraticAttenuation() {
		return quadraticAttenuation;
	}

	/**
	 * Checks if is draw light.
	 *
	 * @return true, if is draw light
	 */
	public boolean isDrawLight() {
		return drawLight;
	}

	/**
	 * Gets the spot angle.
	 *
	 * @return the spot angle
	 */
	public float getSpotAngle() {
		return spotAngle;
	}

	/**
	 * The Enum TYPE.
	 */
	public enum TYPE {
		
		/** The direction. */
		DIRECTION, 
 /** The point. */
 POINT, 
 /** The spot. */
 SPOT
	}
}
