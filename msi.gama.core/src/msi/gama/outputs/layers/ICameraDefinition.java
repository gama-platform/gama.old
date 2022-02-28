/*******************************************************************************************************
 *
 * ICameraDefinition.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.util.List;

import msi.gama.common.interfaces.INamed;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;

/**
 * The Interface ICameraDefinition. Defines the minimal set of information needed for cameras. All other attributes
 * (like follow etc.) should contribute to building these information.
 */
public interface ICameraDefinition extends INamed {

	/** The from top. */
	@constant (
			value = "from_above",
			category = IOperatorCategory.THREED,
			doc = @doc ("Represent the position of the camera, above the scene")) String from_above = "From above";

	/** The from left. */
	@constant (
			value = "from_left",
			category = IOperatorCategory.THREED,
			doc = @doc ("Represent the position of the camera, on the left of the scene")) String from_left =
					"From left";

	/** The from right. */
	@constant (
			value = "from_right",
			category = IOperatorCategory.THREED,
			doc = @doc ("Represent the position of the camera, on the right of the scene")) String from_right =
					"From right";

	/** The from up left. */
	@constant (
			value = "from_up_left",
			category = IOperatorCategory.THREED,
			doc = @doc ("Represent the position of the camera, on the left, slightly above the scene")) String from_up_left =
					"From up left";

	/** The from up right. */
	@constant (
			value = "from_up_right",
			category = IOperatorCategory.THREED,
			doc = @doc ("Represent the position of the camera on the right, slightly above the scene")) String from_up_right =
					"From up right";

	/** The from front. */
	@constant (
			value = "from_front",
			category = IOperatorCategory.THREED,
			doc = @doc ("Represent the position of the camera, in front of the scene")) String from_front =
					"From front";

	/** The from up front. */
	@constant (
			value = "from_up_front",
			category = IOperatorCategory.THREED,
			doc = @doc ("Represent the position of the camera, in front and slightly above the scene")) String from_up_front =
					"From up front";

	/** The presets. */
	String[] PRESETS =
			List.of(from_above, from_left, from_right, from_front, from_up_left, from_up_right, from_up_front)
					.toArray(new String[7]);

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	GamaPoint getLocation();

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	GamaPoint getTarget();

	/**
	 * Gets the lens.
	 *
	 * @return the lens
	 */
	Integer getLens();

	/**
	 * Checks if is interacting.
	 *
	 * @return the boolean
	 */
	Boolean isInteractive();

	/**
	 * Sets the interactive.
	 *
	 * @param b
	 *            the new interactive
	 */
	void setInteractive(Boolean b);

	/**
	 * Sets the location.
	 *
	 * @param point
	 *            the point
	 * @return true, if successful
	 */
	boolean setLocation(GamaPoint point);

	/**
	 * Sets the target.
	 *
	 * @param point
	 *            the point
	 * @return true, if successful
	 */
	boolean setTarget(GamaPoint point);

	/**
	 * Sets the lens.
	 *
	 * @param cameraLens
	 *            the new lens
	 */
	void setLens(Integer cameraLens);

	/**
	 * Sets the distance.
	 *
	 * @param distance
	 *            the distance
	 * @return true, if successful
	 */
	boolean setDistance(Double distance);

	/**
	 * Reset.
	 */
	void reset();

	/**
	 * Refresh. Does nothing by default
	 *
	 * @param scope
	 *            the scope
	 */
	default void refresh(final IScope scope) {}

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	Double getDistance();

	/**
	 * Computes the location of a camera based on a symbolic position, a target and boundarises .
	 *
	 * @param pos
	 *            the pos
	 * @param target
	 *            the target
	 * @param maxX
	 *            the w
	 * @param maxY
	 *            the h
	 * @param maxZ
	 *            the max
	 * @return the gama point
	 */
	default GamaPoint computeLocation(final String pos, final GamaPoint target, final double maxX, final double maxY,
			final double maxZ) {
		return switch (pos) {
			case from_above -> new GamaPoint(target.x, target.y, maxZ);
			case from_left -> new GamaPoint(target.x - maxX, target.y, 0);
			case from_up_left -> new GamaPoint(target.x - maxX, target.y, maxZ);
			case from_right -> new GamaPoint(target.x + maxX, target.y - maxY / 1000, 0);
			case from_up_right -> new GamaPoint(target.x + maxX, target.y - maxY / 1000, maxZ);
			case from_front -> new GamaPoint(target.x, target.y - maxY, 0);
			case from_up_front -> new GamaPoint(target.x, target.y - maxY, maxZ);
			default -> new GamaPoint(target.x, target.y, maxZ); // FROM_ABOVE
		};
	}

}
