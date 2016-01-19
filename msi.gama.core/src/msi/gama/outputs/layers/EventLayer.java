/*********************************************************************************************
 *
 *
 * 'EventLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;
import msi.gaml.types.Types;

/**
 * Written by marilleau
 */

public class EventLayer extends AbstractLayer {

	@Override
	protected void setPositionAndSize(final IDisplayLayerBox box, final IGraphics g) {
		super.setPositionAndSize(box, g);
	}

	EventListener listener;
	private final String pointArg, listArg;
	IScope scope;

	public EventLayer(final ILayerStatement layer) {
		super(layer);
		IExpression exp = layer.getFacet(EventLayerStatement.defaultPointArg);
		pointArg = exp == null ? null : exp.literalValue();
		exp = layer.getFacet(EventLayerStatement.defaultListArg);
		listArg = exp == null ? null : exp.literalValue();
	}

	@Override
	public void enableOn(final IDisplaySurface surface) {
		surface.addListener(listener);
	}

	@Override
	public void disableOn(final IDisplaySurface surface) {
		super.disableOn(surface);
		surface.removeListener(listener);
	}

	@Override
	public void firstLaunchOn(final IDisplaySurface surface) {
		super.firstLaunchOn(surface);
		final IExpression eventType = definition.getFacet(IKeyword.NAME);
		final IExpression actionName = definition.getFacet(IKeyword.ACTION);
		scope = surface.getDisplayScope().copy();

		String currentEvent = Cast.asString(scope, eventType.value(scope));
		String currentAction = Cast.asString(scope, actionName.value(scope));

		listener = new EventListener(surface, currentEvent, currentAction);
		surface.addListener(listener);
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

	// We explicitely translate by the origin of the surface
	@Override
	public ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		return g.getModelCoordinates();
	}

	private class EventListener implements IEventLayerListener {

		private final static int MOUSE_PRESS = 0;
		private final static int MOUSE_RELEASED = 1;
		private final static int MOUSE_CLICKED = 2;
		private final static int KEY_PRESSED = 3;

		private final int listenedEvent;
		private final IStatement.WithArgs executer;
		private final IDisplaySurface surface;
		private final String event;

		public EventListener(final IDisplaySurface display, final String event, final String action) {
			this.event = event;
			listenedEvent = getListeningEvent(event);
			IAgent a = display.getDisplayScope().getSimulationScope();
			if ( a == null ) {
				a = display.getDisplayScope().getExperiment();
			}
			executer = a.getSpecies().getAction(action);
			surface = display;
		}

		public void dispose() {
			surface.removeListener(this);
		}

		public int getListeningEvent(final String eventTypeName) {
			if ( eventTypeName.equals(IKeyword.MOUSE_DOWN) ) { return MOUSE_PRESS; }
			if ( eventTypeName.equals(IKeyword.MOUSE_UP) ) { return MOUSE_RELEASED; }
			if ( eventTypeName.equals(IKeyword.MOUSE_CLICKED) ) { return MOUSE_CLICKED; }
			return KEY_PRESSED;
		}

		@Override
		public void mouseClicked(final int x, final int y, final int button) {
			if ( MOUSE_CLICKED == listenedEvent && button == 1 ) {
				executeEvent(x, y);
			}
		}

		@Override
		public void mouseDown(final int x, final int y, final int button) {
			if ( MOUSE_PRESS == listenedEvent && button == 1 ) {
				executeEvent(x, y);
			}
		}

		@Override
		public void mouseUp(final int x, final int y, final int button) {
			if ( MOUSE_RELEASED == listenedEvent && button == 1 ) {
				executeEvent(x, y);
			}

		}

		private void executeEvent(final int x, final int y) {
			if ( executer == null ) { return; }
			final ILocation pp = getModelCoordinatesFrom(x, y, surface);
			if ( pp == null ) { return; }
			if ( pp.getX() < 0 || pp.getY() < 0 || pp.getX() >= surface.getEnvWidth() ||
				pp.getY() >= surface.getEnvHeight() ) { return; }
			final Arguments args = new Arguments();
			if ( x > -1 && y > -1 ) {

				final IContainer<Integer, IAgent> agentset =
					GamaListFactory.createWithoutCasting(Types.AGENT, surface.selectAgent(x, y));
				if ( pointArg != null ) {
					args.put(pointArg, ConstantExpressionDescription.create(new GamaPoint(pp.getX(), pp.getY())));
				}
				if ( listArg != null ) {
					args.put(listArg, ConstantExpressionDescription.create(agentset));
				}
			}

			surface.runAndUpdate(new Runnable() {

				@Override
				public void run() {
					executer.setRuntimeArgs(args);
					executer.executeOn(scope);
				}
			});

		}

		/**
		 * Method keyPressed()
		 * @see msi.gama.outputs.layers.IEventLayerListener#keyPressed(java.lang.Character)
		 */
		@Override
		public void keyPressed(final String c) {
			if ( c.equals(event) ) {
				executeEvent(-1, -1);
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
