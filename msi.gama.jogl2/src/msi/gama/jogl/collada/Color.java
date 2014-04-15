/*********************************************************************************************
 * 
 *
 * 'Color.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;


public class Color {
	private ArrayList<Float> m_color = new ArrayList<Float>();

	public ArrayList<Float> getColor() {
		return m_color;
	}

	public void setColor(ArrayList<Float> m_color) {
		this.m_color = m_color;
	}
}
