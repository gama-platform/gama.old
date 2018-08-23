package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.ExecutionResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Cast;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.draw.ShapeDrawingAttributes;

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

	@Override
	public void privateDraw(final IScope s, final IGraphics gr) throws GamaRuntimeException {
		final GamaColor borderColor = getData().drawLines() ? getData().getLineColor() : null;
		final IExecutable aspect = sc -> {
			final IAgent agent = sc.getAgent();
			final IGraphics g = sc.getGraphics();
			try {
				if (agent == sc.getGui().getHighlightedAgent()) {
					g.beginHighlight();
				}
				final GamaColor color = Cast.asColor(sc, agent.getDirectVarValue(sc, IKeyword.COLOR));
				final IShape ag = agent.getGeometry();
				final IShape ag2 = ag.copy(sc);
				final ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(ag2, agent, color, borderColor);
				final Rectangle2D r = g.drawShape(ag2.getInnerGeometry(), attributes);
				return r;
			} catch (final GamaRuntimeException e) {
				// cf. Issue 1052: exceptions are not thrown, just displayed
				e.printStackTrace();
			} finally {
				g.endHighlight();
			}
			return null;
		};

		for (final IAgent a : getData().getAgentsToDisplay()) {
			if (a != null) {
				final ExecutionResult result = s.execute(aspect, a, null);
				final Rectangle2D r = (Rectangle2D) result.getValue();
				if (r != null) {
					shapes.put(a, r);
				}
			}
		}

	}

}
