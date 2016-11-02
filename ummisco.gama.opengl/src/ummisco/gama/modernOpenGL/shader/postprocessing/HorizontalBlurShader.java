/*********************************************************************************************
 *
 * 'HorizontalBlurShader.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.shader.postprocessing;

import com.jogamp.opengl.GL2;

public class HorizontalBlurShader extends AbstractPostprocessingShader {

	private static String VERTEX_FILE = "horizontalBlurVertexShader";
	private static String FRAGMENT_FILE = "blurFragmentShader";
	
	private int location_targetWidth;
	
	public HorizontalBlurShader(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	public HorizontalBlurShader(HorizontalBlurShader shader) {
		super(shader.gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_targetWidth = getUniformLocation("targetWidth");
	}

	public void loadTargetWidth(float width){
		super.loadFloat(location_targetWidth, width);
	}
}
