/*********************************************************************************************
 * 
 *
 * 'Geometry.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

public class Geometry {
	
	private Mesh m_mesh = null;
	private String m_name = null;
	private String m_ID = null;
	
	
	/**
	 * Constructor
	 * @param mesh
	 */
	public Geometry(Mesh mesh)
	{
		this.m_mesh = mesh;
	}
	/**
	 * Empty constructor
	 */
	public Geometry() {
	}

	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public Mesh getMesh() {
		return m_mesh;
	}

	public void setMesh(Mesh m_mesh) {
		this.m_mesh = m_mesh;
	}
	public String getName() {
		return m_name;
	}
	public void setName(String m_name) {
		this.m_name = m_name;
	}
	public String getID() {
		return m_ID;
	}
	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}

}
