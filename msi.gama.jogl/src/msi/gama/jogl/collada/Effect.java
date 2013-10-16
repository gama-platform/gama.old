package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Effect {
	
	private String m_ID = null;
	private ArrayList<ProfileCommon> m_profiles = null;
	
	
	public ArrayList<ProfileCommon> getProfiles() {
		return m_profiles;
	}
	public void setProfiles(ArrayList<ProfileCommon> m_profiles) {
		this.m_profiles = m_profiles;
	}
	public String getID() {
		return m_ID;
	}
	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}

}
