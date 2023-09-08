/*******************************************************************************************************
 *
 * CameraDefinition.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
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
import msi.gama.runtime.GraphicsScope;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

/**
 * The Class CameraDefinition. Holds and updates the position, target and lens of a camera from the GAML definition in
 * the "camera" statement.
 */
public class CameraDefinition extends AbstractDefinition implements ICameraDefinition {

	static {
		// DEBUG.ON();
	}

	/** The current. */
	GenericCameraDefinition current;

	/** The location. */
	Attribute<Object> locationAttribute;

	/** The initial location attribute. */
	final Attribute<Object> initialLocationAttribute;

	/** The target. */
	Attribute<GamaPoint> targetAttribute;

	/** The initial target attribute. */
	final Attribute<GamaPoint> initialTargetAttribute;

	/** The distance. */
	Attribute<Double> distanceAttribute;

	/** The initial distance. */
	final Attribute<Double> initialDistanceAttribute;

	/** The lens. */
	Attribute<Integer> lens;

	/** The interaction. */
	Attribute<Boolean> locked;

	/**
	 * Instantiates a new camera definition.
	 *
	 * @param symbol
	 *            the symbol
	 */
	@SuppressWarnings ("unchecked")
	public CameraDefinition(final CameraStatement symbol) {
		super(symbol);
		initialLocationAttribute = locationAttribute = create(IKeyword.LOCATION, Types.NO_TYPE, null);
		initialTargetAttribute = targetAttribute = create(IKeyword.TARGET, Types.POINT, null);
		initialDistanceAttribute = distanceAttribute = create("distance", Types.FLOAT, null);
		lens = create("lens", Types.INT, 45);
		locked = create("locked", Types.BOOL, false);
	}

	@Override
	public void reset() {
		locationAttribute = initialLocationAttribute;
		targetAttribute = initialTargetAttribute;
		distanceAttribute = initialDistanceAttribute;
		current.reset();
	}

	@Override
	public void update(final IScope scope) {

		// First we determine the target.
		GamaPoint target = targetAttribute.get();
		if (target == null) { target = scope.getSimulation().getCentroid(); }
		// Then we determine the location
		Object temp = locationAttribute.get();
		GamaPoint location;
		boolean noLocation = temp == null;
		if (noLocation) { temp = GamaPreferences.Displays.OPENGL_DEFAULT_CAM.getValue(); }
		// We negate the Y ordinate coming from GAML
		target = target.yNegated();
		if (temp instanceof String pos) {
			// If it is a symbolic position
			double coeff = 1.4;
			if ((scope instanceof GraphicsScope gs) && (gs.getGraphics() != null)) {
				coeff = gs.getGraphics().getSurface().getData().getCameraDistanceCoefficient();
			}
			double w = scope.getSimulation().getWidth();
			double h = scope.getSimulation().getHeight();
			double max = Math.max(w, h) * coeff;
			location = computeLocation(pos, target, w, h, max);
		} else {
			location = Cast.asPoint(scope, temp);
			// The location should be a point now and we negate it as well
			location = location.yNegated();
		}

		// We determine the distance and apply it to the location if it is explicitly defined or if no location has been
		// defined
		Double d = distanceAttribute.get();
		if (d != null) {
			GamaPoint vector = location.minus(target).normalized().times(d);
			location = target.plus(vector);
		}

		if (current == null) {
			current = new GenericCameraDefinition(getName(), location, target);
		} else {
			current.setLocation(location);
			current.setTarget(target);
		}
		if (d != null) { current.setDistance(d); }
	}

	/**
	 * Checks if is dynamic.
	 *
	 * @return the boolean
	 */

	@Override
	public GamaPoint getLocation() { return current.getLocation(); }

	/**
	 * Sets the location.Comes from the OpenGL world, where the Y axis is reversed, so we store it as an attribute (to
	 * be evaluated later) where we make sure the Y ordinate is negated
	 *
	 * @param loc
	 *            the loc
	 * @return true, if changed
	 */
	@Override
	public boolean setLocation(final GamaPoint loc) {
		if (!isInteractive() || loc == null) return false;
		locationAttribute = new ConstantAttribute<>(loc.yNegated());
		return current.setLocation(loc);
	}

	/**
	 * Sets the target. Comes from the OpenGL world, where the Y axis is reversed, so we store it as an attribute (to be
	 * evaluated later) where we make sure the Y ordinate is negated
	 *
	 * @param loc
	 *            the loc
	 * @return true, if successful
	 */
	@Override
	public boolean setTarget(final GamaPoint loc) {
		if (!isInteractive() || loc == null) return false;
		targetAttribute = new ConstantAttribute<>(loc.yNegated());
		return current.setTarget(loc);
	}

	/**
	 * Sets the lens.
	 *
	 * @param lens
	 *            the new lens
	 */
	@Override
	public void setLens(final Integer lens) { this.lens = new ConstantAttribute<>(lens == null ? 45 : lens); }

	@Override
	public GamaPoint getTarget() { return current.getTarget(); }

	@Override
	public Integer getLens() { return lens.get(); }

	@Override
	public Boolean isInteractive() { return !locked.get() && !isDynamic(); }

	/**
	 * Sets the interactive.
	 *
	 * @param b
	 *            the new interactive
	 */
	@Override
	public void setInteractive(final Boolean b) { this.locked = new ConstantAttribute<>(b == null ? false : !b); }

	@Override
	public boolean setDistance(final Double d) {
		if (!isInteractive() || d == null) return false;
		distanceAttribute = new ConstantAttribute<>(d);
		return current.setDistance(d);
	}

	@Override
	public Double getDistance() { return current.getDistance(); }

	@Override
	public String getName() { return symbol.getName(); }

	@Override
	protected boolean getDefaultDynamicValue() { return false; }

	@Override
	protected boolean shouldRefresh() {
		return super.shouldRefresh() || current == null;
	}
}
