package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.ExecutionResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.statements.AspectStatement;
import msi.gaml.statements.IExecutable;

public class GridAgentLayer extends AgentLayer {

	public GridAgentLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected ILayerData createData() {
		return new GridLayerData(definition);
	}

	@Override
	public GridLayerData getData() {
		return (GridLayerData) super.getData();
	}
	//
	// @Override
	// public Collection<IAgent> getAgentsToDisplay() {
	// return getData().getAgentsToDisplay();
	// }

	@Override
	public void privateDraw(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		final IExecutable aspect = AspectStatement.DEFAULT_ASPECT;
		final GamaColor previous = AspectStatement.borderColor;
		AspectStatement.borderColor = getData().getLineColor();

		for (final IAgent a : getData().getAgentsToDisplay()) {
			if (a != null) {
				final ExecutionResult result = scope.execute(aspect, a, null);
				final Rectangle2D r = (Rectangle2D) result.getValue();
				if (r != null) {
					shapes.put(a, r);
				}
			}
		}

		AspectStatement.borderColor = previous;

	}

}
