package msi.gama.jogl.utils.Camera;

import static java.awt.event.KeyEvent.*;
import java.awt.Point;
import java.awt.event.*;
import java.util.Iterator;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Camera.Arcball.Vector3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.AbstractTopology;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.runtime.GAMA;
import com.vividsolutions.jts.geom.Envelope;

public class CameraArcBall extends AbstractCamera {

	private double envWidth;
	private double envHeight;

	private double radius;

	private double _orientation;

	public double _sensivity;
	private final Vector3D _forward;

	public double horizInertia, vertInertia = 0;

	// inertia parameter
	public double damping = 0.9;
	public double amplitude = 0.3;
	public boolean enableInertia = false;
	public boolean arcBallInertia = false;
	public boolean moveInertia = false;

	public CameraArcBall(final JOGLAWTGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);

		_forward = new Vector3D();
		_orientation = 1.0;
		_phi = 90.00;
		_theta = 360.00;
		_sensivity = 0.4;
		_keyboardSensivity = 4;

		rotation();
	}

	public CameraArcBall(final double xPos, final double yPos, final double zPos, final double xLPos,
		final double yLPos, final double zLPos, final JOGLAWTGLRenderer renderer) {
		super(xPos, yPos, zPos, xLPos, yLPos, zLPos, renderer);
		_forward = new Vector3D();

		_position.x = xPos;
		_position.y = yPos;
		_position.z = zPos;

		_target.x = xLPos;
		_target.y = yLPos;
		_target.z = zLPos;
	}

	@Override
	public void UpdateCamera(final GL gl, final GLU glu, final int width, int height) {

		if ( height == 0 ) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		// FIXME: need to see the influence of the different parameter.
		glu.gluPerspective(45.0f, aspect, 0.1f, getMaxDim() * 100);
		glu.gluLookAt(this._position.getX(), this._position.getY(), this._position.getZ(), this._target.getX(),
			this._target.getY(), this._target.getZ(), getUpVector().getX(), getUpVector().getY(), getUpVector().getZ());

		animate();
	}

	@Override
	public void rotation() {
		if ( _phi < 360 && _phi > 180 ) {
			_orientation = -1;
			setUpVector(new GamaPoint(0.0, _orientation, 0.0));
		} else {
			_orientation = 1;
			setUpVector(new GamaPoint(0.0, _orientation, 0.0));
		}

		if ( _theta > 360 ) {
			_theta = 0.00000002;
		}
		if ( _theta < 0 ) {
			_theta = 360.00000002;
		}

		if ( _phi >= 360 ) {
			_phi = 0.00000002;
		}
		if ( _phi <= 0 ) {
			_phi = 360.00000002;
		}

		double cosAngle = Math.cos(_theta * Math.PI / 180.f);
		double sinAngle = Math.sin(_theta * Math.PI / 180.f);

		_position.x = radius * sinAngle * Math.sin(_phi * Math.PI / 180.f) + _target.x;
		_position.z = radius * cosAngle * Math.sin(_phi * Math.PI / 180.f) + _target.z;
		_position.y = radius * Math.cos(_phi * Math.PI / 180.f) + _target.y;

		double r_temp = Math.cos(_phi * Math.PI / 180.f);
		_forward.z = -Math.sin(_phi * Math.PI / 180.f);
		_forward.x = r_temp * -Math.cos(_theta * Math.PI / 180.f);
		_forward.y = r_temp * -Math.sin(_theta * Math.PI / 180.f);
	}

	// public void followAgent(IAgent a) {
	//
	// ILocation l = a.getGeometry().getLocation();
	// Envelope env = a.getGeometry().getEnvelope();
	//
	// double xPos = l.getX() - myRenderer.displaySurface.getEnvWidth() / 2;
	// double yPos = -(l.getY() - myRenderer.displaySurface.getEnvHeight() / 2);
	//
	// double zPos = env.maxExtent() * 2 + l.getZ();
	// double zLPos = -(env.maxExtent() * 2);
	//
	// updatePosition(xPos, yPos, zPos);
	// lookPosition(xPos, yPos, zLPos);
	//
	// }

	@Override
	public void initializeCamera(final double envWidth, final double envHeight) {

		this.envWidth = envWidth;
		this.envHeight = envHeight;

		if ( envWidth > envHeight ) {
			setMaxDim(envWidth);
		} else {
			setMaxDim(envHeight);
		}

		radius = getMaxDim() * INIT_Z_FACTOR;
		_target.x = envWidth / 2;
		_target.y = -envHeight / 2;
		_target.z = 0;
		_phi = 90.0;
		_theta = 360.00;
		rotation();
	}

	@Override
	public void initialize3DCamera(final double envWidth, final double envHeight) {
		this.envWidth = envWidth;
		this.envHeight = envHeight;

		if ( envWidth > envHeight ) {
			setMaxDim(envWidth);
		} else {
			setMaxDim(envHeight);
		}

		radius = getMaxDim() * INIT_Z_FACTOR;
		_target.x = envWidth / 2;
		_target.y = -envHeight / 2;
		_target.z = 0;
		_phi = 135.0;
		_theta = 360.00;
		rotation();
	}

	@Override
	public Double getRadius() {
		return this.radius;
	}

	@Override
	public void setRadius(final double r) {
		this.radius = r;
	}

	// Move in the XY plan by changing camera pos and look pos.
	@Override
	public void moveXYPlan2(final double diffx, final double diffy, final double z, final double w, final double h) {

		double translationValue = 0;

		translationValue = Math.abs(diffx) * ((z + 1) / w);

		if ( diffx > 0 ) {// move right
			updatePosition(_position.getX() - translationValue, _position.getY(), _position.getZ());
			lookPosition(_target.getX() - translationValue, _target.getY(), _target.getZ());
		} else {// move left
			updatePosition(_position.getX() + translationValue, _position.getY(), _position.getZ());
			lookPosition(_target.getX() + translationValue, _target.getY(), _target.getZ());
		}

		translationValue = Math.abs(diffy) * Math.abs((z + 1) / h);

		if ( diffy > 0 ) {// move down
			updatePosition(_position.getX(), _position.getY() + translationValue, _position.getZ());
			this.lookPosition(_target.getX(), _target.getY() + translationValue, _target.getZ());
		} else {// move up
			updatePosition(_position.getX(), _position.getY() - translationValue, _position.getZ());
			lookPosition(_target.getX(), _target.getY() - translationValue, _target.getZ());
		}

	}

	@Override
	public void animate() {
		double translationValue = 2 * (Math.abs(_position.getZ()) + 1) / myRenderer.getHeight();
		if ( this.forward ) {
			if ( shiftKeyDown ) {
				_phi -= -_keyboardSensivity * _sensivity;
				rotation();
			} else {
				updatePosition(_position.getX(), _position.getY() - translationValue, _position.getZ());
				lookPosition(_target.getX(), _target.getY() - translationValue, _target.getZ());

			}
		}
		if ( this.backward ) {
			if ( shiftKeyDown ) {
				_phi -= _keyboardSensivity * _sensivity;
				rotation();
			} else {
				updatePosition(_position.getX(), _position.getY() + translationValue, _position.getZ());
				this.lookPosition(_target.getX(), _target.getY() + translationValue, _target.getZ());
			}
		}
		if ( this.strafeLeft ) {
			if ( shiftKeyDown ) {
				_theta -= -_keyboardSensivity * _sensivity;
				rotation();
			} else {
				updatePosition(_position.getX() + translationValue, _position.getY(), _position.getZ());
				lookPosition(_target.getX() + translationValue, _target.getY(), _target.getZ());
			}
		}
		if ( this.strafeRight ) {
			if ( shiftKeyDown ) {
				_theta -= _keyboardSensivity * _sensivity;
				rotation();
			} else {
				updatePosition(_position.getX() - translationValue, _position.getY(), _position.getZ());
				lookPosition(_target.getX() - translationValue, _target.getY(), _target.getZ());
			}
		}
	}

	@Override
	public Vector3D getForward() {
		return this._forward;
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent arg0) {
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		if ( this.radius != 0 ) {
			incrementalZoomStep = (float) radius / 10;
		} else {
			incrementalZoomStep = 0.1f;
		}
		if ( arg0.getWheelRotation() < 0 ) {// Move Down

			radius -= incrementalZoomStep;
			rotation();
		} else {// Move Up
			radius += incrementalZoomStep;
			rotation();
		}
		myRenderer.displaySurface.setZoomLevel(getZoomLevel());
		// PrintParam();
	}

	@Override
	public Double getZoomLevel() {
		return getMaxDim() * INIT_Z_FACTOR / radius;
	}

	@Override
	public void mouseDragged(final MouseEvent arg0) {

		enableInertia = false;

		if ( isArcBallOn(arg0) ) {
			arcBallInertia = true;
		} else {
			horizInertia = arg0.getX() - lastxPressed;
			vertInertia = arg0.getY() - lastyPressed;
			velocityHoriz = horizInertia;
			velocityVert = vertInertia;
			moveInertia = true;
		}

		if ( isArcBallOn(arg0) ) {

			// check the difference between the current x and the last x position
			int horizMovement = arg0.getX() - lastxPressed;
			// check the difference between the current y and the last y position
			int vertMovement = arg0.getY() - lastyPressed;

			horizInertia = arg0.getX() - lastxPressed;
			vertInertia = arg0.getY() - lastyPressed;
			velocityHoriz = horizInertia;
			velocityVert = vertInertia;

			// set lastx to the current x position
			lastxPressed = arg0.getX();
			// set lastyPressed to the current y position
			lastyPressed = arg0.getY();

			_theta -= horizMovement * _sensivity;
			_phi -= vertMovement * _sensivity;

			rotation();

		}
		// ROI Is enabled only if the view is in a 2D plan.
		// else if ( myRenderer.displaySurface.selectRectangle && IsViewIn2DPlan() ) {

		else if ( (arg0.isShiftDown() || arg0.isAltDown()) && IsViewIn2DPlan() ) {
			myRenderer.displaySurface.selectRectangle = true;
			mousePosition.x = arg0.getX();
			mousePosition.y = arg0.getY();
			enableROIDrawing = true;
			myRenderer.DrawROI();

		} else {
			// check the difference between the current x and the last x position
			int diffx = arg0.getX() - lastxPressed;
			// check the difference between the current y and the last y position
			int diffy = arg0.getY() - lastyPressed;
			// set lastx to the current x position
			lastxPressed = arg0.getX();
			// set lastyPressed to the current y position
			lastyPressed = arg0.getY();

			double speed = 0.035;

			// Decrease the speed of the translation if z is negative.
			if ( _position.getZ() < 0 ) {
				speed = speed / Math.abs(_position.getZ()) * 2;
			} else {
				speed = speed * Math.abs(_position.getZ()) / 4;
			}

			moveXYPlan2(diffx, diffy, _position.getZ(), this.myRenderer.getWidth(), this.myRenderer.getHeight());
		}
		// PrintParam();
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {
		if ( arg0.getClickCount() > 1 ) {
			myRenderer.displaySurface.zoomFit();
		}
		velocityHoriz = 0;
		velocityVert = 0;

		if ( arg0.isShiftDown() || arg0.isAltDown() ) {
			myRenderer.displaySurface.selectRectangle = true;

			Point point = myRenderer.getIntWorldPointFromWindowPoint(new Point(arg0.getX(), arg0.getY()));

			mousePosition.x = arg0.getX();
			mousePosition.y = arg0.getY();
			enableROIDrawing = true;
			myRenderer.DrawROI();
			myRenderer.roiCenter.setLocation(point.x, point.y);

			enableROIDrawing = false;
		}
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {

	}

	@Override
	public void mouseExited(final MouseEvent arg0) {}

	@Override
	public void mousePressed(final MouseEvent arg0) {
		velocityHoriz = 0;
		velocityVert = 0;

		lastxPressed = arg0.getX();
		lastyPressed = arg0.getY();

		// Picking mode
		// if ( myRenderer.displaySurface.picking ) {
		// Activate Picking when press and right click and if in Picking mode
		if ( arg0.getButton() == 3 ) {
			isPickedPressed = true;
			myRenderer.setPicking(true);
			// myRenderer.drawPickableObjects();
		} else {
			myRenderer.setPicking(false);
			// }

		}

		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();

		myRenderer.getIntWorldPointFromWindowPoint(new Point(arg0.getX(), arg0.getY()));
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {

		enableInertia = true;
		if ( IsViewIn2DPlan() && enableROIDrawing == true ) {
			if ( arg0.isAltDown() ) {
				Iterator<IShape> shapes =
					GAMA.getSimulation()
						.getTopology()
						.getSpatialIndex()
						.allInEnvelope(
							new GamaPoint(myRenderer.roiCenter.x, -myRenderer.roiCenter.y),
							new Envelope(myRenderer.roi_List.get(0), myRenderer.roi_List.get(2), -myRenderer.roi_List
								.get(1), -myRenderer.roi_List.get(3)), new Different(), true);
				final Iterator<IAgent> agents = AbstractTopology.toAgents(shapes);

				myRenderer.displaySurface.selectSeveralAgents(agents, 0);

			}
			if ( arg0.isShiftDown() ) {
				myRenderer.ROIZoom();
			}
			enableROIDrawing = false;
		}
	}

	@Override
	public void keyPressed(final KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
			case VK_LEFT:
				strafeLeft = true;
				shiftKeyDown = checkShiftKeyDown(arg0);
				break;
			case VK_RIGHT:
				strafeRight = true;
				shiftKeyDown = checkShiftKeyDown(arg0);
				break;
			case VK_UP:
				forward = true;
				shiftKeyDown = checkShiftKeyDown(arg0);
				break;
			case VK_DOWN:
				backward = true;
				shiftKeyDown = checkShiftKeyDown(arg0);
				break;
		}
	}

	@Override
	public void keyReleased(final KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
			case VK_LEFT: // player turns left (scene rotates right)
				strafeLeft = false;
				break;
			case VK_RIGHT: // player turns right (scene rotates left)
				strafeRight = false;
				break;
			case VK_UP:
				forward = false;
				break;
			case VK_DOWN:
				backward = false;
				break;
		}
	}

	@Override
	public void keyTyped(final KeyEvent arg0) {}

	@Override
	public double getMaxDim() {
		return maxDim;
	}

	@Override
	public boolean IsViewIn2DPlan() {
		if ( _phi > 85 && _phi < 95 && _theta > 355 && _theta < 365 ) {
			return true;
		} else {
			return false;
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void arcBallInertia() {
		if ( enableInertia ) {
			if ( arcBallInertia ) {
				velocityHoriz *= damping;
				velocityVert *= damping;

				_theta -= velocityHoriz * amplitude;
				_phi -= velocityVert * amplitude;

				rotation();

				if ( Math.abs(velocityHoriz) < 0.01 || Math.abs(velocityVert) < 0.01 ) {
					velocityHoriz = 0;
					velocityVert = 0;
					enableInertia = false;
					arcBallInertia = false;
				}
			}
		}
	}

	@Override
	public void moveInertia() {
		if ( enableInertia ) {
			if ( moveInertia ) {
				velocityHoriz *= damping;
				velocityVert *= damping;

				moveXYPlan2(velocityHoriz, velocityVert, _position.getZ(), this.myRenderer.getWidth(),
					this.myRenderer.getHeight());

				if ( Math.abs(velocityHoriz) < 0.01 || Math.abs(velocityVert) < 0.01 ) {
					velocityHoriz = 0;
					velocityVert = 0;
					enableInertia = false;
					moveInertia = false;
				}
			}
		}
	}

}// End of Class CameraArcBall
