/*********************************************************************************************
 * 
 *
 * 'Effect.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Effect {
	
	private String m_ID = null;
	private ArrayList<ProfileCommon> m_profiles = new ArrayList<ProfileCommon>();
	
	
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
