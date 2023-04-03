/*******************************************************************************************************
 *
 * EventLayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.IExecutable;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by marilleau
 */

public class EventLayer extends AbstractLayer implements IEventLayerListener {

	static {
		DEBUG.OFF();
	}

	/** The execution scope. */
	IGraphicsScope executionScope;

	/** The listened event. */
	private int listenedEvent;

	/** The surface. */
	private IDisplaySurface surface;

	/** The event. */
	private String event;

	/**
	 * Instantiates a new event layer.
	 *
	 * @param layer
	 *            the layer
	 */
	public EventLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public void enableOn(final IDisplaySurface surface) {
		surface.addListener(this);
	}

	@Override
	public void disableOn(final IDisplaySurface surface) {
		super.disableOn(surface);
		surface.removeListener(this);
	}

	/**
	 * Gets the listening event.
	 *
	 * @param eventTypeName
	 *            the event type name
	 * @return the listening event
	 */
	private int getListeningEvent(final String eventTypeName) {
		return switch (eventTypeName) {
			case MouseEventLayerDelegate.MOUSE_DOWN -> MOUSE_PRESS;
			case MouseEventLayerDelegate.MOUSE_UP -> MOUSE_RELEASED;
			case MouseEventLayerDelegate.MOUSE_CLICKED -> MOUSE_CLICKED;
			case MouseEventLayerDelegate.MOUSE_MOVED -> MOUSE_MOVED;
			case MouseEventLayerDelegate.MOUSE_ENTERED -> MOUSE_ENTERED;
			case MouseEventLayerDelegate.MOUSE_EXITED -> MOUSE_EXITED;
			case MouseEventLayerDelegate.MOUSE_MENU -> MOUSE_MENU;
			case MouseEventLayerDelegate.MOUSE_DRAGGED -> MOUSE_DRAGGED;
			case KeyboardEventLayerDelegate.ARROW_DOWN -> ARROW_DOWN;
			case KeyboardEventLayerDelegate.ARROW_UP -> ARROW_UP;
			case KeyboardEventLayerDelegate.ARROW_LEFT -> ARROW_LEFT;
			case KeyboardEventLayerDelegate.ARROW_RIGHT -> ARROW_RIGHT;
			case KeyboardEventLayerDelegate.KEY_ESC -> KEY_ESC;
			case KeyboardEventLayerDelegate.KEY_PAGE_DOWN -> KEY_PAGE_DOWN;
			case KeyboardEventLayerDelegate.KEY_PAGE_UP -> KEY_PAGE_UP;
			case KeyboardEventLayerDelegate.KEY_ENTER -> KEY_RETURN;
			case KeyboardEventLayerDelegate.KEY_TAB -> KEY_TAB;

			default -> KEY_PRESSED;
		};

	}

	@Override
	public void firstLaunchOn(final IDisplaySurface surface) {
		super.firstLaunchOn(surface);
		this.surface = surface;
		final IExpression eventType = definition.getFacet(IKeyword.NAME);
		if (executionScope != null) { GAMA.releaseScope(executionScope); }
		executionScope = surface.getScope().copy("of event layer");

		// Evaluated in the display surface scope to gather variables defined in
		// there
		event = Cast.asString(surface.getScope(), eventType.value(surface.getScope()));
		listenedEvent = getListeningEvent(event);
		surface.addListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public String getType() { return "Event layer"; }

	// We explicitly translate by the origin of the surface
	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		if (xOnScreen == -1 && yOnScreen == -1) return new GamaPoint(0, 0);
		return g.getModelCoordinates();
	}

	// AD: Fix for Issue #1511
	@Override
	public boolean containsScreenPoint(final int x, final int y) {
		return false;
	}

	@Override
	public void mouseClicked(final int x, final int y, final int button) {
		if (MOUSE_CLICKED == listenedEvent && button == 1) { executeEvent(x, y); }
	}

	@Override
	public void mouseDown(final int x, final int y, final int button) {
		if (MOUSE_PRESS == listenedEvent && button == 1) { executeEvent(x, y); }
	}

	@Override
	public void mouseUp(final int x, final int y, final int button) {
		if (MOUSE_RELEASED == listenedEvent && button == 1) { executeEvent(x, y); }
	}

	@Override
	public void mouseMove(final int x, final int y) {
		if (MOUSE_MOVED == listenedEvent) { executeEvent(x, y); }
	}

	@Override
	public void mouseDrag(final int x, final int y, final int button) {
		if (MOUSE_DRAGGED == listenedEvent && button == 1) { executeEvent(x, y); }
	}

	@Override
	public void mouseEnter(final int x, final int y) {
		if (MOUSE_ENTERED == listenedEvent) { executeEvent(x, y); }
	}

	@Override
	public void mouseExit(final int x, final int y) {
		if (MOUSE_EXITED == listenedEvent) { executeEvent(x, y); }
	}

	@Override
	public void mouseMenu(final int x, final int y) {
		if (MOUSE_MENU == listenedEvent) { executeEvent(x, y); }
	}

	/**
	 * Execute event.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void executeEvent(final int x, final int y) {
		final IAgent agent = ((EventLayerStatement) definition).getExecuter(executionScope);
		if (agent == null) return;
		final IExecutable executer = ((EventLayerStatement) definition).getExecutable(executionScope);
		if (executer == null) return;
		final GamaPoint pp = getModelCoordinatesFrom(x, y, surface);
		if (pp == null) return;
		// DEBUG.OUT("Coordinates in env (before test)" + pp);
		// if (pp.getX() < 0 || pp.getY() < 0 || pp.getX() >= surface.getEnvWidth()
		// || pp.getY() >= surface.getEnvHeight()) {
		// if (MOUSE_EXITED != listenedEvent && MOUSE_ENTERED != listenedEvent) return;
		// }
		// DEBUG.OUT("Coordinates in env (after test)" + pp);
		GAMA.runAndUpdateAll(() -> executionScope.execute(executer, agent, null));

	}

	@Override
	public void keyPressed(final String c) {
		if (c.equals(event)) { executeEvent(-1, -1); }
	}

	@Override
	protected void privateDraw(final IGraphicsScope scope, final IGraphics g) throws GamaRuntimeException {}

	@Override
	public void draw(final IGraphicsScope scope, final IGraphics g) throws GamaRuntimeException {}

	@Override
	public Boolean isControllable() { return false; }

	/**
	 * Gets the event.
	 *
	 * @return the event
	 */
	public String getEvent() { return event; }

	@Override
	public void specialKeyPressed(final int keyCode) {
		if (keyCode == listenedEvent) { executeEvent(-1, -1); }
	}
}
