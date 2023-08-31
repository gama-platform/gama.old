/*******************************************************************************************************
 *
 * LightDefinition.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.properties;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class CameraDefinition. Holds and updates the position, target and lens of a camera from the GAML definition in
 * the "camera" statement.
 */
public class LightDefinition extends AbstractDefinition implements ILightDefinition {

	static {
		DEBUG.OFF();
	}

	/** The location. */
	final Attribute<GamaPoint> locationAttribute;

	/** The type attribute. */
	final Attribute<String> typeAttribute;

	/** The intensity attribute. */
	final Attribute<GamaColor> intensityAttribute;

	/** The target. */
	final Attribute<GamaPoint> directionAttribute;

	/** The angle attribute. */
	Attribute<Double> angleAttribute;

	/** The initial angle. */
	final Attribute<Double> linearAttribute;

	/** The quadratic attribute. */
	final Attribute<Double> quadraticAttribute;

	/** The active attribute. */
	final Attribute<Boolean> activeAttribute;

	/** The draw attribute. */
	final Attribute<Boolean> drawAttribute;

	/** The id. */
	int id;

	/**
	 * Instantiates a new camera definition.
	 *
	 * @param lightStatement
	 *            the symbol
	 */
	@SuppressWarnings ("unchecked")
	public LightDefinition(final LightStatement lightStatement) {
		super(lightStatement);
		typeAttribute = create(IKeyword.TYPE, Types.STRING, ILightDefinition.direction);
		locationAttribute = create(IKeyword.LOCATION, Types.POINT, DEFAULT_LOCATION);
		directionAttribute = create("direction", Types.POINT, DEFAULT_DIRECTION);
		linearAttribute = create(IKeyword.LINEAR_ATTENUATION, Types.FLOAT, 0d);
		quadraticAttribute = create(IKeyword.QUADRATIC_ATTENUATION, Types.FLOAT, 0d);
		angleAttribute = create("angle", Types.FLOAT, DEFAULT_ANGLE);
		drawAttribute = create("show", Types.BOOL, false);
		activeAttribute = create("active", Types.BOOL, true);
		Integer i = GamaPreferences.Displays.OPENGL_DEFAULT_LIGHT_INTENSITY.getValue();
		intensityAttribute = create("intensity", (scope, exp) -> {
			if (exp.getGamlType() == Types.INT) {
				int v = Cast.asInt(scope, exp.value(scope));
				return GamaColor.get(v, v, v, 255);
			}
			return Cast.asColor(scope, exp.value(scope));
		}, Types.COLOR, GamaColor.get(i, i, i, 255));

	}

	@Override
	public Boolean isActive() { return activeAttribute.get() && ILightDefinition.super.isActive(); }

	@Override
	public Boolean isDrawing() { return drawAttribute.get(); }

	@Override
	public GamaPoint getDirection() { return directionAttribute.get(); }

	@Override
	public String getType() { return typeAttribute.get(); }

	@Override
	public GamaColor getIntensity() { return intensityAttribute.get(); }

	@Override
	public double getAngle() { return angleAttribute.get(); }

	@Override
	public String getName() { return symbol.getName(); }

	@Override
	public int getId() { return IKeyword.DEFAULT.equals(getName()) ? 0 : id; }

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(final int id) { this.id = id; }

	@Override
	public GamaPoint getLocation() { return locationAttribute.get(); }

	@Override
	protected boolean getDefaultDynamicValue() { return true; }

	@Override
	protected boolean shouldRefresh() {
		return isDynamic();
	}

	@Override
	protected void update(final IScope scope) {}

	@Override
	protected void reset() {
		// Nothing to do for the moment
	}

}
