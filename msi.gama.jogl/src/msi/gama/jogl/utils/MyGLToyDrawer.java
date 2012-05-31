package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_COMPILE;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_LINEAR_MIPMAP_NEAREST;
import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import msi.gama.common.util.ImageUtils;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

public class MyGLToyDrawer {
	
	
	
	//Texture
	float textureTop, textureBottom, textureLeft, textureRight;
	public Texture[] textures = new Texture[3];
	public static int currTextureFilter = 2; // currently used filter
	private String textureFileName = "/Users/macbookpro/Projects/Gama/Sources/branches/GAMA_CURRENT/msi.gama.jogl/src/textures/bird2.png";

	
	// Display list 
	private int boxDList;
	private int topDList;

	// Array of 5 for box colors
	private static float[][] boxColors = { // Bright: Red, Orange, Yellow,
			// Green, Blue
			{ 1.0f, 0.0f, 0.0f }, { 1.0f, 0.5f, 0.0f }, { 1.0f, 1.0f, 0.0f },
			{ 0.0f, 1.0f, 0.0f }, { 0.0f, 1.0f, 1.0f } };

	// Array for top colors
	private static float[][] topColors = { // Dark: Red, Orange, Yellow, Green,
			// Blue
			{ .5f, 0.0f, 0.0f }, { 0.5f, 0.25f, 0.0f }, { 0.5f, 0.5f, 0.0f },
			{ 0.0f, 0.5f, 0.0f }, { 0.0f, 0.5f, 0.5f } };
	
	// 2D Shape
	
