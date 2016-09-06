package ummisco.gama.webgl;

import java.util.List;

import msi.gama.outputs.LightPropertiesStructure;

/**
 * A simplified representation of a ModelScene
 * 
 * @author drogoul
 *
 */
public class SimpleScene {

	final int displayID;
	final double envWidth;
	final double envHeight;
	final List<SimpleLayer> layers;
	final List<LightPropertiesStructure> lights;
	final int[] backgroundColor;

	public SimpleScene(final List<SimpleLayer> simpleLayers, final List<LightPropertiesStructure> lights, 
			final int[] backgroundColor, final double envWidth, final double envHeight, final int displayID) {
		this.layers = simpleLayers;
		this.lights = lights;
		this.backgroundColor = backgroundColor;
		this.envHeight = envHeight;
		this.envWidth = envWidth;
		this.displayID = displayID;
	}

	public List<SimpleLayer> getLayers() {
		return layers;
	}
	
	public List<LightPropertiesStructure> getLights() {
		return lights;
	}
	
	public int[] getBackgroundColor() {
		return backgroundColor;
	}

}
