package msi.gama.jogl.collada;

import java.util.ArrayList;

public class VisualScenesLibrary {

	private ArrayList<VisualScene> m_visualScenes = new ArrayList<VisualScene>();
	private String m_ID = null;
	private String m_name = null;

	public ArrayList<VisualScene> getVisualScenes() {
		return m_visualScenes;
	}

	public void setVisualScenes(ArrayList<VisualScene> m_visualScenes) {
		this.m_visualScenes = m_visualScenes;
	}

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
}
