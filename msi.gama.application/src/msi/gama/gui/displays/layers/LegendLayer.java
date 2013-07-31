package msi.gama.gui.displays.layers;

import msi.gama.common.interfaces.*;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class LegendLayer extends AbstractLayer {

	protected LegendLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected void privateDrawDisplay(IScope scope, final IGraphics g) throws GamaRuntimeException {
		((LegendLayerStatement) definition).getAspect().drawOverlay(scope, scope.getSimulationScope());
	}

	@Override
	protected String getType() {
		return IKeyword.LEGENDS;
	}

}
	