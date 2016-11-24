/*********************************************************************************************
 *
 * 'KeystoneShaderProgram.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.shader.postprocessing;

import com.jogamp.opengl.GL2;

public class KeystoneShaderProgram extends AbstractPostprocessingShader {
	
	private static String VERTEX_FILE = "keystoneVertexShader";		
	private static String FRAGMENT_FILE = "keystoneFragmentShader";
	
	public KeystoneShaderProgram(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	public KeystoneShaderProgram(KeystoneShaderProgram shader) {
		super(shader.gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords3D");
	}
}
