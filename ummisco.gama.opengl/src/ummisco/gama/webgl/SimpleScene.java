package ummisco.gama.webgl;

import java.util.List;

/**
 * A simplified representation of a ModelScene
 * 
 * @author drogoul
 *
 */
public class SimpleScene {

	final List<SimpleLayer> layers;

	public SimpleScene(final List<SimpleLayer> simpleLayers) {
		layers = simpleLayers;
	}

	public List<SimpleLayer> getLayers() {
		return layers;
	}

}