	public void DrawOpenGLHelloWorldShape(GL gl, float size) {

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
		// ----- Render a triangle -----

		gl.glTranslatef(-1.5f * size, 0.0f, -6.0f); // translate left and into
													// the screen

		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glVertex3f(0.0f, size, 0.0f);
		gl.glVertex3f(-size, -size, 0.0f);
		gl.glVertex3f(size, -size, 0.0f);
		gl.glEnd();

		// ----- Render a quad -----

		// translate right, relative to the previous translation
		gl.glTranslatef(3.0f * size, 0.0f, 0.0f);

		gl.glBegin(GL_POLYGON); // draw using quads
		gl.glVertex3f(-size, size, 0.0f);
		gl.glVertex3f(size, size, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(-size, -size, 0.0f);
		gl.glEnd();
	}

	public void DrawColorTriangle(GL gl, float x, float y, float z, float size) {
		// ----- Render a triangle -----
		gl.glTranslatef(x, y, z); // translate left and into the screen
		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
		gl.glVertex3f(0.0f, size, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
		gl.glVertex3f(-size, -size, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		gl.glVertex3f(size, -size, 0.0f);
		gl.glEnd();
	}
	
	
	// 3D Shape 
	
	public void Draw3DOpenGLHelloWorldShape(GL gl, float size){


	      // ----- Render the Pyramid -----

	      gl.glTranslatef(-1.5f*size, 0.0f, -6.0f); // translate left and into the screen

	      gl.glBegin(GL_TRIANGLES); // of the pyramid

	      // Font-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, size, 0.0f);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(-size, -size, size);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(size, -size, size);

	      // Right-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, size, 0.0f);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(size, -size, size);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(size, -size, -size);

	      // Back-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, size, 0.0f);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(size, -size, -size);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(-size, -size, -size);

	      // Left-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, size, 0.0f);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(-size, -size, -size);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(-size, -size, size);

	      gl.glEnd(); // of the pyramid

	      // ----- Render the Color Cube -----
	      
	      gl.glTranslatef(3.0f*size, 0.0f, 0.0f); // translate right and into the screen

	      gl.glBegin(GL_QUADS); // of the color cube

	      // Top-face
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // green
	      gl.glVertex3f(size, size, -size);
	      gl.glVertex3f(-size, size, -size);
	      gl.glVertex3f(-size, size, size);
	      gl.glVertex3f(size, size, size);

	      // Bottom-face
	      gl.glColor3f(1.0f, 0.5f, 0.0f); // orange
	      gl.glVertex3f(size, -size, size);
	      gl.glVertex3f(-size, -size, size);
	      gl.glVertex3f(-size, -size, -size);
	      gl.glVertex3f(size, -size, -size);

	      // Front-face
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // red
	      gl.glVertex3f(size, size, size);
	      gl.glVertex3f(-size, size, size);
	      gl.glVertex3f(-size, -size, size);
	      gl.glVertex3f(size, -size, size);

	      // Back-face
	      gl.glColor3f(1.0f, 1.0f, 0.0f); // yellow
	      gl.glVertex3f(size, -size, -size);
	      gl.glVertex3f(-size, -size, -size);
	      gl.glVertex3f(-size, size, -size);
	      gl.glVertex3f(size, size, -size);

	      // Left-face
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
	      gl.glVertex3f(-size, size, size);
	      gl.glVertex3f(-size, size, -size);
	      gl.glVertex3f(-size, -size, -size);
	      gl.glVertex3f(-size, -size, size);

	      // Right-face
	      gl.glColor3f(1.0f, 0.0f, 1.0f); // violet
	      gl.glVertex3f(size, size, -size);
	      gl.glVertex3f(size, size, size);
	      gl.glVertex3f(size, -size, size);
	      gl.glVertex3f(size, -size, -size);

	      gl.glEnd(); // of the color cube
	}
	
	public void Draw3DCube(GL gl, float size){

	      // ----- Render the Centered Cube -----
		  gl.glColor3f(0.0f, 0.0f, 0.0f); // black
	      // Top-face
		  gl.glBegin(GL.GL_LINES);
		  
	      gl.glVertex3f(size, size, -size);
	      gl.glVertex3f(-size, size, -size);
	      
	      gl.glVertex3f(-size, size, -size);
	      gl.glVertex3f(-size, size, size);
	      
	      gl.glVertex3f(-size, size, size);
	      gl.glVertex3f(size, size, size);
	      
	      gl.glVertex3f(size, size, size);
	      gl.glVertex3f(size, size, -size);
	      
	      gl.glEnd();

	      // Bottom-face
	      gl.glBegin(GL.GL_LINES);
	      
	      gl.glVertex3f(size, -size, size);
	      gl.glVertex3f(-size, -size, size);
	      
	      gl.glVertex3f(-size, -size, size);
	      gl.glVertex3f(-size, -size, -size);
	      
	      gl.glVertex3f(-size, -size, -size);
	      gl.glVertex3f(size, -size, -size);
	      
	      gl.glVertex3f(size, -size, -size);
	      gl.glVertex3f(size, -size, size);
	      
	      gl.glEnd();

	      // Front-face
	      gl.glBegin(GL.GL_LINES);
	      
	      gl.glVertex3f(size, size, size);
	      gl.glVertex3f(-size, size, size);
	      
	      gl.glVertex3f(-size, size, size);
	      gl.glVertex3f(-size, -size, size);
	      
	      gl.glVertex3f(-size, -size, size);
	      gl.glVertex3f(size, -size, size);
	      
	      gl.glVertex3f(size, -size, size);
	      gl.glVertex3f(size, size, size);
	      
	      gl.glEnd();

	      // Back-face
	      gl.glBegin(GL.GL_LINES);
	      
	      gl.glVertex3f(size, -size, -size);
	      gl.glVertex3f(-size, -size, -size);
	      
	      gl.glVertex3f(-size, -size, -size);
	      gl.glVertex3f(-size, size, -size);
	      
	      gl.glVertex3f(-size, size, -size);
	      gl.glVertex3f(size, size, -size);
	      
	      gl.glVertex3f(size, size, -size);
	      gl.glVertex3f(size, -size, -size);
	      
	      
	      gl.glEnd();

	      // Left-face
	      gl.glBegin(GL.GL_LINES);
	      
	      gl.glVertex3f(-size, size, size);
	      gl.glVertex3f(-size, size, -size);
	      
	      gl.glVertex3f(-size, size, -size);
	      gl.glVertex3f(-size, -size, -size);
	      
	      gl.glVertex3f(-size, -size, -size);
	      gl.glVertex3f(-size, -size, size);
	      
	      gl.glVertex3f(-size, -size, size);
	      gl.glVertex3f(-size, size, size);
	      
	      gl.glEnd();

	      // Right-face
	      gl.glBegin(GL.GL_LINES);
	      gl.glVertex3f(size, size, -size);
	      gl.glVertex3f(size, size, size);
	      
	      gl.glVertex3f(size, size, size);
	      gl.glVertex3f(size, -size, size);
	      
	      gl.glVertex3f(size, -size, size);
	      gl.glVertex3f(size, -size, -size);
	      
	      gl.glVertex3f(size, -size, -size);
	      gl.glVertex3f(size, size, -size);
	      
	      gl.glEnd(); 
	}
	
	
	public void DrawSphere(GL gl, GLU glu,float x, float y, float z, float radius){
		// Draw sphere (possible styles: FILL, LINE, POINT).
        gl.glColor3f(0.3f, 0.5f, 1f);
        GLUquadric earth = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
        glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
        final int slices = 16;
        final int stacks = 16;
        glu.gluSphere(earth, radius, slices, stacks);
        glu.gluDeleteQuadric(earth);
	}
	
	//textured shape
	
	
	public void LoadTextureFromImage(GL gl) {

		// Load textures from image
		try {
			// Use URL so that can read from JAR and disk file.
			BufferedImage image = ImageUtils.getInstance().getImageFromFile(
					textureFileName);

			// Create a OpenGL Texture object from (URL, mipmap, file suffix)
			textures[0] = TextureIO.newTexture(image, false);
			// Nearest filter is least compute-intensive
			// Use nearer filter if image is larger than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			// Use nearer filter if image is smaller than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

			textures[1] = TextureIO.newTexture(image, false);
			// Linear filter is more compute-intensive
			// Use linear filter if image is larger than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			// Use linear filter if image is smaller than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			textures[2] = TextureIO.newTexture(image, true); // mipmap is true
			// Use mipmap filter is the image is smaller than the texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
					GL_LINEAR_MIPMAP_NEAREST);

			// Get the top and bottom coordinates of the textures. Image flips
			// vertically.
			TextureCoords textureCoords;
			textureCoords = textures[0].getImageTexCoords();
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();

		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void DrawTexture(GL gl, float width){
		
		//WARNING: Be sure to have call LoadTextureFromImage() in the init method og the GLRenderer

		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
		// context's state.
		textures[currTextureFilter].enable();
		// Binds this texture to the current GL context.
		textures[currTextureFilter].bind();

		gl.glBegin(GL_QUADS);

		// Front Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, width); // bottom-left of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, width); // bottom-right of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, width); // top-right of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, width); // top-left of the texture and quad
		
		gl.glEnd();
		
	}
	
	public void DrawTexturedQuad(GL gl, float width) {

		
		//WARNING: Be sure to have call LoadTextureFromImage() in the init method og the GLRenderer

		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
		// context's state.
		textures[currTextureFilter].enable();
		// Binds this texture to the current GL context.
		textures[currTextureFilter].bind();

		gl.glBegin(GL_QUADS);

		// Front Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, width); // bottom-left of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, width); // bottom-right of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, width); // top-right of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, width); // top-left of the texture and quad

		// Back Face
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, -width);

		// Top Face
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);

		// Bottom Face
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);

		// Right face
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);

		// Left Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);

		gl.glEnd();

	}
	
