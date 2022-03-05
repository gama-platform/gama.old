/*******************************************************************************************************
 *
 * RotationDefinition.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import static msi.gama.common.interfaces.IKeyword.CENTER;

import msi.gama.common.geometry.Rotation3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gaml.statements.draw.AttributeHolder;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class CameraDefinition. Holds and updates the position, target and lens of a camera from the GAML definition in
 * the "camera" statement.
 */
public class RotationDefinition extends AttributeHolder {

	static {
		DEBUG.ON();
	}

	/** The location. */
	final Attribute<GamaPoint> centerAttribute;

	/** The target. */
	final Attribute<GamaPoint> axisAttribute;

	/** The angle. Can be changed from outside to another value */
	Attribute<Double> angleAttribute;

	/** The initial angle. */
	final Attribute<Double> initialAngleAttribute;

	/** The dynamic. */
	Attribute<Boolean> dynamic;

	/**
	 * Instantiates a new camera definition.
	 *
	 * @param symbol
	 *            the symbol
	 */
	@SuppressWarnings ("unchecked")
	public RotationDefinition(final RotationStatement symbol) {
		super(symbol);
		centerAttribute = create(CENTER,
				symbol.hasFacet(CENTER) ? symbol.getFacet(CENTER) : scope -> scope.getSimulation().getCentroid(),
				Types.POINT, null, null);
		axisAttribute = create("axis", Types.POINT, Rotation3D.PLUS_K);
		angleAttribute = initialAngleAttribute = create("angle", Types.FLOAT, 0d);
		dynamic = create("dynamic", Types.BOOL, false);
	}

	@Override
	public void refresh(final IScope scope) {
		super.refresh(scope);
		if (dynamic.get()) {
			angleAttribute = new ConstantAttribute<>(angleAttribute.get() + initialAngleAttribute.get());
		}
	}

	/**
	 * Gets the angle.
	 *
	 * @return the angle
	 */
	public Double getAngleDelta() { return initialAngleAttribute.get(); }

	/**
	 * Gets the current angle.
	 *
	 * @return the current angle
	 */
	public Double getCurrentAngle() { return angleAttribute.get(); }

	/**
	 * Checks if is dynamic.
	 *
	 * @return the boolean
	 */
	public Boolean isDynamic() { return dynamic.get(); }

	/**
	 * Gets the center.
	 *
	 * @return the center
	 */
	public GamaPoint getCenter() { return centerAttribute.get(); }

	/**
	 * Gets the axis.
	 *
	 * @return the axis
	 */
	public GamaPoint getAxis() { return axisAttribute.get(); }

	/**
	 * Reset.
	 */
	public void reset() {
		angleAttribute = initialAngleAttribute;

	}

	/**
	 * Sets the angle.
	 *
	 * @param val
	 *            the new angle
	 */
	public void setAngle(final double val) { angleAttribute = new ConstantAttribute<>(val); }

	/**
	 * Sets the dynamic.
	 *
	 * @param r
	 *            the new dynamic
	 */
	public void setDynamic(final boolean r) { dynamic = new ConstantAttribute<>(r); }

}
