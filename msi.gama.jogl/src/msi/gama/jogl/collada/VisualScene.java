/*********************************************************************************************
 * 
 *
 * 'VisualScene.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

public class VisualScene {
	
	private String m_ID = null;
	private Node m_node = null;

	public Node getNode() {
		return m_node;
	}

	public void setNode(Node m_node) {
		this.m_node = m_node;
	}

	public String getID() {
		return m_ID;
	}

	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}
	

}
