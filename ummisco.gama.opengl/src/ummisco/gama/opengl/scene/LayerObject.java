/*********************************************************************************************
 *
 *
 * 'LayerObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import ummisco.gama.modernOpenGL.Light;
import ummisco.gama.modernOpenGL.Maths;
import ummisco.gama.modernOpenGL.VAOExtractor;
import ummisco.gama.modernOpenGL.shader.ShaderProgram;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.camera.ICamera;
import ummisco.gama.webgl.SimpleGeometryObject;
import ummisco.gama.webgl.SimpleLayer;

/**
 * Class LayerObject.
 *
 * @author drogoul
 * @since 3 mars 2014
 *
 */
public class LayerObject implements Iterable<GeometryObject> {

	GamaPoint offset = new GamaPoint();
	GamaPoint scale = new GamaPoint(1, 1, 1);
	Double alpha = 1d;
	final ILayer layer;
	volatile boolean isInvalid;
	volatile boolean overlay;
	volatile boolean locked;
	final JOGLRenderer renderer;
	final LinkedList<List<AbstractObject>> objects = new LinkedList();
	List<AbstractObject> currentList;
	Integer openGLListIndex;
	boolean isFading;
	
	
	
	
	
	boolean isInit = false;
	ShaderProgram shaderProgram;
	int[] vboHandles;
	static final int COLOR_IDX = 0;
	static final int VERTICES_IDX = 1;
	static final int IDX_BUFF_IDX = 2;
	static final int NORMAL_IDX = 3;
	
	ArrayList<ArrayList<float[]>> vbos = new ArrayList<ArrayList<float[]>>();
	
	private GL2 gl;
	
	private Matrix4f projectionMatrix;
	private Matrix4f transformationMatrix;
	
	
	
	
	

	public LayerObject(final JOGLRenderer renderer, final ILayer layer) {
		this.renderer = renderer;
		this.layer = layer;
		currentList = newCurrentList();
		objects.add(currentList);
	}

	private List newCurrentList() {
		return new CopyOnWriteArrayList();
	}

	private boolean isPickable() {
		return layer == null ? false : layer.isSelectable();
	}

	public void draw(final GL2 gl, final JOGLRenderer renderer) {
		if (isInvalid()) {
			return;
		}
		
//		if (this.renderer.data.isUseShader()) {
//			drawWithShader(gl, renderer);
//		}
//		else {
			drawWithoutShader(gl, renderer);
//		}
	}
	
	public void drawWithShader(final GL2 gl, final JOGLRenderer renderer) {
		if (!isInit) {
			initShader(gl);
			isInit=true;
		}
		
		for (final List<AbstractObject> list : objects) {
			for (final AbstractObject object : list) {
				if (object instanceof GeometryObject) {
					ArrayList<float[]> vao = new ArrayList<float[]>();
					
					float[] vertices = VAOExtractor.getObjectVertices(object);
					float[] colors = VAOExtractor.getObjectColors(object,vertices.length/3);
					float[] indices = VAOExtractor.getObjectIndexBuffer(object);
					
					vao.add(vertices);
					vao.add(colors);
					vao.add(indices);
					
					vbos.add(vao);
				}
			}
		}	
		
		if (vbos.size()>0)
		{
			// Clear screen
			gl.glClearColor(1, 0, 1, 0.5f);  // Purple
			gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT   );
		
			for (ArrayList<float[]> vbo : vbos) {
				float[] vtxPos = vbo.get(0);
				float[] vtxCol = vbo.get(1);
				float[] vtxIdxBuff = vbo.get(2);
				if (vtxPos.length > 2)
					newDraw(vtxPos,vtxCol,vtxIdxBuff);
			}
		}
		
