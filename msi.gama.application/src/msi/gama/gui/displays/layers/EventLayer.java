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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.displays.layers.listeners.CustomisedEventListener;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.util.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import org.eclipse.swt.widgets.Composite;

/**
 * Written by marilleau
 */

public class EventLayer extends AgentLayer {

	private EventLayerStatement myStatement;
	private IDisplaySurface display;

	public EventLayer(final ILayerStatement layer) {
		super(layer);
		buildEventLayer();
	}

	private void buildEventLayer() {

	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		this.myStatement = (EventLayerStatement) this.definition;

		IScope scope = GAMA.getDefaultScope();

		IExpression eventType = this.myStatement.getFacet(IKeyword.NAME);
		IExpression actionName = this.myStatement.getFacet(IKeyword.ACTION);

		String currentMouseEvent = Cast.asString(scope, eventType.value(scope));
		String currentAction = Cast.asString(scope, actionName.value(scope));
		this.display = container;
		container.addMouseListener(new CustomisedEventListener(this, currentMouseEvent, currentAction));

	}

	public IDisplaySurface getDisplay() {
		return display;
	}

	public void setDisplay(IDisplaySurface display) {
		this.display = display;
	}

	@Override
	public String getType() {
		return "Event layer";
	}

	public IList<IAgent> selectAgent(final int x, final int y) {
		int xc = x - this.getDisplay().getOriginX();
		int yc = y - this.getDisplay().getOriginY();
		IList<IAgent> result = new GamaList<IAgent>();
		final List<ILayer> layers = this.getDisplay().getLayerManager().getLayersIntersecting(xc, yc);

		for ( ILayer layer : layers ) {
			Set<IAgent> agents = layer.collectAgentsAt(xc, yc, getDisplay());
			if ( !agents.isEmpty() ) {
				result.addAll(agents);
			}
		}
		return result;
	}
}
