package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.util.GamaColor;

public class GamaColorReducer {

	
	public float getR() {
		return r;
	}


	public void setR(float r) {
		this.r = r;
	}


	public float getG() {
		return g;
	}


	public void setG(float g) {
		this.g = g;
	}


	public float getB() {
		return b;
	}


	public void setB(float b) {
		this.b = b;
	}


	public float getA() {
		return a;
	}


	public void setA(float a) {
		this.a = a;
	}


	private float r;
	private float g;
	private float b;
	private float a;
	
	
	public GamaColorReducer(GamaColor c) {
		r = c.red();
		g = c.green();
		b = c.blue();
		a = c.alpha();
	}

	
	public Object constructObject() {
		return new GamaColor(r/255.0,g/255.0,b/255.0,a/255.0);
	}
	
	
	
	

}
