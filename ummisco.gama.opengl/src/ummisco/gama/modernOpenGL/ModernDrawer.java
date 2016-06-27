package ummisco.gama.modernOpenGL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import ummisco.gama.modernOpenGL.shader.ShaderProgram;
import ummisco.gama.opengl.ModernRenderer;
import ummisco.gama.opengl.vaoGenerator.TransformationMatrix;

public class ModernDrawer {
	
	private Matrix4f transformationMatrix;

	ShaderProgram shaderProgram;
	int[] vboHandles;
	ArrayList<Entity> entities = new ArrayList<Entity>();
	final ModernRenderer renderer;
	GL2 gl;

	static final int COLOR_IDX = 0;
	static final int VERTICES_IDX = 1;
	static final int IDX_BUFF_IDX = 2;
	static final int NORMAL_IDX = 3;
	
	public ModernDrawer(ModernRenderer renderer, GL2 gl, ShaderProgram shaderProgram) {
		this.renderer = renderer;
		this.gl = gl;
		this.shaderProgram = shaderProgram;
		
		vboHandles = new int[4];
		this.gl.glGenBuffers(4, vboHandles, 0);
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	public void clearEntityList() {
		entities.clear();
	}
	
	public void draw() {
		// Clear screen
		gl.glClearColor(1, 0, 1, 0.5f);  // Purple
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT   );
		
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do

		shaderProgram.start();
			
		transformationMatrix = TransformationMatrix.createTransformationMatrix(new Vector3f(0,0,0), 0, 0, 0, 1);
		shaderProgram.loadTransformationMatrix(transformationMatrix);
		
		Light light = new Light(new Vector3f(50,50,100),new Vector3f(1,1,1));
		shaderProgram.loadLight(light);
		
		for (Entity entity : entities) {
			shaderProgram.loadShineVariables(10.0f, 1.0f);
			
			float[] vertices = entity.getVertices();
			float[] colors = entity.getColors();
			float[] idxBuffer = entity.getIndices();
			float[] normals = entity.getNormals();
	
	
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
		}
		
		shaderProgram.stop();
	}

}
