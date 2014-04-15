/*********************************************************************************************
 * 
 *
 * 'Lambert.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

public class Lambert {
	private Diffuse m_diffuse = null;

	public Diffuse getDiffuse() {
		return m_diffuse;
	}

	public void setDiffuse(Diffuse m_diffuse) {
		this.m_diffuse = m_diffuse;
	}
}