		vbos.clear();
	}
	
	public void drawWithoutShader(final GL2 gl, final JOGLRenderer renderer) {
		
		if (overlay) {
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, renderer.data.getEnvWidth(), renderer.data.getEnvHeight(), 0.0, -1.0,
					renderer.getMaxEnvDim());
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			gl.glLoadIdentity();
		}
		try {
			gl.glPushMatrix();
			gl.glTranslated(offset.x, -offset.y, offset.z);
			gl.glScaled(scale.x, scale.y, scale.z);
			final boolean picking = renderer.getPickingState().isPicking() && isPickable();
			if (objects.size() == 0) {
				return;
			}
			renderer.setCurrentColor(gl, Color.white);
			if (picking) {
				drawPicking(gl);
				return;
			}
			Integer index = openGLListIndex;
			if (index == null) {
				index = gl.glGenLists(1);
				gl.glNewList(index, GL2.GL_COMPILE);
				double alpha = 0d;
				final int size = objects.size();
				final double delta = size == 0 ? 0 : 1d / size;
				for (final List<AbstractObject> list : objects) {
					alpha = alpha + delta;
					for (final AbstractObject object : list) {
						final ObjectDrawer drawer = renderer.getDrawerFor(object.getClass());
						if (isFading) {
							final double originalAlpha = object.getAlpha();
							object.setAlpha(originalAlpha * alpha);
							object.draw(gl, drawer, picking);
							object.setAlpha(originalAlpha);
						} else {
							object.draw(gl, drawer, picking);
						}
					}
				}
				gl.glEndList();
			}
			gl.glCallList(index);
			openGLListIndex = index;
		} finally {
			gl.glPopMatrix();
		}

		if (overlay) {
			// Making sure we can render 3d again
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		}
	}
	
	public void initShader(final GL2 gl) {
		this.gl = gl;
		
		createProjectionMatrix();
		
		shaderProgram = new ShaderProgram(gl);
		shaderProgram.start();
		shaderProgram.loadProjectionMatrix(projectionMatrix);
		shaderProgram.stop();

		vboHandles = new int[4];
		this.gl.glGenBuffers(4, vboHandles, 0);
	}
	
	private void createProjectionMatrix() {
		
		final int height = renderer.getDrawable().getSurfaceHeight();
		final double aspect = (double) renderer.getDrawable().getSurfaceWidth() / (double) (height == 0 ? 1 : height);
		final double maxDim = renderer.getMaxEnvDim();
		final double zNear = maxDim / 1000;
		final double zFar = maxDim*10;
		final double frustum_length = zFar - zNear;
		double fW, fH;
		//final double fovY = 45.0d;
		final double fovY = renderer.data.getCameralens();
		if (aspect > 1.0) {
			fH = FastMath.tan(fovY / 360 * Math.PI) * zNear;
			fW = fH * aspect;
		} else {
			fW = FastMath.tan(fovY / 360 * Math.PI) * zNear;
			fH = fW / aspect;
		}
		
		projectionMatrix = new Matrix4f();
		
		projectionMatrix.m00 = (float) (zNear / fW);
		projectionMatrix.m11 = (float) (zNear / fH);
		projectionMatrix.m22 = (float) -((zFar + zNear) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = (float) -((2 * zNear * zFar) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	private void newDraw(float[] vertices, float[] colors, float[] idxBuffer) {
		shaderProgram.start();
			
		transformationMatrix = Maths.createTransformationMatrix(new Vector3f(0,0,0), 0, 0, 0, 1);
		shaderProgram.loadTransformationMatrix(transformationMatrix);
		shaderProgram.loadViewMatrix(renderer.camera);
		
		Light light = new Light(new Vector3f(50,50,100),new Vector3f(1,1,1));
		shaderProgram.loadLight(light);
		
		shaderProgram.loadShineVariables(10.0f, 1.0f);
		
		float[][] newArraysWithSmoothShading = VAOExtractor.setSmoothShading(vertices,colors,idxBuffer,60f);
		vertices = newArraysWithSmoothShading[0];
		colors = newArraysWithSmoothShading[1];
		idxBuffer = newArraysWithSmoothShading[2];
		
		float[] normals = Maths.getNormals(vertices,idxBuffer/*VAOExtractor.getExtendedIndicesForRectangularFaces(idxBuffer)*/);


		// VERTICES POSITIONS BUFFER
		// Observe that the vertex data passed to glVertexAttribPointer must stay valid
		// through the OpenGL rendering lifecycle.
		// Therefore it is mandatory to allocate a NIO Direct buffer that stays pinned in memory
		// and thus can not get moved by the java garbage collector.
		// Also we need to keep a reference to the NIO Direct buffer around up until
		// we call glDisableVertexAttribArray first then will it be safe to garbage collect the memory.
		// I will here use the com.jogamp.common.nio.Buffers to quickly wrap the array in a Direct NIO buffer.
		FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
		// Select the VBO, GPU memory data, to use for vertices
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboHandles[VERTICES_IDX]);
		// transfer data to VBO, this perform the copy of data from CPU -> GPU memory
		int numBytes = vertices.length * 4;
		gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbVertices, GL.GL_STATIC_DRAW);
		fbVertices.rewind(); // It is OK to release CPU vertices memory after transfer to GPU
		// Associate Vertex attribute 0 with the last bound VBO
		gl.glVertexAttribPointer(ShaderProgram.POSITION_ATTRIBUTE_IDX /* the vertex attribute */, 3,
		                    GL2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
		                    0 /* The bound VBO data offset */);
		gl.glEnableVertexAttribArray(ShaderProgram.POSITION_ATTRIBUTE_IDX);
		
		// COLORS BUFFER
		FloatBuffer fbColors = Buffers.newDirectFloatBuffer(colors);
		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboHandles[COLOR_IDX]);
		numBytes = colors.length * 4;
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, fbColors, GL2.GL_STATIC_DRAW);
		fbColors.rewind(); // It is OK to release CPU color memory after transfer to GPU
		// Associate Vertex attribute 1 with the last bound VBO
		gl.glVertexAttribPointer(ShaderProgram.COLOR_ATTRIBUTE_IDX /* the vertex attribute */, 4 /* four positions used for each vertex */,
		                    GL2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
		                    0 /* The bound VBO data offset */);
		gl.glEnableVertexAttribArray(ShaderProgram.COLOR_ATTRIBUTE_IDX);
		
		// NORMAL BUFFER
		FloatBuffer fbNormal = Buffers.newDirectFloatBuffer(normals);
		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboHandles[NORMAL_IDX]);
		numBytes = normals.length * 4;
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, fbNormal, GL2.GL_STATIC_DRAW);
		fbNormal.rewind(); // It is OK to release CPU color memory after transfer to GPU
		// Associate Vertex attribute 1 with the last bound VBO
		gl.glVertexAttribPointer(ShaderProgram.NORMAL_ATTRIBUTE_IDX /* the vertex attribute */, 3 /* three positions used for each vertex */,
		                    GL2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
		                    0 /* The bound VBO data offset */);
		gl.glEnableVertexAttribArray(ShaderProgram.NORMAL_ATTRIBUTE_IDX);
		
		// INDEX BUFFER
		int[] intIdxBuff = new int[idxBuffer.length];
		for (int i = 0; i < idxBuffer.length ; i++) {
			intIdxBuff[i] = (int) idxBuffer[i];
		}
		IntBuffer ibIdxBuff = Buffers.newDirectIntBuffer(intIdxBuff);
		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vboHandles[IDX_BUFF_IDX]);
		numBytes = colors.length * 4;
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, numBytes, ibIdxBuff, GL2.GL_STATIC_DRAW);
		ibIdxBuff.rewind();

