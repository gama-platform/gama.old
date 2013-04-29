package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;

public class AWTDisplaySurfaceMenu {

	private final IDisplaySurface surface;
	private final PopupMenu agentsMenu;

	AWTDisplaySurfaceMenu(final IDisplaySurface s) {
		surface = s;
		agentsMenu = new PopupMenu();
		((AbstractAWTDisplaySurface) s).add(agentsMenu);

	}

	public void selectAgents(final int mousex, final int mousey, final int x, final int y, final List<ILayer> displays) {
		agentsMenu.removeAll();
		if ( displays.isEmpty() ) { return; }
		GamaPoint p = displays.get(0).getModelCoordinatesFrom(x, y);
		SelectedAgent world = new SelectedAgent(GAMA.getFrontmostSimulation(), p);
		world.buildMenuItems(agentsMenu, displays.get(0), "World agent");
		agentsMenu.addSeparator();
		for ( ILayer display : displays ) {
			Set<IAgent> agents = display.collectAgentsAt(x, y);
			if ( agents.isEmpty() ) {
				continue;
			}
			p = display.getModelCoordinatesFrom(x, y);
			java.awt.Menu m = new java.awt.Menu(display.getName());
			MenuItem grey = new MenuItem("Selected agents");
			grey.setEnabled(false);
			m.add(grey);
			if ( !agents.isEmpty() ) {
				m.addSeparator();

				for ( IAgent agent : agents ) {
					SelectedAgent sa = new SelectedAgent(agent, p);
					sa.buildMenuItems(m, display);
				}
			}
			agentsMenu.add(m);
		}
		agentsMenu.show((Component) surface, mousex, mousey);
	}

	private final ActionListener menuListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			AWTDisplaySurfaceMenu.AgentMenuItem source = (AWTDisplaySurfaceMenu.AgentMenuItem) e.getSource();
			IAgent a = source.getAgent();
			if ( a != null ) {
				surface.fireSelectionChanged(a);
			}
		}

	};

	private final ActionListener focusListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			AWTDisplaySurfaceMenu.AgentMenuItem source = (AWTDisplaySurfaceMenu.AgentMenuItem) e.getSource();
			IAgent a = source.getAgent();
			if ( a != null ) {
				surface.focusOn(a.getGeometry(), source.getDisplay());
			}
		}

	};

	private final ActionListener highlightListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			AWTDisplaySurfaceMenu.AgentMenuItem source = (AWTDisplaySurfaceMenu.AgentMenuItem) e.getSource();
			IAgent a = source.getAgent();
			if ( a != null ) {
				if ( a == GuiUtils.getHighlightedAgent() ) {
					GuiUtils.setHighlightedAgent(null);
				} else {
					GuiUtils.setHighlightedAgent(a);
				}
			}
		}

	};

	private static ActionListener commandListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			AgentMenuItem source = (AgentMenuItem) e.getSource();
			final IAgent a = source.getAgent();
			final IStatement c = source.getCommand();
			final ILocation p = source.getLocation();
			if ( c != null && a != null && !a.dead() ) {
				GamaHelper action = new GamaHelper() {

					@Override
					public Object run(final IScope scope) {
						if ( p != null ) {
							scope.addVarWithValue(IKeyword.USER_LOCATION, p);
						}
						if ( !a.dead() ) {
							scope.execute(c, a);
						}
						return null;
					}

				};
				GAMA.getFrontmostSimulation().getScheduler().executeOneAction(action);
			}
		}

	};

	public class SelectedAgent {

		private final IAgent macro;
		private final GamaPoint userLocation;
		private Map<ISpecies, java.util.List<SelectedAgent>> micros;

		SelectedAgent(final IAgent agent, final GamaPoint point) {
			macro = agent;
			userLocation = point;

		}

		void buildMenuItems(final Menu parentMenu, final ILayer display) {
			buildMenuItems(parentMenu, display, macro.getName());
		}

		void buildMenuItems(final Menu parentMenu, final ILayer display, final String name) {
			Menu macroMenu = new Menu(name);
			parentMenu.add(macroMenu);

			MenuItem inspectItem = new AgentMenuItem("Inspect", macro, display);
			inspectItem.addActionListener(menuListener);
			macroMenu.add(inspectItem);

			MenuItem focusItem = new AgentMenuItem("Focus", macro, display);
			focusItem.addActionListener(focusListener);
			macroMenu.add(focusItem);

			MenuItem highlightItem =
				new AgentMenuItem(macro == GuiUtils.getHighlightedAgent() ? "Remove highlight" : "Highlight", macro,
					display);
			highlightItem.addActionListener(highlightListener);
			macroMenu.add(highlightItem);

			Collection<UserCommandStatement> commands = macro.getSpecies().getUserCommands();
			if ( !commands.isEmpty() ) {
				macroMenu.addSeparator();
				for ( UserCommandStatement c : commands ) {
					MenuItem actionItem = new AgentMenuItem(macro, c, userLocation);
					actionItem.addActionListener(commandListener);
					macroMenu.add(actionItem);
				}
			}
			if ( micros != null && !micros.isEmpty() ) {
				Menu microsMenu = new Menu("Micro agents");
				macroMenu.add(microsMenu);

				Menu microSpecMenu;
				for ( ISpecies microSpec : micros.keySet() ) {
					microSpecMenu = new Menu("Species " + microSpec.getName());
					microsMenu.add(microSpecMenu);

					for ( SelectedAgent micro : micros.get(microSpec) ) {
						micro.buildMenuItems(microSpecMenu, display);
					}
				}
			}
		}
	}

	static class AgentMenuItem extends MenuItem {

		private final IAgent agent;
		private final ILayer display;
		private final IStatement command;
		private final GamaPoint userLocation;

		AgentMenuItem(final String name, final IAgent agent, final ILayer display) {
			super(name);
			this.agent = agent;
			this.display = display;
			command = null;
			userLocation = null;
		}

		AgentMenuItem(final IAgent agent, final IStatement command, final GamaPoint point) {
			super(command.getName());
			this.agent = agent;
			this.display = null;
			this.command = command;
			userLocation = point;
		}

		ILocation getLocation() {
			return userLocation;
		}

		IAgent getAgent() {
			return agent;
		}

		IStatement getCommand() {
			return command;
		}

		ILayer getDisplay() {
			return display;
		}
	}
}
