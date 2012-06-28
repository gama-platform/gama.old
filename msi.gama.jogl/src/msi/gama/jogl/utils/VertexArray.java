package msi.gama.jogl.utils;

import java.nio.FloatBuffer;

import com.sun.opengl.util.BufferUtil;



//A vertex has xyz (location) and uv (for texture)
public class VertexArray {
	public FloatBuffer[] tmpVerticesBuf;
	public int size;
	
	public VertexArray(int nbVertex){
		tmpVerticesBuf=new FloatBuffer[nbVertex];
		size=nbVertex;
	}
}