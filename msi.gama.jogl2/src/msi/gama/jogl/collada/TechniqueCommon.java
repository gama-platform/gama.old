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
