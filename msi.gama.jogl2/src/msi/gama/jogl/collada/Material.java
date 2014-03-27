package msi.gama.jogl.collada;

public class Material {

	private String m_ID = null;
	private String m_name = null;
	private InstanceEffect m_instanceEffect = null;
	
	public String getID() {
		return m_ID;
	}

	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String m_name) {
		this.m_name = m_name;
	}

	public InstanceEffect getInstanceEffect() {
		return m_instanceEffect;
	}

	public void setInstanceEffect(InstanceEffect m_instanceEffect) {
		this.m_instanceEffect = m_instanceEffect;
	}
}