	public void DrawTexturedQuadWithNormal(GL gl, float width) {

		//WARNING: Be sure to have call LoadTextureFromImage() in the init method og the GLRenderer
		
		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
		// context's state.
		textures[currTextureFilter].enable();
		// Bind the texture with the currently chosen filter to the current
		// OpenGL
		// graphics context.
		textures[currTextureFilter].bind();

		gl.glBegin(GL_QUADS); // of the color cube

		// Front Face
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, width); // bottom-left of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, width); // bottom-right of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, width); // top-right of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, width); // top-left of the texture and quad

		// Back Face
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, -width);

		// Top Face
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);

		// Bottom Face
		gl.glNormal3f(0.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);

		// Right face
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);

		// Left Face
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);

		gl.glEnd();
	}
	
	
	//  Display List Shape ///////
	
	public void buildDisplayLists(GL gl, float size) {
		
	
		// Build two lists, and returns handle for the first list
		int base = gl.glGenLists(2);

		// Create a new list for box (with open-top), pre-compile for efficiency
		boxDList = base;
		gl.glNewList(boxDList, GL_COMPILE);

		gl.glBegin(GL_QUADS);
		// Front Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-size, -size, size); // bottom-left of the texture and
											// quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(size, -size, size); // bottom-right of the texture and
											// quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(size, size, size); // top-right of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-size, size, size); // top-left of the texture and quad
		// Back Face
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-size, -size, -size);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-size, size, -size);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(size, size, -size);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(size, -size, -size);
		// Top Face
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-size, size, -size);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-size, size, size);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(size, size, size);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(size, size, -size);
		// Bottom Face
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-size, -size, -size);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(size, -size, -size);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(size, -size, size);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-size, -size, size);
		// Right face
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(size, -size, -size);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(size, size, -size);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(size, size, size);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(size, -size, size);
		// Left Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-size, -size, -size);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-size, -size, size);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-size, size, size);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-size, size, -size);

		gl.glEnd();
		gl.glEndList();

		// Ready to make the second display list
		topDList = boxDList + 1;
		gl.glNewList(topDList, GL_COMPILE);
		gl.glBegin(GL_QUADS);
		// Top Face
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-size, size, -size);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-size, size, size);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(size, size, size);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(size, size, -size);
		gl.glEnd();
		gl.glEndList();

	}
	
	public void DrawTexturedDisplayList(GL gl, float size) {

		//WARNING: Be sure to call buildDisplayLists() in the init method of the GLRenderer  
		
		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
		// context's state.
		textures[currTextureFilter].enable();
		// Bind the texture with the currently chosen filter to the current
		// OpenGL
		// graphics context.
		textures[currTextureFilter].bind();

		gl.glColor3fv(boxColors[0], 0);
		gl.glCallList(boxDList); // draw the box
		gl.glColor3fv(topColors[0], 0);
		gl.glCallList(topDList); // draw the top

		gl.glTranslatef(size, 0.0f, 0.0f);

		gl.glColor3fv(boxColors[2], 0);
		gl.glCallList(boxDList); // draw the box
		gl.glColor3fv(topColors[2], 0);
		gl.glCallList(topDList); // draw the top

		gl.glTranslatef(0.0f, size, 0.0f);

		gl.glColor3fv(boxColors[3], 0);
		gl.glCallList(boxDList); // draw the box
		gl.glColor3fv(topColors[3], 0);
		gl.glCallList(topDList); // draw the top

		gl.glTranslatef(-size, 0.0f, 0.0f);

		
		gl.glColor3fv(boxColors[4], 0);
		gl.glCallList(boxDList); // draw the box
		gl.glColor3fv(topColors[4], 0);
		gl.glCallList(topDList); // draw the top
	}

}
