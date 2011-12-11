/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.util.*;
import msi.gama.gui.application.GUI;
import msi.gama.gui.application.views.AgentInspectView;
import msi.gama.gui.graphics.*;
import msi.gama.gui.util.events.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.util.GamaList;

public class GuiOutputManager implements GamaSelectionProvider, GamaSelectionListener
/* ,IPartListener */{

	private final Map<String, IDisplayOutput> displayOutputs = new HashMap();
	private final Map<String, MonitorOutput> monitorOutputs = new HashMap();
	private final Inspect inspect = new Inspect();
	List<GamaSelectionListener> listeners = new ArrayList();

	GuiOutputManager(final OutputManager m) {
		GUI.prepareFor(true);
	}

	private class Inspect implements Runnable {

		Object entity;

		public void setEntity(final Object ent) {
			entity = ent;
			GUI.asyncRun(this);
		}

		@Override
		public void run() {
			if ( !displayOutputs.containsKey(AgentInspectView.ID) && entity != null ) {
				try {
					new InspectDisplayOutput("Agent inspector", InspectDisplayOutput.INSPECT_AGENT)
						.launch();
				} catch (GamlException e) {
					GamaRuntimeException g = new GamaRuntimeException(e);
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

	public void buildOutputs(final IExperiment exp) {
		GUI.run(new Runnable() {

			@Override
			public void run() {
				// GUI.debug("Displaying the console");
				GUI.showConsoleView();
				// GUI.debug("Hiding the monitors (if any)");
				GUI.hideMonitorView();
				// GUI.debug("Setting the title of the workbench");
				GUI.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFileName());
				if ( !monitorOutputs.isEmpty() ) {
					// GUI.debug("Redisplaying the monitors");
					GUI.showMonitorView();
				}
				if ( !exp.getParametersToDisplay().isEmpty() ) {
					// GUI.debug("Showing the parameters view");
					GUI.showParameterView(exp);
				}
				GUI.informStatus(" Simulation ready ");
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
			GUI.closeViewOf(out);
		}
		displayOutputs.clear();
		monitorOutputs.clear();
		// GUI.debug("Closing the parameter view");
		GUI.hideParameterView();
		GUI.hideMonitorView();
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

	/**
	 * @param output
	 */

	public void setShowDisplayOutputs(final IScope sim, final boolean selection)
		throws GamaRuntimeException {
		if ( !selection ) {
			pauseDisplayOutputs();
		} else {
			resumeDisplayOutputs(sim);
		}
	}

	public IDisplaySurface getDisplaySurfaceFor(final LayerDisplayOutput layerDisplayOutput,
		final double w, final double h) {
		return new AWTDisplaySurface(w, h, layerDisplayOutput);
	}
}
