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
package msi.gama.outputs;

import java.util.*;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

public class GuiOutputManager implements GamaSelectionProvider, GamaSelectionListener
/* ,IPartListener */{

	private final Map<String, IDisplayOutput> displayOutputs = new HashMap();
	private final Map<String, MonitorOutput> monitorOutputs = new HashMap();
	private final Inspect inspect = new Inspect();
	List<GamaSelectionListener> listeners = new ArrayList();

	public GuiOutputManager() {}

	GuiOutputManager(final OutputManager m) {
		GuiUtils.prepareFor(true);
	}

	private class Inspect implements Runnable {

		Object entity;

		public void setEntity(final Object ent) {
			entity = ent;
			GuiUtils.asyncRun(this);
		}

		@Override
		public void run() {
			if ( !displayOutputs.containsKey(GuiUtils.AGENT_VIEW_ID) && entity != null ) {
				try {
					new InspectDisplayOutput("Agent inspector", InspectDisplayOutput.INSPECT_AGENT)
						.launch();
				} catch (GamaRuntimeException g) {
					g.addContext("In opening the agent inspector");
					GAMA.reportError(g);
				}
			}
			for ( final GamaSelectionListener l : listeners ) {
				// GUI.debug("Telling " + l + " that the selection has changed to " + entity);
				l.selectionChanged(entity);
			}
		}
	}

	public void buildOutputs(final IExperimentSpecies exp) {
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				// GUI.debug("Displaying the console");
				// GuiUtils.showConsoleView();
				// GuiUtils.showView("org.eclipse.swt.tools.views.SpyView", null);
				// GUI.debug("Hiding the monitors (if any)");
				GuiUtils.hideMonitorView();
				// GUI.debug("Setting the title of the workbench");
				GuiUtils.setWorkbenchWindowTitle(exp.getName() + " - " +
					exp.getModel().getFilePath());
				if ( !monitorOutputs.isEmpty() ) {
					// GUI.debug("Redisplaying the monitors");
					GuiUtils.showView(GuiUtils.MONITOR_VIEW_ID, null);
				}
				// if ( !exp.getParametersToDisplay().isEmpty() ) {
				// GUI.debug("Showing the parameters view");
				GuiUtils.showParameterView(exp);
				// }
				GuiUtils.informStatus(" Simulation of experiment " + exp.getName() + " of model " +
					exp.getModel().getName() + " ready.");
			}
		});
	}

	public void addDisplayOutput(final IOutput output) {
		if ( output.getClass() == MonitorOutput.class ) {
			monitorOutputs.put(output.getName(), (MonitorOutput) output);
		} else {
			displayOutputs.put(output.getId(), (IDisplayOutput) output);
		}
	}

	public void removeDisplayOutput(final IOutput output) {
		if ( output instanceof MonitorOutput ) {
			monitorOutputs.remove(output.getName());
		} else if ( output != null ) {
			displayOutputs.remove(output.getId());
		}
	}

	void resumeDisplayOutputs(final IScope sim) throws GamaRuntimeException {
		for ( final IOutput output : new GamaList<IOutput>(displayOutputs.values()) ) {
			if ( output.isOpen() && !output.isPermanent() ) {
				output.resume();
				output.update();
			}
		}
	}

	void pauseDisplayOutputs() {
		for ( final IOutput output : new GamaList<IOutput>(displayOutputs.values()) ) {
			if ( output.isOpen() && !output.isPermanent() ) {
				output.pause();
			}

		}
	}

	public void dispose() {
		// GUI.debug("Cancelling the current selection");
		fireSelectionChanged(null);
		for ( IDisplayOutput out : new GamaList<IDisplayOutput>(displayOutputs.values()) ) {
			// GUI.debug("Closing the view of output " + out.getName());
			GuiUtils.closeViewOf(out);
		}
		displayOutputs.clear();
		monitorOutputs.clear();
		// GUI.debug("Closing the parameter view");
		GuiUtils.hideView(GuiUtils.PARAMETER_VIEW_ID);
		GuiUtils.hideMonitorView();
	}

	@Override
	public void addGamaSelectionListener(final GamaSelectionListener listener) {
		if ( !listeners.contains(listener) ) {
			// GUI.debug("Gama listener added :" + listener);
			listeners.add(listener);
		}
	}

	@Override
	public void fireSelectionChanged(final Object entity) {
		// GUI.debug("Selection has changed :" + entity);
		if ( inspect != null ) {
			inspect.setEntity(entity);
		}
	}

	@Override
	public void removeGamaSelectionListener(final GamaSelectionListener listener) {
		// GUI.debug("Gama listener removed :" + listener);
		listeners.remove(listener);
	}

	@Override
	public void selectionChanged(final Object entity) {
		fireSelectionChanged(entity);
	}

	// @Override
	// public void partActivated(final IWorkbenchPart part) {}
	//
	// @Override
	// public void partBroughtToTop(final IWorkbenchPart part) {}
	//
	// @Override
	// public void partClosed(final IWorkbenchPart part) {
	// if ( part instanceof IGamaView ) {
	// IDisplayOutput output = ((IGamaView) part).getOutput();
	// GUI.debug("Output " + output.toGaml() +
	// " closed and unscheduled because its view is closed");
	// output.close();
	// manager.unscheduleOutput(output);
	// removeDisplayOutput(output);
	// output.dispose();
	// }
	// }
	//
	// @Override
	// public void partDeactivated(final IWorkbenchPart part) {}
	//
	// @Override
	// public void partOpened(final IWorkbenchPart part) {
	// if ( part instanceof IGamaView ) {
	// IDisplayOutput output = ((IGamaView) part).getOutput();
	// if ( !output.isOpen() ) {
	// output.open();
	// }
	// }
	// }

	public void setShowDisplayOutputs(final IScope sim, final boolean selection)
		throws GamaRuntimeException {
		if ( !selection ) {
			pauseDisplayOutputs();
		} else {
			resumeDisplayOutputs(sim);
		}
	}

	/**
	 * 
	 */
	public void desynchronizeOutputs() {
		for ( IDisplayOutput o : displayOutputs.values() ) {
			IDisplaySurface s = o.getSurface();
			if ( s != null ) {
				s.setSynchronized(false);
			}
		}
	}

	public void forceUpdateOutputs() {

		for ( IDisplayOutput o : displayOutputs.values() ) {
			o.update();
		}
	}

	public Map<String, IDisplayOutput> getDisplayOutputs() {
		return displayOutputs;
	}

	public Map<String, MonitorOutput> getMonitorOutputs() {
		return monitorOutputs;
	}
	
	
}
