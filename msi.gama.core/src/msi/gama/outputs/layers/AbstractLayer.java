/*******************************************************************************************************
 *
 * AbstractLayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
public abstract class AbstractLayer implements ILayer {

	static {
		DEBUG.ON();
	}

	/** The definition. */
	protected ILayerStatement definition;

	/** The name. */
	private String name;

	/** The has been drawn once. */
	volatile boolean hasBeenDrawnOnce;

	/** The counter. */
	volatile int counter;

	/** The data. */
	private final ILayerData data;

	/**
	 * Instantiates a new abstract layer.
	 *
	 * @param layer
	 *            the layer
	 */
	public AbstractLayer(final ILayerStatement layer) {
		definition = layer;
		if (definition != null) { setName(definition.getName()); }
		data = createData();
	}

	@Override
	public ILayerStatement getDefinition() { return definition; }

	@Override
	public ILayerData getData() { return data; }

	/**
	 * Creates the data.
	 *
	 * @return the i layer data
	 */
	protected ILayerData createData() {
		return new LayerData(definition);
	}

	@Override
	public void forceRedrawingOnce() {
		hasBeenDrawnOnce = false;
	}

	/**
	 * Sets the has been drawn once.
	 */
	public void setHasBeenDrawnOnce() {
		hasBeenDrawnOnce = true;
	}

	/**
	 * Draw.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public void draw(final IGraphicsScope scope, final IGraphics g) throws GamaRuntimeException {

		// if (g.isNotReadyToUpdate()) {
		// DEBUG.OUT("Does not draw" + this.getName() + " because the renderer is not ready to update");
		// return;
		// }

		if (!data.isVisible() || !g.is2D() && !data.isDynamic() && hasBeenDrawnOnce) // DEBUG.OUT("Does not draw" +
																						// this.getName() + "
			// because it is static and it has been drawn already "
			// + counter + " times : " + hasBeenDrawnOnce);
			return;
		g.setAlpha(1 - data.getTransparency(scope));
		g.beginDrawingLayer(this);
		privateDraw(scope, g);
		g.endDrawingLayer(this);
		// Necessary to handle Issue #3392
		if (!hasBeenDrawnOnce) {
			hasBeenDrawnOnce = true;
			// counter++;
			// hasBeenDrawnOnce = scope.getExperiment().getSpecies().isAutorun() ? counter > 10 : counter == 1;
		}
	}

	/**
	 * A layer should not be drawn if it has been drawn once and either (1) it is considered as static (and we are in
	 * OpenGL), or (2) the graphics environment is not ready to update (we skip one frame)
	 *
	 * @param g
	 *            the IGraphics instance on which we draw
	 * @return false if ok to draw, true otherwise
	 */
	protected boolean shouldDraw(final IGraphics g) {
		if (!getData().isVisible() || g.isNotReadyToUpdate()) return false;
		if (getData().isDynamic() || hasBeenDrawnOnce) return true;
		return false;
	}

	/**
	 * Private draw.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract void privateDraw(IGraphicsScope scope, final IGraphics g) throws GamaRuntimeException;

	@Override
	public final String getName() { return name; }

	@Override
	public final void setName(final String name) { this.name = name; }

}
