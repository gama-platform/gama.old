package ummisco.gama.opengl.vaoGenerator;

import java.util.HashMap;
import java.util.LinkedList;

public class ShapeCache {
	
	public static int MAX_SIZE = 20;
	
	private static HashMap<String,ManyFacedShape> mapPreloadedShapes = new HashMap<String,ManyFacedShape>();
	private static LinkedList<String> fifo = new LinkedList<String>();
	
	public static ManyFacedShape loadShape(String shapeName) {
		// put the shape in first place of the fifo
		fifo.remove(shapeName);
		fifo.addFirst(shapeName);
		return mapPreloadedShapes.get(shapeName);
	}
	
	public static boolean isLoaded(String shapeName) {
		return mapPreloadedShapes.keySet().contains(shapeName);
	}
	
	public static void preloadShape(String shapeName, ManyFacedShape entity) {
		// if the cache is full, remove the shape used the less recently
		if (fifo.size() > MAX_SIZE) {
			String idx = fifo.removeLast();
			mapPreloadedShapes.remove(idx);
		}
		if (!shapeName.startsWith("POINT"))
		{
			// the "point" type is a bit particular to handle, plus there is no need to
			// keep such a simple geometry in the cache.
			mapPreloadedShapes.put(shapeName, entity);
			fifo.addFirst(shapeName);
		}
	}

}
