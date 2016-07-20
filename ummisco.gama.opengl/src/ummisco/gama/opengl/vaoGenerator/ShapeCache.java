package ummisco.gama.opengl.vaoGenerator;

import java.util.HashMap;

public class ShapeCache {
	
	private static HashMap<String,ManyFacedShape> mapPreloadedShapes = new HashMap<String,ManyFacedShape>();
	
	public static ManyFacedShape loadShape(String shapeName) {
		return mapPreloadedShapes.get(shapeName);
	}
	
	public static boolean isLoaded(String shapeName) {
		return mapPreloadedShapes.keySet().contains(shapeName);
	}
	
	public static void preloadShape(String shapeName, ManyFacedShape entity) {
		if (!shapeName.startsWith("POINT"))
			// the "point" type is a bit particular to handle, plus there is no need to
			// keep such a simple geometry in the cache.
			mapPreloadedShapes.put(shapeName, entity);
	}

}
