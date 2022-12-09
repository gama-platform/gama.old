/*******************************************************************************************************
 *
 * ITesselator.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl;

import com.jogamp.opengl.glu.GLUtessellatorCallback;

/**
 * The Interface ITesselator.
 */
public interface ITesselator extends GLUtessellatorCallback {

	/**
	 * Begin.
	 *
	 * @param type the type
	 */
	@Override
	default void begin(final int type) {
		beginDrawing(type);
	}

	/**
	 * Edge flag.
	 *
	 * @param boundaryEdge the boundary edge
	 */
	@Override
	default void edgeFlag(final boolean boundaryEdge) {}

	/**
	 * Vertex.
	 *
	 * @param vertexData the vertex data
	 */
	@Override
	default void vertex(final Object vertexData) {
		final double[] v = (double[]) vertexData;
		drawVertex(0, v[0], v[1], v[2]);
	}

	/**
	 * Draw vertex.
	 *
	 * @param i the i
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	void drawVertex(final int i, final double x, final double y, final double z);

	/**
	 * End.
	 */
	@Override
	default void end() {
		endDrawing();
	}

	/**
	 * End drawing.
	 */
	default void endDrawing() {}

	/**
	 * Begin drawing.
	 *
	 * @param type the type
	 */
	default void beginDrawing(final int type) {}

	/**
	 * Error.
	 *
	 * @param errnum the errnum
	 */
	@Override
	default void error(final int errnum) {}

	/**
	 * Combine.
	 *
	 * @param coords the coords
	 * @param data the data
	 * @param weight the weight
	 * @param outData the out data
	 */
	@Override
	default void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {}

	/**
	 * Begin data.
	 *
	 * @param type the type
	 * @param polygonData the polygon data
	 */
	@Override
	default void beginData(final int type, final Object polygonData) {}

	/**
	 * Edge flag data.
	 *
	 * @param boundaryEdge the boundary edge
	 * @param polygonData the polygon data
	 */
	@Override
	default void edgeFlagData(final boolean boundaryEdge, final Object polygonData) {}

	/**
	 * Vertex data.
	 *
	 * @param vertexData the vertex data
	 * @param polygonData the polygon data
	 */
	@Override
	default void vertexData(final Object vertexData, final Object polygonData) {}

	/**
	 * End data.
	 *
	 * @param polygonData the polygon data
	 */
	@Override
	default void endData(final Object polygonData) {}

	/**
	 * Error data.
	 *
	 * @param errnum the errnum
	 * @param polygonData the polygon data
	 */
	@Override
	default void errorData(final int errnum, final Object polygonData) {}

	/**
	 * Combine data.
	 *
	 * @param coords the coords
	 * @param data the data
	 * @param weight the weight
	 * @param outData the out data
	 * @param polygonData the polygon data
	 */
	@Override
	default void combineData(final double[] coords, final Object[] data, final float[] weight, final Object[] outData,
			final Object polygonData) {}

}
