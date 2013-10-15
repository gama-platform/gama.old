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
