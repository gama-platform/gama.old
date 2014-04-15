/*********************************************************************************************
 * 
 *
 * 'EffectsLibrary.java', in plugin 'msi.gama.jogl2', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.collada;

import java.util.ArrayList;

public class EffectsLibrary {

	private ArrayList<Effect> m_effects = new ArrayList<Effect>();

	public ArrayList<Effect> getEffects() {
		return m_effects;
	}

	public void setEffects(ArrayList<Effect> m_effects) {
		this.m_effects = m_effects;
	}
}
