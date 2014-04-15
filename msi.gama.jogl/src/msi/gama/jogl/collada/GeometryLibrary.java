/*********************************************************************************************
 * 
 *
 * 'GeometryLibrary.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class GeometryLibrary {
	
	private ArrayList<Geometry> m_geometries = new ArrayList<Geometry>();

	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public ArrayList<Geometry> getGeometries() {
		return m_geometries;
	}

	public void setGeometries(ArrayList<Geometry> m_geometries) {
		this.m_geometries = m_geometries;
	}

}
