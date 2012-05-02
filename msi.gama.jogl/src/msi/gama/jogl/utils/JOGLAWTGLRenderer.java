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
import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL.GL_POSITION;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_SMOOTH;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.*;

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
	
	//Textures list to store all the texture.
	public ArrayList<Texture> myTextures = new ArrayList<Texture>() ;
	
	float textureTop,textureBottom,textureLeft,textureRight;
	
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
		
		//((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawMyImages();
		//((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawEnvironmentBounds();
		//((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawScale();
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawMyGeometries();
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawXYZAxis(((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myMaxDim/10);
		this.DrawZValue();
				
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
		displaySurface.openGLGraphics = new JOGLAWTDisplayGraphics(gl, glu, this,displaySurface.envWidth,
				displaySurface.envHeight);

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
		
		//FIXME: Now only one BufferedImage is load to test the texture mapping function. But this should 
		//be done in IGraphics to create the texture when drawImage is called.
		//Now there is a thread problem it seems that TextureIO.newTexture(image, false);
		//can only be call in the opengl thread. (i.e in init() or diaply() method)
		//FIXME: The calling of this function change the color of the drawn geometry. (need to check that).
		// Use URL so that can read from JAR and disk file.
        BufferedImage image = null;
		try {
			image = ImageIO.read(this.getClass().getResource(
			      "arnoi.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//InitTexture(image);
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
	

	
	public void DrawZValue(){
		GLUT glut = new GLUT();
		//X Axis
	    gl.glRasterPos3f(0.0f, ((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myHeight * 0.01f, 0.0f);   
	    glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "z = " + String.valueOf((int)camera.zPos));	
	}
	
	public void DrawTexture(int index,MyImage img){
			
		Texture t = this.myTextures.get(index);
		//texture
	    t.bind();
	    
	    TextureCoords textureCoords;
	    textureCoords = t.getImageTexCoords();
	    textureTop = textureCoords.top();
	    textureBottom = textureCoords.bottom();
	    textureLeft = textureCoords.left();
	    textureRight = textureCoords.right();
	        
		gl.glBegin(GL_QUADS);	           
	      gl.glTexCoord2f(textureLeft, textureBottom);
	      gl.glVertex3f(img.x, -(img.y+img.image.getHeight()), 0); // bottom-left of the texture and quad
	      gl.glTexCoord2f(textureRight, textureBottom);
	      gl.glVertex3f((img.x+img.image.getWidth()), -(img.y+img.image.getHeight()), 0); // bottom-right of the texture and quad
	      gl.glTexCoord2f(textureRight, textureTop);
	      gl.glVertex3f((img.x+img.image.getWidth()), -(img.y), 0); // top-right of the texture and quad
	      gl.glTexCoord2f(textureLeft, textureTop);
	      gl.glVertex3f(img.x, -img.y, 0); // top-left of the texture and quad	      
       gl.glEnd();	
	}
	
	public void InitTexture(BufferedImage image){
		
        // Create a OpenGL Texture object from (URL, mipmap, file suffix)
        Texture curTexture = TextureIO.newTexture(image, false);
        // Nearest filter is least compute-intensive
        // Use nearer filter if image is larger than the original texture
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        // Use nearer filter if image is smaller than the original texture
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // For texture coordinates more than 1, set to wrap mode to GL_REPEAT for
        // both S and T axes (default setting is GL_CLAMP)
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// Nearest filter is least compute-intensive
        // Use nearer filter if image is larger than the original texture
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        // Use nearer filter if image is smaller than the original texture
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
     // For texture coordinates more than 1, set to wrap mode to GL_REPEAT for
        // both S and T axes (default setting is GL_CLAMP)
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    
     // Enable the texture
        gl.glEnable(GL_TEXTURE_2D);
        
        this.myTextures.add(curTexture);
	}
}
