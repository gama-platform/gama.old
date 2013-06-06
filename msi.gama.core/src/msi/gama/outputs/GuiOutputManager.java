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
			if ( ent != null ) {
				GuiUtils.asyncRun(this);
			}
		}

		@Override
		public void run() {
			if ( !displayOutputs.containsKey(GuiUtils.AGENT_VIEW_ID) && entity != null ) {
				try {
					new InspectDisplayOutput("Agent inspector", InspectDisplayOutput.INSPECT_AGENT).launch();
				} catch (final GamaRuntimeException g) {
					g.addContext("In opening the agent inspector");
					GAMA.reportError(g);
				}
			}
			for ( final GamaSelectionListener l : listeners ) {
				l.selectionChanged(entity);
			}
		}
	}

	public void buildOutputs(final IExperimentSpecies exp) {
		GuiUtils.hideMonitorView();
		GuiUtils.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
		if ( !monitorOutputs.isEmpty() ) {
			GuiUtils.showView(GuiUtils.MONITOR_VIEW_ID, null);
		}
		GuiUtils.showParameterView(exp);
	}

	public void addDisplayOutput(final IOutput output) {
		if ( output.getClass() == MonitorOutput.class ) {
			monitorOutputs.put(output.getName(), (MonitorOutput) output);
		} else {
			displayOutputs.put(output.getId(), (IDisplayOutput) output);
		}
	}

	public void removeDisplayOutput(final IOutput output) {
		if ( output instanceof MonitorOutput && !(output instanceof InspectDisplayOutput) ) {
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
		fireSelectionChanged(null);
		for ( final IDisplayOutput out : new GamaList<IDisplayOutput>(displayOutputs.values()) ) {
			GuiUtils.closeViewOf(out);
		}
		displayOutputs.clear();
		monitorOutputs.clear();
		inspect.setEntity(null);
		GuiUtils.hideView(GuiUtils.PARAMETER_VIEW_ID);
		GuiUtils.hideMonitorView();
	}

	@Override
	public void addGamaSelectionListener(final GamaSelectionListener listener) {
		if ( !listeners.contains(listener) ) {
			listeners.add(listener);
		}
	}

	@Override
	public void fireSelectionChanged(final Object entity) {
		if ( inspect != null ) {
			inspect.setEntity(entity);
		}
	}

	@Override
	public void removeGamaSelectionListener(final GamaSelectionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void selectionChanged(final Object entity) {
		fireSelectionChanged(entity);
	}

	public void setShowDisplayOutputs(final IScope sim, final boolean selection) throws GamaRuntimeException {
		if ( !selection ) {
			pauseDisplayOutputs();
		} else {
			resumeDisplayOutputs(sim);
		}
	}

	public void desynchronizeOutputs() {
		for ( final IDisplayOutput o : displayOutputs.values() ) {
			final IDisplaySurface s = o.getSurface();
			if ( s != null ) {
				s.setSynchronized(false);
			}
		}
	}

	public void forceUpdateOutputs() {
		for ( final IDisplayOutput o : displayOutputs.values() ) {
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
