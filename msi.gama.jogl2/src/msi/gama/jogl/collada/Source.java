/*********************************************************************************************
 * 
 *
 * 'Source.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

public class Source 
{
	private String m_ID = null;
	private Float_Array m_floatArray = null;
	private Name_Array m_nameArray = null;
	private TechniqueCommon m_techniqueCommon = null;

	
	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public String getID() {
		return m_ID;
	}
	
	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}

	public Float_Array getFloatArray() {
		return m_floatArray;
	}

	public void setFloatArray(Float_Array m_floatArray) {
		this.m_floatArray = m_floatArray;
	}

	public Name_Array getNameArray() {
		return m_nameArray;
	}

	public void setNameArray(Name_Array m_nameArray) {
		this.m_nameArray = m_nameArray;
	}

	public TechniqueCommon getTechniqueCommon() {
		return m_techniqueCommon;
	}

	public void setTechniqueCommon(TechniqueCommon m_techniqueCommon) {
		this.m_techniqueCommon = m_techniqueCommon;
	}
	
	
}
