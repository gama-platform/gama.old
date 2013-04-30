package msi.gama.gui.displays.layers;

import msi.gama.common.interfaces.*;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GraphicLayer extends AbstractLayer {

	protected GraphicLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected void privateDrawDisplay(final IGraphics g) throws GamaRuntimeException {
		IScope scope = GAMA.obtainNewScope();
		try {
			if ( scope != null ) {
				scope.setGraphics(g);
				((GraphicLayerStatement) definition).getAspect().draw(scope, scope.getSimulationScope());
			}
		} finally {
			GAMA.releaseScope(scope);
		}
	}

	@Override
	protected String getType() {
		return IKeyword.GRAPHICS;
	}

}
