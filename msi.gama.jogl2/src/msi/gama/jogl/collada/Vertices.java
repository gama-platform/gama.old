/*********************************************************************************************
 * 
 *
 * 'Vertices.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Vertices 
{
	private String m_ID = null;
	private ArrayList<Input> m_inputs = new ArrayList<Input>();
	
	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public String getID() {
		return m_ID;
	}

	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}

	public ArrayList<Input> getInputs() {
		return m_inputs;
	}

	public void setInputs(ArrayList<Input> m_inputs) {
		this.m_inputs = m_inputs;
	}
	
}
