/*********************************************************************************************
 * 
 *
 * 'Float_Array.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Float_Array 
{
	private int m_count = -1;
	private String m_ID = null;
	private ArrayList<Float> m_floats = new ArrayList<Float>();
	
	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public int getCount() {
		return m_count;
	}
	
	public void setCount(int m_count) {
		this.m_count = m_count;
	}

	public String getID() {
		return m_ID;
	}

	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}

	public ArrayList<Float> getFloats() {
		return m_floats;
	}

	public void setFloats(ArrayList<Float> m_floats) {
		this.m_floats = m_floats;
	}
	
}
