package ummisco.gama.modernOpenGL.shader.postprocessing;

import com.jogamp.opengl.GL2;

public class VerticalBlurShader extends AbstractPostprocessingShader{

	private static String VERTEX_FILE = "verticalBlurVertex.txt";
	private static String FRAGMENT_FILE = "blurFragment.txt";
	
	private int location_targetHeight;
	
	public VerticalBlurShader(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	public VerticalBlurShader(VerticalBlurShader shader) {
		super(shader.gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_targetHeight = getUniformLocation("targetHeight");
	}
	
	public void loadTargetWidth(float width){
		super.loadFloat(location_targetHeight, width);
	}
}
