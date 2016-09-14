package ummisco.gama.modernOpenGL.shader.postprocessing;

import com.jogamp.opengl.GL2;

public class InverseColorShader extends AbstractPostprocessingShader{

	private static String VERTEX_FILE = "inverseColorVertex";
	private static String FRAGMENT_FILE = "inverseColorFragment";
	
	public InverseColorShader(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	public InverseColorShader(InverseColorShader shader) {
		super(shader.gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
	}
}
