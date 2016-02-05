package msi.gama.doc.websiteGen;

import java.util.List;

class LearningConcept {
	public String m_id;
	public String m_name;
	public String m_description;
	public List<String> m_prerequisitesList;
	public float m_xPos;
	public float m_yPos;
	
	public LearningConcept(String id, String name, String description, float xPos, float yPos, List<String> prerequisitesList) {
		m_id = id;
		m_name = name;
		m_description = description;
		m_xPos = xPos;
		m_yPos = yPos;
		m_prerequisitesList = prerequisitesList;
	}
}
