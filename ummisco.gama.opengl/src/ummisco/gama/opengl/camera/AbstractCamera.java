/*********************************************************************************************
 *
 * 'AbstractCamera.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.camera;

import java.awt.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import com.jogamp.opengl.glu.GLU;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.ui.bindings.GamaKeyBindings;

public abstract class AbstractCamera implements ICamera {

	static {
		PRESETS.put("Choose...", (c) -> {});
		PRESETS.put("From top", (c) -> {
			c.setPosition(c.getTarget().x, c.getTarget().y, c.getRenderer().getMaxEnvDim() * INIT_Z_FACTOR);
			c.setUpVector(0, 1, 0);
		});
		PRESETS.put("From left", (c) -> {
			c.setPosition(c.getTarget().x - c.getRenderer().getEnvWidth() * INIT_Z_FACTOR, c.getTarget().y, 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up left", (c) -> {
			c.setPosition(c.getTarget().x - c.getRenderer().getEnvWidth() * INIT_Z_FACTOR, c.getTarget().y,
					c.getRenderer().getMaxEnvDim() * INIT_Z_FACTOR);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From right", (c) -> {
			c.setPosition(c.getTarget().x + c.getRenderer().getEnvWidth() * INIT_Z_FACTOR, c.getTarget().y, 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up right", (c) -> {
			c.setPosition(c.getTarget().x + c.getRenderer().getEnvWidth() * INIT_Z_FACTOR, c.getTarget().y,
					c.getRenderer().getMaxEnvDim() * INIT_Z_FACTOR);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From front", (c) -> {
			c.setPosition(c.getTarget().x, c.getTarget().y - c.getRenderer().getEnvHeight() * INIT_Z_FACTOR, 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up front", (c) -> {
			c.setPosition(c.getTarget().x, c.getTarget().y - c.getRenderer().getEnvHeight() * INIT_Z_FACTOR,
					c.getRenderer().getMaxEnvDim() * INIT_Z_FACTOR);
			c.setUpVector(0, 0, 1);
		});

	}

	private final Abstract3DRenderer renderer;
	private final GLU glu;
	protected static final GamaPoint UNDEFINED = new GamaPoint();
	protected boolean initialized;

	// Mouse
	private Point mousePosition;
	protected Point lastMousePressedPosition = new Point(0, 0);
	protected Point firstMousePressedPosition;
	protected boolean firsttimeMouseDown = true;
	protected boolean cameraInteraction = true;

	protected final GamaPoint position = new GamaPoint(0, 0, 0);
	protected final GamaPoint target = new GamaPoint(0, 0, 0);
	protected final GamaPoint upVector = new GamaPoint(0, 0, 0);
	protected GamaPoint initialPosition, initialTarget, initialUpVector;

	protected double theta;
	protected double phi;
	protected boolean flipped = false;
	// protected double upVectorAngle;

	private final double _keyboardSensivity = 1d;
	private final double _sensivity = 1;

	// Mouse and keyboard state
	private boolean goesForward;
	private boolean goesBackward;
	private boolean strafeLeft;
	private boolean strafeRight;

	private volatile boolean ROICurrentlyDrawn = false;
	private volatile boolean isROISticky = false;

	protected boolean ctrlPressed = false;
	protected boolean shiftPressed = false;

	protected boolean keystoneMode = false;

	public AbstractCamera(final Abstract3DRenderer renderer) {
		this.renderer = renderer;
		setMousePosition(new Point(0, 0));
		setUpVector(0.0, 1.0, 0.0);
		glu = new GLU();
	}

	public void updateSphericalCoordinatesFromLocations() {}

	@Override
	public void toggleStickyROI() {
		isROISticky = !isROISticky;
	}

	@Override
	public boolean isROISticky() {
		return isROISticky;
	}

	@Override
	public void applyPreset(final String name) {
		final CameraPreset p = PRESETS.get(name);
		if (p != null) {
			target.setLocation(renderer.getEnvWidth() / 2, -renderer.getEnvHeight() / 2, 0);
			p.applyTo(this);
			flipped = false;
			initialized = false;
			update();
			getRenderer().data.setZoomLevel(zoomLevel(), true);
			getRenderer().data.setZoomLevel(1d, true);
		}
	}

	@Override
	public void updatePosition() {
		position.setLocation(renderer.data.getCameraPos());
	}

	@Override
	public void updateTarget() {
		target.setLocation(renderer.data.getCameraLookPos());
	}

	@Override
	public void updateOrientation() {
		upVector.setLocation(renderer.data.getCameraUpVector());
	}

	@Override
	public void update() {
		final LayeredDisplayData data = renderer.data;
		cameraInteraction = !data.cameraInteractionDisabled();
		updateSphericalCoordinatesFromLocations();
		if (initialized) {
			if (flipped)
				setUpVector(-(-Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad)),
						-(-Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad)), -Math.sin(phi * Maths.toRad));
			else
				setUpVector(-Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad),
						-Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad), Math.sin(phi * Maths.toRad));
			drawRotationHelper();
		}

		initialized = true;
	}

	protected abstract void drawRotationHelper();

	@Override
	public void setPosition(final double xPos, final double yPos, final double zPos) {
		position.setLocation(xPos, yPos, zPos);
		getRenderer().data.setCameraPos((GamaPoint) position.clone());
	}

	public void setTarget(final double xLPos, final double yLPos, final double zLPos) {
		target.setLocation(xLPos, yLPos, zLPos);
		getRenderer().data.setCameraLookPos((GamaPoint) target.clone());
	}

	@Override
	public void setUpVector(final double xPos, final double yPos, final double zPos) {
		upVector.setLocation(xPos, yPos, zPos);
		getRenderer().data.setCameraUpVector((GamaPoint) upVector.clone(), true);
	}

	/* -------Get commands--------- */

	@Override
	public GamaPoint getPosition() {
		return position;
	}

	@Override
	public GamaPoint getTarget() {
		return target;
	}

	@Override
	public GamaPoint getOrientation() {
		return upVector;
	}

	@Override
	public void animate() {
		glu.gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, upVector.x, upVector.y,
				upVector.z);
	}

	/*------------------ Events controls ---------------------*/

	final void setShiftPressed(final boolean value) {
		shiftPressed = value;
	}

	final void setCtrlPressed(final boolean value) {
		ctrlPressed = value;
	}

	protected void setMouseLeftPressed(final boolean b) {
		// TODO Auto-generated method stub

	}

	/**
	 * Method mouseScrolled()
	 * 
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseScrolled(final MouseEvent e) {
		renderer.getDrawable().invoke(false, drawable -> {
			if (cameraInteraction) {
				internalMouseScrolled(e);
			}
			return false;
		});

	}

	protected void internalMouseScrolled(final MouseEvent e) {
		zoom(e.count > 0);
	}

	/**
	 * Method mouseMove()
	 * 
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseMove(final org.eclipse.swt.events.MouseEvent e) {

		renderer.getDrawable().invoke(false, drawable -> {
			if (cameraInteraction) {
				internalMouseMove(e);
			}
			return false;
		});

	}

	protected void internalMouseMove(final MouseEvent e) {
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;
		setCtrlPressed(GamaKeyBindings.ctrl(e));
		setShiftPressed(GamaKeyBindings.shift(e));
	}

	/**
	 * Method mouseEnter()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseEnter(final org.eclipse.swt.events.MouseEvent e) {}

	/**
	 * Method mouseExit()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseExit(final org.eclipse.swt.events.MouseEvent e) {}

	/**
	 * Method mouseHover()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseHover(final org.eclipse.swt.events.MouseEvent e) {}

	/**
	 * Method mouseDoubleClick()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDoubleClick(final org.eclipse.swt.events.MouseEvent e) {
		// Already taken in charge by the ZoomListener in the view
		if (keystoneMode) {
			final int corner = clickOnKeystone(e);
			if (corner != -1)
				getRenderer().getKeystone().resetCorner(corner);
		}
	}

	/**
	 * Method mouseDown()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDown(final org.eclipse.swt.events.MouseEvent e) {
		renderer.getDrawable().invoke(false, drawable -> {
			if (cameraInteraction) {
				internalMouseDown(e);
			}
			return false;
		});

	}

	protected GamaPoint getNormalizedCoordinates(final int x, final int y) {
		final double xCoordNormalized = x / getRenderer().getWidth();
		double yCoordNormalized = y / getRenderer().getHeight();
		if (!renderer.useShader())
			yCoordNormalized = 1 - yCoordNormalized;
		return new GamaPoint(xCoordNormalized, yCoordNormalized);
	}

	private int clickOnKeystone(final MouseEvent e) {
		// return the number of the corner clicked. Return -1 if no click on
		// keystone.
		// final GamaPoint p = getNormalizedCoordinates(e);
		return renderer.getKeystone().cornerSelected(new GamaPoint(e.x, e.y));
	}

	protected int hoverOnKeystone(final MouseEvent e) {
		// return the number of the corner clicked. Return -1 if no click on
		// keystone. Return 10 if click on the center.
		// final GamaPoint p = getNormalizedCoordinates(e);
		return renderer.getKeystone().cornerHovered(new GamaPoint(e.x, e.y));
	}

	protected void internalMouseDown(final MouseEvent e) {
		if (firsttimeMouseDown) {
			firstMousePressedPosition = new Point(e.x, e.y);
			firsttimeMouseDown = false;
		}
		if (keystoneMode) {
			// final int cornerSelected = clickOnKeystone(e);
			if (getRenderer().getKeystone().getCornerSelected() != -1) {
				getRenderer().getKeystone().setCornerSelected(-1);
				return;
			}
			final int cornerSelected = clickOnKeystone(e);
			if (cornerSelected != -1) {
				getRenderer().getKeystone().setCornerSelected(cornerSelected);
			}
		}

		lastMousePressedPosition = new Point(e.x, e.y);
		// Activate Picking when press and right click
		if (e.button == 3 && !keystoneMode) {
			if (renderer.mouseInROI(lastMousePressedPosition)) {
				renderer.getSurface().selectionIn(renderer.getROIEnvelope());
			} else
				renderer.getPickingState().setPicking(true);
		} else if (e.button == 2) { // mouse wheel
			resetPivot();
		} else {
			if (GamaKeyBindings.shift(e) && isViewInXYPlan()) {
				startROI(e);
			}
			// else {
			// renderer.getPickingState().setPicking(false);
			// }
		}
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;

		setMouseLeftPressed(e.button == 1 ? true : false);
		setCtrlPressed(e.button == 1 ? GamaKeyBindings.ctrl(e) : false);
		setShiftPressed(e.button == 1 ? GamaKeyBindings.shift(e) : false);

	}

	/**
	 * Method mouseUp()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseUp(final org.eclipse.swt.events.MouseEvent e) {

		renderer.getDrawable().invoke(false, drawable -> {
			if (cameraInteraction) {
				internalMouseUp(e);
			}
			return false;
		});

	}

	protected void internalMouseUp(final MouseEvent e) {
		firsttimeMouseDown = true;
		if (canSelectOnRelease(e) && isViewInXYPlan()) {
			if (GamaKeyBindings.shift(e)) {
				finishROISelection();
			}
		}
		if (e.button == 1)
			setMouseLeftPressed(false);

	}

	private void startROI(final org.eclipse.swt.events.MouseEvent e) {
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;
		renderer.defineROI(firstMousePressedPosition, getMousePosition());
		ROICurrentlyDrawn = true;
	}

	void finishROISelection() {
		if (ROICurrentlyDrawn) {
			final Envelope3D env = renderer.getROIEnvelope();
			if (env != null) {
				renderer.getSurface().selectionIn(env);
			}
		}
	}

	protected abstract boolean canSelectOnRelease(org.eclipse.swt.events.MouseEvent arg0);
	//
	// protected void dump() {
	// System.out.println("xPos:" + position.x + " yPos:" + position.y + "
	// zPos:" + position.z);
	// System.out.println("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:"
	// + target.z);
	// System.out.println("upX" + upVector.x + " upY:" + upVector.y + " upZ:" +
	// upVector.z);
	// System.out.println("_phi " + phi + " _theta " + theta);
	// }

	@Override
	public Point getMousePosition() {
		return mousePosition;
	}

	protected void setMousePosition(final Point mousePosition) {
		this.mousePosition = mousePosition;
	}

	public boolean isViewInXYPlan() {
		return true;
		// return phi > 170 || phi < 10;// && theta > -5 && theta < 5;
	}

	@Override
	public Point getLastMousePressedPosition() {
		return lastMousePressedPosition;
	}

	protected double getKeyboardSensivity() {
		return _keyboardSensivity;
	}

	protected double getSensivity() {
		return _sensivity;
	}

	protected boolean isForward() {
		return goesForward;
	}

	protected boolean isBackward() {
		return goesBackward;
	}

	protected boolean isStrafeLeft() {
		return strafeLeft;
	}

	protected boolean isStrafeRight() {
		return strafeRight;
	}

	@Override
	public Abstract3DRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Method keyPressed()
	 * 
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyPressed(final org.eclipse.swt.events.KeyEvent e) {

		renderer.getDrawable().invoke(false, drawable -> {
			if (cameraInteraction && !keystoneMode) {
				switch (e.keyCode) {
					case SWT.ARROW_LEFT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.strafeLeft = true;
						break;
					case SWT.ARROW_RIGHT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.strafeRight = true;
						break;
					case SWT.ARROW_UP:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.goesForward = true;
						break;
					case SWT.ARROW_DOWN:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.goesBackward = true;
						break;
					case SWT.SPACE:
						resetPivot();
						break;
					case SWT.CTRL:
						setCtrlPressed(!firsttimeMouseDown);
						break;
					case SWT.COMMAND:
						setCtrlPressed(!firsttimeMouseDown);
						break;
				}
				switch (e.character) {
					case '+':
						zoom(true);
						break;
					case '-':
						zoom(false);
						break;
					case '4':
						quickLeftTurn();
						break;
					case '6':
						quickRightTurn();
						break;
					case '8':
						quickUpTurn();
						break;
					case '2':
						quickDownTurn();
						break;
					case 'k':
						if (!GamaKeyBindings.ctrl(e))
							activateKeystoneMode();
						break;
					default:
						return true;
				}
			} else if (e.character == 'k') {
				if (!GamaKeyBindings.ctrl(e))
					activateKeystoneMode();
				return true;
			}
			return true;
		});
	}

	protected void resetPivot() {}

	protected void quickLeftTurn() {}

	protected void quickRightTurn() {}

	protected void quickUpTurn() {}

	protected void quickDownTurn() {}

	protected final void activateKeystoneMode() {
		if (!keystoneMode) {
			getRenderer().getSurface().zoomFit();
			getRenderer().getKeystone().startDrawHelper();
		} else {
			getRenderer().getKeystone().stopDrawHelper();
		}
		keystoneMode = !keystoneMode;
	}

	/**
	 * Method keyReleased()
	 * 
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyReleased(final org.eclipse.swt.events.KeyEvent e) {

		renderer.getDrawable().invoke(false, drawable -> {
			if (cameraInteraction && !keystoneMode) {
				switch (e.keyCode) {
					case SWT.ARROW_LEFT: // turns left (scene rotates right)
						strafeLeft = false;
						break;
					case SWT.ARROW_RIGHT: // turns right (scene rotates left)
						strafeRight = false;
						break;
					case SWT.ARROW_UP:
						goesForward = false;
						break;
					case SWT.ARROW_DOWN:
						goesBackward = false;
						break;
					case SWT.CTRL:
						setCtrlPressed(false);
						break;
					case SWT.COMMAND:
						setCtrlPressed(false);
						break;
					case SWT.SHIFT:
						setShiftPressed(false);
						finishROISelection();
						break;
					default:
						return true;
				}
			}
			return false;
		});
	}

	@Override
	public boolean inKeystoneMode() {
		return keystoneMode;
	}

	public void setTarget(final GamaPoint centre) {
		setTarget(centre.x, centre.y, centre.z);

	}

	public void setPosition(final GamaPoint centre) {
		setPosition(centre.x, centre.y, centre.z);

	}

}
