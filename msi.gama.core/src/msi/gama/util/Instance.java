package msi.gama.util;

import org.apache.commons.math3.stat.clustering.EuclideanDoublePoint;

public class Instance extends EuclideanDoublePoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id;
	public Instance(int id, double[] point) {
		super(point);
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
}