package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_AMBIENT;
import static javax.media.opengl.GL.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_COLOR_MATERIAL;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_DIFFUSE;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_LIGHT1;
import static javax.media.opengl.GL.GL_LIGHTING;
import static javax.media.opengl.GL.GL_MODELVIEW;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL.GL_POSITION;

import msi.gama.jogl.gis_3D.Camera;
import msi.gama.jogl.gis_3D.World_3D;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class MyGLEventListener implements GLEventListener {

	public World_3D myWorld;
	public Camera myCamera;
	private int width, height;
	private GL gl;
	private GLU glu;
	
	
    
 // Draw a sector made of poygon as polygon or as only the contour of the
 	// polygon.
 	public boolean drawAsPolygon = false;

	public MyGLEventListener(World_3D world, Camera camera) {
		myWorld = world;
		myCamera = camera;
	}
	
	public GL GetGL(){
		return gl;
	}
	
	public GLU GetGLU(){
		return glu;
	}

	/**
	 * Called by the drawable immediately after the OpenGL context is
	 * initialized for the first time. Can be used to perform one-time OpenGL
	 * initialization such as setup of lights and display lists. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {

		width = drawable.getWidth();
		height = drawable.getHeight();
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		// GL Utilities
		glu = new GLU();

		// Enable smooth shading, which blends colors nicely across a polygon,
		// and smoothes out lighting.
		GLUtil.enableSmooth(gl);
		// Set background color (in RGBA). Alpha of 0 for total transparency
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		// We want the best perspective correction to be done
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(myCamera.getXPos(), myCamera.getYPos(), myCamera.getZPos(),
				myCamera.getXLPos(), myCamera.getYLPos(), myCamera.getZLPos(), 0.0,
				1.0, 0.0);


		// Set up the lighting for Light-1
		// Ambient light does not come from a particular direction. Need some
		// ambient light to light up the scene. Ambient's value in RGBA
		float[] lightAmbientValue = { 0.5f, 0.5f, 0.5f, 1.0f };
		// Diffuse light comes from a particular location. Diffuse's value in
		// RGBA
		float[] lightDiffuseValue = { 1.0f, 1.0f, 1.0f, 1.0f };
		// Diffuse light location xyz (in front of the screen).
		float lightDiffusePosition[] = { 0.0f, 0.0f, 2.0f, 1.0f };

		gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue, 0);
		gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
		gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
		gl.glEnable(GL_LIGHT1); // Enable Light-1
		gl.glDisable(GL_LIGHTING); // But disable lighting

	}

	/**
	 * Called by the drawable to initiate OpenGL rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		// Get the OpenGL graphics context
		gl = drawable.getGL();

		width = drawable.getWidth();
		height = drawable.getHeight();

		// Clear the screen and the depth buffer
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gl.glViewport(0, 0, width, height); // Reset The Current Viewport

		gl.glMatrixMode(GL.GL_PROJECTION);
		// Reset the view (x, y, z axes back to normal)
		gl.glLoadIdentity();

		// Blending control
		if (myWorld.blendingEnabled) {
			gl.glEnable(GL_BLEND); // Turn Blending On
			gl.glDisable(GL_DEPTH_TEST); // Turn Depth Testing Off
		} else {
			gl.glDisable(GL_BLEND); // Turn Blending Off
			gl.glEnable(GL_DEPTH_TEST); // Turn Depth Testing On
		}

		// Select a texture based on filter
		// textures[currTextureFilter].bind();

		// handle lighting
		gl.glEnable(GL_LIGHTING);
	

		if (myWorld.isFilledOn) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		} else {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		}

		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(myCamera.getXPos(), myCamera.getYPos(), myCamera.getZPos(),
				myCamera.getXLPos(), myCamera.getYLPos(), myCamera.getZLPos(), 0.0,
				1.0, 0.0);

		gl.glMatrixMode(GL.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The Modelview Matrix

		// enable color tracking
		gl.glEnable(GL_COLOR_MATERIAL);
		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

		myWorld.update(gl,glu);

	}

	/**
	 * Called by the drawable during the first repaint after the component has
	 * been resized. Run at least once when the window is first set to visible.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// Get the OpenGL graphics context
		gl = drawable.getGL();

		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;

		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Enable the model view - any new transformations will affect the
		// model-view
		// matrix
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset

		// perspective view
		gl.glViewport(10, 10, width - 20, height - 20);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(myCamera.getXPos(), myCamera.getYPos(), myCamera.getZPos(),
				myCamera.getXLPos(), myCamera.getYLPos(), myCamera.getZLPos(), 0.0,
				1.0, 0.0);
	}

	/**
	 * Called when the display mode (eg. resolution) has been changed.
	 */
	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

}
