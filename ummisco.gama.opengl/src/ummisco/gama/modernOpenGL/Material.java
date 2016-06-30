package ummisco.gama.modernOpenGL;

public class Material {
	
	float shineDamper = 5;
	float reflectivity = 1;
	
	public Material(float shineDamper, float reflectivity) {
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}
	
	public boolean equalsTo(Material otherMaterial) {
		if ( (shineDamper == otherMaterial.getShineDamper())
				&& (reflectivity == otherMaterial.getReflectivity())) {
			return true;
		}
		else return false;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

}
