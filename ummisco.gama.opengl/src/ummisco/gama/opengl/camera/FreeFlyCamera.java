/*******************************************************************************************************
 *
 * FreeFlyCamera.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.camera;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

/**
 * The Class FreeFlyCamera.
 */
public class FreeFlyCamera extends AbstractCamera {

	/** The Constant up. */
	private static final GamaPoint up = new GamaPoint(0.0f, 0.0f, 1.0f);

	/** The forward. */
	private final GamaPoint forward = new GamaPoint(0, 0, 0);

	/** The left. */
	private final GamaPoint left = new GamaPoint(0, 0, 0);

	/** The speed. */
	private final double speed = getRenderer().getMaxEnvDim() * 0.0001;

	/**
	 * Instantiates a new free fly camera.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public FreeFlyCamera(final IOpenGLRenderer renderer) {
		super(renderer);
		initialize();
	}

	@Override
	public void updateCartesianCoordinatesFromAngles() {
		if (phi > 89) {
			this.phi = 89;
		} else if (phi < -89) { this.phi = -89; }
		final double factorP = phi * Maths.toRad;
		final double factorT = theta * Maths.toRad;
		final double r_temp = Math.cos(factorP);
		forward.setLocation(r_temp * Math.cos(factorT), r_temp * Math.sin(factorT), Math.sin(factorP));
		left.setLocation(new GamaPoint(up.y * forward.z - up.z * forward.y, up.z * forward.x - up.x * forward.z,
				up.x * forward.y - up.y * forward.x).normalized());
		setTarget(forward.plus(position));
	}

	@Override
	public double getDistance() { return position.minus(target).norm(); }

	@Override
	public void animate() {
		super.animate();
		if (isForward()) {
			if (shiftPressed) {
				this.phi = phi - -getKeyboardSensivity() * getSensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				setPosition(position.plus(forward.times(speed * 200))); // go
																		// forward
			}
		}
		if (isBackward()) {
			if (shiftPressed) {
				this.phi = phi - getKeyboardSensivity() * getSensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				setPosition(position.minus(forward.times(speed * 200))); // go
																			// backward
			}
		}
		if (isStrafeLeft()) {
			if (shiftPressed) {
				this.theta = theta - -getKeyboardSensivity() * getSensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				setPosition(position.plus(left.times(speed * 200))); // move
																		// on
																		// the
																		// right
			}
		}
		if (isStrafeRight()) {
			if (shiftPressed) {
				this.theta = theta - getKeyboardSensivity() * getSensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				setPosition(position.minus(left.times(speed * 200))); // move
																		// on
																		// the
																		// left
			}
		}

		setTarget(position.plus(forward));
	}

	@Override
	public void setUpVector(final double xPos, final double yPos, final double zPos) {
		// Not allowed for this camera
	}

	@Override
	public void initialize() {
		upVector.setLocation(up);
		final LayeredDisplayData data = getRenderer().getData();
		final double envWidth = data.getEnvWidth();
		final double envHeight = data.getEnvHeight();
		setPosition(envWidth / 2, -envHeight * 1.75, getRenderer().getMaxEnvDim());
		setTarget(envWidth / 2, -envHeight * 0.5, 0);
		this.phi = -45;
		this.theta = 90;
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public Double zoomLevel() {
		return getRenderer().getMaxEnvDim() * getInitialZFactor() / position.getZ();
	}

	@Override
	public void zoom(final double level) {
		setPosition(position.x, position.y, getRenderer().getMaxEnvDim() * getInitialZFactor() / level);
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void zoom(final boolean in) {
		final float step = Math.abs(getPosition().getZ() != 0 ? (float) position.getZ() / 10 : 0.1f);
		final GamaPoint vector = forward.times(speed * 800 + step);
		setPosition(getPosition().plus(in ? vector : vector.negated()));
		setTarget(forward.plus(getPosition()));
		getRenderer().getData().setZoomLevel(zoomLevel(), true, false);
	}

	@Override
	public void setDistance(final double distance) {
		// ??
	}

	@Override
	public void zoomFocus(final Envelope3D env) {
		final double extent = env.maxExtent();
		final double z;
		if (extent == 0) {
			z = env.getMaxZ() + getRenderer().getMaxEnvDim() / 100d;
		} else {
			z = extent * 1.5;
		}
		setPosition(env.centre().x, env.centre().y, z);
		getRenderer().getData().setZoomLevel(zoomLevel(), true, false);
	}

	@Override
	public void internalMouseMove(final int x, final int y, final int button, final boolean buttonPressed,
			final boolean isCtrl, final boolean isShift) {
		super.internalMouseMove(x, y, button, buttonPressed, isCtrl, isShift);
		if (button == 0) return;
		if (isShift /** || alt(e)) */
				&& isViewInXYPlan()) {
			getMousePosition().x = x;
			getMousePosition().y = y;
			getRenderer().getOpenGLHelper().defineROI(
					new GamaPoint(firstMousePressedPosition.x, firstMousePressedPosition.y),
					new GamaPoint(getMousePosition().x, getMousePosition().y));
		} else {
			final int horizMovement = x - (int) getLastMousePressedPosition().x;
			final int vertMovement = y - (int) getLastMousePressedPosition().y;
			lastMousePressedPosition.setLocation(x, y, 0);
			this.theta = theta - horizMovement * getSensivity();
			this.phi = phi - vertMovement * getSensivity();
			updateCartesianCoordinatesFromAngles();
		}
	}

	/**
	 * Can select on release.
	 *
	 * @param isShift
	 *            the is shift
	 * @return true, if successful
	 */
	@Override
	protected boolean canSelectOnRelease(final boolean isShift) {
		return isShift /* || alt(arg0) */;
	}

	@Override
	public boolean isViewInXYPlan() {
		return phi >= -89 && phi < -85;

	}

	@Override
	protected void drawRotationHelper() {}

}
