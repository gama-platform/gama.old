package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Name_Array 
{
	private int m_count = -1;
	private String m_ID = null;
	private ArrayList<String> m_names = new ArrayList<String>();
	
	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public int getCount() {
		return m_count;
	}
	
	public void setCount(int m_count) {
		this.m_count = m_count;
	}

	public String getID() {
		return m_ID;
	}

	public void setID(String m_ID) {
		this.m_ID = m_ID;
	}

	public ArrayList<String> getNames() {
		return m_names;
	}

	public void setNames(ArrayList<String> m_names) {
		this.m_names = m_names;
	}

}
