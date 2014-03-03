package msi.gama.jogl.collada;

public class InstanceGeometry {
	
	private String m_url = null;
	private BindMaterial m_binMaterial = null;
	
	
	public String getUrl() {
		return m_url;
	}
	public void setUrl(String m_url) {
		this.m_url = m_url;
	}
	public BindMaterial getBinMaterial() {
		return m_binMaterial;
	}
	public void setBinMaterial(BindMaterial m_binMaterial) {
		this.m_binMaterial = m_binMaterial;
	}

}
