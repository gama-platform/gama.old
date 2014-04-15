/*********************************************************************************************
 * 
 *
 * 'BindVertexInput.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

public class BindVertexInput {
	
	private String m_semantic = null;
	private String m_input_semantic = null;
	private String m_input_set = null;
	
	
	public String getInput_semantic() {
		return m_input_semantic;
	}

	public void setInput_semantic(String m_input_semantic) {
		this.m_input_semantic = m_input_semantic;
	}

	public String getInput_set() {
		return m_input_set;
	}

	public void setInput_set(String m_input_set) {
		this.m_input_set = m_input_set;
	}

	public String getSemantic() {
		return m_semantic;
	}

	public void setSemantic(String m_semantic) {
		this.m_semantic = m_semantic;
	}
	
	
}
