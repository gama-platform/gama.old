/*******************************************************************************************************
 *
 * GenericCameraDefinition.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.properties;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * The Class GenericCameraDefinition. A simple holder of position, target and lens of the camera. All coordinates are
 * supposed to be stored with the Y ordinate negated (the reverse of the value in GAML)
 */
public class GenericCameraDefinition implements ICameraDefinition {

	/** The current target and location. */
	final GamaPoint currentLocation, currentTarget;

	/** The initial target and location. */
	final GamaPoint initialLocation, initialTarget;

	/** The lens. */
	Integer lens = 45;

	/** The is interactive. */
	Boolean isInteractive = true;

	/** The name. */
	final String name;

	/**
	 * Instantiates a new generic camera definition.
	 *
	 * @param loc
	 *            the loc
	 * @param target
	 *            the target
	 */
	public GenericCameraDefinition(final String name, final GamaPoint loc, final GamaPoint target) {
		initialLocation = new GamaPoint(loc);
		currentLocation = new GamaPoint(loc);
		initialTarget = new GamaPoint(target);
		currentTarget = new GamaPoint(target);
		this.name = name;
	}

	/**
	 * Instantiates a new generic camera definition.
	 *
	 * @param name
	 *            the name.
	 * @param target
	 *            the target
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param max
	 *            the max
	 */
	public GenericCameraDefinition(final String name, final GamaPoint target, final double w, final double h,
			final double max) {
		GamaPoint loc = computeLocation(name, target, w, h, max);
		initialLocation = new GamaPoint(loc);
		currentLocation = new GamaPoint(loc);
		initialTarget = new GamaPoint(target);
		currentTarget = new GamaPoint(target);
		this.name = name;
	}

	@Override
	public GamaPoint getLocation() { return currentLocation; }

	@Override
	public GamaPoint getTarget() { return currentTarget; }

	@Override
	public Integer getLens() { return lens; }

	@Override
	public Boolean isInteractive() { return isInteractive; }

	@Override
	public void setInteractive(final Boolean b) {
		isInteractive = b;

	}

	@Override
	public boolean setLocation(final GamaPoint point) {
		if (currentLocation.equals(point)) return false;
		currentLocation.setLocation(point);
		return true;
	}

	@Override
	public boolean setTarget(final GamaPoint point) {
		if (currentTarget.equals(point)) return false;
		currentTarget.setLocation(point);
		return true;
	}

	@Override
	public void setLens(final Integer cameraLens) { lens = cameraLens; }

	@Override
	public void reset() {
		currentLocation.setLocation(initialLocation);
		currentTarget.setLocation(initialTarget);
	}

	@Override
	public boolean setDistance(final Double distance) {
		if (distance.equals(currentLocation.distance3D(currentTarget))) return false;
		GamaPoint vector = currentLocation.minus(currentTarget).normalized().times(distance);
		currentLocation.setLocation(currentTarget.plus(vector));
		return true;
	}

	@Override
	public Double getDistance() { return currentLocation.distance3D(currentTarget); }

	@Override
	public String getName() { return name; }

}
