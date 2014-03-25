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

import java.awt.geom.Rectangle2D;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.*;
import msi.gaml.types.*;
import org.eclipse.swt.widgets.Composite;

/**
 * Written by drogoul Modified on 23 août 2008
 * 
 * @todo Description
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AgentLayer extends AbstractLayer {

	public AgentLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		return shapes.get(geometry);
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		IExpression expr = null;
		if ( definition instanceof AgentLayerStatement ) {
			expr = ((AgentLayerStatement) definition).getFacet(IKeyword.VALUE);
		}
		if ( expr != null ) {
			EditorFactory.createExpression(compo, "Agents:", expr.toGaml(), new EditorListener<IExpression>() {

				@Override
				public void valueModified(final IExpression newValue) throws GamaRuntimeException {
					((AgentLayerStatement) definition).setAgentsExpr(newValue);
					if ( isPaused(container) ) {
						container.forceUpdateDisplay();
					}
				}
			}, Types.get(IType.LIST));
		}
	}

	protected final Map<IAgent, Rectangle2D> shapes = new HashMap();

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		shapes.clear();
		// performance issue
		String aspectName = IKeyword.DEFAULT;
		if ( definition instanceof AgentLayerStatement ) {
			aspectName = ((AgentLayerStatement) definition).getAspectName();
		}
		for ( final IAgent a : getAgentsToDisplay() ) {
			if ( a != null/* && !scope.interrupted() */) {
				IExecutable aspect = a.getSpecies().getAspect(aspectName);
				if ( aspect == null ) {
					aspect = AspectStatement.DEFAULT_ASPECT;
				}
				Object[] result = new Object[1];
				scope.execute(aspect, a, null, result);
				final Rectangle2D r = (Rectangle2D) result[0];
				// final Rectangle2D r = aspect.draw(scope, a);
				if ( r != null ) {
					shapes.put(a, r);
				}
			}
		}
	}

	public Collection<IAgent> getAgentsForMenu() {
		if ( shapes.isEmpty() ) { return getAgentsToDisplay(); }
		// Avoid recalculating the agents
		return shapes.keySet();
	}

	public Collection<IAgent> getAgentsToDisplay() {
		// return agents;
		if ( definition instanceof AgentLayerStatement ) { return ((AgentLayerStatement) definition)
			.getAgentsToDisplay(); }
		return ((GridLayerStatement) definition).getAgentsToDisplay();
	}

	@Override
	public Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		final Set<IAgent> selectedAgents = new HashSet();

		// GamaGeometry selectionPoint = new GamaGeometry(getModelCoordinatesFrom(x, y));
		// Envelope selectionEnvelope = selectionPoint.getEnvelope();
		// selectionEnvelope.expandBy(selectionWidthInModel);
		//
		// ISpatialIndex globalEnv =
		// GAMA.getFrontmostSimulation().getModel().getModelEnvironment().getSpatialIndex();
		// GamaList<IAgent> agents =
		// globalEnv.queryAllInEnvelope(selectionPoint, selectionEnvelope,
		// In.list(this.getAgentsToDisplay()), false);
		final Rectangle2D selection = new Rectangle2D.Double();
		selection.setFrameFromCenter(x, y, x + IDisplaySurface.SELECTION_SIZE / 2, y + IDisplaySurface.SELECTION_SIZE /
			2);
		// Point2D p = new Point2D.Double(x, y);

		// Set<IAgent> closeAgents = new HashSet();
		for ( final Map.Entry<IAgent, Rectangle2D> entry : shapes.entrySet() ) {
			if ( entry.getValue().intersects(selection) ) {
				selectedAgents.add(entry.getKey());
			}
		}

		// for ( IAgent agent : closeAgents ) {
		// selectedAgents.add(agent);
		// SelectedAgent sa = new SelectedAgent();
		// sa.macro = agent;
		// selectedAgents.add(sa);
		// }

		// direct micro-populations of "world"
		// List<IPopulation> microPopulations =
		// GAMA.getFrontmostSimulation().getWorld().getMicroPopulations();
		// TODO to be implemented

		// GamaList<IAgent> closeAgents = globalEnv.getAgentsInEnvelope(selectionPoint,
		// selectionEnvelope, In.list(getAgentsToDisplay()), false);

		/*
		 * SelectedAgent sa;
		 * for (IAgent a : closeAgents) {
		 * sa = new SelectedAgent();
		 * sa.macro = a;
		 * 
		 * if (a.getSpecies().hasMicroSpecies() &&
		 * a.getGlobalGeometry().getEnvelope().intersects(selectionEnvelope) && a.hasMembers()) {
		 * collectMicroAgentsIn(sa, selectionEnvelope);
		 * }
		 * 
		 * selectedAgents.add(sa);
		 * }
		 */

		return selectedAgents;
	}

	// private void collectMicroAgentsIn(final SelectedAgent targetMacro,
	// final Envelope selectionEnvelope) {
	//
	// SelectedAgent sMicro;
	// IPopulation microManager;
	// IList<IAgent> intersectingMicros;
	//
	// List<String> microSpecies = targetMacro.macro.getSpecies().getMicroSpeciesNames();
	// Collections.sort(microSpecies);
	//
	// for ( String microSpec : microSpecies ) {
	// microManager = targetMacro.macro.getPopulationFor(microSpec);
	//
	// if ( microManager != null && microManager.size() > 0 ) {
	// intersectingMicros = new GamaList<IAgent>();
	// for ( IAgent m : microManager.getAgentsList() ) {
	// if ( m.getEnvelope().intersects(selectionEnvelope) ) {
	// intersectingMicros.add(m);
	// }
	// }
	//
	// if ( !intersectingMicros.isEmpty() ) {
	// List<SelectedAgent> selectedMicros = new GamaList<SelectedAgent>();
	// for ( IAgent iMicro : intersectingMicros ) {
	// sMicro = new SelectedAgent();
	// sMicro.macro = iMicro;
	// if ( iMicro.getSpecies().hasMicroSpecies() && iMicro.hasMembers() ) {
	// collectMicroAgentsIn(sMicro,
	// iMicro.getEnvelope().intersection(selectionEnvelope));
	// }
	//
	// selectedMicros.add(sMicro);
	// }
	//
	// if ( targetMacro.micros == null ) {
	// targetMacro.micros = new HashMap<ISpecies, List<SelectedAgent>>();
	// }
	//
	// if ( !selectedMicros.isEmpty() ) {
	// targetMacro.micros.put(microManager.getSpecies(),
	// new GamaList<SelectedAgent>(selectedMicros));
	// }
	// }
	// }
	// }
	// }

	@Override
	public String getType() {
		return "Agents layer";
	}

}
