/*********************************************************************************************
 * 
 *
 * 'MaterialsLibrary.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class MaterialsLibrary {
	private ArrayList<Material> m_materials = new ArrayList<Material>();

	public ArrayList<Material> getMaterials() {
		return m_materials;
	}

	public void setMaterials(ArrayList<Material> m_materials) {
		this.m_materials = m_materials;
	}
}
