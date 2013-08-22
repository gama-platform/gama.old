package msi.gama.gui.displays.layers;

import msi.gama.common.interfaces.*;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class LegendLayer extends AbstractLayer {

	protected LegendLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		// FIXME Deactivated for the moment as this solution is not satisfying
		// ((LegendLayerStatement) definition).getAspect().drawOverlay(scope, scope.getSimulationScope());
	}

	@Override
	public String getType() {
		return IKeyword.LEGENDS;
	}

}
