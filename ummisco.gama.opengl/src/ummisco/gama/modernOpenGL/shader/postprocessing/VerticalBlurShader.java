/*********************************************************************************************
 *
 * 'VerticalBlurShader.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.shader.postprocessing;

import com.jogamp.opengl.GL2;

public class VerticalBlurShader extends AbstractPostprocessingShader{

	private static String VERTEX_FILE = "verticalBlurVertexShader";
	private static String FRAGMENT_FILE = "blurFragmentShader";
	
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
	
	public void loadTargetHeight(float height){
		super.loadFloat(location_targetHeight, height);
	}
}
