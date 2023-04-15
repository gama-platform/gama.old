/*******************************************************************************************************
 *
 * Topic.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc.websiteGen.utilClasses;

import java.util.List;

/**
 * The Class Topic.
 */
public class Topic {
	
	/** The m id. */
	public String m_id;
	
	/** The m name. */
	public String m_name;
	
	/** The m associated learning concept list. */
	public List<String> m_associatedLearningConceptList;
	
	/** The m x pos. */
	public float m_xPos;
	
	/** The m y pos. */
	public float m_yPos;
	
	/** The m x pos big hallow. */
	public float m_xPosBigHallow;
	
	/** The m y pos big hallow. */
	public float m_yPosBigHallow;
	
	/** The m size big hallow. */
	public float m_sizeBigHallow;
	
	/** The m color. */
	public String m_color;
	
	/**
	 * Instantiates a new topic.
	 *
	 * @param id the id
	 * @param name the name
	 * @param xPos the x pos
	 * @param yPos the y pos
	 * @param xPosBigHallow the x pos big hallow
	 * @param yPosBigHallow the y pos big hallow
	 * @param sizeBigHallow the size big hallow
	 * @param associatedLearningConceptList the associated learning concept list
	 * @param color the color
	 */
	public Topic(String id, String name, float xPos, float yPos, 
			float xPosBigHallow, float yPosBigHallow, float sizeBigHallow,
			List<String> associatedLearningConceptList, String color) {
		m_id = id;
		m_name = name;
		m_xPos = xPos;
		m_yPos = yPos;
		m_xPosBigHallow = xPosBigHallow;
		m_yPosBigHallow = yPosBigHallow;
		m_sizeBigHallow = sizeBigHallow;
		m_associatedLearningConceptList = associatedLearningConceptList;
		m_color = color;
	}
}