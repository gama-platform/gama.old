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
