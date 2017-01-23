/*********************************************************************************************
 *
 * 'GraphicLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GraphicLayer extends AbstractLayer {

	protected GraphicLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		final IAgent agent = scope.getAgent();
		scope.execute(((GraphicLayerStatement) definition).getAspect(), agent, null);
	}

	@Override
	public String getType() {
		return IKeyword.GRAPHICS;
	}

}
