/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.graphics.*;
import msi.gama.gui.parameters.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.outputs.layers.*;
import msi.gama.util.GamaList;
import org.eclipse.swt.widgets.Composite;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 23 août 2008
 * 
 * @todo Description
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AgentDisplay extends AbstractDisplay {

	private ActionListener menuListener;
	private ActionListener focusListener;

	private final Set<IAgent> agents = new HashSet();
	private final Set<SelectedAgent> selectedAgents = new HashSet<SelectedAgent>();

	public AgentDisplay(final double env_width, final double env_height,
		final AbstractDisplayLayer layer, final IGraphics dg) {
		super(env_width, env_height, layer, dg);
	}

	@Override
	public void initMenuItems(final IDisplaySurface surface) {
		super.initMenuItems(surface);
		menuListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					surface.fireSelectionChanged(a);
				}
			}

		};

		focusListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					surface.focusOn(a.getGeometry(), AgentDisplay.this);
				}
			}

		};

	}

	@Override
	public void fillComposite(final Composite compo, final DisplayItem item,
		final IDisplaySurface container) throws GamaRuntimeException {
		super.fillComposite(compo, item, container);
		IExpression expr = ((AgentDisplayLayer) model).getFacet(ISymbol.VALUE);
		if ( expr != null ) {
			EditorFactory.createExpression(compo, "Agents:", expr.toGaml(),
				new EditorListener<IExpression>() {

					@Override
					public void valueModified(final IExpression newValue)
						throws GamaRuntimeException, GamlException {
						((AgentDisplayLayer) model).setAgentsExpr(newValue);
						container.updateDisplay();
					}
				}, Types.get(IType.LIST));
		}
	}

	@Override
	public void putMenuItemsIn(final Menu inMenu, final int x, final int y) {
		super.putMenuItemsIn(inMenu, x, y);
		collectAgentsAt(x, y);

		if ( !selectedAgents.isEmpty() ) {
			inMenu.addSeparator();

			for ( SelectedAgent sa : selectedAgents ) {
				sa.buildMenuItems(inMenu);
			}
		}
	}

	protected final Map<Rectangle2D, IAgent> shapes = new HashMap();

	@Override
	public void privateDrawDisplay(final IGraphics g) throws GamaRuntimeException {

		shapes.clear();
		// performance issue
		String aspectName = ((AgentDisplayLayer) model).getAspectName();
		IScope scope = GAMA.obtainNewScope();
		if ( scope != null ) {
			scope.setContext(g);
			for ( IAgent a : getAgentsToDisplay() ) {
				if ( disposed ) {
					break;
				}
				if ( a != null && !a.dead() ) {
					IAspect aspect = a.getSpecies().getAspect(aspectName);
					if ( aspect == null ) {
						aspect = IAspect.DEFAULT_ASPECT;
					}
					shapes.put(aspect.draw(scope, a), a);
				}
			}
			GAMA.releaseScope(scope);
		}
	}

	public Set<IAgent> getAgentsToDisplay() {
		// return agents;
		return ((AgentDisplayLayer) model).getAgentsToDisplay();
	}

	private static class AgentMenuItem extends MenuItem {

		private final IAgent agent;

		AgentMenuItem(final String name, final IAgent agent) {
			super(name);
			this.agent = agent;
		}

		IAgent getAgent() {
			return agent;
		}
	}

	private class SelectedAgent {

		IAgent macro;
		Map<ISpecies, List<SelectedAgent>> micros;

		void buildMenuItems(final Menu parentMenu) {
			Menu macroMenu = new Menu(macro.getName());
			parentMenu.add(macroMenu);

			MenuItem inspectItem = new AgentMenuItem("Inspect", macro);
			inspectItem.addActionListener(menuListener);
			macroMenu.add(inspectItem);

			MenuItem focusItem = new AgentMenuItem("Focus", macro);
			focusItem.addActionListener(focusListener);
			macroMenu.add(focusItem);

			if ( micros != null && !micros.isEmpty() ) {
				Menu microsMenu = new Menu("Micro agents");
				macroMenu.add(microsMenu);

				Menu microSpecMenu;
				for ( ISpecies microSpec : micros.keySet() ) {
					microSpecMenu = new Menu("Species " + microSpec.getName());
					microsMenu.add(microSpecMenu);

					for ( SelectedAgent micro : micros.get(microSpec) ) {
						micro.buildMenuItems(microSpecMenu);
					}
				}
			}
		}
	}

	@Override
	public void collectAgentsAt(final int x, final int y) {
		selectedAgents.clear();

		// GamaGeometry selectionPoint = new GamaGeometry(getModelCoordinatesFrom(x, y));
		// Envelope selectionEnvelope = selectionPoint.getEnvelope();
		// selectionEnvelope.expandBy(selectionWidthInModel);
		//
		// ISpatialIndex globalEnv =
		// GAMA.getFrontmostSimulation().getModel().getModelEnvironment().getSpatialIndex();
		// GamaList<IAgent> agents =
		// globalEnv.queryAllInEnvelope(selectionPoint, selectionEnvelope,
		// In.list(this.getAgentsToDisplay()), false);
		Rectangle2D selection = new Rectangle2D.Double();
		selection.setFrameFromCenter(x, y, x + IDisplaySurface.SELECTION_SIZE / 2, y +
			IDisplaySurface.SELECTION_SIZE / 2);
		// Point2D p = new Point2D.Double(x, y);

		Collection<IAgent> closeAgents = new HashSet();
		for ( Map.Entry<Rectangle2D, IAgent> entry : shapes.entrySet() ) {
			if ( entry.getKey().intersects(selection) ) {
				closeAgents.add(entry.getValue());
			}
		}

		for ( IAgent agent : closeAgents ) {
			SelectedAgent sa = new SelectedAgent();
			sa.macro = agent;
			selectedAgents.add(sa);
		}

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
	}

	private void collectMicroAgentsIn(final SelectedAgent targetMacro,
		final Envelope selectionEnvelope) {

		SelectedAgent sMicro;
		IPopulation microManager;
		List<IAgent> intersectingMicros;

		List<String> microSpecies = targetMacro.macro.getSpecies().getMicroSpeciesNames();
		Collections.sort(microSpecies);

		for ( String microSpec : microSpecies ) {
			microManager = targetMacro.macro.getPopulationFor(microSpec);

			if ( microManager != null && microManager.size() > 0 ) {
				intersectingMicros = new GamaList<IAgent>();
				for ( IAgent m : microManager.getAgentsList() ) {
					if ( m.getEnvelope().intersects(selectionEnvelope) ) {
						intersectingMicros.add(m);
					}
				}

				if ( !intersectingMicros.isEmpty() ) {
					List<SelectedAgent> selectedMicros = new GamaList<SelectedAgent>();
					for ( IAgent iMicro : intersectingMicros ) {
						sMicro = new SelectedAgent();
						sMicro.macro = iMicro;
						if ( iMicro.getSpecies().hasMicroSpecies() && iMicro.hasMembers() ) {
							collectMicroAgentsIn(sMicro,
								iMicro.getEnvelope().intersection(selectionEnvelope));
						}

						selectedMicros.add(sMicro);
					}

					if ( targetMacro.micros == null ) {
						targetMacro.micros = new HashMap<ISpecies, List<SelectedAgent>>();
					}

					if ( !selectedMicros.isEmpty() ) {
						targetMacro.micros.put(microManager.getSpecies(),
							new GamaList<SelectedAgent>(selectedMicros));
					}
				}
			}
		}
	}

	@Override
	protected String getType() {
		return "Agents layer";
	}

	/**
	 * @param agents
	 */
	public void setAgentsToDisplay(final HashSet<IAgent> agents) {
		synchronized (this.agents) {
			this.agents.clear();
			this.agents.addAll(agents);
		}

	}
}
