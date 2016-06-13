/*********************************************************************************************
 *
 *
 * 'SceneObjects.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.google.common.collect.Iterables;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import msi.gama.precompiler.GamlAnnotations.display;
import ummisco.gama.modernOpenGL.Camera;
import ummisco.gama.modernOpenGL.Maths;
import ummisco.gama.modernOpenGL.ShaderProgram;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.camera.ICamera;

public class SceneObjects<T extends AbstractObject> implements ISceneObjects<T> {
	
	boolean isInit = false;
	ShaderProgram shaderProgram;
	ICamera camera;
	int[] vboHandles;
	static final int COLOR_IDX = 1;
	static final int VERTICES_IDX = 0;
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000f;
	
	private List<Integer> vbos = new ArrayList<Integer> ();
	
	private double t0 = System.currentTimeMillis();
	private double theta=0;
	private double s;
	
	private GL2 gl;
	
	private Matrix4f projectionMatrix;
	private Matrix4f transformationMatrix;

	public static class Static<T extends AbstractObject> extends SceneObjects<T> {

		Static(final ObjectDrawer<T> drawer) {
			super(drawer);
		}

		@Override
		public void add(final T object) {
			super.add(object);
			openGLListIndex = null;
		}

		@Override
		public void clear(final GL gl, final int traceSize, final boolean fading) {
		}
	}

	final ObjectDrawer<T> drawer;
	final LinkedList<List<T>> objects = new LinkedList();
	List<T> currentList;
	Integer openGLListIndex;
	boolean isFading;

	SceneObjects(final ObjectDrawer<T> drawer) {
		this.drawer = drawer;
		currentList = newCurrentList();
		objects.add(currentList);
	}

	private List newCurrentList() {
		return new CopyOnWriteArrayList();
	}

	@Override
	public void clear(final GL gl, final int sizeLimit, final boolean fading) {
		isFading = fading;

		final int size = objects.size();
		for (int i = 0, n = size - sizeLimit; i < n; i++) {
			final List<T> list = objects.poll();
			for (final T t : list) {
				t.dispose(gl);
			}
		}

		currentList = newCurrentList();
		objects.offer(currentList);
		final Integer index = openGLListIndex;
		if (index != null) {
			gl.getGL2().glDeleteLists(index, 1);
			openGLListIndex = null;
		}
	}

	@Override
	public void add(final T object) {
		currentList.add(object);
	}

	@Override
	public void remove(final T object) {
		currentList.remove(object);
	}

	@Override
	public Iterable<T> getObjects() {
		return Iterables.concat(objects);
	}

	private void drawPicking(final GL2 gl, final JOGLRenderer renderer) {
		gl.glPushMatrix();
		gl.glInitNames();
		gl.glPushName(0);
		double alpha = 0d;
		final int size = objects.size();
		final double delta = size == 0 ? 0 : 1d / size;
		for (final List<T> list : objects) {
			alpha = alpha + delta;
			for (final T object : list) {
				if (isFading) {
					final double originalAlpha = object.getAlpha();
					object.setAlpha(originalAlpha * alpha);
					object.draw(gl, drawer, true);
					object.setAlpha(originalAlpha);
				} else {
					object.draw(gl, drawer, true);
				}
			}
		}

		gl.glPopName();
		gl.glPopMatrix();

	}
	
	private void createProjectionMatrix() {
		float aspectRatio = drawer.getRenderer().getDisplayWidth() / drawer.getRenderer().getDisplayHeight();
		float y_scale = (1f / (float)Math.tan(Math.toRadians(FOV/2f))) * aspectRatio;
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		
		projectionMatrix.setIdentity();
	}
	
	private void createTransformationMatrix() {
		// create entity matrix
		transformationMatrix = new Matrix4f();
		transformationMatrix.setIdentity();
	}
	
	public void initShader(final GL2 gl) {
		this.gl = gl;
		
		camera = drawer.getRenderer().camera;
		
		createProjectionMatrix();
		createTransformationMatrix();
		
		shaderProgram = new ShaderProgram(gl);
		shaderProgram.start();
		shaderProgram.loadProjectionMatrix(projectionMatrix);
		shaderProgram.stop();

		
		vboHandles = new int[2];
		this.gl.glGenBuffers(2, vboHandles, 0);
	}
	
	private void storeDataInAttributeList(int attributeNumber,int coordinateSize, float[] data) {

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboHandles[attributeNumber]);
		FloatBuffer fbuff = storeDataInFloatBuffer(data);
		
		int numBytes = data.length * 4;
		
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, fbuff, GL2.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(attributeNumber, coordinateSize, GL2.GL_FLOAT, false, 0, 0);

	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = FloatBuffer.allocate(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	@Override
	public void draw(final GL2 gl, final boolean picking) {
		final JOGLRenderer renderer = drawer.getRenderer();
		if (objects.size() == 0) {
			return;
		}
		renderer.setCurrentColor(gl, Color.white);
		// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 1.0f, 1.0f);
		if (picking) {
			drawPicking(gl, renderer);
			return;
		}		
		
		/////////////////////////////////////////////////////////////////////
		
		if (!isInit) {
			initShader(gl);
			isInit=true;
		}
		
		//////: DISPLAY PART
		
	    double t1 = System.currentTimeMillis();
        theta += (t1-t0)*0.005f;
        t0 = t1;
        s = /*Math.sin(*/theta/*)*/;
		
		shaderProgram.start();
		
		// Clear screen
		gl.glClearColor(1, 0, 1, 0.5f);  // Purple
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT   );
		
		transformationMatrix = Maths.createTransformationMatrix(new Vector3f(0,0,0), (float) -s, 0, 0, 1);
		shaderProgram.loadTransformationMatrix(transformationMatrix);
		shaderProgram.loadViewMatrix(camera);


		float[] vertices = {  0.0f,  1.0f, 0.0f, //Top
					-1.0f, -1.0f, 0.0f, //Bottom Left
					1.0f, -1.0f, 0.0f  //Bottom Right
        };
		
		storeDataInAttributeList(VERTICES_IDX,3,vertices);

		float[] colors = {    1.0f, 0.0f, 0.0f, 1.0f, //Top color (red)
		0.0f, 0.0f, 0.0f, 1.0f, //Bottom Left color (black)
		1.0f, 1.0f, 0.0f, 0.9f  //Bottom Right color (yellow) with 10% transparence
		};
		
		storeDataInAttributeList(COLOR_IDX,4,colors);

		gl.glEnableVertexAttribArray(VERTICES_IDX);
		gl.glEnableVertexAttribArray(COLOR_IDX);

		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3); //Draw the vertices as triangle
