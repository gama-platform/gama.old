/*********************************************************************************************
 * 
 *
 * 'TechniqueCommon.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class TechniqueCommon 
{
	private Accessor m_accessor = null;
	private ArrayList<InstanceMaterial> m_instanceMaterials = new ArrayList<InstanceMaterial>();

	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public Accessor getAccessor() {
		return m_accessor;
	}

	public void setAccessor(Accessor m_accessor) {
		this.m_accessor = m_accessor;
	}

	public ArrayList<InstanceMaterial> getInstanceMaterial() {
		return m_instanceMaterials;
	}

	public void setInstanceMaterial(ArrayList<InstanceMaterial> m_instanceMaterial) {
		this.m_instanceMaterials = m_instanceMaterial;
	}
}
