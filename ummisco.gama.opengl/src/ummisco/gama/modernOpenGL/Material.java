/*********************************************************************************************
 *
 * 'Material.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL;

public class Material {
	
	double shineDamper;
	double reflectivity;
	boolean useLight = true;
	
	public Material(double d, double r) {
		this.shineDamper = d;
		this.reflectivity = r;
	}
	
	public Material(double d, double r, boolean useLight) {
		this.shineDamper = d;
		this.reflectivity = r;
		this.useLight = useLight;
	}
	
	public void disableLight() {
		useLight = false;
	}
	
	public boolean equalsTo(Material otherMaterial) {
		if ( (shineDamper == otherMaterial.getShineDamper())
				&& (reflectivity == otherMaterial.getReflectivity())
				&& (useLight == otherMaterial.useLight())) {
			return true;
		}
		else return false;
	}

	public double getShineDamper() {
		return shineDamper;
	}

	public double getReflectivity() {
		return reflectivity;
	}
	
	public boolean useLight() {
		return useLight;
	}

}
