package msi.gama.jogl.utils;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_Z;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import msi.gama.jogl.utils.myarcball.*;


public class MyListener implements KeyListener, MouseListener,
		MouseMotionListener, MouseWheelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Camera myCamera;
	private JOGLAWTGLRenderer myRenderer;

	//To handle mouse event
	private int lastx, lasty;
	
	public boolean isArcBallEnable=false;

	public MyListener(Camera camera) {
		myCamera = camera;
	}

	public MyListener(Camera camera, JOGLAWTGLRenderer renderer){
		myCamera = camera;
		myRenderer = renderer;
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		// TODO Auto-generated method stub
		if (mouseEvent.isControlDown() && myCamera.isModelCentered) {
			if (SwingUtilities.isRightMouseButton(mouseEvent)) {
				myRenderer.reset();
			}
		} else {
			myCamera.PrintParam();
			System.out.println( "x:" + mouseEvent.getX() + 
								" y:" + mouseEvent.getY());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		
		//FIXME: Need to use mouseEvent.isControlDown() for windows and Linux
		if (mouseEvent.isMetaDown() && myCamera.isModelCentered) {
			if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
				myRenderer.startDrag(mouseEvent.getPoint());
			}
		} else{
			lastx = mouseEvent.getX();
			lasty = mouseEvent.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {

		//FIXME: Need to use mouseEvent.isControlDown() for windows and Linux
		if (mouseEvent.isMetaDown() && myCamera.isModelCentered) {
			if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
				myRenderer.drag(mouseEvent.getPoint());
			}
		} else {
			int diffx = mouseEvent.getX() - lastx; // check the difference between the
												// current x and the last x
												// position
			int diffy = mouseEvent.getY() - lasty; // check the difference between the
												// current y and the last y
												// position
			lastx = mouseEvent.getX(); // set lastx to the current x position
			lasty = mouseEvent.getY(); // set lasty to the current y position

			double speed = 0.035;

			// Decrease the speed of the translation if z is negative.
			if (myCamera.getZPos() < 0) {
				speed = (speed / Math.abs(myCamera.getZPos()) * 2);
			} else {
				speed = (speed * Math.abs(myCamera.getZPos()) / 4);
			}
			// camera.PrintParam();
			myCamera.moveXYPlan(diffx, diffy, speed);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

		//System.out.println("x:"+arg0.getX()+ " y:" +arg0.getY());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
//		if (arg0.getWheelRotation() < 0) {// Move Up
//			myCamera.zPos -= 0.5;
//			myCamera.zLPos -= 0.5;
//			//myCamera.moveForward(0.1);
//			// camera.look(10);
//		} else {// Move down
//			myCamera.zPos += 0.5;
//			myCamera.zLPos += 0.5;
//			//myCamera.moveForward(-0.1);
//		}
		
		float incrementalZoomStep=(float) myCamera.zPos/10;
		if (arg0.getWheelRotation() < 0) {// Move Up

			myCamera.zPos -=incrementalZoomStep;
			myCamera.zLPos -=incrementalZoomStep;
			//myCamera.moveForward(incrementalStep);
			//myCamera.look(10);
		} else {// Move down
 
			myCamera.zPos +=incrementalZoomStep;
			myCamera.zLPos +=incrementalZoomStep;
			//myCamera.moveForward(-incrementalStep);
			//myCamera.look(10);
		}

	}


	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_LEFT: // player turns left (scene rotates right)
			myCamera.strafeLeft(0.1);
			myCamera.look(10);
			break;
		case VK_RIGHT: // player turns right (scene rotates left)
			myCamera.strafeRight(0.1);
			myCamera.look(10);
			break;
		case VK_UP:
			//myCamera.cameraZPosition += 0.1;
			//myCamera.cameraLZPosition += 0.1;
			myCamera.moveForward(0.1);
			myCamera.look(10);
			break;
		case VK_DOWN:
			//myWorld.cameraZPosition -= 0.1;
			//myWorld.cameraLZPosition -= 0.1;
			myCamera.moveForward(-0.1);
			myCamera.look(10);
			break;
		case KeyEvent.VK_PAGE_UP:
			myCamera.pitchDown(0.05);
			myCamera.look(10);
			break;
		case KeyEvent.VK_PAGE_DOWN:
			myCamera.pitchUp(0.05);
			myCamera.look(10);
			break;
		case KeyEvent.VK_HOME:
			myCamera.yawLeft(0.01);
			myCamera.look(10);
			break;
		case KeyEvent.VK_END:
			myCamera.yawRight(0.01);
			myCamera.look(10);
			break;
		case VK_I:
			//myCamera.InitParam();
			break;
		case VK_H:
			//myCamera.Init3DView();
			break;
		}		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
