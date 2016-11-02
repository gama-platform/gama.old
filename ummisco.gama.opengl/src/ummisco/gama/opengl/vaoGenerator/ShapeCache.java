/*********************************************************************************************
 *
 * 'ShapeCache.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import java.util.HashMap;
import java.util.LinkedList;

public class ShapeCache {
	
	private static int MAX_SIZE = 20;
	
	private static HashMap<String,AbstractTransformer> mapPreloadedShapes = new HashMap<String,AbstractTransformer>();
	private static LinkedList<String> fifo = new LinkedList<String>();
	
	public static synchronized AbstractTransformer loadShape(String shapeName) {
		// put the shape in first place of the fifo
		fifo.remove(shapeName);
		fifo.addFirst(shapeName);
		return mapPreloadedShapes.get(shapeName);
	}
	
	public static synchronized void freedShapeCache() {
		while (fifo.size()>0) {
			fifo.remove();
		}
	}
	
	public static synchronized boolean isLoaded(String shapeName) {
		if (shapeName == null) {
			return false;
		}
		return mapPreloadedShapes.keySet().contains(shapeName);
	}
	
	public static synchronized void preloadShape(String shapeName, AbstractTransformer entity) {
		// if the cache is full, remove the shape used the less recently
		if (fifo.size() > MAX_SIZE) {
			String idx = fifo.removeLast();
			mapPreloadedShapes.remove(idx);
		}
		if (shapeName != null)
		{
			// the "point" type is a bit particular to handle, plus there is no need to
			// keep such a simple geometry in the cache.
			mapPreloadedShapes.put(shapeName, entity);
			fifo.addFirst(shapeName);
		}
	}

}
