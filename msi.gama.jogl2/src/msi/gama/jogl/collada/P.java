package msi.gama.jogl.collada;

import java.util.ArrayList;

public class P 
{
	private int m_count = -1;
	private ArrayList<Integer> m_indices = new ArrayList<Integer>();
	
	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public int getCount() {
		return m_count;
	}

	public void setCount(int m_count) {
		this.m_count = m_count;
	}

	public ArrayList<Integer> getIndices() {
		return m_indices;
	}

	public void setIndices(ArrayList<Integer> m_indices) {
		this.m_indices = m_indices;
	}

}
