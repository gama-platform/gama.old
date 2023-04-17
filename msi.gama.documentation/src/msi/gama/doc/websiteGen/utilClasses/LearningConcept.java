/*******************************************************************************************************
 *
 * LearningConcept.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc.websiteGen.utilClasses;

import java.util.List;

/**
 * The Class LearningConcept.
 */
public class LearningConcept {
	
	/** The m id. */
	public String m_id;
	
	/** The m name. */
	public String m_name;
	
	/** The m description. */
	public String m_description;
	
	/** The m prerequisites list. */
	public List<String> m_prerequisitesList;
	
	/** The m x pos. */
	public float m_xPos;
	
	/** The m y pos. */
	public float m_yPos;
	
	/**
	 * Instantiates a new learning concept.
	 *
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 * @param xPos the x pos
	 * @param yPos the y pos
	 * @param prerequisitesList the prerequisites list
	 */
	public LearningConcept(String id, String name, String description, float xPos, float yPos, List<String> prerequisitesList) {
		m_id = id;
		m_name = name;
		m_description = description;
		m_xPos = xPos;
		m_yPos = yPos;
		m_prerequisitesList = prerequisitesList;
	}
}
