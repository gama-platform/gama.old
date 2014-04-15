/*********************************************************************************************
 * 
 *
 * 'Node.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Node {
	
	private String m_name = null;
	private ArrayList<InstanceGeometry> m_instanceGeometries = new ArrayList<InstanceGeometry>();
	
	public String getName() {
		return m_name;
	}
	public void setName(String m_name) {
		this.m_name = m_name;
	}
	public ArrayList<InstanceGeometry> getInstanceGeometry() {
		return m_instanceGeometries;
	}
	public void setInstanceGeometry(ArrayList<InstanceGeometry> m_instanceGeometries) {
		this.m_instanceGeometries = m_instanceGeometries;
	}
		
}
