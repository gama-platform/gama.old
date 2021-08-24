/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.AbstractLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
public abstract class AbstractLayer implements ILayer {

	protected ILayerStatement definition;
	private String name;
	boolean hasBeenDrawnOnce;
	private final ILayerData data;

	public AbstractLayer(final ILayerStatement layer) {
		definition = layer;
		if (definition != null) { setName(definition.getName()); }
		data = createData();
	}

	@Override
	public ILayerStatement getDefinition() {
		return definition;
	}

	@Override
	public ILayerData getData() {
		return data;
	}

	protected ILayerData createData() {
		return new LayerData(definition);
	}

	@Override
	public void forceRedrawingOnce() {
		hasBeenDrawnOnce = false;
	}

	@Override
	public void draw(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if (shouldNotDraw(g)) return;
		getData().compute(scope, g);
		g.setAlpha(1 - getData().getTransparency(scope));
		g.beginDrawingLayer(this);
		privateDraw(scope, g);
		g.endDrawingLayer(this);
		hasBeenDrawnOnce = true;
	}

	/**
	 * A layer should not be drawn if it has been drawn once and either (1) it is considered as static (and we are in
	 * OpenGL), or (2) the graphics environment is not ready to update (we skip one frame)
	 *
	 * @param g
	 *            the IGraphics instance on which we draw
	 * @return true if ok to draw, false otherwise
	 */
	protected boolean shouldNotDraw(final IGraphics g) {
		return hasBeenDrawnOnce && (!g.is2D() && !getData().isDynamic() || g.isNotReadyToUpdate());
	}

	protected abstract void privateDraw(IScope scope, final IGraphics g) throws GamaRuntimeException;

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void setName(final String name) {
		this.name = name;
	}

}
