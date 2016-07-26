package ummisco.gama.opengl.vaoGenerator;

import java.util.ArrayList;

import ummisco.gama.modernOpenGL.shader.ShaderProgram;

public class ModernLayerStructure {
	
	public int[] vboHandles;
	public ArrayList<Integer> listOfVAOUsed = new ArrayList<Integer>();
	public ArrayList<ShaderProgram> shaderList = new ArrayList<ShaderProgram>();
	public int[] typeOfDrawing;
	
	public ModernLayerStructure() {
	}

}
