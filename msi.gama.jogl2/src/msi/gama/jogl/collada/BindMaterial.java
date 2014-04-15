/*********************************************************************************************
 * 
 *
 * 'BindMaterial.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

public class BindMaterial {

	private TechniqueCommon m_techniqueCommon = null;

	public TechniqueCommon getTechniqueCommon() {
		return m_techniqueCommon;
	}

	public void setTechniqueCommon(TechniqueCommon m_techniqueCommon) {
		this.m_techniqueCommon = m_techniqueCommon;
	}
}
