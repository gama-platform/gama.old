package msi.gama.jogl.gis_3D;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.FPSAnimator;
import static javax.media.opengl.GL.*; // static import constants

/**
 * NeHe Lesson 2: Your First Polygon
 */
public class NeheJOGL02Basics extends JPanel implements GLEventListener {
   private static final int REFRESH_FPS = 60;    // Display refresh frames per second
   private GLU glu;             // For the GL Utility
   public final FPSAnimator animator;  // Used to drive display() 
   
   // Constructor
   public NeheJOGL02Basics() {
      GLCanvas canvas = new GLCanvas();
      this.setLayout(new BorderLayout());
      this.add(canvas, BorderLayout.CENTER);
      canvas.addGLEventListener(this);
   
      // Run the animation loop using the fixed-rate Frame-per-second animator,
      // which calls back display() at this fixed-rate (FPS).
      animator = new FPSAnimator(canvas, REFRESH_FPS, true);
   }


   // Main program
   public static void main(String[] args) {
      final int WINDOW_WIDTH = 320;
      final int WINDOW_HEIGHT = 240;
      final String WINDOW_TITLE = "Nehe #2: Your First Polygon";

      JFrame frame = new JFrame();
      final NeheJOGL02Basics joglMain = new NeheJOGL02Basics();
      frame.setContentPane(joglMain);
      frame.addWindowListener(new WindowAdapter() {
         @Override 
         public void windowClosing(WindowEvent e) {
            // Use a dedicate thread to run the stop() to ensure that the
            // animator stops before program exits.
            new Thread() {
               @Override 
               public void run() {
                  joglMain.animator.stop(); // stop the animator loop
                  System.exit(0);
               }
            }.start();
         }
      });
      frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
      frame.setTitle(WINDOW_TITLE);
      frame.setVisible(true);
      joglMain.animator.start(); // start the animation loop
   }

   // ------ Implement methods declared in GLEventListener ------

   /**
    * Called back immediately after the OpenGL context is initialized. Can be used 
    * to perform one-time initialization. Run only once.
    */
   @Override
   public void init(GLAutoDrawable drawable) {
      GL gl = drawable.getGL(); // Get the OpenGL graphics context
      glu = new GLU(); // GL Utilities
      gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set background (clear) color
      gl.glClearDepth(1.0f); // Set clear depth value to farthest
      gl.glEnable(GL_DEPTH_TEST); // Enables depth testing
      gl.glDepthFunc(GL_LEQUAL); // The type of depth test to do
      // Do the best perspective correction
      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
      // Enable smooth shading, which blends colors nicely, and smoothes out lighting.
      gl.glShadeModel(GL_SMOOTH);

      // ----- Your OpenGL initialization code here -----
   }

   /**
    * Call-back handler for window re-size event. Also called when the drawable is 
    * first set to visible.
    */
   @Override
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL gl = drawable.getGL(); // Get the OpenGL graphics context

      if (height == 0) {
         height = 1; // prevent divide by zero
      }
      float aspect = (float)width / height;

      // Set the viewport (display area) to cover the entire window
      gl.glViewport(0, 0, width, height);

      // Setup perspective projection, with aspect ratio matches viewport
      gl.glMatrixMode(GL_PROJECTION); // Choose projection matrix
      gl.glLoadIdentity(); // Reset projection matrix
      glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar

      // Enable the model-view transform
      gl.glMatrixMode(GL_MODELVIEW);
      gl.glLoadIdentity(); // reset
   }

   /**
    * Called back by the animator to perform rendering.
    */
   @Override
   public void display(GLAutoDrawable drawable) {
      GL gl = drawable.getGL(); // Get the OpenGL graphics context
      gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear color and depth buffers
      gl.glLoadIdentity();  // reset the model-view matrix

      // ----- Render a triangle -----

      gl.glTranslatef(-1.5f, 0.0f, -6.0f); // translate left and into the screen

      gl.glBegin(GL_TRIANGLES); // draw using triangles
      gl.glVertex3f(0.0f, 1.0f, 0.0f);
      gl.glVertex3f(-1.0f, -1.0f, 0.0f);
      gl.glVertex3f(1.0f, -1.0f, 0.0f);
      gl.glEnd();

      // ----- Render a quad -----

      // translate right, relative to the previous translation
      gl.glTranslatef(3.0f, 0.0f, 0.0f);

      gl.glBegin(GL_POLYGON); // draw using quads
      gl.glVertex3f(-1.0f, 1.0f, 0.0f);
      gl.glVertex3f(1.0f, 1.0f, 0.0f);
      gl.glVertex3f(0.0f, 0.0f, 0.0f);
      gl.glVertex3f(-1.0f, -1.0f, 0.0f);
      gl.glEnd();
   }

   /**
    * Called back when the display mode (eg. resolution) has been changed.
    * (not implemented by JOGL)
    */
   @Override
   public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
         boolean deviceChanged) {}
}
