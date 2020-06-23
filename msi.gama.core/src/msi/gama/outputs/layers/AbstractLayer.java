/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.AbstractLayer.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
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
		if (definition != null) {
			setName(definition.getName());
		}
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
		if (!g.is2D() && !getData().isDynamic() && hasBeenDrawnOnce) { return; }
		if (g.isNotReadyToUpdate() && hasBeenDrawnOnce) { return; }
		getData().compute(scope, g);
		g.setOpacity(1-getData().getTransparency(scope));
		g.beginDrawingLayer(this);
		privateDraw(scope, g);
		g.endDrawingLayer(this);
		hasBeenDrawnOnce = true;
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
