/*********************************************************************************************
 *
 * 'ModernLayerStructure.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;

import ummisco.gama.modernOpenGL.shader.AbstractShader;

public class ModernLayerStructure {
	
	public int[] vboHandles;
	public ArrayList<AbstractShader> shaderList = new ArrayList<AbstractShader>();
	
	public ModernLayerStructure() {
	}

}
