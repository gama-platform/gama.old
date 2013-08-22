package msi.gama.gui.displays.layers;

import msi.gama.common.interfaces.*;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GraphicLayer extends AbstractLayer {

	protected GraphicLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		Object[] result = new Object[1];
		scope.execute(((GraphicLayerStatement) definition).getAspect(), scope.getSimulationScope(), null, result);
	}

	@Override
	public String getType() {
		return IKeyword.GRAPHICS;
	}

}
