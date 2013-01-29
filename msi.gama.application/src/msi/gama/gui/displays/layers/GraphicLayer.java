package msi.gama.gui.displays.layers;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GraphicLayer extends AbstractLayer {

	protected GraphicLayer(final double env_width, final double env_height,
		final ILayerStatement layer, final IGraphics dg) {
		super(env_width, env_height, layer, dg);
	}

	@Override
	protected void privateDrawDisplay(final IGraphics g) throws GamaRuntimeException {
		IScope scope = GAMA.obtainNewScope();
		if ( scope != null ) {
			scope.setGraphics(g);
			((GraphicLayerStatement) definition).getAspect().draw(scope, scope.getWorldScope());
		}
	}

	@Override
	protected String getType() {
		return "graphics";
	}

}
