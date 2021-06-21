/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.EventLayer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.IExecutable;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by marilleau
 */

public class EventLayer extends AbstractLayer implements IEventLayerListener {

	private final static int MOUSE_PRESS = 0;
	private final static int MOUSE_RELEASED = 1;
	private final static int MOUSE_CLICKED = 2;
	private final static int MOUSE_MOVED = 4;
	private final static int MOUSE_ENTERED = 5;
	private final static int MOUSE_EXITED = 6;
	private final static int MOUSE_MENU = 7;
	private final static int KEY_PRESSED = 3;

	static {
		DEBUG.OFF();
	}

	IScope executionScope;

	private int listenedEvent;
	private IDisplaySurface surface;
	private String event;

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

	private int getListeningEvent(final String eventTypeName) {
		if (eventTypeName.equals(IKeyword.MOUSE_DOWN)) return MOUSE_PRESS;
		if (eventTypeName.equals(IKeyword.MOUSE_UP)) return MOUSE_RELEASED;
		if (eventTypeName.equals(IKeyword.MOUSE_CLICKED)) return MOUSE_CLICKED;
		if (eventTypeName.equals(IKeyword.MOUSE_MOVED)) return MOUSE_MOVED;
		if (eventTypeName.equals(IKeyword.MOUSE_ENTERED)) return MOUSE_ENTERED;
		if (eventTypeName.equals(IKeyword.MOUSE_EXITED)) return MOUSE_EXITED;
		if (eventTypeName.equals(IKeyword.MOUSE_MENU)) return MOUSE_MENU;
		return KEY_PRESSED;
	}

	@Override
	public void firstLaunchOn(final IDisplaySurface surface) {
		super.firstLaunchOn(surface);
		this.surface = surface;
		final IExpression eventType = definition.getFacet(IKeyword.NAME);
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
	public String getType() {
		return "Event layer";
	}

	// We explicitly translate by the origin of the surface
	@Override
	public ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
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

	private void executeEvent(final int x, final int y) {
		final IAgent agent = ((EventLayerStatement) definition).getExecuter(executionScope);
		if (agent == null) return;
		final IExecutable executer = ((EventLayerStatement) definition).getExecutable(executionScope);
		if (executer == null) return;
		final ILocation pp = getModelCoordinatesFrom(x, y, surface);
		if (pp == null) return;
		// DEBUG.OUT("Coordinates in env (before test)" + pp);
		if (pp.getX() < 0 || pp.getY() < 0 || pp.getX() >= surface.getEnvWidth()
				|| pp.getY() >= surface.getEnvHeight()) {
			if (MOUSE_EXITED != listenedEvent && MOUSE_ENTERED != listenedEvent) return;
		}
		// DEBUG.OUT("Coordinates in env (after test)" + pp);
		GAMA.runAndUpdateAll(() -> executionScope.execute(executer, agent, null));

	}

	@Override
	public void keyPressed(final String c) {
		if (c.equals(event)) { executeEvent(-1, -1); }
	}

	@Override
	protected void privateDraw(final IScope scope, final IGraphics g) throws GamaRuntimeException {}

	@Override
	public void draw(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		getData().compute(scope, g);
	}

	@Override
	public Boolean isControllable() {
		return false;
	}

	public String getEvent() {
		return event;
	}
}
