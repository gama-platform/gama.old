package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Accessor {
	
	private String m_source = null;
	private int m_count = -1;
	private int m_stride = -1;
	private ArrayList<Param> m_params = new ArrayList<Param>();

	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public String getSource() {
		return m_source;
	}
	
	public void setSource(String m_source) {
		this.m_source = m_source;
	}
	
	public int getCount() {
		return m_count;
	}

	public void setCount(int m_count) {
		this.m_count = m_count;
	}

	public int getStride() {
		return m_stride;
	}

	public void setStride(int m_stride) {
		this.m_stride = m_stride;
	}

	public ArrayList<Param> getParams() {
		return m_params;
	}

	public void setParams(ArrayList<Param> m_params) {
		this.m_params = m_params;
	}
	
}
