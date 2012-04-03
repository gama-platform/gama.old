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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;



public class MyListener implements KeyListener, MouseListener,
		MouseMotionListener, MouseWheelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Camera myCamera;


	//To handle mouse event
	private int lastx, lasty;

	public MyListener(Camera camera) {
		myCamera = camera;
	}

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
			myCamera.InitParam();
			break;
		case VK_H:
			myCamera.Init3DView();
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {

		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		myCamera.PrintParam();

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
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		lastx = arg0.getX();
		lasty = arg0.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

		int diffx = arg0.getX() - lastx; // check the difference between the
											// current x and the last x position
		int diffy = arg0.getY() - lasty; // check the difference between the
											// current y and the last y position
		lastx = arg0.getX(); // set lastx to the current x position
		lasty = arg0.getY(); // set lasty to the current y position

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

	@Override
	public void mouseMoved(MouseEvent arg0) {

		//System.out.println("x:"+arg0.getX()+ " y:" +arg0.getY());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
	
//		if (arg0.getWheelRotation() < 0) {// Move Up
//			myWorld.cameraZPosition += 0.1;
//			myWorld.cameraLZPosition += 0.1;
//
//			myCamera.moveForward(0.1);
//			// camera.look(10);
//		} else {// Move down
//			myWorld.cameraZPosition -= 0.1;
//			myWorld.cameraLZPosition -= 0.1;
//
//			myCamera.moveForward(-0.1);
//
//		}

	}

}
