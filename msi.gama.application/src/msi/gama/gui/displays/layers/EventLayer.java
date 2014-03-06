/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import java.awt.event.*;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;

/**
 * Written by marilleau
 */

public class EventLayer extends AbstractLayer {

	EventListener listener;

	public EventLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public void enableOn(final IDisplaySurface surface) {
		surface.addMouseListener(listener);
	}

	@Override
	public void disableOn(final IDisplaySurface surface) {
		surface.removeMouseListener(listener);
	}

	@Override
	public void firstLaunchOn(final IDisplaySurface surface) {
		super.firstLaunchOn(surface);
		final IExpression eventType = definition.getFacet(IKeyword.NAME);
		final IExpression actionName = definition.getFacet(IKeyword.ACTION);

		String currentMouseEvent = GAMA.run(new InScope<String>() {

			@Override
			public String run(final IScope scope) {
				return Cast.asString(scope, eventType.value(scope));
			}
		});

		String currentAction = GAMA.run(new InScope<String>() {

			@Override
			public String run(final IScope scope) {
				return Cast.asString(scope, actionName.value(scope));
			}
		});

		listener = new EventListener(surface, currentMouseEvent, currentAction);
		surface.addMouseListener(listener);
	}

	@Override
	public void dispose() {
		super.dispose();
		listener.dispose();
	}

	@Override
	public String getType() {
		return "Event layer";
	}

	private class EventListener implements MouseListener {

		private final static int MOUSE_PRESS = 0;
		private final static int MOUSE_RELEASED = 1;
		private final static int MOUSE_CLICKED = 2;

		private final int listenedEvent;
		private final IStatement.WithArgs executer;
		private final IDisplaySurface surface;

		public EventListener(final IDisplaySurface display, final String event, final String action) {
			listenedEvent = getListeningEvent(event);
			executer = GAMA.getModel().getAction(action);
			surface = display;
		}

		public void dispose() {
			surface.removeMouseListener(this);
		}

		public int getListeningEvent(final String eventTypeName) {
			if ( eventTypeName.equals(IKeyword.MOUSE_DOWN) ) { return MOUSE_PRESS; }
			if ( eventTypeName.equals(IKeyword.MOUSE_UP) ) { return MOUSE_RELEASED; }
			if ( eventTypeName.equals(IKeyword.MOUSE_CLICKED) ) { return MOUSE_CLICKED; }
			return -1;
		}

		@Override
		public void mouseClicked(final MouseEvent arg0) {}

		@Override
		public void mouseEntered(final MouseEvent arg0) {}

		@Override
		public void mouseExited(final MouseEvent arg0) {}

		@Override
		public void mousePressed(final MouseEvent arg0) {
			if ( MOUSE_PRESS == listenedEvent && arg0.getButton() == MouseEvent.BUTTON1 ) {
				executeEvent(arg0);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent arg0) {
			if ( MOUSE_RELEASED == listenedEvent && arg0.getButton() == MouseEvent.BUTTON1 ) {
				executeEvent(arg0);
			}
		}

		private IList<IAgent> selectAgent(final int x, final int y) {
			int xc = x - surface.getOriginX();
			int yc = y - surface.getOriginY();
			IList<IAgent> result = new GamaList<IAgent>();
			final List<ILayer> layers = surface.getManager().getLayersIntersecting(xc, yc);
			for ( ILayer layer : layers ) {
				Set<IAgent> agents = layer.collectAgentsAt(xc, yc, surface);
				if ( !agents.isEmpty() ) {
					result.addAll(agents);
				}
			}
			return result;
		}

		private void executeEvent(final MouseEvent arg0) {
			final GamaPoint pp = getModelCoordinatesFrom(arg0.getPoint().x, arg0.getPoint().y, surface);
			if ( pp.x < 0 || pp.getY() < 0 || pp.x >= surface.getEnvWidth() || pp.y >= surface.getEnvHeight() ) { return; }
			final Arguments args = new Arguments();
			final IList<IAgent> agentset = selectAgent(arg0.getX(), arg0.getY());
			args.put("location", ConstantExpressionDescription.create(new GamaPoint(pp.x, pp.y)));
			args.put("selected_agents", ConstantExpressionDescription.create(new GamaList(agentset)));
			executer.setRuntimeArgs(args);
			GAMA.run(new GAMA.InScope.Void() {

				@Override
				public void process(final IScope scope) {
					executer.executeOn(scope);
				}
			});
			if ( surface.isPaused() || GAMA.isPaused() ) {
				surface.forceUpdateDisplay();
			}
		}
	}

	@Override
	protected void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {}

	@Override
	public void drawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if ( definition != null ) {
			definition.getBox().compute(scope);
			setPositionAndSize(definition.getBox(), g);
		}
	}

}