//		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, idxBuffer.length); //Draw the vertices as triangle
		gl.glDrawElements(GL2.GL_TRIANGLES, idxBuffer.length, GL2.GL_UNSIGNED_INT, 0);

		gl.glDisableVertexAttribArray(ShaderProgram.POSITION_ATTRIBUTE_IDX); // Allow release of vertex position memory
		gl.glDisableVertexAttribArray(ShaderProgram.COLOR_ATTRIBUTE_IDX); // Allow release of vertex color memory
		gl.glDisableVertexAttribArray(ShaderProgram.NORMAL_ATTRIBUTE_IDX); // Allow release of vertex normal memory
		
		shaderProgram.stop();
	}

	private void drawPicking(final GL2 gl) {
		gl.glPushMatrix();
		gl.glInitNames();
		gl.glPushName(0);
		double alpha = 0d;
		final int size = objects.size();
		final double delta = size == 0 ? 0 : 1d / size;
		for (final List<AbstractObject> list : objects) {
			alpha = alpha + delta;
			for (final AbstractObject object : list) {
				final ObjectDrawer drawer = renderer.getDrawerFor(object.getClass());
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

	public boolean isStatic() {
		if (layer == null) {
			return true;
		}
		final Boolean isDynamic = layer.isDynamic();
		return isDynamic == null ? false : !isDynamic;
	}

	public void setAlpha(final Double a) {
		alpha = a;
	}

	public GamaPoint getOffset() {
		return offset;
	}

	public void setOffset(final GamaPoint offset) {
		this.offset = offset;
	}

	public GamaPoint getScale() {
		return scale;
	}

	public void setScale(final GamaPoint scale) {
		this.scale = scale;
	}

	public void addString(final String string, final DrawingAttributes attributes) {
		currentList.add(new StringObject(string, attributes, this));
	}

	public void addFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		currentList.add(new ResourceObject(file, attributes, this));
	}

	public void addImage(final GamaImageFile img, final DrawingAttributes attributes) {
		currentList.add(new ImageObject(img, attributes, this));
	}

	public void addImage(final BufferedImage img, final DrawingAttributes attributes) {
		currentList.add(new ImageObject(img, attributes, this));
	}

	public void addField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		currentList.add(new FieldObject(fieldValues, attributes, this));
	}

	public void addGeometry(final Geometry geometry, final DrawingAttributes attributes) {
		currentList.add(new GeometryObject(geometry, attributes, this));
	}

	public int getOrder() {
		return layer == null ? 0 : layer.getOrder();
	}

	private int getTrace() {
		if (layer == null) {
			return 0;
		}
		final Integer trace = layer.getTrace();
		return trace == null ? 0 : trace;
	}

	private boolean getFading() {
		if (layer == null) {
			return false;
		}
		final Boolean fading = layer.getFading();
		return fading == null ? false : fading;
	}

	public void clear(final GL gl) {
		final int sizeLimit = getTrace();
		final boolean fading = getFading();

		isFading = fading;

		final int size = objects.size();
		for (int i = 0, n = size - sizeLimit; i < n; i++) {
			final List<AbstractObject> list = objects.poll();
			for (final AbstractObject t : list) {
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
	public Iterator<GeometryObject> iterator() {
		return Iterators.filter(currentList.iterator(), GeometryObject.class);
	}

	public boolean isInvalid() {
		return isInvalid;
	}

	public void invalidate() {
		isInvalid = true;
	}

	public boolean hasTrace() {
		return getTrace() > 0;
	}

	public void preload(final GL2 gl) {
		if (objects.size() == 0) {
			return;
		}
		for (final AbstractObject object : currentList) {
			object.preload(gl, renderer);
		}
	}

	public void setOverlay(final boolean b) {
		overlay = b;
	}

	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	public SimpleLayer toSimpleLayer() {
		final List<SimpleGeometryObject> geom = new ArrayList();
		for (final GeometryObject object : Iterables.filter(currentList, GeometryObject.class)) {
			geom.add(object.toSimpleGeometryObject());
		}
		return new SimpleLayer(offset, scale, alpha, geom);
	}

}
