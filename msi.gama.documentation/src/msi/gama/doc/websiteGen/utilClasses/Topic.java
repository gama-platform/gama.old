package msi.gama.doc.websiteGen.utilClasses;

import java.util.List;

public class Topic {
	public String m_id;
	public String m_name;
	public List<String> m_associatedLearningConceptList;
	public float m_xPos;
	public float m_yPos;
	public float m_xPosBigHallow;
	public float m_yPosBigHallow;
	public float m_sizeBigHallow;
	public String m_color;
	
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