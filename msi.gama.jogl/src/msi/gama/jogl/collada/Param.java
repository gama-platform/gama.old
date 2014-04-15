/*********************************************************************************************
 * 
 *
 * 'Param.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

public class Param 
{
	private String m_name = null;
	private String m_type = null;

	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public String getName() {
		return m_name;
	}
	
	public void setName(String m_name) {
		this.m_name = m_name;
	}

	public String getType() {
		return m_type;
	}

	public void setType(String m_type) {
		this.m_type = m_type;
	}
}
