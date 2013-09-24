package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import msi.gama.common.util.ImageUtils;
import com.sun.opengl.util.texture.*;

public class MyGLToyDrawer {

	// Texture
	float textureTop, textureBottom, textureLeft, textureRight;
	public Texture[] textures = new Texture[3];
	public static int currTextureFilter = 2; // currently used filter

	// Display list
	private int boxDList;
	private int topDList;

	// Array of 5 for box colors
	private static float[][] boxColors = { // Bright: Red, Orange, Yellow,
			// Green, Blue
			{ 1.0f, 0.0f, 0.0f }, { 1.0f, 0.5f, 0.0f }, { 1.0f, 1.0f, 0.0f }, { 0.0f, 1.0f, 0.0f },
			{ 0.0f, 1.0f, 1.0f } };

	// Array for top colors
	private static float[][] topColors = { // Dark: Red, Orange, Yellow, Green,
			// Blue
			{ .5f, 0.0f, 0.0f }, { 0.5f, 0.25f, 0.0f }, { 0.5f, 0.5f, 0.0f }, { 0.0f, 0.5f, 0.0f },
			{ 0.0f, 0.5f, 0.5f } };

	// 2D Shape

	public void DrawOpenGLHelloWorldShape(final GL gl, final float size) {

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

	public void DrawColorTriangle(final GL gl, final float x, final float y, final float z, final float alpha,
		final float size) {
		// ----- Render a triangle -----
		gl.glTranslatef(x, y, z); // translate left and into the screen
		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glColor4f(1.0f, 0.0f, 0.0f, alpha); // Red
		gl.glVertex3f(0.0f, size, 0.0f);
		gl.glColor4f(0.0f, 1.0f, 0.0f, alpha); // Green
		gl.glVertex3f(-size, -size, 0.0f);
		gl.glColor4f(0.0f, 0.0f, 1.0f, alpha); // Blue
		gl.glVertex3f(size, -size, 0.0f);
		gl.glEnd();
		gl.glTranslatef(-x, -y, -z); // retranslate right and into the screen
	}

	// 3D Shape

	public void Draw3DOpenGLHelloWorldShape(final GL gl, final float size) {

		// ----- Render the Pyramid -----

		gl.glTranslatef(-1.5f * size, 0.0f, -6.0f); // translate left and into the screen

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

		gl.glTranslatef(3.0f * size, 0.0f, 0.0f); // translate right and into the screen

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

	public void DrawArrayListCube(final GL gl, final float size) {
		float vertices[] = {
			// Top-face
			size, size, -size, -size, size, -size, -size, size, size, size, size, size,
			// Bottom-face
			size, -size, size, -size, -size, size, -size, -size, -size, size, -size, -size,
			// Front-face
			size, size, size, -size, size, size, -size, -size, size, size, -size, size,
			// Back-face
			size, -size, -size, -size, -size, -size, -size, size, -size, size, size, -size,
			// Left-face
			-size, size, size, -size, size, -size, -size, -size, -size, -size, -size, size,
			// Right-face
			size, size, -size, size, size, size, size, -size, size, size, -size, -size, };

		gl.glBegin(GL_QUADS);
		for ( int i = 0; i < 24; i++ ) {
			gl.glVertex3f(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
		}
		gl.glEnd();
	}

	public static void Draw3DCube(final GL gl, final float size) {

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

	public static void DrawSphere(final GL gl, final GLU glu, final double radius) {
		// Draw sphere (possible styles: FILL, LINE, POINT).
		GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		glu.gluSphere(earth, radius, slices, stacks);
		glu.gluDeleteQuadric(earth);
	}

	public void DrawROI(final GL gl, final double x1, final double y1, final double x2, final double y2,
		final boolean z_fighting, final double maxEnvDim) {

		if ( z_fighting ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
			// Draw on top of everything
			gl.glPolygonOffset(0.0f, (float) -maxEnvDim);
			gl.glBegin(GL.GL_POLYGON);

			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glVertex3d(x2, -y1, 0.0f);

			gl.glVertex3d(x2, -y1, 0.0f);
			gl.glVertex3d(x2, -y2, 0.0f);

			gl.glVertex3d(x2, -y2, 0.0f);
			gl.glVertex3d(x1, -y2, 0.0f);

			gl.glVertex3d(x1, -y2, 0.0f);
			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glEnd();
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		} else {
			gl.glBegin(GL.GL_LINES);

			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glVertex3d(x2, -y1, 0.0f);

			gl.glVertex3d(x2, -y1, 0.0f);
			gl.glVertex3d(x2, -y2, 0.0f);

			gl.glVertex3d(x2, -y2, 0.0f);
			gl.glVertex3d(x1, -y2, 0.0f);

			gl.glVertex3d(x1, -y2, 0.0f);
			gl.glVertex3d(x1, -y1, 0.0f);
			gl.glEnd();
		}

	}

	// textured shape

	public void LoadTextureFromImage(final GL gl, final String textureFileName) {

		// Load textures from image
		try {
			// Use URL so that can read from JAR and disk file.
			BufferedImage image = ImageUtils.getInstance().getImageFromFile(textureFileName);

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
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);

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

	public void DrawTexture(final GL gl, final float width) {

		// WARNING: Be sure to have call LoadTextureFromImage() in the init method og the GLRenderer

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

	public void DrawTexturedSphere(final GL gl, final GLU glu) {

		// Apply texture.
		textures[currTextureFilter].enable();
		textures[currTextureFilter].bind();

		// Draw sphere (possible styles: FILL, LINE, POINT).
		GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricTexture(earth, true);
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
		final float radius = 6.378f;
		final int slices = 16;
		final int stacks = 16;
		glu.gluSphere(earth, radius, slices, stacks);
		glu.gluDeleteQuadric(earth);

	}

	public void DrawTexturedQuad(final GL gl, final float width) {

		// WARNING: Be sure to have call LoadTextureFromImage() in the init method og the GLRenderer

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

	public void DrawTexturedQuadWithNormal(final GL gl, final float width) {

		// WARNING: Be sure to have call LoadTextureFromImage() in the init method og the GLRenderer

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

	// Display List Shape ///////

	public void buildDisplayLists(final GL gl, final float size) {

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

	public void DrawTexturedDisplayList(final GL gl, final float size) {

		// WARNING: Be sure to call buildDisplayLists() in the init method of the GLRenderer

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

	// public void DrawColladaObject(String filename) {
	//
	// ColladaReaderXPath myColladaReader = new ColladaReaderXPath(filename);
	// try {
	// ColladaReaderXPath.GetObjectVertex();
	// } catch (XPathExpressionException e) {
	// e.printStackTrace();
	// }
	//
	// }

}
