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
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;

/**
 * The Class OutputManager.
 * 
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */
@symbol(name = IKeyword.OUTPUT, kind = ISymbolKind.OUTPUT, with_sequence = true)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.EXPERIMENT })
public class SimulationOutputManager extends Symbol implements IOutputManager {

	private final Map<String, IOutput> outputs = new HashMap<String, IOutput>();
	private final Map<String, IDisplayOutput> displayOutputs = new HashMap();
	private final Map<String, MonitorOutput> monitorOutputs = new HashMap();
	private final Set<IOutput> outputsToUnschedule = new HashSet<IOutput>();
	private final Set<IOutput> scheduledOutputs = new HashSet<IOutput>();
	private final Set<IOutput> outputsToUpdateNow = new HashSet();

	public SimulationOutputManager(final IDescription desc) {
		super(desc);
	}

	@Override
	public IOutput getOutput(final String id) {
		return outputs.get(id);
	}

	public IOutput getOutputWithName(final String name) {
		for ( final IOutput output : outputs.values() ) {
			if ( output.getName().equals(name) ) { return output; }
		}
		return null;
	}

	@Override
	public void addOutput(final IOutput output) {
		if ( output == null || outputs.containsValue(output) ) { return; }
		outputs.put(output.getId(), output);
		if ( output instanceof IDisplayOutput ) {
			if ( output.getClass() == MonitorOutput.class ) {
				monitorOutputs.put(output.getName(), (MonitorOutput) output);
			} else {
				displayOutputs.put(output.getId(), (IDisplayOutput) output);
			}
		}
	}

	public void buildOutputs(final IExperimentSpecies exp) {
		GuiUtils.prepareFor(exp.isGui());
		GuiUtils.hideMonitorView();
		GuiUtils.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
		if ( !monitorOutputs.isEmpty() ) {
			GuiUtils.showView(GuiUtils.MONITOR_VIEW_ID, null);
		}
		GuiUtils.showParameterView(exp);
	}

	@Override
	public void init(final IScope scope) {
		GuiUtils.debug("SimulationOutputManager.init");
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				for ( final IOutput output : outputs.values() ) {
					if ( !output.isPermanent() ) {
						try {
							if ( !scope.init(output) ) { return; }
						} catch (final GamaRuntimeException e) {
							e.addContext("in preparing output " + output.getName());
							e.addContext("output " + output.getName() + " has not been opened");
							GAMA.reportError(e);
							continue;
						} catch (final Exception e) {
							e.printStackTrace();
						}
						try {
							output.schedule();
							output.open();
							outputsToUpdateNow.add(output);
						} catch (final GamaRuntimeException e) {
							e.addContext("in opening output " + output.getName());
							e.addContext("output " + output.getName() + " has not been opened");
							GAMA.reportError(e);
							continue;
						} catch (final Exception e) {
							e.printStackTrace();
						}

					}
				}

			}
		});

		OutputSynchronizer.waitForViewsToBeInitialized();
		GuiUtils.informStatus("Experiment ready");
		scope.step(this);

	}

	public synchronized void dispose(final boolean includingBatch) {
		try {
			outputsToUpdateNow.clear();
			GuiUtils.setSelectedAgent(null);
			GuiUtils.setHighlightedAgent(null);
			for ( final IDisplayOutput out : new GamaList<IDisplayOutput>(displayOutputs.values()) ) {
				GuiUtils.closeViewOf(out);
			}
			displayOutputs.clear();
			monitorOutputs.clear();
			GuiUtils.hideView(GuiUtils.PARAMETER_VIEW_ID);
			GuiUtils.hideMonitorView();

			outputsToUnschedule.clear();
			scheduledOutputs.clear();
			for ( final IOutput output : outputs.values() ) {
				if ( includingBatch || !output.isPermanent() ) {
					output.dispose();
				}
			}
			outputs.clear();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void scheduleOutput(final IOutput output) {
		scheduledOutputs.add(output);
	}

	@Override
	public void unscheduleOutput(final IOutput o) {
		if ( scheduledOutputs.contains(o) ) {
			outputsToUnschedule.add(o);
		}
		if ( o instanceof MonitorOutput && !(o instanceof InspectDisplayOutput) ) {
			monitorOutputs.remove(o.getName());
		} else if ( o != null ) {
			displayOutputs.remove(o.getId());
		}
	}

	@Override
	public void step(final IScope scope) {
		final int cycle = scope.getClock().getCycle();
		scheduledOutputs.removeAll(outputsToUnschedule);
		outputsToUnschedule.clear();
		for ( final IOutput o : new HashSet<IOutput>(scheduledOutputs) ) {
			if ( !o.isPaused() && !o.isClosed() ) {
				final long ii = o.getNextTime();
				if ( cycle >= ii ) {
					try {
						try {
							if ( !scope.step(o) ) { return; }
							o.update();
						} catch (final GamaRuntimeException e) {
							throw e;
						} catch (final Exception e) {
							throw GamaRuntimeException.create(e);
						}
					} catch (final GamaRuntimeException e) {
						e.addContext("in computing output " + o.getName());
						e.addContext("output " + o.getName() + " has not been computed at cycle " + cycle);
						GAMA.reportError(e);
						continue;
					}
					o.setNextTime(cycle + o.getRefreshRate());
				}
			}
		}
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		for ( final ISymbol s : commands ) {
			if ( s instanceof IOutput ) {
				addOutput((IOutput) s);
			}
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

	@Override
	public void forceUpdateOutputs() {
		for ( final IDisplayOutput o : displayOutputs.values() ) {
			o.update();
		}
	}

	public Map<String, IOutput> getOutputs() {
		return outputs;
	}

}