//		gl.glDrawElements(GL2.GL_TRIANGLES, 3, GL2.GL_UNSIGNED_INT, 0);

		gl.glDisableVertexAttribArray(VERTICES_IDX); // Allow release of vertex position memory
		gl.glDisableVertexAttribArray(COLOR_IDX); // Allow release of vertex color memory
		
		shaderProgram.stop();
		
		/////////////////////////////////////////////////////////////

//		Integer index = openGLListIndex;
//		if (index == null) {
//			index = gl.glGenLists(1);
//			gl.glNewList(index, GL2.GL_COMPILE);
//			double alpha = 0d;
//			final int size = objects.size();
//			final double delta = size == 0 ? 0 : 1d / size;
//			for (final List<T> list : objects) {
//				alpha = alpha + delta;
//				for (final T object : list) {
//					if (isFading) {
//						final double originalAlpha = object.getAlpha();
//						object.setAlpha(originalAlpha * alpha);
//						object.draw(gl, drawer, picking);
//						object.setAlpha(originalAlpha);
//					} else {
//						object.draw(gl, drawer, picking);
//					}
//				}
//			}
//			gl.glEndList();
//		}
//		gl.glCallList(index);
//		openGLListIndex = index;

	}

	@Override
	public void preload(final GL2 gl) {
		final JOGLRenderer renderer = drawer.getRenderer();
		if (objects.size() == 0) {
			return;
		}
		for (final T object : objects.get(0)) {
			object.preload(gl, renderer);
		}
	}

}