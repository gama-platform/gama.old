package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;

import ummisco.gama.modernOpenGL.shader.AbstractShader;

public class ModernLayerStructure {
	
	public int[] vboHandles;
	public ArrayList<Integer> listOfVAOUsed = new ArrayList<Integer>();
	public ArrayList<AbstractShader> shaderList = new ArrayList<AbstractShader>();
	public int[] typeOfDrawing;
	
	public ModernLayerStructure() {
	}

}
