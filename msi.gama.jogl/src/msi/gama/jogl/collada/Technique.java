package msi.gama.jogl.collada;

public class Technique {
	
	private String m_sid= null;
	private Lambert m_lambert = null;
	
	
	public String getSid() {
		return m_sid;
	}
	public void setSid(String m_sid) {
		this.m_sid = m_sid;
	}
	public Lambert getLambert() {
		return m_lambert;
	}
	public void setLambert(Lambert m_lambert) {
		this.m_lambert = m_lambert;
	}

}
