package ummisco.gama.opengl;

import com.jogamp.opengl.glu.GLUtessellatorCallback;

public interface Tesselator extends GLUtessellatorCallback {

	@Override
	default void begin(final int type) {
		beginDrawing(type);
	}

	@Override
	default void edgeFlag(final boolean boundaryEdge) {}

	@Override
	default void vertex(final Object vertexData) {
		final double[] v = (double[]) vertexData;
		drawVertex(0, v[0], v[1], v[2]);
	}

	public void drawVertex(final int i, final double x, final double y, final double z);

	@Override
	default void end() {
		endDrawing();
	}

	public void endDrawing();

	public void beginDrawing(int type);

	@Override
	default void error(final int errnum) {}

	@Override
	default void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {}

	@Override
	default void beginData(final int type, final Object polygonData) {}

	@Override
	default void edgeFlagData(final boolean boundaryEdge, final Object polygonData) {}

	@Override
	default void vertexData(final Object vertexData, final Object polygonData) {}

	@Override
	default void endData(final Object polygonData) {}

	@Override
	default void errorData(final int errnum, final Object polygonData) {}

	@Override
	default void combineData(final double[] coords, final Object[] data, final float[] weight, final Object[] outData,
			final Object polygonData) {}

}
