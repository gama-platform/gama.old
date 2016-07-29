package ummisco.gama.webgl;

import java.awt.Color;
import java.util.List;

import msi.gama.outputs.LightPropertiesStructure;

/**
 * A simplified representation of a ModelScene
 * 
 * @author drogoul
 *
 */
public class SimpleScene {

	final List<SimpleLayer> layers;
	final List<LightPropertiesStructure> lights;
	final Color backgroundColor;

	public SimpleScene(final List<SimpleLayer> simpleLayers, final List<LightPropertiesStructure> lights, final Color backgroundColor) {
		this.layers = simpleLayers;
		this.lights = lights;
		this.backgroundColor = backgroundColor;
	}

	public List<SimpleLayer> getLayers() {
		return layers;
	}
	
	public List<LightPropertiesStructure> getLights() {
		return lights;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}

}
