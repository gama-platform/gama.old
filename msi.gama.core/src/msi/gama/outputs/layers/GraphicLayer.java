/*******************************************************************************************************
 *
 * GraphicLayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class GraphicLayer.
 */
public class GraphicLayer extends AbstractLayer {

	/**
	 * Instantiates a new graphic layer.
	 *
	 * @param layer
	 *            the layer
	 */
	public GraphicLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected FramedLayerData createData() {
		return new FramedLayerData(definition);
	}

	@Override
	protected void privateDraw(final IGraphicsScope scope, final IGraphics g) throws GamaRuntimeException {
		final IAgent agent = scope.getAgent();
		scope.execute(((GraphicLayerStatement) definition).getAspect(), agent, null);
	}

	@Override
	public String getType() { return IKeyword.GRAPHICS; }

	// Just a trial to make sure that graphics + chart produce not proportional results.
	@Override
	public boolean stayProportional() {
		return false;
	}
}
