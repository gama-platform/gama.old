package ummisco.gama.modernOpenGL;

public class Material {
	
	double shineDamper;
	double reflectivity;
	
	public Material(double d, double e) {
		this.shineDamper = d;
		this.reflectivity = e;
	}
	
	public boolean equalsTo(Material otherMaterial) {
		if ( (shineDamper == otherMaterial.getShineDamper())
				&& (reflectivity == otherMaterial.getReflectivity())) {
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

}
