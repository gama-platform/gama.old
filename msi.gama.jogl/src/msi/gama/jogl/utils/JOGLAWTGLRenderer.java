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
import static javax.media.opengl.GL.GL_SMOOTH;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.FPSAnimator;

import msi.gama.jogl.JOGLAWTDisplayGraphics;
import msi.gama.jogl.JOGLAWTDisplaySurface;

public class JOGLAWTGLRenderer implements GLEventListener{
	
/////OpenGL member///////
	private GLU glu;
	private GL gl;
	FPSAnimator animator;
	public boolean opengl = true;
	// Event Listener
	public MyListener myListener;

	private int width, height;
	// Camera
	private Camera camera;
	
	//
	private JOGLAWTDisplaySurface displaySurface;
	
	public JOGLAWTGLRenderer(JOGLAWTDisplaySurface d ){
		displaySurface= d;
		camera= d.camera;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// System.out.println("opengl display");
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

		gl.glEnable(GL_BLEND); // Turn Blending On
		gl.glDisable(GL_DEPTH_TEST); // Turn Depth Testing Off

		// handle lighting
		gl.glEnable(GL_LIGHTING);

		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		
		camera.UpdateCamera(gl,width,height);

		gl.glMatrixMode(GL.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The Modelview Matrix

		// enable color tracking
		gl.glEnable(GL_COLOR_MATERIAL);
		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

		//((JOGLAWTDisplayGraphics) openGLGraphics).DrawEnvironmentBounds();
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawMyGeometries();
		//((JOGLAWTDisplayGraphics) openGLGraphics).draw(gl);

		// this.DrawOpenGLHelloWorldShape(gl);
		
	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		width = drawable.getWidth();
		height = drawable.getHeight();
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		// GL Utilities
		glu = new GLU();

		// Initialize the IGraphics (FIXME: Should we initialize it here??)
		displaySurface.openGLGraphics = new JOGLAWTDisplayGraphics(gl, glu, displaySurface.envWidth,
				displaySurface.envHeight, displaySurface.scale_rate);

		// Enable smooth shading, which blends colors nicely across a polygon,
		// and smoothes out lighting.
		GLUtil.enableSmooth(gl);
		// Set background color (in RGBA). Alpha of 0 for total transparency
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		// We want the best perspective correction to be done
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		//gl.glShadeModel(GL_SMOOTH);

		//gl.glEnable(GL_BLEND);
		// gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		camera.UpdateCamera(gl,width,height);

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
		System.out.println("openGL init ok");
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3,
			int arg4) {
		// Get the OpenGL graphics context
		gl = drawable.getGL();

		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;

		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Enable the model view - any new transformations will affect the
		// model-view matrix
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset

		// perspective view
		gl.glViewport(10, 10, width - 20, height - 20);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getZPos(),
				camera.getXLPos(), camera.getYLPos(), camera.getZLPos(), 0.0,
				1.0, 0.0);
		
	}

}
